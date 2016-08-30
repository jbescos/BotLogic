package com.botlogic.speech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.botlogic.audio.Microphone;

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

	@Override
	public String toString() {
		return "SpeechDtoIn [config=" + config + ", audio=" + audio + "]";
	}

	public static class RecognitionConfig {
		private String encoding = "LINEAR16";
		//private int sampleRate = 44100;
		private int sampleRate = (int) Microphone.SAMPLE_RATE;
		private String languageCode = Languages.EN_US;
		private int maxAlternatives = 1;
		private boolean profanityFilter = false;
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

		@Override
		public String toString() {
			return "RecognitionConfig [encoding=" + encoding + ", sampleRate="
					+ sampleRate + ", languageCode=" + languageCode
					+ ", maxAlternatives=" + maxAlternatives
					+ ", profanityFilter=" + profanityFilter
					+ ", speechContext=" + speechContext + "]";
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

		@Override
		public String toString() {
			return "SpeechContext [phrases=" + phrases + "]";
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
		

		@Override
		public String toString() {
			return "RecognitionAudio [content=" + content + ", uri=" + uri
					+ "]";
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public static RecognitionAudio create(File file) throws FileNotFoundException, IOException {
			RecognitionAudio audio = new RecognitionAudio();
			audio.setContent(AudioUtility.toString64(file));
			return audio;
		}
	}
}
