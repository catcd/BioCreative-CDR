package edu.ktlab.bionlp.cdr.dataset.ctd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.ktlab.bionlp.cdr.util.FileHelper;

public class MeshExtractorExample {

	static Set<String> meshs = Sets.newHashSet();

	public static void main(String... strings) throws Exception {
		loadCTD("tools/CTD_chemicals_diseases.tsv");
		FileHelper.writeSetToFile(meshs, "data/dis_mesh.txt");
	}

	static void loadCTD(String file) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line;
		while ((line = in.readLine()) != null) {
			String[] fields = line.split("\t");
			if (fields[4].startsWith("MESH"))
				meshs.add(fields[3].toLowerCase() + "\t" + fields[4].replace("MESH:", ""));
		}
		in.close();

	}

}
