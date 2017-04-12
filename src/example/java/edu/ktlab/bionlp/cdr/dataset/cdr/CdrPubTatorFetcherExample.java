package edu.ktlab.bionlp.cdr.dataset.cdr;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import edu.ktlab.bionlp.cdr.util.FileHelper;

public class CdrPubTatorFetcherExample {
	static String filePMID = "data/cdr/cdr_pmid.txt";
	static String outFile = "data/cdr/cdr_pubtator.xml";
	
	public static void main(String[] args) throws Exception {
		String[] pmids = FileHelper.readFileAsLines(filePMID);
		for (String pmid : pmids) {
			String content = fetch(pmid);
			FileHelper.appendToFile(content, new File(outFile), Charset.forName("UTF-8"));
		}
	}
	
	public static String fetch(String pmid) throws Exception {
		String url = "http://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/RESTful/tmTool.cgi/BioConcept/" + pmid + "/BioC/";
		String doc = download(url);		
		return doc;
	}
	
	public static String download(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine + "\n");

        in.close();

        return response.toString();
    }
}
