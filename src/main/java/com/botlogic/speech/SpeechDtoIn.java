package com.botlogic.speech;

import java.util.Collections;
import java.util.List;

public class SpeechDtoIn {

	private RecognitionConfig config;
	private RecognitionAudio audio;
	
	public RecognitionConfig getConfig() {
		return config;
	}

	public void setConfig(RecognitionConfig config) {
		this.config = config;
	}

	public RecognitionAudio getAudio() {
		return audio;
	}

	public void setAudio(RecognitionAudio audio) {
		this.audio = audio;
	}

	public static class RecognitionConfig {
		private String encoding;
		private int sampleRate;
		private String languageCode;
		private int maxAlternatives;
		private boolean profanityFilter;
		private SpeechContext speechContext;
		public String getEncoding() {
			return encoding;
		}
		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}
		public int getSampleRate() {
			return sampleRate;
		}
		public void setSampleRate(int sampleRate) {
			this.sampleRate = sampleRate;
		}
		public String getLanguageCode() {
			return languageCode;
		}
		public void setLanguageCode(String languageCode) {
			this.languageCode = languageCode;
		}
		public int getMaxAlternatives() {
			return maxAlternatives;
		}
		public void setMaxAlternatives(int maxAlternatives) {
			this.maxAlternatives = maxAlternatives;
		}
		public boolean isProfanityFilter() {
			return profanityFilter;
		}
		public void setProfanityFilter(boolean profanityFilter) {
			this.profanityFilter = profanityFilter;
		}
		public SpeechContext getSpeechContext() {
			return speechContext;
		}
		public void setSpeechContext(SpeechContext speechContext) {
			this.speechContext = speechContext;
		}
	}
	
	public static class SpeechContext {
		private List<String> phrases = Collections.emptyList();
		public List<String> getPhrases() {
			return phrases;
		}
		public void setPhrases(List<String> phrases) {
			this.phrases = phrases;
		}
	}
	
	public static class RecognitionAudio {
		private String content;
		private String uri;
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
	}
}
