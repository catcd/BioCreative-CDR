package edu.ktlab.bionlp.cdr.dataset.mesh;

import java.util.Map;

import edu.ktlab.bionlp.cdr.dataset.MESHSearcher;

public class MESHSearchExample {

	public static void main(String[] args) throws Exception {
		MESHSearcher searcher = new MESHSearcher();
		searcher.loadMESH("models/nen/mesh2015.gzip");
		Map<String, String> results = searcher.searchMESHById("D008627", 100);
		System.out.println(results);
	}

}
