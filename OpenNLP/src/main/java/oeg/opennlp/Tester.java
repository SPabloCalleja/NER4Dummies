/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.opennlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    
    
    private static String TokenizerModel;
    
    private static String NEModel;
    
    public static void main (String [] args) throws IOException{
        
        
        
        TokenizerModel="models\\en-token.bin";
        NEModel="models\\en-ner-date.bin";
    
        
        //String sentence="Malcom Young (born 31 March 1955) is an Australian guitarist";
        String sentence=readFile("../CORPORA/Plain/Wikipedia/en/Angus Young.txt");
        
        String [] tokens= tokenize(sentence);
        
        Span [] entities= namedEntityRecognition( tokens);
    
    }
    
    
    public static String[] tokenize(String Sentence) throws IOException{
   
        InputStream modelIn = null;
        String [] tokensOut = null;

        modelIn = new FileInputStream(TokenizerModel);
        // Model
        TokenizerModel model = new TokenizerModel(modelIn);

        // Tokenizer
        TokenizerME tokenizer = new TokenizerME(model);

        // Get Tokens
        String tokens[] = tokenizer.tokenize(Sentence);

        tokensOut = tokens;


       
        return tokensOut;


    }
    
    
   
    
    public static Span[] namedEntityRecognition(String [] tokens )throws IOException {
        
        // load the model from file
        InputStream is = new FileInputStream(NEModel);
 
        //  NE model
        TokenNameFinderModel model = new TokenNameFinderModel(is);
        is.close();
 
        // feed the model to name finder class
        NameFinderME nameFinder = new NameFinderME(model);
 
        // Get entities
        Span[] nameSpans = nameFinder.find(tokens);
 
        // nameSpans contain all the possible entities detected
        for(Span s: nameSpans){
            System.out.print(s.toString()); // type
            System.out.print("  :  ");
           
            for(int index=s.getStart();index<s.getEnd();index++){ // text
                System.out.print(tokens[index]+" ");
            }
            System.out.println();
        }
        
        return nameSpans;
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
