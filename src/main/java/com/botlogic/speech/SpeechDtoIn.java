package com.botlogic.speech;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

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
		private String encoding = "FLAC";
		private int sampleRate = 16000;
		private String languageCode = "en-US";
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

		public static RecognitionAudio create(File file) throws FileNotFoundException, IOException {
			byte[] encoded = new byte[0];
			try (FileInputStream fin = new FileInputStream(file)) {
				byte fileContent[] = new byte[(int) file.length()];
				fin.read(fileContent);
				encoded = Base64.encodeBase64(fileContent);
			}
			RecognitionAudio audio = new RecognitionAudio();
			audio.setContent(new String(encoded));
			return audio;
		}
	}
}
