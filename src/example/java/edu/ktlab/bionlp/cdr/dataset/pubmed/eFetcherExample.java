package edu.ktlab.bionlp.cdr.dataset.pubmed;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class eFetcherExample {

	public static void main(String[] args) throws Exception {
		Document pmcFetcher = Jsoup
				.connect("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?")
				.data("db", "pubmed").data("id", "23612000").data("retmode", "xml").post();
		System.out.println(pmcFetcher.html());
	}

}
