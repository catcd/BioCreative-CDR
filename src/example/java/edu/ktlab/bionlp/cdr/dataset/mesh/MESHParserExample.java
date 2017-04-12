package edu.ktlab.bionlp.cdr.dataset.mesh;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ktlab.bionlp.cdr.util.FileHelper;

public class MESHParserExample {
	static String file = "data/c2015.bin";

	public static void main(String[] args) throws Exception {
		String[] lines = FileHelper.readFileAsLines(file);
		Map<String, List<String>> entries = new HashMap<String, List<String>>();
		List<String> fields = new ArrayList<String>();
		String id = "";
		for (String line : lines) {
			if (line.equals("*NEWRECORD")) {
				entries.put(id, fields);
				fields = new ArrayList<String>();
				id = "";
			} else {
				if (line.length() == 0)
					continue;
			}
			if (line.startsWith("UI = "))
				id = line.replace("UI = ", "");
			fields.add(line);
		}

		for (String key : entries.keySet()) {
			fields = entries.get(key);
			for (String field : fields) {
				if (field.startsWith("NM = ") || field.startsWith("MH = ") || field.startsWith("ENTRY = ") || field.startsWith("PRINT ENTRY = ") 
						|| field.startsWith("SY = ")) {
					String name = field.substring(field.indexOf("= ") + 2);
					int of = name.indexOf("|");
					name = name.substring(0, (of == -1) ? name.length() : of);
					FileHelper.appendToFile(key + "\t" + name + "\n", new File("data/mesh2015.txt"),
							Charset.forName("UTF-8"));
				}
			}
		}

	}
}
