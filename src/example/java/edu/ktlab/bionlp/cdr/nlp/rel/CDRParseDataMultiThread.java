package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

class ParseDep implements Runnable {
	static SentSplitterMESingleton splitter = SentSplitterMESingleton.getInstance();
	static SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
	static LexicalizedParser parser = LexicalizedParser.getParserFromFile(DefaultPaths.DEFAULT_PARSER_MODEL, new Options());
	
	static final Object lock = new Object();
	
	FileWriter FR;
	FileWriter FRV;
	FileWriter FRU;
	FileWriter FRUV;
	
	BufferedReader br;
	
	private Thread t;
	private String threadName;
	static int tCount = 0;
	
	ParseDep(String name, FileWriter _FR, FileWriter _FRV, FileWriter _FRU, FileWriter _FRUV, BufferedReader _br) {
		threadName = name;
		FR = _FR;
		FRV = _FRV;
		FRU = _FRU;
		FRUV = _FRUV;
		br = _br;
		
		System.out.println("Creating " +  threadName);
		tCount++;
	}
	
	public void run() {
		System.out.println("Running " +  threadName );
		int sentCount = 0;
		int stmCount = 0;

		try {
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
						
						synchronized (lock) {
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
				if (sentCount % 100 == 0) {
					System.out.println(threadName + ": " + sentCount + " " + stmCount);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(threadName + " (final): " + sentCount + " " + stmCount);
		System.out.println("Thread " +  threadName + " exiting.");
		tCount--;
		if (tCount == 0) {
			try {
				br.close();

				FR.close();
				FRV.close();
				FRU.close();
				FRUV.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void start () {
		System.out.println("Starting " +  threadName );
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	private static boolean dropout(IndexedWord token) {
		return false;
	}
}

public class CDRParseDataMultiThread {
	public static void main(String args[]) throws Exception {
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
		
		FileReader fr = new FileReader("/home/catcan/GitProjects/word_embedding_trainer/data/merge.sent");
		BufferedReader br = new BufferedReader(fr);
		
		ParseDep R1 = new ParseDep("Thread-1", FR, FRV, FRU, FRUV, br);
		R1.start();
		  
		ParseDep R2 = new ParseDep("Thread-2", FR, FRV, FRU, FRUV, br);
		R2.start();
		  
		ParseDep R3 = new ParseDep("Thread-3", FR, FRV, FRU, FRUV, br);
		R3.start();
				
		ParseDep R4 = new ParseDep("Thread-4", FR, FRV, FRU, FRUV, br);
		R4.start();
	}
}
