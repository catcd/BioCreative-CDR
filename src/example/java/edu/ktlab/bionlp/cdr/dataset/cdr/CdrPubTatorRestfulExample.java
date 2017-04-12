package edu.ktlab.bionlp.cdr.dataset.cdr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CdrPubTatorRestfulExample {

	public static void main(String[] args) throws Exception {
		String id = "26094";
		String url = "http://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/RESTful/tmTool.cgi/BioConcept/"
				+ id + "/PubTator/";
		String data = getText(url);
		System.out.println(data);
	}
	
	public static String getText(String url) throws Exception {
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
