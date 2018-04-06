/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.ixapipes.old;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jdom2.JDOMException;

import com.google.common.io.Files;

import eus.ixa.ixa.pipe.chunk.ChunkTagger;
import eus.ixa.ixa.pipe.lemma.StatisticalLemmatizer;
import eus.ixa.ixa.pipe.ml.tok.RuleBasedSegmenter;
import eus.ixa.ixa.pipe.ml.tok.RuleBasedTokenizer;
import eus.ixa.ixa.pipe.ml.tok.Token;
import eus.ixa.ixa.pipe.nerc.Annotate;
import eus.ixa.ixa.pipe.nerc.Name;
import eus.ixa.ixa.pipe.nerc.NameFactory;
import eus.ixa.ixa.pipe.nerc.StatisticalNameFinder;
import eus.ixa.ixa.pipe.nerc.train.Flags;
import eus.ixa.ixa.pipe.pos.StatisticalTagger;
import ixa.kaflib.KAFDocument;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
/**
 *
 * @author pcalleja
 */

public class CLI {

  /**
   * Get dynamically the version of ixa-pipes-api-sample by looking at the
   * MANIFEST file.
   */
  private final String version = CLI.class.getPackage()
      .getImplementationVersion();
  /**
   * Get the git commit of the ixa-pipes-api-sample compiled by looking at the
   * MANIFEST file.
   */
  private final String commit = CLI.class.getPackage()
      .getSpecificationVersion();
  /**
   * Name space of the arguments provided at the CLI.
   */
  private Namespace parsedArguments = null;
  /**
   * Argument parser instance.
   */
  private ArgumentParser argParser = ArgumentParsers
      .newArgumentParser("ixa-pipes-api-sample-" + version + "-exec.jar")
      .description("ixa-pipes-api-sample-" + version
          + " shows how to programatically use IXA pipes.\n");
  /**
   * Sub parser instance.
   */
  private Subparsers subParsers = argParser.addSubparsers()
      .help("sub-command help");
  /**
   * The parser that manages the tokenizer sub-command.
   */
  private Subparser tokParser;
  private Subparser posParser;
  private Subparser nerParser;
  private Subparser chunkParser;
  private Subparser parseParser;
  private Subparser docParser;
  private Subparser pipelineParser;

  private static final String TOKEN_PARSER = "tok";
  private static final String POS_PARSER = "pos";
  private static final String NER_PARSER = "ner";
  private static final String CHUNK_PARSER = "chunk";
  public static final String PARSER_PARSER = "parse";
  public static final String DOC_PARSER = "doc";
  public static final String PIPELINE_PARSER = "pipeline";

  /**
   * Construct a CLI object with the sub-parsers to manage the command line
   * parameters.
   */
  public CLI() {
    tokParser = subParsers.addParser(TOKEN_PARSER).help("The tokenizer CLI");
    loadTokParameters();
    posParser = subParsers.addParser(POS_PARSER).help("The POS tagger CLI");
    loadPOSParameters();
    nerParser = subParsers.addParser(NER_PARSER).help("The NER CLI");
    loadNERParameters();
    chunkParser = subParsers.addParser(CHUNK_PARSER).help("The chunker CLI");
    loadChunkParameters();
    parseParser = subParsers.addParser(PARSER_PARSER)
        .help("The constituent parser CLI");
    loadParseParameters();
    docParser = subParsers.addParser(DOC_PARSER)
        .help("The document classifier CLI");
    loadDocParameters();
    pipelineParser = subParsers.addParser(PIPELINE_PARSER).help("The pipeline parser");
    loadPipelineParameters();
  }

  /**
   * Main entry point of ixa-pipes-api-sample.
   * 
   * @param args
   *          the arguments passed through the CLI
   * @throws IOException
   *           exception if input data not available
   * @throws JDOMException
   *           if problems with the xml formatting of NAF
   */
  public static void main(final String[] args)
      throws IOException, JDOMException {
    CLI cmdLine = new CLI();
    String [] ar = {"ner","-m","en-pos-perceptron-autodict01-conll09.bin"};
    
    cmdLine.parseCLI(ar);
  }

