package edu.ktlab.bionlp.cdr.dataset.bioc;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.ListUtils;

import edu.ktlab.bionlp.cdr.util.FileHelper;

public class IntersectDataExample {
	public static void main(String[] args) {
		String[] listStr1 = FileHelper.readFileAsLines("data/cdr/cdr_pmid.txt");
		String[] listStr2 = FileHelper.readFileAsLines("data/cdr/ncbi_pmid.txt");
		List<String> list1 = Arrays.asList(listStr1);
		List<String> list2 = Arrays.asList(listStr2);
		List<?> intersect = ListUtils.intersection(list1, list2);
		for (Object item : intersect) {
			System.out.println((String) item);
		}
	}
}
