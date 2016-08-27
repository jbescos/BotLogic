package com.botlogic.analyzer.strategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.botlogic.analyzer.TagConstants;
import com.botlogic.analyzer.TextAnalyzer;
import com.botlogic.analyzer.WordContent;

public class OrderExecuteStrategy implements InstructionStrategy<Map<String, Object>>{

	@Override
	public Map<String, Object> createInstruction(TextAnalyzer analyzer, String sentence) throws IOException {
		List<WordContent> words = analyzer.posTagger(sentence);
		Map<String, Object> content = new HashMap<>();
		for(WordContent word : words){
			if(TagConstants.VERB_BASE_FORM.equals(word.getTag()) || TagConstants.PREPOSITION_OR_SUBORDINATING_CONJUNCTION.equals(word.getTag())){
				content.put("action", word.getWord());
			}else if(TagConstants.NOUN_SINGULAR_OR_MASS.equals(word.getTag())){
				@SuppressWarnings("unchecked")
				List<String> programs = (List<String>) content.get("program");
				if(programs == null){
					programs = new ArrayList<>();
					content.put("program", programs);
				}
				programs.add(word.getWord());
			}
		}
		return content;
	}

}