  /**
   * Parse the command interface parameters with the argParser.
   * 
   * @param args
   *          the arguments passed through the CLI
   * @throws IOException
   *           exception if problems with the incoming data
   * @throws JDOMException
   *           if xml format problems
   */
  public final void parseCLI(final String[] args)
      throws IOException, JDOMException {
    try {
      parsedArguments = argParser.parseArgs(args);
      System.err.println("CLI options: " + parsedArguments);
      switch (args[0]) {
      case TOKEN_PARSER:
        tokenize();
        break;
      case POS_PARSER:
        posTag();
        break;
      case NER_PARSER:
        nerTag();
        break;
      case PIPELINE_PARSER:
        pipeline();
        break;
      }
    } catch (ArgumentParserException e) {
      argParser.handleError(e);
      System.out.println("Run java -jar ixa-pipes-api-sample-" + version
          + "-exec.jar (tok|pos|ner|chunk|parse|doc) -help for details");
      System.exit(1);
    }
  }

  public final void tokenize() throws IOException, JDOMException {
    BufferedReader breader = new BufferedReader(
        new InputStreamReader(System.in, "UTF-8"));
    BufferedWriter bwriter = new BufferedWriter(
        new OutputStreamWriter(System.out, "UTF-8"));
    // read KAF document from inputstream
    final String lang = parsedArguments.getString("lang");
    // API begin
    KAFDocument kaf = new KAFDocument(lang, "newsreader");
    final KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor(
        "text", "ixa-pipe-tok-" + lang, version + "-" + commit);
    newLp.setBeginTimestamp();
    eus.ixa.ixa.pipe.tok.Annotate annotator = new eus.ixa.ixa.pipe.tok.Annotate(
        breader, setTokenizeProperties(lang));
    annotator.tokenizeToKAF(kaf);
    newLp.setEndTimestamp();
    // API end
    bwriter.write(kaf.toString());
    breader.close();
    bwriter.close();

  }

  public final void posTag() throws IOException, JDOMException {
    BufferedReader breader = new BufferedReader(
        new InputStreamReader(System.in, "UTF-8"));
    BufferedWriter bwriter = new BufferedWriter(
        new OutputStreamWriter(System.out, "UTF-8"));
    // read KAF document from inputstream
    KAFDocument kaf = KAFDocument.createFromStream(breader);
    // load parameters from CLI
    String posModel = parsedArguments.getString("posModel");
    String lemmaModel = parsedArguments.getString("lemmaModel");
    String outputFormat = parsedArguments.getString("outputFormat");
    Properties posProperties = setPOSProperties(posModel, lemmaModel,
        kaf.getLang());
    // API begin
    final KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor(
        "terms", "ixa-pipe-pos-" + Files.getNameWithoutExtension(posModel),
        this.version + "-" + this.commit);
    final eus.ixa.ixa.pipe.pos.Annotate annotator = new eus.ixa.ixa.pipe.pos.Annotate(
        posProperties);
    newLp.setBeginTimestamp();
    annotator.annotatePOSToKAF(kaf);
    // API end
    if (outputFormat.equalsIgnoreCase("conll")) {
      bwriter.write(annotator.annotatePOSToCoNLL(kaf));
    } else {
      annotator.annotatePOSToKAF(kaf);
      newLp.setEndTimestamp();
      bwriter.write(kaf.toString());
    }
    breader.close();
    bwriter.close();
  }

  public final void nerTag() throws IOException, JDOMException {

    BufferedReader breader = new BufferedReader(
        new InputStreamReader(System.in, "UTF-8"));
    BufferedWriter bwriter = new BufferedWriter(
        new OutputStreamWriter(System.out, "UTF-8"));
    // read KAF document from inputstream
    KAFDocument kaf = KAFDocument.createFromStream(breader);
    // load parameters from CLI
    String model = parsedArguments.getString("model");
    String outputFormat = parsedArguments.getString("outputFormat");
    // API begin
    KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor(
        "entities", "ixa-pipe-nerc-" + Files.getNameWithoutExtension(model),
        version + "-" + commit);
    newLp.setBeginTimestamp();
    Annotate annotator = new Annotate(setNERProperties(model, kaf.getLang()));
    annotator.annotateNEs(kaf);
    newLp.setEndTimestamp();
    // end of API
    String kafToString = null;
    if (outputFormat.equalsIgnoreCase("conll03")) {
      kafToString = annotator.annotateNEsToCoNLL2003(kaf);
    } else if (outputFormat.equalsIgnoreCase("conll02")) {
      kafToString = annotator.annotateNEsToCoNLL2002(kaf);
    } else {
      kafToString = kaf.toString();
    }
    bwriter.write(kafToString);
    bwriter.close();
    breader.close();
  }

 
  
