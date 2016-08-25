package com.botlogic.analyzer;

public class WordContent {

	private final String word;
	private final String tag;
	
	public WordContent(String word, String tag) {
		this.word = word;
		this.tag = tag;
	}

	public String getWord() {
		return word;
	}

	public String getTag() {
		return tag;
	}

	@Override
	public String toString() {
		return "WordContent [word=" + word + ", tag=" + tag + "]";
	}
	
}
