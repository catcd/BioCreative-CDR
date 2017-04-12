package edu.ktlab.bionlp.cdr.io;

public interface IWriter<T> {
	public void write(T object) throws Exception;
}
