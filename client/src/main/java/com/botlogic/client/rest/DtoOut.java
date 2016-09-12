package com.botlogic.client.rest;

public class DtoOut<T> {

	private String sentence;
	private String category;
	private Double probability;
	private T instruction;
	
	public Double getProbability() {
		return probability;
	}
	public void setProbability(Double probability) {
		this.probability = probability;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public T getInstruction() {
		return instruction;
	}
	public void setInstruction(T instruction) {
		this.instruction = instruction;
	}
	@Override
	public String toString() {
		return "DtoOut [sentence=" + sentence + ", category=" + category
				+ ", probability=" + probability + ", instruction="
				+ instruction + "]";
	}
}
