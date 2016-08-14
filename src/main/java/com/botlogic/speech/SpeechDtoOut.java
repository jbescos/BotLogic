package com.botlogic.speech;

import java.util.Collections;
import java.util.List;

public class SpeechDtoOut {

	private List<SpeechRecognitionAlternative> alternatives = Collections.emptyList();
	
	public List<SpeechRecognitionAlternative> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<SpeechRecognitionAlternative> alternatives) {
		this.alternatives = alternatives;
	}

	public static class SpeechRecognitionAlternative {
		private String transcript;
		private int confidence;
		public String getTranscript() {
			return transcript;
		}
		public void setTranscript(String transcript) {
			this.transcript = transcript;
		}
		public int getConfidence() {
			return confidence;
		}
		public void setConfidence(int confidence) {
			this.confidence = confidence;
		}
	}
}
