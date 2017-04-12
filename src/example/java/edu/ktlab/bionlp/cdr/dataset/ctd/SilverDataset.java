package edu.ktlab.bionlp.cdr.dataset.ctd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import edu.ktlab.bionlp.cdr.base.RawDocument;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.util.FileHelper;
import edu.ktlab.bionlp.cdr.util.FileUtil;

public class SilverDataset {
	private Map<Integer, RawDocument> docs = new HashMap<Integer, RawDocument>();

	public SilverDataset() {
	}

	public void generateDataset(String fileCTDRelation, String folderPubmed) throws Exception {
		Map<String, Set<Relation>> mapRelations = parseRelations(fileCTDRelation);

		String[] files = FileUtil.findFiles(folderPubmed, ".*\\.xml");
		for (String file : files)
			parseXML(file, mapRelations);
	}

	private Map<String, Set<Relation>> parseRelations(String file) {
		Map<String, Set<Relation>> mapRelations = Maps.newHashMap();
		String[] lines = FileHelper.readFileAsLines(file);

		for (String line : lines) {
			String[] segs = line.split("\t");
			Relation relation = new Relation("CID", segs[0].split(" ")[0], segs[0].split(" ")[1]);
			String[] pmids = segs[1].split("\\|");
			for (String pmid : pmids) {
				Set<Relation> rels = mapRelations.get(pmid);
				if (rels == null) {
					rels = new HashSet<Relation>();
					rels.add(relation);
					mapRelations.put(pmid, rels);
				} else {
					rels.add(relation);
				}

			}

		}
		return mapRelations;
	}

	private void parseXML(String file, Map<String, Set<Relation>> mapRelations) throws Exception {
		InputStream is = new FileInputStream(new File(file));
		org.jsoup.nodes.Document xml = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
		Elements elemArticles = xml.select("PubmedArticle");
		for (Element elem : elemArticles) {
			RawDocument doc = new RawDocument();
			Elements pmid = elem.select("PMID");
			Elements abstracts = elem.select("AbstractText");
			Elements titles = elem.select("ArticleTitle");

			doc.setPmid(pmid.get(0).text());
			if (titles.size() != 0)
				doc.setTitle(titles.get(0).text());
			else
				continue;
			if (abstracts.size() != 0)
				doc.setSummary(abstracts.get(0).text().replaceAll("\\(ABSTRACT TRUNCATED AT \\d+ WORDS\\)", ""));
			doc.setRelations(mapRelations.get(doc.getPmid()));
			docs.put(doc.getTitle().hashCode(), doc);
		}
	}

	public Map<Integer, RawDocument> getDocs() {
		return docs;
	}

	public void setDocs(Map<Integer, RawDocument> docs) {
		this.docs = docs;
	}

	public void saveJsonFile(String file) throws Exception {
		Gson gson = new Gson();
		JsonWriter writer;

		if (file.endsWith(".gzip"))
			writer = new JsonWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file))));
		else
			writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file)));

		writer.setIndent("  ");
		gson.toJson(this, SilverDataset.class, writer);
		writer.close();
	}

	public void loadJsonFile(String file) throws Exception {
		Gson gson = new Gson();
		JsonReader reader;
		if (file.endsWith(".gzip"))
			reader = new JsonReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
		else
			reader = new JsonReader(new InputStreamReader(new FileInputStream(file)));
		SilverDataset ds = gson.fromJson(reader, SilverDataset.class);
		this.setDocs(ds.docs);
	}

	public static void main(String[] args) throws Exception {
		SilverDataset silver = new SilverDataset();
		silver.generateDataset("data/ctd_relations_m_pmid.txt", "data/ctd_pubmed");
		silver.saveJsonFile("models/silver.gzip");
		silver.loadJsonFile("models/silver.gzip");
		System.out.println(silver.getDocs().size());
	}

}
