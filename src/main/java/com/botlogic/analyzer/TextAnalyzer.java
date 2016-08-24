package com.botlogic.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
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
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// http://opennlp.sourceforge.net/models-1.5/
public class TextAnalyzer {
	
	private final static Logger log = LogManager.getLogger();

	public String[] splitSentences(String text) throws IOException {
		try (InputStream modelIn = getClass().getResourceAsStream(
				"/com/botlogic/analyzer/en-sent.bin")) {
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
			return sentenceDetector.sentDetect(text);
		}
	}

	public String[] tokens(String text) throws InvalidFormatException,
			IOException {
		try (InputStream modelIn = getClass().getResourceAsStream(
				"/com/botlogic/analyzer/en-token.bin")) {
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);
			return tokenizer.tokenize(text);
		}
	}

	public Span[] names(String... sentences) throws InvalidFormatException,
			IOException {
		try (InputStream modelIn = getClass().getResourceAsStream(
				"/com/botlogic/analyzer/en-ner-person.bin")) {
			TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
			NameFinderME nameFinder = new NameFinderME(model);
			Span names[] = nameFinder.find(sentences);
			nameFinder.clearAdaptiveData();
			return names;
		}
	}

	public Parse[] parse(String sentence) throws InvalidFormatException,
			IOException {
		try (InputStream modelIn = getClass().getResourceAsStream(
				"/com/botlogic/analyzer/en-parser-chunking.bin")) {
			ParserModel model = new ParserModel(modelIn);
			Parser parser = ParserFactory.create(model);
			return ParserTool.parseLine(sentence, parser, 1);
		}
	}

	public List<WordContent> parseSentence(String sentence)
			throws InvalidFormatException, IOException {
		List<WordContent> words = new ArrayList<>();
		Parse[] parse = parse(sentence);
		createWords(words, parse);
		return words;
	}

	private void createWords(List<WordContent> words, Parse... parse) {
		for (Parse p : parse) {
			if (p.getChildCount() == 0) {
				Parse parent = p.getParent();
				words.add(new WordContent(parent.getCoveredText(), parent
						.getType(), parent.getSpan().getStart(), parent
						.getSpan().getEnd(), parent.getSpan().getProb()));
			}
			createWords(words, p.getChildren());
		}
	}

	public String[] chunker(String[] toks, String[] tags)
			throws InvalidFormatException, IOException {
		try (InputStream modelIn = getClass().getResourceAsStream(
				"/com/botlogic/analyzer/en-chunker.bin")) {
			ChunkerModel model = new ChunkerModel(modelIn);
			ChunkerME chunker = new ChunkerME(model);
			return chunker.chunk(toks, tags);
		}
	}

	public String categorize(String text, File readFileModel) throws IOException {
		try (InputStream modelIn = new FileInputStream(readFileModel)) {
			DoccatModel model = new DoccatModel(modelIn);
			DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
			double[] outcomes = myCategorizer.categorize(text);
			return myCategorizer.getBestCategory(outcomes);
		}
	}

	public void trainCategorizer(File trainingFile, File writeFileModel) throws IOException{
//		new DoccatTrainerTool().run(null, new String[]{"-lang", "en", "-encoding", "UTF-8", "-data", trainingFile.getAbsolutePath(), "-model", writeFileModel.getAbsolutePath()});
		try(OutputStream modelOut = new FileOutputStream(writeFileModel)){
	       ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainingFile), "UTF-8");
	       ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
	       TrainingParameters parameters = new TrainingParameters();
//	       parameters.put(AbstractTrainer.CUTOFF_PARAM, "1");
	       DoccatFactory factory = new DoccatFactory(WhitespaceTokenizer.INSTANCE, null);
	       DoccatModel model = DocumentCategorizerME.train("en", sampleStream, parameters, factory);
	       model.serialize(modelOut);  
		}
	}
}
