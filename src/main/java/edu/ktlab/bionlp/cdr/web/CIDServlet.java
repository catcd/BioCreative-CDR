package edu.ktlab.bionlp.cdr.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jersey.repackaged.com.google.common.collect.Lists;
import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.dataset.CTDRelationMatcher;
import edu.ktlab.bionlp.cdr.dataset.ctd.SilverDataset;
import edu.ktlab.bionlp.cdr.nlp.nen.MentionNormalization;
import edu.ktlab.bionlp.cdr.nlp.ner.MaxentNERFactoryExample;
import edu.ktlab.bionlp.cdr.nlp.ner.CDRNERRecognizer;
import edu.ktlab.bionlp.cdr.nlp.rel.CIDRelationClassifier;
import edu.ktlab.bionlp.cdr.util.FileHelper;
import edu.stanford.nlp.util.Pair;

public class CIDServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CDRNERRecognizer nerFinder;
	private File temp = new File("temp/cid_services.txt");
	private MentionNormalization normalizer;
	private CIDRelationClassifier classifier;
	private CollectionFactory factory;
	private CTDRelationMatcher ctdmatcher;
	private CTDRelationMatcher trickmatcher;
	private SilverDataset silver;

	public CIDServlet() {
		try {
			nerFinder = new CDRNERRecognizer("models/ner/cdr_full.perc.model",
					MaxentNERFactoryExample.createFeatureGenerator());
			normalizer = new MentionNormalization("models/nen/cdr_full.txt", "models/nen/mesh2015.gzip");
			classifier = new CIDRelationClassifier("models/cid.full.model", "models/cid.full.wordlist");
			ctdmatcher = new CTDRelationMatcher("models/ctd_relations_m.txt");
			trickmatcher = new CTDRelationMatcher("models/trick_relations.txt");
			silver = new SilverDataset();
			silver.loadJsonFile("models/silver.gzip");

			factory = new CollectionFactory(true);

			if (temp.exists())
				temp.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "All services require POST.");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// test parameters
		String format = request.getParameter("format");
		if (format == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter format ");
			return;
		}

		int run = 1;
		String setString = request.getParameter("run");
		if (setString != null) {
			try {
				run = Integer.parseInt(setString);
			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot parse parameter set: " + setString);
				return;
			}
		}
		if (!(1 <= run && run <= 3)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Run has to be one of 1, 2, 3: " + run);
			return;
		}

		// read data
		Optional<String> optional = readData(request);
		if (!optional.isPresent()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No data is found in the POST.");
			return;
		}

		// test format
		String data = optional.get();
		if (!checkFormat(data, format)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					String.format("The input text is not in %s format", format));
			return;
		}

		try {
			String result = annotate(data, run);
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
			response.setStatus(HttpServletResponse.SC_OK);
			FileHelper.appendToFile("Run " + run + "\n" + result, temp, Charset.forName("UTF-8"));
		} catch (Exception e) {
			FileHelper.appendToFile("Run " + run + " fail\n", temp, Charset.forName("UTF-8"));
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
		}
	}

	/**
	 * Read data from the http post
	 *
	 * @param request
	 *            the http post
	 * @return data from the http post
	 */
	private Optional<String> readData(HttpServletRequest request) {
		try {
			StringBuilder sb = new StringBuilder();
			String line = null;
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
			if (sb.length() == 0) {
				return Optional.empty();
			} else {
				return Optional.of(sb.toString());
			}
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	private boolean checkFormat(String data, String format) {
		if (format.equals("pubtator")) {
			return true;
		} else if (format.equals("bioc")) {
			return true;
		} else {
			return false;
		}
	}

	private String annotate(String data, int run) throws Exception {
		Document doc = factory.loadDocumentFromString(data);

		for (Sentence sent : doc.getSentences()) {
			List<Annotation> anns = nerFinder.recognize(doc, sent, normalizer);
			for (Annotation ann : anns) {
				doc.addAnnotation(ann);
				data += doc.getPmid() + "\t" + ann.getStartBaseOffset() + "\t" + ann.getEndBaseOffset() + "\t"
						+ ann.getContent() + "\t" + ann.getType() + "\t" + ann.getReference() + "\n";
			}
		}

		Set<Relation> candidateRels = doc.getRelationCandidates();
		Set<Relation> predictRels = new HashSet<Relation>();
		List<Sentence> sents = doc.getSentences();
		for (Sentence sent : sents) {
			for (Relation candidateRel : candidateRels) {
				if (sent.containRelation(candidateRel)) {
					List<Annotation> annsChed = sent.getAnnotation(candidateRel.getChemicalID());
					List<Annotation> annsDis = sent.getAnnotation(candidateRel.getDiseaseID());

					List<Pair<Annotation, Annotation>> pairs = Lists.newArrayList();
					for (Annotation annChed : annsChed)
						for (Annotation annDis : annsDis) {
							pairs.add(new Pair<Annotation, Annotation>(annChed, annDis));
						}

					for (Pair<Annotation, Annotation> pair : pairs) {
						double score = classifier.classify(pair, sent);
						String predict = classifier.getLabel(score);
						if (predict.equals("CID"))
							predictRels.add(candidateRel);
					}
				}
			}
		}

		for (Relation rel : candidateRels) {
			List<Relation> rs = trickmatcher.find(rel);
			if (rs.size() == 0)
				predictRels.addAll(rs);
		}

		if (predictRels.size() == 0) {
			if (run == 1) {
				for (Relation rel : candidateRels) {
					if (ctdmatcher.match(rel))
						predictRels.add(rel);
				}
			} else if (run == 2) {
				if (silver.getDocs().containsKey(doc.getPassages().get(0).getContent().hashCode())) {
					Set<Relation> ctdRels = silver.getDocs().get(doc.getPassages().get(0).getContent().hashCode())
							.getRelations();
					for (Relation rel : candidateRels) {
						if (ctdRels.contains(rel))
							predictRels.add(rel);
					}
				}
			}
		}

		if (run == 3) {
			if (silver.getDocs().containsKey(doc.getPassages().get(0).getContent().hashCode())) {
				Set<Relation> ctdRels = silver.getDocs().get(doc.getPassages().get(0).getContent().hashCode())
						.getRelations();
				for (Relation rel : candidateRels) {
					if (ctdRels.contains(rel))
						predictRels.add(rel);
				}
			}
		}

		for (Relation rel : predictRels)
			if (!rel.getChemicalID().equals("-1") && !rel.getDiseaseID().equals("-1"))
				data += doc.getPmid() + "\tCID\t" + rel.getChemicalID() + "\t" + rel.getDiseaseID() + "\n";

		return data;
	}
}
