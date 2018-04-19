/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.corenlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author pcalleja
 */
public class Tester {
    
    public static String CorePOSModel;
    
    public static String CoreNERModel;
   
    public static void main(String [] args) throws IOException{

        String sentence= "John Malkovich is a famous actor that puts money on Mossack Fonseca & Co. Asia Ltd. and LENA HOLDING OVERSEAS";

        //String sentence= readFile("../CORPORA/Plain/Wikipedia/en/Angus Young.txt");


        
        String myModel="models/ner-model.Companies.gz"; // from files -- my own model
        String englishNERModel="models//english.muc.7class.distsim.crf.ser.gz"; // from files
        String posEnglishModel="edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
        
        String spanishNERModel="edu/stanford/nlp/models/ner/spanish.ancora.distsim.s512.crf.ser.gz"; // from jars -- official
        String posSpanishModel="edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger";

        
        CorePOSModel=posEnglishModel;
        CoreNERModel=englishNERModel;
        namedEntityRecognition(sentence,null );


    
    }
    
    public static void namedEntityRecognition(String Sentence, String modelPOS) {

        String text = Sentence;
        
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit,pos,lemma, ner");
        props.setProperty("pos.model",CorePOSModel );
        props.setProperty("ner.model", CoreNERModel);

        
        
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
    
 
    
    
    public static String readFile(String FilePath) throws IOException{
    
        BufferedReader br = null;
        File fr = new File(FilePath);

  
        br =   new BufferedReader(new InputStreamReader(new FileInputStream(fr), "UTF8"));

        String Line;
        StringBuffer buffer=new StringBuffer();

        while ((Line = br.readLine()) != null) {
           
            buffer.append(Line +"\n");
        }

        br.close();
        
        System.out.println(buffer.toString());
        
        return buffer.toString();


    }
  
}
