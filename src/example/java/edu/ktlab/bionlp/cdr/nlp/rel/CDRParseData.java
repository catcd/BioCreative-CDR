package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;

import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Passage;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.nlp.splitter.SentSplitterMESingleton;
import edu.ktlab.bionlp.cdr.util.DependencyHelper;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.pipeline.DefaultPaths;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

public class CDRParseData {
	static SentSplitterMESingleton splitter = SentSplitterMESingleton.getInstance();
	static SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
	static LexicalizedParser parser = LexicalizedParser.getParserFromFile(DefaultPaths.DEFAULT_PARSER_MODEL, new Options());
	static HashMap<String, Double> lut = new HashMap<String, Double>();

	public static void main(String[] args) throws Exception {
		prepareLUT();
		
		File []fs = {
		              new File("/home/catcan/Desktop/data/data.directed.tag"),
		              new File("/home/catcan/Desktop/data/data.directed.value"),
		              new File("/home/catcan/Desktop/data/data.undirected.tag"),
		              new File("/home/catcan/Desktop/data/data.undirected.value")
		};
		
		for (File f : fs) {
			if (f.exists()) {
				f.createNewFile();
			}
		}
		
		FileWriter FR = new FileWriter(fs[0]);
		FileWriter FRV = new FileWriter(fs[1]);
		FileWriter FRU = new FileWriter(fs[2]);
		FileWriter FRUV = new FileWriter(fs[3]);
		
		FileReader fr = new FileReader("/home/catcan/GitProjects/word_embedding_trainer/data/pubmed_text_only/efetch_1.txt");
		BufferedReader br = new BufferedReader(fr);

		String anAbs;
		while ((anAbs = br.readLine()) != null) {
			int offset = 0;
			Document doc = new Document();
			Passage passage = new Passage();
			passage.setType("abstract");
			passage.setStartOffset(offset);
			passage.setEndOffset(offset + anAbs.length());
			passage.setContent(anAbs);
			Span[] sentSpans = splitter.senPosSplit(anAbs);
			
			for (Span span : sentSpans) {
				Sentence sent = new Sentence();
				sent.setStartOffset(passage.getStartOffset() + span.getStart());
				sent.setEndOffset(passage.getStartOffset() + span.getEnd());
				String content = anAbs.substring(span.getStart(), span.getEnd());
				sent.setContent(content);
				String[] tokens = tokenizer.tokenize(content);
				
				Tree tree = parser.parseStrings(DependencyHelper.transform(tokens));
				sent.setDeptree(tree.toString());
	
				sent.setTokens(tokens);
				passage.addSentence(sent);
			}
			doc.addPassage(passage);
			
			for (Sentence sentence : doc.getSentences()) {
				SemanticGraph semgraph = DependencyHelper.convertSemanticGraph(sentence.getDeptree());
				
				List<IndexedWord> edgeList = semgraph.vertexListSorted();
				int edgeCount = edgeList.size();
				
				for (int i = 0; i < edgeCount - 1; ++i) {
					IndexedWord token1 = edgeList.get(i);
					if (dropout(token1)) continue;
					
					for (int j = i + 1; j < edgeCount; ++j) {
						IndexedWord token2 = edgeList.get(j);
						if (dropout(token2)) continue;
						
						List<SemanticGraphEdge> shortestPathEdges = semgraph.getShortestUndirectedPathEdges(token1, token2);
						if (shortestPathEdges.size() <= 2) continue;
		
						String r = "";
						String rR = "";
						String rV = "";
						String rRV = "";
						String rUndirected = "";
						String rUndirectedR = "";
						String rUndirectedV = "";
						String rUndirectedRV = "";
						IndexedWord next = token1;
						for(SemanticGraphEdge edge : shortestPathEdges) {
							r += next + " ";
							rR = " " + next + rR;
							rV += next.value() + " ";
							rRV = " " + next.value() + rRV;
							rUndirected += next + " ";
							rUndirectedR = " " + next + rUndirectedR;
							rUndirectedV += next.value() + " ";
							rUndirectedRV = " " + next.value() + rUndirectedRV;
							if (edge.getSource().equals(next)) {
								r += "-ARR- (" + edge.getRelation() + ") ";
								rR = " -ARL- (" + edge.getRelation() + ")" + rR;
								rV += "-ARR- (" + edge.getRelation() + ") ";
								rRV = " -ARL- (" + edge.getRelation() + ")" + rRV;
								rUndirected += "(" + edge.getRelation() + ") ";
								rUndirectedR = " (" + edge.getRelation() + ")" + rUndirectedR;
								rUndirectedV += "(" + edge.getRelation() + ") ";
								rUndirectedRV = " (" + edge.getRelation() + ")" + rUndirectedRV;
								next = edge.getTarget();
							} else {
								r += "-ARL- (" + edge.getRelation() + ") ";
								rR = " -ARR- (" + edge.getRelation() + ")" + rR;
								rV += "-ARL- (" + edge.getRelation() + ") ";
								rRV = " -ARR- (" + edge.getRelation() + ")" + rRV;
								rUndirected += "(" + edge.getRelation() + ") ";
								rUndirectedR = " (" + edge.getRelation() + ")" + rUndirectedR;
								rUndirectedV += "(" + edge.getRelation() + ") ";
								rUndirectedRV = " (" + edge.getRelation() + ")" + rUndirectedRV;
								next = edge.getSource();
							}
						}
						r += next;
						rR = next + rR;
						rV += next.value();
						rRV = next.value() + rRV;
						rUndirected += next;
						rUndirectedR = next + rUndirectedR;
						rUndirectedV += next.value();
						rUndirectedRV = next.value() + rUndirectedRV;
						
						FR.write(r + '\n');
						FR.write(rR + '\n');
						FRV.write(rV + '\n');
						FRV.write(rRV + '\n');
						FRU.write(rUndirected + '\n');
						FRU.write(rUndirectedR + '\n');
						FRUV.write(rUndirectedV + '\n');
						FRUV.write(rUndirectedRV + '\n');
					}
				}
			}
		}

		FR.close();
		FRV.close();
		FRU.close();
		FRUV.close();
		br.close();
	}

