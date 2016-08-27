package com.botlogic.analyzer.strategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.botlogic.analyzer.TagConstants;
import com.botlogic.analyzer.TextAnalyzer;
import com.botlogic.analyzer.WordContent;

public class OrderMovementStrategy implements InstructionStrategy<Map<String,Object>>{

	@Override
	public Map<String, Object> createInstruction(TextAnalyzer analyzer, String sentence) throws IOException {
		List<WordContent> items = analyzer.posTagger(sentence);
		List<String> tags = items.stream().map(word -> word.getTag()).collect(Collectors.toList());
		boolean toMatched = false;
		Map<String, Object> content = new HashMap<>();
		for(WordContent word : items){
			if(TagConstants.TO.equals(word.getTag())){
				toMatched = true;
			}
			if(TagConstants.CARDONAL_NUMBER.equals(word.getTag())){
				content.put("amount", word.getWord());
			}else if(TagConstants.VERB_BASE_FORM.equals(word.getTag())){
				content.put("action", word.getWord());
			}else if(TagConstants.ADVERB.equals(word.getTag())){
				content.put("direction", word.getWord());
			}else if(TagConstants.NOUN_SINGULAR_OR_MASS.equals(word.getTag()) || TagConstants.NOUN_PLURAL.equals(word.getTag())){
				if(toMatched){
					content.put("direction", word.getWord());
					toMatched = false;
				}else{
					content.put("units", word.getWord());
				}
			}
		}
		return content;
	}

}
