package edu.ktlab.bionlp.cdr.nlp.wsim.distance;

public class CosineDistance implements Distance {
	public float distance(float[] x, float[] y) {
		if (x.length != y.length)
			return 0f;
		float xx = 0f;
		float yy = 0f;
		float xy = 0f;

		for (int i = 0; i < x.length; i++) {
			float x1 = x[i];
			float y1 = y[i];
			xx += x1 * x1;
			yy += y1 * y1;
			xy += x1 * y1;
		}
		if (xx == 0 || yy == 0) {
			return 0f;
		} else {
			return (float) (xy / Math.sqrt(xx * yy));
		}
	}
	
	public static void main(String...strings) {
		CosineDistance cosine = new CosineDistance();
		float[] x = {1f, 1f, 2f};
		float[] y = {1f, 1f, 1f};
		System.out.println(cosine.distance(x, y));
	}
}
