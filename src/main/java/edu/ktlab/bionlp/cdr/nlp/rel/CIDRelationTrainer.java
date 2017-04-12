package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jersey.repackaged.com.google.common.collect.Lists;
import de.bwaldvogel.liblinear.Train;
import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.CIDDependencyFeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.ContextGenerator;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.FeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.FeatureSet;
import edu.ktlab.bionlp.cdr.util.FileHelper;
import edu.stanford.nlp.util.Pair;

public class CIDRelationTrainer {
	@SuppressWarnings("unchecked")
	public static FeatureGenerator<Pair<Annotation, Annotation>, Sentence>[] mFeatureGenerators = new FeatureGenerator[] { new CIDDependencyFeatureGenerator() };

	static ContextGenerator<Pair<Annotation, Annotation>, Sentence> contextGenerator;
	static FeatureSet featureSet;

	public static void init() throws Exception {
		contextGenerator = new ContextGenerator<Pair<Annotation, Annotation>, Sentence>(mFeatureGenerators);
		featureSet = new FeatureSet();
	}

	private static void createVectorTrainingFile() throws Exception {
		Collection col = CollectionFactory.loadJsonFile(fileCorpus);

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileTraining)));

		for (Document doc : col.getDocuments()) {
			System.out.println(doc.getPmid());
			Set<Relation> candidateRels = doc.getRelationCandidates();
			List<Sentence> sents = doc.getSentences();
			for (Sentence sent : sents) {
				for (Relation candidateRel : candidateRels) {
					if (sent.containRelation(candidateRel)) {
						String label = "CID";
						if (!doc.getRelations().contains(candidateRel))
							label = "NONE";
						List<Annotation> annsChed = sent.getAnnotation(candidateRel.getChemicalID());
						List<Annotation> annsDis = sent.getAnnotation(candidateRel.getDiseaseID());

						List<Pair<Annotation, Annotation>> pairs = Lists.newArrayList();
						for (Annotation annChed : annsChed)
							for (Annotation annDis : annsDis) {
								pairs.add(new Pair<Annotation, Annotation>(annChed, annDis));
							}

						for (Pair<Annotation, Annotation> pair : pairs) {
							ArrayList<String> features = new ArrayList<String>();

							ArrayList<String> fts = contextGenerator.getContext(pair, sent);
							features.addAll(fts);

							String vector = featureSet.addprintVector(features, label, false);
							if (vector.equals("")) {
								continue;
							}
							writer.append(vector).append("\n");
						}
					}
				}
			}
		}

		writer.close();

		FileHelper.writeObjectToFile(featureSet, new File(fileWordlist));
	}
	
	static String fileCorpus = "data/cdr/cdr_full/cdr_full.gzip";
	static String fileTraining = "models/cdr.full.training";
	static String fileModel = "models/cid.full.model";
	static String fileWordlist = "models/cid.full.wordlist";
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		long current = System.currentTimeMillis();
		init();
		createVectorTrainingFile();
		// Training
		new Train().main(new String[] { "-s", "3", fileTraining, fileModel});

		// Cross-validation
		new Train().main(new String[] { "-v", "10", "-s", "3", "-c", "1", fileTraining, fileModel });

		System.out.println("Processing time: " + (System.currentTimeMillis() - current) + " ms");

	}

}
