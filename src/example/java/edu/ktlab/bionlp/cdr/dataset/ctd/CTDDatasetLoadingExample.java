package edu.ktlab.bionlp.cdr.dataset.ctd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.ktlab.bionlp.cdr.util.FileHelper;

public class CTDDatasetLoadingExample {
	static Set<String> relations = Sets.newHashSet();

	public static void main(String...strings) throws Exception {
		loadCTD("tools/CTD_chemicals_diseases.tsv");
		FileHelper.writeSetToFile(relations, "data/ctd_relations_m_pmid.txt");
	}
	
	static void loadCTD(String file) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line;
		while ((line = in.readLine()) != null) {
			String[] fields = line.split("\t");
			if (fields[4].startsWith("MESH") && fields[5].contains("marker/mechanism")) {
				String pmid = "";
				if (fields.length == 10)
					pmid = fields[9];
				relations.add(fields[1] + " " + fields[4].replace("MESH:", "") + "\t" + pmid);
			}				
		}			
		in.close();

	}
}
