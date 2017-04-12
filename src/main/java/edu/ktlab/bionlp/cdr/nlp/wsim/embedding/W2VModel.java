package edu.ktlab.bionlp.cdr.nlp.wsim.embedding;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class W2VModel extends Model {
	private static final int MAX_SIZE = 50;

	public W2VModel(String path) throws IOException {
		loadGoogleModel(path);
	}

	private void loadGoogleModel(String path) throws IOException {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			words = Integer.parseInt(readStr(dis));
			size = Integer.parseInt(readStr(dis));
			String word;
			float[] vectors = null;
			for (int i = 0; i < words; i++) {
				word = readStr(dis);
				vectors = new float[size];
				len = 0;
				for (int j = 0; j < size; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;
				}
				len = Math.sqrt(len);

				for (int j = 0; j < size; j++) {
					vectors[j] /= len;
				}

				wordVectors.put(word, vectors);
				dis.read();
			}
		} finally {
			bis.close();
			dis.close();
		}
	}

	public static float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}

	public static float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	private static String readStr(DataInputStream dis) throws IOException {
		byte[] bytes = new byte[MAX_SIZE];
		byte b = dis.readByte();
		int i = -1;
		StringBuilder sb = new StringBuilder();
		while (b != 32 && b != 10) {
			i++;
			bytes[i] = b;
			b = dis.readByte();
			if (i == 49) {
				sb.append(new String(bytes));
				i = -1;
				bytes = new byte[MAX_SIZE];
			}
		}
		sb.append(new String(bytes, 0, i + 1));
		return sb.toString();
	}
}
