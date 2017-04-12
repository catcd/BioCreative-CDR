package edu.ktlab.bionlp.cdr.base;

public class CDRLoaderExample02 {

	public static void main(String[] args) {
		CollectionFactory factory = new CollectionFactory(false);
		Collection col = factory.loadFile("data/cdr/cdr_test/cdr_test.txt");
		for (Document doc : col.getDocuments()) {
			for (Annotation ann : doc.getAnnotations()) {
				if (ann.getType().equals("Disease")) {
					String[] ids = ann.getReference().split("\\|");
					for (String id : ids)
						System.out.println(doc.getPmid() + "\t" + id);
				}
					
					
			}	
		}
		
	}

}
