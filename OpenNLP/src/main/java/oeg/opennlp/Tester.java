/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.opennlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
 
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
/**
 *
 * @author pcalleja
 */
public class Tester {
    
    
    
    public static void main (String [] args) throws IOException{
    
        String [] tokens= tokenizeOpenNLP("John is 26 years old.", "models\\en-token.bin");
        
        Span [] entities=findName( tokens, "models\\en-ner-person.bin");
    
    }
    
    
    public static String[] tokenizeOpenNLP(String Sentence, String Model){
   
        InputStream modelIn = null;
        String[] tokensOut = null;

        try {
            modelIn = new FileInputStream(Model);
            TokenizerModel model = new TokenizerModel(modelIn);
            TokenizerME tokenizer = new TokenizerME(model);
            String tokens[] = tokenizer.tokenize(Sentence);
            //double tokenProbs[] = tokenizer.getTokenProbabilities();
            tokensOut = tokens;
            // System.out.println("Token\t: Probability\n-------------------------------");
            for (int i = 0; i < tokens.length; i++) {
                //System.out.println(tokens[i]+"\t: "+tokenProbs[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                }
            }
        }
        return tokensOut;


    }
    
    
   
    
    public static Span[] findName(String [] sentence, String Model) throws IOException {
        InputStream is = new FileInputStream(Model);
 
        // load the model from file
        TokenNameFinderModel model = new TokenNameFinderModel(is);
        is.close();
 
        // feed the model to name finder class
        NameFinderME nameFinder = new NameFinderME(model);
 
 
        Span[] nameSpans = nameFinder.find(sentence);
 
        // nameSpans contain all the possible entities detected
        for(Span s: nameSpans){
            System.out.print(s.toString());
            System.out.print("  :  ");
            // s.getStart() : contains the start index of possible name in the input string array
            // s.getEnd() : contains the end index of the possible name in the input string array
            for(int index=s.getStart();index<s.getEnd();index++){
                System.out.print(sentence[index]+" ");
            }
            System.out.println();
        }
        
        return nameSpans;
    }
    
    
    
    
}
