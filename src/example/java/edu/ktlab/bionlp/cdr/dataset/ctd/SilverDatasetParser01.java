package edu.ktlab.bionlp.cdr.dataset.ctd;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import jersey.repackaged.com.google.common.collect.Maps;
import jersey.repackaged.com.google.common.collect.Sets;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import edu.ktlab.bionlp.cdr.base.RawDocument;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.base.TextSpan;
import edu.ktlab.bionlp.cdr.nlp.matcher.LongestMatching;
import edu.ktlab.bionlp.cdr.nlp.splitter.SentSplitterMESingleton;
import edu.ktlab.bionlp.cdr.util.FileHelper;
import edu.stanford.nlp.util.StringUtils;

public class SilverDatasetParser01 {

	public static void main(String[] args) throws Exception {
		String[] lines = FileHelper.readFileAsLines(new File("data/mesh2015.txt"), Charset.forName("UTF-8"));
		Map<String, String> meshs = Maps.newHashMap();
		for (String line : lines) {
			String[] segs = line.split("\t");
			String concept = StringUtils.join(SimpleTokenizer.INSTANCE.tokenize(segs[1].toLowerCase()));
			// String concept = segs[1].toLowerCase();
			meshs.put(concept, segs[0]);
		}
		File output = new File("temp/silver.0.1b.txt");
		if (output.exists())
			output.delete();

		SilverDataset silver = new SilverDataset();
		silver.loadJsonFile("models/silver.gzip");
		for (RawDocument doc : silver.getDocs().values()) {
			if (doc.getRelations().size() == 0)
				continue;

			Map<String, String> dictionary = Maps.newHashMap();
			Set<String> chedids = Sets.newHashSet();
			Set<String> disids = Sets.newHashSet();

			for (Relation relation : doc.getRelations()) {
				chedids.add(relation.getChemicalID());
				disids.add(relation.getDiseaseID());
			}

			for (String key : meshs.keySet()) {
				if (chedids.contains(meshs.get(key)))
					dictionary.put(key, "Chemical:" + meshs.get(key));
				if (disids.contains(meshs.get(key)))
					dictionary.put(key, "Disease:" + meshs.get(key));
			}

			LongestMatching matcher = new LongestMatching(dictionary);
			String content = doc.getTitle();
			if (doc.getSummary() != null)
				content += "\n" + doc.getSummary();
			String[] sents = SentSplitterMESingleton.getInstance().split(content);

			for (String sent : sents) {
				String[] tokens = SimpleTokenizer.INSTANCE.tokenize(sent);
				// String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(sent);
				TextSpan[] spans = matcher.tagging(tokens, -1, false);
				String annotated = TextSpan.getStringAnnotated(spans, tokens);
				Set<Relation> rels = Sets.newHashSet();

				for (TextSpan span1 : spans) {
					String first = span1.getType().replace("Disease:", "").replace("Chemical:", "");
					for (TextSpan span2 : spans) {
						String second = span2.getType().replace("Disease:", "").replace("Chemical:", "");
						Relation rel = new Relation("CID", first, second);
						if (doc.getRelations().contains(rel))
							rels.add(rel);
					}
				}
				if (rels.size() != 0)
					FileHelper.appendToFile(doc.getPmid() + "\t" + rels + "\t" + annotated + "\n", output,
							Charset.forName("UTF-8"));
			}
		}
	}

}
