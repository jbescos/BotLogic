package com.botlogic.server.analyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
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
		try (InputStream modelIn = getClass().getResourceAsStream("/com/botlogic/server/analyzer/en-sent.bin")) {
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
			return sentenceDetector.sentDetect(text);
		}
	}
	
	public List<WordContent> posTagger(String text) throws IOException {
		try(InputStream modelIn = getClass().getResourceAsStream("/com/botlogic/server/analyzer/en-pos-maxent.bin")){
			List<WordContent> contents = new ArrayList<>(); 
			POSModel model = new POSModel(modelIn);
			POSTaggerME tagger = new POSTaggerME(model);
			String[] words = tokens(text);
			String tags[] = tagger.tag(words);
			for(int i=0;i<tags.length;i++){
				WordContent content = new WordContent(words[i], tags[i]);
				contents.add(content);
			}
			log.debug(contents.toString());
			return contents;
		}
	}

	public String[] tokens(String text) throws InvalidFormatException,
			IOException {
		try (InputStream modelIn = getClass().getResourceAsStream("/com/botlogic/server/analyzer/en-token.bin")) {
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);
			return tokenizer.tokenize(text);
		}
	}

	public String[] chunker(String[] toks, String[] tags)
			throws InvalidFormatException, IOException {
		try (InputStream modelIn = getClass().getResourceAsStream("/com/botlogic/server/analyzer/en-chunker.bin")) {
			ChunkerModel model = new ChunkerModel(modelIn);
			ChunkerME chunker = new ChunkerME(model);
			return chunker.chunk(toks, tags);
		}
	}

	public Map.Entry<Double,String> categorize(String text, File readFileModel) throws IOException {
		try (InputStream modelIn = new FileInputStream(readFileModel)) {
			DoccatModel model = new DoccatModel(modelIn);
			DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
			SortedMap<Double, Set<String>> map = myCategorizer.sortedScoreMap(text);
			log.debug(map.toString());
			double probability = map.lastKey();
			String category = map.get(probability).iterator().next();
			Map.Entry<Double,String> pair = new AbstractMap.SimpleImmutableEntry<>(probability, category);
			return pair;
		}
	}

	public void trainCategorizer(File trainingFile, File writeFileModel) throws IOException{
//		new DoccatTrainerTool().run(null, new String[]{"-lang", "en", "-encoding", "UTF-8", "-data", trainingFile.getAbsolutePath(), "-model", writeFileModel.getAbsolutePath()});
		try(OutputStream modelOut = new FileOutputStream(writeFileModel)){
	       ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainingFile), "UTF-8");
	       ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
	       TrainingParameters parameters = new TrainingParameters();
	       parameters.put(AbstractTrainer.CUTOFF_PARAM, "1");
	       DoccatFactory factory = new DoccatFactory(SimpleTokenizer.INSTANCE, null);
	       DoccatModel model = DocumentCategorizerME.train("en", sampleStream, parameters, factory);
	       model.serialize(modelOut);  
		}
	}
	
	public void makeTrainingFile(File currentFile, BufferedWriter bw) throws FileNotFoundException, IOException{
		if(!currentFile.isDirectory()){
			String category = currentFile.getParentFile().getName();
			try(InputStream input = new FileInputStream(currentFile); BufferedReader reader = new BufferedReader(new InputStreamReader(input));){
				String line = reader.readLine();
				while(line != null){
					if(line.matches(".*[a-zA-Z]+.*")){
						bw.write(category+" "+line);
						bw.newLine();
					}
					line = reader.readLine();
				}
			}
		}else{
			log.debug("Category "+currentFile.getName());
			for(File children : currentFile.listFiles()){
				makeTrainingFile(children, bw);
			}
		}
	}
}
