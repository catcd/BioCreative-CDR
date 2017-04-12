package edu.ktlab.bionlp.cdr.dataset;

import java.util.Map;

import jersey.repackaged.com.google.common.collect.Maps;
import opennlp.tools.tokenize.SimpleTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import edu.ktlab.bionlp.cdr.util.FileHelper;

public class MESHSearcher {
	private Directory ramDirectory;

	public MESHSearcher() {
		ramDirectory = new RAMDirectory();
	}

	public void loadMESH(String file) throws Exception {
		Analyzer analyzer1 = new StandardAnalyzer(CharArraySet.EMPTY_SET);
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer1);

		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter indexWriter = new IndexWriter(ramDirectory, iwc);

		String[] lines = FileHelper.readFileAsLines(file);

		for (String line : lines) {
			String[] fields = StringUtils.split(line, "\t");
			Document doc = new Document();
			doc.add(new TextField("id", fields[0], Field.Store.YES));

			String[] tokens = SimpleTokenizer.INSTANCE.tokenize(fields[1]);
			doc.add(new TextField("name", StringUtils.join(tokens, " "), Field.Store.YES));

			indexWriter.addDocument(doc);
		}
		indexWriter.close();
	}

	public Map<String, String> searchMESHByName(String query, int size) throws Exception {
		Map<String, String> results = Maps.newHashMap();

		IndexReader reader = DirectoryReader.open(ramDirectory);
		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(new DefaultSimilarity());
		Analyzer analyzer2 = new StandardAnalyzer(CharArraySet.EMPTY_SET);
		QueryParser parser = new QueryParser("name", analyzer2);
		Query q = parser.parse(query);
		ScoreDoc[] hits = searcher.search(q, size).scoreDocs;
		for (ScoreDoc hit : hits) {
			Document doc = searcher.doc(hit.doc);
			results.put(doc.get("name"), doc.get("id"));
		}
		reader.close();

		return results;
	}

	public Map<String, String> searchMESHById(String id, int size) throws Exception {
		Map<String, String> results = Maps.newHashMap();

		IndexReader reader = DirectoryReader.open(ramDirectory);
		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(new DefaultSimilarity());
		Analyzer analyzer2 = new StandardAnalyzer(CharArraySet.EMPTY_SET);
		QueryParser parser = new QueryParser("id", analyzer2);
		Query q = parser.parse(id);
		ScoreDoc[] hits = searcher.search(q, size).scoreDocs;
		System.out.println(hits.length);
		for (ScoreDoc hit : hits) {
			Document doc = searcher.doc(hit.doc);
			results.put(doc.get("name"), doc.get("id"));
		}
		reader.close();

		return results;
	}
}
