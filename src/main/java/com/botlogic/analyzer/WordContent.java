package com.botlogic.analyzer;

public class WordContent {

	private final String word;
	private final String tag;
	private final int start;
	private final int end;
	private double probability;
	
	public WordContent(String word, String tag, int start, int end, double probability) {
		this.word = word;
		this.tag = tag;
		this.start = start;
		this.end = end;
		this.probability = probability;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getWord() {
		return word;
	}

	public String getTag() {
		return tag;
	}
	
}
