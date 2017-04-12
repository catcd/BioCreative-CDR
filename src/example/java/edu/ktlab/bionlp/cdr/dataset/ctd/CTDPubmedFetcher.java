package edu.ktlab.bionlp.cdr.dataset.ctd;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class CTDPubmedFetcher {
	public static void main(String[] args) throws Exception {
		/*String[] lines = FileHelper.readFileAsLines("data/ctd_relations_m_pmid.txt");
		Map<String, List<String>> ctdMap = Maps.newHashMap();
		for (String line : lines) {
			String[] fi = line.split("\t");
			String[] ids = fi[1].split("\\|");
			ctdMap.put(fi[0], Arrays.asList(ids));
		}

		Set<String> idSet = Sets.newHashSet();
		for (String rel : ctdMap.keySet()) {
			for (String id : ctdMap.get(rel)) {
				idSet.add(id);
			}
		}

		for (String id : idSet) {
			String url = "http://ctdbase.org/detail.go?6578706f7274=1&d-1332398-e=3&view=disease&type=reference&acc="
					+ id;
			Document doc = Jsoup.connect(url).timeout(30000).get();
			FileHelper.writeToFile(doc.html(), new File("data/ctd_pubmed2/" + id + ".html"), Charset.forName("UTF-8"));
		}
		*/
		CollectionFactory factory = new CollectionFactory(false);
		Collection col = factory.loadFile("data/cdr/cdr_full/cdr_full.txt");

		for (edu.ktlab.bionlp.cdr.base.Document doc : col.getDocuments()) {
			String url = "http://ctdbase.org/detail.go?6578706f7274=1&d-1332398-e=3&view=disease&type=reference&acc="
					+ doc.getPmid();
			Document docHtml = Jsoup.connect(url).timeout(30000).get();
			FileHelper.writeToFile(docHtml.html(), new File("data/ctd_pubmed3/" + doc.getPmid() + ".html"), Charset.forName("UTF-8"));
		}
	}

}
