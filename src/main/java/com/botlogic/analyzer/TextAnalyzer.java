package com.botlogic.analyzer;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

// http://opennlp.sourceforge.net/models-1.5/
public class TextAnalyzer {

	public String[] splitSentences(String text) throws IOException{
		try(InputStream modelIn = getClass().getResourceAsStream("/com/botlogic/analyzer/en-sent.bin")){
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
			return sentenceDetector.sentDetect(text);
		}
	}
	
	public String[] tokens(String text) throws InvalidFormatException, IOException{
		try(InputStream modelIn = getClass().getResourceAsStream("/com/botlogic/analyzer/en-token.bin")){
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);
			return tokenizer.tokenize(text);
		}
	}
	
	public Span[] names(String ... sentences) throws InvalidFormatException, IOException{
		try(InputStream modelIn = getClass().getResourceAsStream("/com/botlogic/analyzer/en-ner-person.bin")){
			TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
			NameFinderME nameFinder = new NameFinderME(model);
			Span names[] = nameFinder.find(sentences);
			nameFinder.clearAdaptiveData();
			return names;
		}
	}
	
	public Parse[] parse(String sentence) throws InvalidFormatException, IOException{
		try(InputStream modelIn = getClass().getResourceAsStream("/com/botlogic/analyzer/en-parser-chunking.bin")){
			ParserModel model = new ParserModel(modelIn);
			Parser parser = ParserFactory.create(model);
			return ParserTool.parseLine(sentence, parser, 1);
		}
	}
	
	public String[] chunker(String[] toks, String[] tags) throws InvalidFormatException, IOException{
		try(InputStream modelIn = getClass().getResourceAsStream("/com/botlogic/analyzer/en-chunker.bin")){
			ChunkerModel model = new ChunkerModel(modelIn);
			ChunkerME chunker = new ChunkerME(model);
			return chunker.chunk(toks, tags);
		}
	}
	
}
