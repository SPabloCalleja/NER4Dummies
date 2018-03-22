/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.opennlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 *
 * @author pcalleja
 */
public class Trainer {
    
    
    
    
    public static void main (String [] args){
    
    
        createModel("resources/en-ner-person.train","models/my-ner-person.bin",70,1,"en");
    
    }
    
    
    public static void createModel(String InputFileName, String OutputModelFileName, int Iterations,int Cutoff, String lang){
    
           // reading training data
        InputStreamFactory in = null;
        try {
            in = new MarkableFileInputStreamFactory(new File(InputFileName)); //"resources/AnnotatedSentences.txt"
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        
        ObjectStream sampleStream = null;
        try {
            sampleStream = new NameSampleDataStream(
                new PlainTextByLineStream(in, StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
 
        // setting the parameters for training
        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, Iterations); // Iteration=70
        params.put(TrainingParameters.CUTOFF_PARAM, Cutoff); //Cutoff=1
 
        // training the model using TokenNameFinderModel class 
        TokenNameFinderModel nameFinderModel = null;
        try {
            nameFinderModel = NameFinderME.train(lang, null, sampleStream, // lang = "en"
                params, TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // saving the model to "ner-custom-model.bin" file
        try {
            File output = new File(OutputModelFileName); //"ner-custom-model.bin"
            FileOutputStream outputStream = new FileOutputStream(output);
            nameFinderModel.serialize(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    
    }
    
    
}
