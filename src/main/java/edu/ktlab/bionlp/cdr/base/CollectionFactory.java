package edu.ktlab.bionlp.cdr.base;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import edu.ktlab.bionlp.cdr.nlp.splitter.SentSplitterMESingleton;
import edu.ktlab.bionlp.cdr.util.DependencyHelper;
import edu.ktlab.bionlp.cdr.util.FileHelper;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.pipeline.DefaultPaths;
import edu.stanford.nlp.trees.Tree;

public class CollectionFactory {
	static Gson gson = new Gson();
	static SentSplitterMESingleton splitter = SentSplitterMESingleton.getInstance();
	static SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
	static LexicalizedParser parser = null;
	boolean isParser = false;
	
	public CollectionFactory(boolean isParser) {
		this.isParser = isParser;
		if (isParser)
			parser = LexicalizedParser.getParserFromFile(DefaultPaths.DEFAULT_PARSER_MODEL, new Options());
	}
	
	public static Collection loadJsonFile(String file) throws Exception {
		JsonReader reader;
		if (file.endsWith(".gzip"))
			reader = new JsonReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
		else
			reader = new JsonReader(new InputStreamReader(new FileInputStream(file)));
		return gson.fromJson(reader, Collection.class);
	}

	public static void saveJsonFile(String file, Collection col) throws Exception {
		JsonWriter writer;

		if (file.endsWith(".gzip"))
			writer = new JsonWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file))));
		else
			writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file)));

		writer.setIndent("  ");
		gson.toJson(col, Collection.class, writer);
		writer.close();
	}

	public Collection loadFile(String file) {
		Collection collection = new Collection();
		String[] lines = FileHelper.readFileAsLines(file);
		Document doc = new Document();
		int offset = 0;
		for (String line : lines) {
			if (line.length() == 0) {
				collection.addDocument(doc);
				doc = new Document();
				offset = 0;
			} else {
				String[] strs = line.split("\\|");
				if (strs.length == 3 && strs[1].matches("t|a")) {
					doc.setPmid(strs[0]);
					Passage passage = new Passage();
					if (strs[1].equals("t")) {
						passage.setType("title");
					} else {
						passage.setType("abstract");
					}
					passage.setStartOffset(offset);
					passage.setEndOffset(offset + strs[2].length());
					passage.setContent(strs[2]);
					Span[] sentSpans = splitter.senPosSplit(strs[2]);
					for (Span span : sentSpans) {
						Sentence sent = new Sentence();
						sent.setStartOffset(passage.getStartOffset() + span.getStart());
						sent.setEndOffset(passage.getStartOffset() + span.getEnd());
						String content = strs[2].substring(span.getStart(), span.getEnd());
						sent.setContent(content);
						String[] tokens = tokenizer.tokenize(content);
						if (isParser) {
							Tree tree = parser.parseStrings(DependencyHelper.transform(tokens));
							sent.setDeptree(tree.toString());
						}

						sent.setTokens(tokens);
						passage.addSentence(sent);
					}
					doc.addPassage(passage);
					offset = strs[2].length() + 1;
				} else {
					strs = line.split("\t");
					if (strs.length > 3) {
						if (strs[1].equals("CID")) {
							Relation rel = new Relation();
							rel.setId("CID");
							rel.setChemicalID(strs[2]);
							rel.setDiseaseID(strs[3]);
							doc.addRelation(rel);
						} else {
							Annotation ann = new Annotation();
							ann.setStartBaseOffset(Integer.parseInt(strs[1]));
							ann.setEndBaseOffset(Integer.parseInt(strs[2]));
							ann.setContent(strs[3]);
							ann.setType(strs[4]);
							ann.setReference(strs[5]);
							doc.addAnnotation(ann);
						}
					}
				}
			}
		}
		return collection;
	}

	public Document loadDocumentFromString(String text) {
		Document doc = new Document();
		int offset = 0;
		String[] lines = text.split("\n");

		for (String line : lines) {
			String[] strs = line.split("\\|");
			if (strs.length == 3 && strs[1].matches("t|a")) {
				doc.setPmid(strs[0]);
				Passage passage = new Passage();
				if (strs[1].equals("t")) {
					passage.setType("title");
				} else {
					passage.setType("abstract");
				}
				passage.setStartOffset(offset);
				passage.setEndOffset(offset + strs[2].length());
				passage.setContent(strs[2]);
				Span[] sentSpans = splitter.senPosSplit(strs[2]);
				for (Span span : sentSpans) {
					Sentence sent = new Sentence();
					sent.setStartOffset(passage.getStartOffset() + span.getStart());
					sent.setEndOffset(passage.getStartOffset() + span.getEnd());
					String content = strs[2].substring(span.getStart(), span.getEnd());
					sent.setContent(content);
					String[] tokens = tokenizer.tokenize(content);
					if (isParser) {
						Tree tree = parser.parseStrings(DependencyHelper.transform(tokens));
						sent.setDeptree(tree.toString());
					}

					sent.setTokens(tokens);
					passage.addSentence(sent);
				}
				doc.addPassage(passage);
				offset = strs[2].length() + 1;
			} else {
				strs = line.split("\t");
				if (strs.length > 3) {
					if (strs[1].equals("CID")) {
						Relation rel = new Relation();
						rel.setId("CID");
						rel.setChemicalID(strs[2]);
						rel.setDiseaseID(strs[3]);
						doc.addRelation(rel);
					} else {
						Annotation ann = new Annotation();
						ann.setStartBaseOffset(Integer.parseInt(strs[1]));
						ann.setEndBaseOffset(Integer.parseInt(strs[2]));
						ann.setContent(strs[3]);
						ann.setType(strs[4]);
						ann.setReference(strs[5]);
						doc.addAnnotation(ann);
					}
				}
			}
		}

		return doc;
	}
}