	private static boolean dropout(IndexedWord token) {
		Double ran = 0.5;
		Double lutV = lut.get(token.tag());
		if (lutV == null) {
			return true;
		} else {
			return lutV < ran;
		}
	}

	private static void prepareLUT() {
		lut.put("CC", 0.0);
		lut.put("CD", 1.0);
		lut.put("DT", 0.0);
		lut.put("EX", 0.0);
		lut.put("FW", 0.0);
		lut.put("IN", 0.0);
		lut.put("JJ", 1.0);
		lut.put("JJR", 0.0);
		lut.put("JJS", 0.0);
		lut.put("LS", 0.0);
		lut.put("MD", 0.0);
		lut.put("NN", 1.0);
		lut.put("NNP", 1.0);
		lut.put("NNPS", 0.0);
		lut.put("NNS", 1.0);
		lut.put("PDT", 0.0);
		lut.put("POS", 0.0);
		lut.put("PRP", 0.0);
		lut.put("PRP$", 0.0);
		lut.put("RB", 0.0);
		lut.put("RBR", 0.0);
		lut.put("RBS", 0.0);
		lut.put("RP", 0.0);
		lut.put("SYM", 0.0);
		lut.put("TO", 0.0);
		lut.put("UH", 0.0);
		lut.put("VB", 0.0);
		lut.put("VBD", 0.0);
		lut.put("VBG", 0.0);
		lut.put("VBN", 0.0);
		lut.put("VBP", 0.0);
		lut.put("VBZ", 0.0);
		lut.put("WDT", 0.0);
		lut.put("WP", 0.0);
		lut.put("WP$", 0.0);
		lut.put("WRB", 0.0);
	}
}
