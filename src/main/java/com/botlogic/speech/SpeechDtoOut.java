package com.botlogic.speech;

import java.util.Collections;
import java.util.List;

public class SpeechDtoOut {

	private List<Alternative> results = Collections.emptyList();
	
	@Override
	public String toString() {
		return "SpeechDtoOut [results=" + results + "]";
	}

	public List<Alternative> getResults() {
		return results;
	}

	public void setResults(List<Alternative> results) {
		this.results = results;
	}

	public static class Alternative {
		private List<SpeechRecognitionAlternative> alternatives = Collections.emptyList();

		public List<SpeechRecognitionAlternative> getAlternatives() {
			return alternatives;
		}

		public void setAlternatives(List<SpeechRecognitionAlternative> alternatives) {
			this.alternatives = alternatives;
		}

		@Override
		public String toString() {
			return "Alternative [alternatives=" + alternatives + "]";
		}
		
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
		@Override
		public String toString() {
			return "SpeechRecognitionAlternative [transcript=" + transcript
					+ ", confidence=" + confidence + "]";
		}
		
	}
}
