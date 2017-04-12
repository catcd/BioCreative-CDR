package edu.ktlab.bionlp.cdr.base;

public class PubtatorFormatLoaderExample {

	public static void main(String[] args) {
		String text = "000|t|Tricuspid valve regurgitation and lithium carbonate toxicity in a newborn infant.\n000|a|A newborn with massive tricuspid regurgitation, atrial flutter, congestive heart failure, and a high serum lithium level is described. This is the first patient to initially manifest tricuspid regurgitation and atrial flutter, and the 11th described patient with cardiac disease among infants exposed to lithium compounds in the first trimester of pregnancy. Sixty-three percent of these infants had tricuspid valve involvement. Lithium carbonate may be a factor in the increasing incidence of congenital heart disease when taken during early pregnancy. It also causes neurologic depression, cyanosis, and cardiac arrhythmia when consumed prior to delivery.";
		CollectionFactory factory = new CollectionFactory(true);
		Document doc = factory.loadDocumentFromString(text);
		for (Sentence sent : doc.getSentences()) {
			System.out.println(sent.getDeptree());	
		}
		
	}

}
