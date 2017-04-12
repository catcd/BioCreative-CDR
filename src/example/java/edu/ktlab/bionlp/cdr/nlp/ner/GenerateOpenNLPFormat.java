package edu.ktlab.bionlp.cdr.nlp.ner;

import java.io.File;
import java.nio.charset.Charset;

import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class GenerateOpenNLPFormat {
	
	public static String generateOpenNLPFormat(String pubFile) {
		String output = "";
		CollectionFactory factory = new CollectionFactory(false);
		Collection col = factory.loadFile(pubFile);
		
		for (Sentence sent : col.getSentences())
			output += sent.getTokenized2OpenNLPFormat() + "\n";
		
		return output;
	}
	
	public static void main(String[] args) throws Exception {
		FileHelper.writeToFile(generateOpenNLPFormat("data/cdr/cdr_full/cdr_full.txt"), new File("data/cdr/cdr_full/cdr_full.opennlp"), Charset.defaultCharset());
	}

}
