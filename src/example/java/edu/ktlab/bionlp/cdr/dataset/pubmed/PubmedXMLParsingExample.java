package edu.ktlab.bionlp.cdr.dataset.pubmed;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import edu.ktlab.bionlp.cdr.nlp.splitter.SentSplitterMESingleton;
import edu.ktlab.bionlp.cdr.nlp.tokenizer.TokenizerMESingleton;
import edu.ktlab.bionlp.cdr.util.FileHelper;
import edu.ktlab.bionlp.cdr.util.FileUtil;

public class PubmedXMLParsingExample {

	public static void main(String[] args) throws Exception {
		String[] files = FileUtil.findFiles("data/ctd_pubmed", ".*\\.xml");
		for (String file : files)
			FileHelper.appendToFile(parse(file, false), new File("temp/pubmed_ctd_sentence.txt"),
					Charset.forName("UTF-8"));

	}

	public static String parse(String file, boolean flagDoc) throws Exception {
		String output = "";
		InputStream is = new FileInputStream(new File(file));
		Document doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
		Elements elemArticles = doc.select("PubmedArticle");
		for (Element elem : elemArticles) {
			Elements abstracts = elem.select("AbstractText");
			Elements titles = elem.select("ArticleTitle");
			String content = "";
			if (titles.size() != 0)
				content += titles.get(0).text() + "\n";
			if (abstracts.size() != 0)
				content += abstracts.get(0).text().replaceAll("\\(ABSTRACT TRUNCATED AT \\d+ WORDS\\)", "");

			String[] sents = SentSplitterMESingleton.getInstance().split(content);
			for (String sent : sents) {
				if (flagDoc)
					output += StringUtils.join(TokenizerMESingleton.getInstance().tokenize(sent), " ").toLowerCase()
							+ " ";
				else
					output += StringUtils.join(TokenizerMESingleton.getInstance().tokenize(sent), " ").toLowerCase() + "\n";
			}

			if (flagDoc)
				output += "\n";
		}

		return output;
	}

}