  public final void pipeline() throws IOException {
    BufferedReader breader = new BufferedReader(
        new InputStreamReader(System.in, "UTF-8"));
    // load parameters from CLI
    final String lang = parsedArguments.getString("lang");
    String posModel = parsedArguments.getString("posModel");
    String lemmaModel = parsedArguments.getString("lemmaModel");
    String nerModel = parsedArguments.getString("nerModel");
    String chunkModel = parsedArguments.getString("chunkModel");
    //tokenizer
    List<List<Token>> tokenizedDocument = tokenizeDocument(breader, lang);
    //postagger and lemmatizer
    StatisticalTagger posTagger = new StatisticalTagger(setPOSProperties(posModel, lemmaModel, lang));
    StatisticalLemmatizer lemmatizer = new StatisticalLemmatizer(setPOSProperties(posModel, lemmaModel, lang));
    //NER tagger
    NameFactory nameFactory = new NameFactory();
    StatisticalNameFinder nerTagger = new StatisticalNameFinder(setNERProperties(nerModel, lang), nameFactory);
    //chunker
    ChunkTagger chunker = new ChunkTagger(setChunkProperties(chunkModel, lang));
    StringBuilder sb = new StringBuilder();
    for (List<Token> sentence : tokenizedDocument) {
      List<String> tokenList = new ArrayList<>();
      for (Token token : sentence) {
        if (!token.getTokenValue().equals(RuleBasedSegmenter.PARAGRAPH)) {
          tokenList.add(token.getTokenValue());
        }
      }
      String[] tokens = new String[tokenList.size()];
      tokens = tokenList.toArray(tokens);
      List<String> posTags = posTagger.posAnnotate(tokens);
      String[] posTagsArray = new String[posTags.size()];
      posTagsArray = posTags.toArray(posTagsArray);
      List<String> lemmas = lemmatizer.lemmatize(tokens, posTagsArray);
      List<Name> names = nerTagger.getNames(tokens);
      String[] chunks = chunker.chunkToString(tokens, posTagsArray);
      
      for (int i = 0; i < tokens.length; i++) {
        sb.append(tokens[i]).append("\t").append(posTags.get(i)).append("\t").append(lemmas.get(i)).append("\t").append(chunks[i]).append("\n");
      }
      sb.append("\n");
    }
    System.out.println(sb.toString());
    breader.close();
  }

  private void loadTokParameters() {
    // specify language (for language dependent treatment of apostrophes)
    tokParser.addArgument("-l", "--lang")
        .choices("de", "en", "es", "eu", "fr", "gl", "it", "nl").required(true)
        .help(
            "It is REQUIRED to choose a language to perform annotation with ixa-pipe-tok.\n");
  }

  /**
   * Generate the annotation parameter of the CLI.
   */
  private void loadPOSParameters() {
    this.posParser.addArgument("-m", "--posModel").required(true)
        .help("It is required to provide a POS tagging model.");
    this.posParser.addArgument("-lm", "--lemmaModel").required(true);
    this.posParser.addArgument("-o", "--outputFormat").required(false)
        .choices("naf", "conll").setDefault(Flags.DEFAULT_OUTPUT_FORMAT)
        .help("Choose output format; it defaults to NAF.\n");
  }

  /**
   * Create the available parameters for NER tagging.
   */
  private void loadNERParameters() {

    nerParser.addArgument("-m", "--model").required(true)
        .help("Pass the model to do the tagging as a parameter.\n");
    nerParser.addArgument("-o", "--outputFormat").required(false)
        .choices("conll03", "conll02", "naf")
        .setDefault(Flags.DEFAULT_OUTPUT_FORMAT)
        .help("Choose output format; it defaults to NAF.\n");
  }

