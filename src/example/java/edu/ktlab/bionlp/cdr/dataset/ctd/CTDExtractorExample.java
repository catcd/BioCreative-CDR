package edu.ktlab.bionlp.cdr.dataset.ctd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.ktlab.bionlp.cdr.io.WPMultiJSONWriter;
import edu.ktlab.bionlp.cdr.io.Webpage;

public class CTDExtractorExample {
	static WPMultiJSONWriter writer;

	public static void main(String[] args) throws Exception {
		String file = "tools/CTD_chemicals_diseases.tsv";
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line;
		Set<String> pmids = new HashSet<String>();
		while ((line = in.readLine()) != null) {
			String[] fields = line.split("\t");
			String pmid = "";
			if (fields.length == 10) {
				pmid = fields[9];
				if (!pmid.equals("")) {
					String[] pm = pmid.split("\\|");
					for (String p : pm)
						pmids.add(p);
				}

			}

		}

		writer = new WPMultiJSONWriter("data/ctd_pubmed/");
		writer.setCompress(true);
		writer.setMaxDocumentPerFile(10);

		int runID = 0;
		String ids = "";
		for (String pmid : pmids) {
			if (runID == 30) {
				System.out.println("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&retmode=xml&id=" + ids);
				// writer.write(fetch(ids));
				ids = "";
				runID = 0;
			}
			ids += pmid + ",";
			runID++;
		}
		System.out.println("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&retmode=xml&id=" + ids);
		// writer.write(fetch(ids));
		writer.close();

		in.close();
	}

	static Webpage fetch(String ids) {
		Document doc;
		try {
			doc = Jsoup.connect("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?")
					.data("db", "pubmed").data("id", ids).data("retmode", "xml")
					.userAgent("Mozilla").cookie("auth", "token").timeout(300000).post();
			return new Webpage(ids, "ctd", doc.html());

		} catch (Exception e) {
			return new Webpage(ids, "ctd", "");
		}
	}

}
