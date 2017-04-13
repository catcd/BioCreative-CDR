package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
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
import edu.stanford.nlp.trees.Tree;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

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
		
		FileReader fr = new FileReader("/home/catcan/GitProjects/word_embedding_trainer/data/pubmed_text_only/efetch_1.txt");
		BufferedReader br = new BufferedReader(fr);
		
		HashSet<String> tags = new HashSet<String>();

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
				
				for (IndexedWord w : edgeList) {
					tags.add(w.tag());
				}
			}
		}

		System.out.println(tags);
		System.out.println(tags.size());

		FR.close();
		FRV.close();
		FRU.close();
		FRUV.close();
		br.close();
	}
}