  /**
   * Create the available parameters for NER tagging.
   */
  private void loadChunkParameters() {

    chunkParser.addArgument("-m", "--model").required(true)
        .help("Pass the model to do the tagging as a parameter.\n");
    chunkParser.addArgument("-o", "--outputFormat").required(false)
        .choices("conll", "naf").setDefault(Flags.DEFAULT_OUTPUT_FORMAT)
        .help("Choose output format; it defaults to NAF.\n");
  }

  /**
   * Create the available parameters for NER tagging.
   */
  private void loadParseParameters() {

    parseParser.addArgument("-m", "--model").required(true)
        .help("Pass the model to do the tagging as a parameter.\n");
    parseParser.addArgument("-o", "--outputFormat").required(false)
        .choices("oneline", "naf").setDefault(Flags.DEFAULT_OUTPUT_FORMAT)
        .help("Choose output format; it defaults to NAF.\n");
  }

  private void loadDocParameters() {
    docParser.addArgument("-m", "--model").required(true)
        .help("Pass the model for document classification.\n");
  }
  
  private void loadPipelineParameters() {
    // specify language (for language dependent treatment of apostrophes)
    pipelineParser.addArgument("-l", "--lang")
        .choices("de", "en", "es", "eu", "fr", "gl", "it", "nl").required(true)
        .help(
            "It is REQUIRED to choose a language to perform annotation with ixa pipes.\n");
    pipelineParser.addArgument("--posModel").required(true)
    .help("Pass the pos tagging model to do the tagging as a parameter.\n");
    pipelineParser.addArgument("--lemmaModel").required(true)
    .help("Pass the lemmatizer model to do the tagging as a parameter.\n");
    pipelineParser.addArgument("--nerModel").required(true)
    .help("Pass the ner model to do the tagging as a parameter.\n");
    pipelineParser.addArgument("--chunkModel").required(true)
    .help("Pass the chunk model to do the tagging as a parameter.\n");
  }

  private static Properties setTokenizeProperties(String language) {
    Properties annotateProperties = new Properties();
    annotateProperties.setProperty("language", language);
    annotateProperties.setProperty("normalize", "default");
    annotateProperties.setProperty("hardParagraph", "no");
    annotateProperties.setProperty("untokenizable", "no");
    return annotateProperties;
  }

  private static Properties setPOSProperties(final String posModel,
      final String lemmaModel, String language) {
    final Properties annotateProperties = new Properties();
    annotateProperties.setProperty("model", posModel);
    annotateProperties.setProperty("lemmatizerModel", lemmaModel);
    annotateProperties.setProperty("language", language);
    return annotateProperties;
  }

  private Properties setNERProperties(String model, String language) {
    Properties annotateProperties = new Properties();
    annotateProperties.setProperty("model", model);
    annotateProperties.setProperty("language", language);
    annotateProperties.setProperty("ruleBasedOption", Flags.DEFAULT_LEXER);
    annotateProperties.setProperty("dictTag", Flags.DEFAULT_DICT_OPTION);
    annotateProperties.setProperty("dictPath", Flags.DEFAULT_DICT_PATH);
    annotateProperties.setProperty("clearFeatures", Flags.DEFAULT_FEATURE_FLAG);
    return annotateProperties;
  }

  private Properties setChunkProperties(final String model, final String language) {
    final Properties annotateProperties = new Properties();
    annotateProperties.setProperty("model", model);
    annotateProperties.setProperty("language", language);
    return annotateProperties;
  }

  private Properties setParseProperties(final String model) {
    final Properties annotateProperties = new Properties();
    annotateProperties.setProperty("model", model);
    return annotateProperties;
  }
  
  public static List<List<Token>> tokenizeDocument(BufferedReader breader,
      String language) {
    String textSegment = RuleBasedSegmenter.readText(breader);
    RuleBasedSegmenter segmenter = new RuleBasedSegmenter(textSegment,
        setTokenizeProperties(language));
    RuleBasedTokenizer toker = new RuleBasedTokenizer(textSegment,
        setTokenizeProperties(language));
    String[] sentences = segmenter.segmentSentence();
    List<List<Token>> tokens = toker.tokenize(sentences);
    return tokens;
  }

}
