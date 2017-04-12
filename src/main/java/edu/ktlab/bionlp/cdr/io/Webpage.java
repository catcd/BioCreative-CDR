package edu.ktlab.bionlp.cdr.io;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Webpage {
	String user;
	String type;
	String content;
	String time;

	public Webpage() {
		this.time = getNow();
	}

	public Webpage(String user, String type, String content) {
		this.user = user;
		this.type = type;
		this.content = content;
		this.time = getNow();
	}

	public Webpage(String user, String type, String content, String time) {
		this.user = user;
		this.type = type;
		this.content = content;
		this.time = time;
	}

	String getNow() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
