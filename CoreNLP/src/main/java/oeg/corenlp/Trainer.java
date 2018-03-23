/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.corenlp;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author pcalleja
 */
public class Trainer {
    
  
    
    public static void main(String [] args) throws IOException, Exception{
    
    
        // NORMAL
  
        String [] ar= {"-prop","resources/austen.prop"};
        CRFClassifier.main(ar);
        
    
        // YOUR OWN PATHS AND PROPERTIES
        String PropFile = "resources/myprop.prop";
        String InputFile = "resources/jane-austen-emma-ch1.tsv";
        String ModelOutputFile = "models/myownmodel.gz";
        
        createProps( PropFile,  InputFile,  ModelOutputFile);
        String [] ar2= {"-prop",PropFile};
        CRFClassifier.main(ar2);
        
    
        // command line
        //java -cp D:/ProyectosJava/HNER/ICIJ/ICIJ-Projects/resources/GateHome/Plugins/Stanford_CoreNLP/ner\stanford-ner.jar edu.stanford.nlp.ie.crf.CRFClassifier -prop D:/ProyectosJava/HNER/ICIJ/ICIJ-Projects/resources/GateHome/Plugins/Stanford_CoreNLP/ner\properties.prop

    }
    
    
    
    
    public static void createProps(String PropertiesFile, String InputFile, String Model) throws UnsupportedEncodingException, FileNotFoundException {

        String s = "#location of the training file\n"
                + "trainFile =" +InputFile+"\n" //INputFile.tsv
                + "#location where you would like to save (serialize to) your\n"
                + "#classifier; adding .gz at the end automatically gzips the file,\n"
                + "#making it faster and smaller\n"
                + "serializeTo ="+Model+"\n" //ner-model.ser.gz
                + "\n"
                + "#structure of your training file; this tells the classifier\n"
                + "#that the word is in column 0 and the correct answer is in\n"
                + "#column 1\n"
                + "map = word=0,answer=1\n"
                + "\n"
                + "#these are the features we'd like to train with\n"
                + "#some are discussed below, the rest can be\n"
                + "#understood by looking at NERFeatureFactory\n"
                + "useClassFeature=true\n"
                + "useWord=true\n"
                + "useNGrams=true\n"
                + "#no ngrams will be included that do not contain either the\n"
                + "#beginning or end of the word\n"
                + "noMidNGrams=true\n"
                + "useDisjunctive=true\n"
                + "maxNGramLeng=6\n"
                + "usePrev=true\n"
                + "useNext=true\n"
                + "useSequences=true\n"
                + "usePrevSequences=true\n"
                + "maxLeft=1\n"
                + "#the next 4 deal with word shape features\n"
                + "useTypeSeqs=true\n"
                + "useTypeSeqs2=true\n"
                + "useTypeySequences=true\n"
                + "wordShape=chris2useLC";

        

        PrintWriter writer = new PrintWriter( PropertiesFile, "utf-8"); //"properties.prop"
        writer.append(s.trim());
        writer.close();

    }

    
    
    
    
}
