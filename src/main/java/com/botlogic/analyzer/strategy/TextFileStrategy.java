package com.botlogic.analyzer.strategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.botlogic.analyzer.TextAnalyzer;
import com.botlogic.analyzer.WordContent;

public class TextFileStrategy implements InstructionStrategy<Map<String,Set<String>>>{

	private final static Logger log = LogManager.getLogger();
	private final String category;
	private final TextAnalyzer analyzer;
	private final String CHUNK_SEPARATOR = ";";
	private final String PAIR_SEPARATOR = " -> ";
	private final String EQUALS = "=";
	private final String NULL = "null";
	private final char REFERENCE = '^';
	
	public TextFileStrategy(String category, TextAnalyzer analyzer){
		this.category = category;
		this.analyzer = analyzer;
	}
	
	@Override
	public Map<String, Set<String>> createInstruction(String sentence) throws IOException {
		Map<String, Set<String>> result = new HashMap<>();
		iterateInFile(sentence, result);
		return result;
	}
	
	private Map<String, Set<String>> getTagsPerWords(List<WordContent> items){
		Map<String, Set<String>> tagsPerWords = new HashMap<>();
		for(WordContent content : items){
			Set<String> words = tagsPerWords.get(content.getTag());
			if(words == null){
				words = new HashSet<>();
				tagsPerWords.put(content.getTag(), words);
			}
			words.add(content.getWord());
		}
		return tagsPerWords;
	}
	
	private void iterateInFile(String sentence, Map<String, Set<String>> result) throws IOException{
		List<WordContent> items = analyzer.posTagger(sentence);
		Map<String, Set<String>> tagPerWords = getTagsPerWords(items);
		try(InputStream input = getClass().getResourceAsStream(category+".txt"); BufferedReader in = new BufferedReader(new InputStreamReader(input));){
			String line = null;
			while((line = in.readLine()) != null) {
				result.putAll(processEntry(tagPerWords, line));
			}
		}
	}
	
	private Map<String, Set<String>> processEntry(Map<String, Set<String>> tagPerWords, String line){
		Map<String, Set<String>> newEntry = new HashMap<>();
		List<ChunksInfo> chunks = getChunksInfo(line);
		Set<String> tagsToRemove = new HashSet<>();
		for(ChunksInfo chunk : chunks){
			if(tagPerWords.containsKey(chunk.tag)){
				Pattern pattern = Pattern.compile(chunk.regexp);
				for(String word : tagPerWords.get(chunk.tag)){
					if(pattern.matcher(word).find()){
						if(chunk.name != null && chunk.value != null){
							String value = chunk.value;
							if(value.charAt(0) == REFERENCE){
								value = word;
							}
//							log.debug(word+" ["+chunk.tag+"] allowed. Adding "+chunk.name+"="+value.toLowerCase());
							Set<String> words = newEntry.get(chunk.name);
							if(words == null){
								words = new LinkedHashSet<>();
								newEntry.put(chunk.name, words);
							}
							words.add(value.toLowerCase());
						}
					}
				}
				tagsToRemove.add(chunk.tag);
//				log.debug("Evaluated line: "+line);
			}else{
				newEntry.clear();
				return newEntry;
			}
		}
		for(String tag : tagsToRemove){
			tagPerWords.remove(tag);
		}
		return newEntry;
	}
	
	public static boolean contains(Map<String,Set<String>> instruction, String key, String ... values){
		if(instruction.containsKey(key)){
			Set<String> programs = instruction.get(key);
			for(String value : values){
				if(programs.contains(value)){
					return true;
				}
			}
		}
		return false;
	}
	
	private List<ChunksInfo> getChunksInfo(String line){
		List<ChunksInfo> chunks = new ArrayList<>();
		for(String chunk : line.split(CHUNK_SEPARATOR)){
			String matchAndReference[] = chunk.split(PAIR_SEPARATOR);
			String match = matchAndReference[0];
			String reference = matchAndReference[1];
			String[] pairMatch = match.split(EQUALS);
			String tag = pairMatch[0];
			String[] pairRef = reference.split(EQUALS);
			String name = null;
			String value = null;
			if(!NULL.equals(reference)){
				name = pairRef[0];
				value = pairRef[1];
			}
			ChunksInfo info = new ChunksInfo(pairMatch[1], tag, name, value);
			chunks.add(info);
		}
//		log.debug(chunks);
		return chunks;
	}
	
	private class ChunksInfo{
		private final String regexp;
		private final String tag;
		private final String name;
		private final String value;
		public ChunksInfo(String regexp, String tag, String name, String value) {
			this.regexp = regexp;
			this.tag = tag;
			this.name = name;
			this.value = value;
		}
		@Override
		public String toString() {
			return "ChunksInfo [regexp=" + regexp + ", tag=" + tag + ", name=" + name + ", value=" + value + "]";
		}
	}
	
}
