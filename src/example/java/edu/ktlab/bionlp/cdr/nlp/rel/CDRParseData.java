package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.BufferedReader;
import java.io.FileReader;
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

	public static void main(String[] args) throws Exception {
		FileReader fr = new FileReader("/home/catcan/GitProjects/word_embedding_trainer/data/pubmed_text_only/efetch_1000.txt");
		BufferedReader br = new BufferedReader(fr);
		String anAbs = br.readLine();
		System.out.println(anAbs);

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
		
		Sentence sentence = doc.getSentences().get(0);
		
		System.out.println(sentence);
		
		SemanticGraph semgraph = DependencyHelper.convertSemanticGraph(sentence.getDeptree());
		
		List<IndexedWord> edgeList = semgraph.vertexListSorted();
		int edgeCount = edgeList.size();
		
		for (int i = 0; i < edgeCount - 1; ++i) {
			for (int j = i + 1; j < edgeCount; ++j) {
				IndexedWord token1 = edgeList.get(i),
							token2 = edgeList.get(j);
				
				List<SemanticGraphEdge> shortestPathEdges = semgraph.getShortestUndirectedPathEdges(token1, token2);
			}
		}		
		br.close();
	}
}
