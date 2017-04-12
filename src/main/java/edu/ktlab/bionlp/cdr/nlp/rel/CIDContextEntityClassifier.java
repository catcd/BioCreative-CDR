package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.base.Token;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.ContextGenerator;
import edu.ktlab.bionlp.cdr.nlp.rel.feature.FeatureSet;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class CIDContextEntityClassifier {
	String fileModel = "models/cid_ctx.full.model";
	String fileWordlist = "models/cid_ctx.full.wordlist";

	ContextGenerator<Token, Sentence> contextGenerator;
	Model model;
	FeatureSet featureSet;

	public void init() throws Exception {
		contextGenerator = new ContextGenerator<Token, Sentence>(CIDContextEntityClassifyTrainer.mFeatureGenerators);
	}

	public CIDContextEntityClassifier() throws Exception {
		init();
		loadModel();
		loadWordlist();
	}

	public CIDContextEntityClassifier(String model, String wordlist) throws Exception {
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

	public double classify(Token token, Sentence sentence) throws Exception {
		ArrayList<String> features = new ArrayList<String>();

		ArrayList<String> fts = contextGenerator.getContext(token, sentence);
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
		return featureSet.getLabels().get((int) score);
	}

	public static void main(String[] args) throws Exception {
		CIDContextEntityClassifier classifier = new CIDContextEntityClassifier("models/cid_ctx.full.model",
				"models/cid_ctx.full.wordlist");
		Collection col = CollectionFactory.loadJsonFile("data/cdr/cdr_dev/cdr_dev.gzip");

		for (Document doc : col.getDocuments()) {
			Map<String, String> refsInTitle = doc.getReferencesInTitle();
			Set<Relation> rels = new HashSet<Relation>();
			List<Sentence> sents = doc.getSentences();
			for (Sentence sent : sents) {
				Set<String> chedIds = new HashSet<String>();
				Set<String> disIds = new HashSet<String>();
				for (Annotation ann : sent.getAnnotations()) {
					for (Token token : ann.getTokens()) {
						double score = classifier.classify(token, sent);
						String label = classifier.getLabel(score);
						if (label.equals("CID_CTX")) {
							if (ann.getType().equals("Disease"))
								disIds.add(ann.getReference());
							else if (ann.getType().equals("Chemical"))
								chedIds.add(ann.getReference());
						}
					}
				}

				if (chedIds.size() == 0 && disIds.size() == 0)
					continue;

				if (chedIds.size() == 0)
					for (String disId : disIds)
						for (String id : refsInTitle.keySet())
							if (refsInTitle.get(id).equals("Chemical")) {
								Relation rel = new Relation();
								rel.setId("CID");
								rel.setDiseaseID(disId);
								rel.setChemicalID(id);
								rels.add(rel);
							}

				if (disIds.size() == 0)
					for (String chedId : chedIds)
						for (String id : refsInTitle.keySet())
							if (refsInTitle.get(id).equals("Disease")) {
								Relation rel = new Relation();
								rel.setId("CID");
								rel.setDiseaseID(id);
								rel.setChemicalID(chedId);
								rels.add(rel);
							}

				for (String chedId : chedIds)
					for (String disId : disIds) {
						Relation rel = new Relation();
						rel.setId("CID");
						rel.setDiseaseID(disId);
						rel.setChemicalID(chedId);
						rels.add(rel);
					}
			}
			System.out.println(doc.getPmid() + "\t" + doc.getRelations());
			System.out.println(doc.getPmid() + "\t" + rels);
		}
	}

}
