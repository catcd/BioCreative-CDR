package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import jersey.repackaged.com.google.common.collect.Lists;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.ContextGenerator;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.FeatureSet;
import edu.ktlab.bionlp.cdr.util.FileHelper;
import edu.stanford.nlp.util.Pair;

public class CIDRelationClassifier {
	String fileModel = "models/cid.full.model";
	String fileWordlist = "models/cid.full.wordlist";

	ContextGenerator<Pair<Annotation, Annotation>, Sentence> contextGenerator;
	Model model;
	FeatureSet featureSet;

	public void init() throws Exception {
		contextGenerator = new ContextGenerator<Pair<Annotation, Annotation>, Sentence>(
				CIDRelationTrainer.mFeatureGenerators);
	}

	public CIDRelationClassifier() throws Exception {
		init();
		loadModel();
		loadWordlist();
	}

	public CIDRelationClassifier(String model, String wordlist) throws Exception {
		this.fileModel = model;
		this.fileWordlist = wordlist;
		init();
		loadModel();
		loadWordlist();
	}

	void loadWordlist() throws Exception {
		featureSet = (FeatureSet) FileHelper.readObjectFromFile(new File(fileWordlist));
	}

	void loadModel() throws Exception {
		model = Linear.loadModel(new File(fileModel));
	}

	public double classify(Pair<Annotation, Annotation> relation, Sentence sentence) throws Exception {
		ArrayList<String> features = new ArrayList<String>();

		ArrayList<String> fts = contextGenerator.getContext(relation, sentence);
		features.addAll(fts);

		TreeMap<Integer, Integer> vector = featureSet.addStringFeatureVector(features, "", true);
		ArrayList<FeatureNode> vfeatures = new ArrayList<FeatureNode>();

		if (vector == null)
			return -1;
		for (int key : vector.keySet()) {
			if (key == featureSet.getLabelKey())
				continue;
			FeatureNode featurenode = new FeatureNode(key, vector.get(key));
			vfeatures.add(featurenode);
		}

		double output = Linear.predict(model, vfeatures.toArray(new FeatureNode[vfeatures.size()]));
		return output;
	}	

	public FeatureSet getFeatureSet() {
		return featureSet;
	}

	public void setFeatureSet(FeatureSet featureSet) {
		this.featureSet = featureSet;
	}

	public String getLabel(double score) {
		try {
			return featureSet.getLabels().get((int) score);	
		} catch (Exception e) {
			return "NONE";
		}
	}

	public static void main(String[] args) throws Exception {
		CIDRelationClassifier classifier = new CIDRelationClassifier("models/cid.full.model", "models/cid.full.wordlist");
		Collection col = CollectionFactory.loadJsonFile("data/cdr/cdr_dev/cdr_dev.gzip");
		
		for (Document doc : col.getDocuments()) {
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
							double score = classifier.classify(pair, sent);
							String predict = classifier.getLabel(score);
							System.out.println(label + "\t" + label.equals(predict));
						}
					}
				}
			}
		}

	}

}
