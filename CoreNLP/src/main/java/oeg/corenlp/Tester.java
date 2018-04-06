/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.corenlp;

import com.google.common.io.Files;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author pcalleja
 */
public class Tester {
    
    public static void main(String [] args){

        String sentence= "John Malkovich is a famous actor that puts money on Mossack Fonseca & Co. Asia Ltd. and LENA HOLDING OVERSEAS";



        //english.muc.7class.distsim.crf.ser.gz
        String myModel="models\\"+ "ner-model.Companies.gz"; // from files -- my own model
        String englishNERModel="models\\"+ "english.muc.7class.distsim.crf.ser.gz"; // from files
        String spanishNERModel="edu/stanford/nlp/models/ner/spanish.ancora.distsim.s512.crf.ser.gz"; // from jars -- official
        String posSpanishModel="edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger";

        namedEntityRecognition(sentence,null , myModel);


    
    }
    
    public static void namedEntityRecognition(String Sentence, String modelPOS, String modelNER) {

        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit,pos,lemma, ner");//
        
        

        String text = Sentence;
        

        // props.setProperty("tokenize.language", "ES");
        if(modelPOS!=null){
         props.setProperty("pos.model",modelPOS );
        }
       //
       
        props.setProperty("ner.model", modelNER);

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                //String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

                //ystem.out.println("word: " + word + " pos: " + pos + " ne:" + ne);
                System.out.println(word +"\t"+ne);
            }

        }

    }
    
    
    public static void standardCorNLPExecution(String FilePath) throws IOException{
    
    
        
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    
    // read some text from the file..
    File inputFile = new File(FilePath); //"src\\test\\resources\\sample-content.txt"
    String text = Files.toString(inputFile, Charset.forName("UTF-8"));

    // create an empty Annotation just with the given text
    Annotation document = new Annotation(text);

    // run all Annotators on this text
    pipeline.annotate(document);

    // these are all the sentences in this document
    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

    for(CoreMap sentence: sentences) {
      // traversing the words in the current sentence
      // a CoreLabel is a CoreMap with additional token-specific methods
      for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
        // this is the text of the token
        String word = token.get(CoreAnnotations.TextAnnotation.class);
        // this is the POS tag of the token
        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        // this is the NER label of the token
        String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
        
        System.out.println("word: " + word + " pos: " + pos + " ne:" + ne);
      }

      // this is the parse tree of the current sentence
      Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
      System.out.println("parse tree:\n" + tree);

      // this is the Stanford dependency graph of the current sentence
      SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
      System.out.println("dependency graph:\n" + dependencies);
    }

    // This is the coreference link graph
    // Each chain stores a set of mentions that link to each other,
    // along with a method for getting the most representative mention
    // Both sentence and token offsets start at 1!
    Map<Integer, CorefChain> graph = 
        document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
    
  
    
    }
    
    
  
}
