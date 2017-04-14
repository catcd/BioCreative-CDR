package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

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

public class CDRParseData2 {
	static SentSplitterMESingleton splitter = SentSplitterMESingleton.getInstance();
	static SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
	static LexicalizedParser parser = LexicalizedParser.getParserFromFile(DefaultPaths.DEFAULT_PARSER_MODEL, new Options());

	public static void main(String[] args) throws Exception {
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
		int sentCount = 0;
		int stmCount = 0;
		
		FileReader fr = new FileReader("/home/catcan/GitProjects/word_embedding_trainer/data/merge.sent");
		BufferedReader br = new BufferedReader(fr);

		String s;
		while ((s = br.readLine()) != null) {
			++sentCount;
			
			Sentence sent = new Sentence();
			sent.setStartOffset(0);
			sent.setEndOffset(s.length() - 1);
			sent.setContent(s);
			String[] tokens = tokenizer.tokenize(s);
			
			Tree tree = parser.parseStrings(DependencyHelper.transform(tokens));
			sent.setDeptree(tree.toString());

			sent.setTokens(tokens);
			
			SemanticGraph semgraph = DependencyHelper.convertSemanticGraph(sent.getDeptree());
			if (semgraph == null) continue;

			List<IndexedWord> edgeList = semgraph.vertexListSorted();
			int edgeCount = edgeList.size();
			
			for (int i = 0; i < edgeCount - 1; ++i) {
				IndexedWord token1 = edgeList.get(i);
				if (dropout(token1)) continue;
				
				for (int j = i + 1; j < edgeCount; ++j) {
					IndexedWord token2 = edgeList.get(j);
					if (dropout(token2)) continue;
					
					List<SemanticGraphEdge> shortestPathEdges = semgraph.getShortestUndirectedPathEdges(token1, token2);
					if (shortestPathEdges == null) continue;
					++stmCount;
	
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
			if (sentCount % 100 == 0) {
				System.out.println(sentCount + " " + stmCount);
			}
		}
		
		br.close();

		FR.close();
		FRV.close();
		FRU.close();
		FRUV.close();
	}

	private static boolean dropout(IndexedWord token) {
		return false;
	}
}
