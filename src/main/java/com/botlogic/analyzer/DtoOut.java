package com.botlogic.analyzer;

public class DtoOut<T> {

	private String sentence;
	private String category;
	private T instruction;
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
				+ ", instruction=" + instruction + "]";
	}
}
