package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.bwaldvogel.liblinear.Train;
import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.base.Token;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.ContextGenerator;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.CTXDependencyFeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.FeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.FeatureSet;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.CTXTokenFeatureGenerator;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class CIDContextEntityClassifyTrainer {
	//static String fileCorpus = "data/cdr/cdr_train/cdr_train.gzip";
	static String fileCorpus = "data/cdr/cdr_full/cdr_full.gzip";
	static String fileTraining = "models/cid_ctx.full.training";
	static String fileModel = "models/cid_ctx.full.model";
	static String fileWordlist = "models/cid_ctx.full.wordlist";

	@SuppressWarnings("unchecked")
	public static FeatureGenerator<Token, Sentence>[] mFeatureGenerators = new FeatureGenerator[] {
			new CTXDependencyFeatureGenerator(), new CTXTokenFeatureGenerator() };

	static ContextGenerator<Token, Sentence> contextGenerator;
	static FeatureSet featureSet;

	public static void init() throws Exception {
		contextGenerator = new ContextGenerator<Token, Sentence>(mFeatureGenerators);
		featureSet = new FeatureSet();
	}

	private static void createVectorTrainingFile() throws Exception {
		Collection col = CollectionFactory.loadJsonFile(fileCorpus);

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileTraining)));

		for (Document doc : col.getDocuments()) {
			System.out.println(doc.getPmid());
			Set<String> refs = doc.getRelationReferences();
			List<Sentence> sents = doc.getSentences();
			for (Sentence sent : sents) {
				Set<Integer> ids = new HashSet<Integer>();
				for (Annotation ann : sent.getAnnotations()) {
					String[] mentionRefs = StringUtils.split(ann.getReference(), "|");
					for (String ref : mentionRefs)
						if (refs.contains(ref))
							for (Token token : ann.getTokens()) {
								if (!StringUtils.isAlphanumeric(token.getContent()))
									continue;
								
								ArrayList<String> features = new ArrayList<String>();

								ArrayList<String> fts = contextGenerator.getContext(token, sent);
								features.addAll(fts);

								String vector = featureSet.addprintVector(features, "CID_CTX", false);
								if (vector.equals("")) {
									continue;
								}
								writer.append(vector).append("\n");
								ids.add(token.getId());
							}
						else
							for (Token token : ann.getTokens()) {
								if (!StringUtils.isAlphanumeric(token.getContent()))
									continue;
								
								ArrayList<String> features = new ArrayList<String>();

								ArrayList<String> fts = contextGenerator.getContext(token, sent);
								features.addAll(fts);

								String vector = featureSet.addprintVector(features, "NONE", false);
								if (vector.equals("")) {
									continue;
								}
								writer.append(vector).append("\n");
								ids.add(token.getId());
							}
				}
				
			}

		}

		writer.close();

		FileHelper.writeObjectToFile(featureSet, new File(fileWordlist));
	}

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
