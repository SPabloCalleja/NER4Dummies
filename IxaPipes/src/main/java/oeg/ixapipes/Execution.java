/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.ixapipes;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;
import com.google.common.io.Files;
import eus.ixa.ixa.pipe.ml.utils.Flags;
import eus.ixa.ixa.pipe.pos.Annotate;
import eus.ixa.ixa.pipe.pos.CLI;


import ixa.kaflib.Entity;
import ixa.kaflib.KAFDocument;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 *
 * @author pcalleja
 */
public class Execution {
    

 
    

    String PosModel;
    String lemmatizerModel;
    String NERModel;
   
    String language;
    String kafVersion;

    
    
    eus.ixa.ixa.pipe.nerc.Annotate neAnnotator;
    eus.ixa.ixa.pipe.pos.Annotate posAnnotator;
    
    private Properties annotatePosProperties;
    private Properties annotateNEProperties;
  

    
    public static void main (String [] args) throws IOException{
    
       Execution exec= new Execution();

       //String sentence=readFile("../CORPORA/Plain/Wikipedia/es/Angus Young.txt");
       String sentence = "Mi amigo Juan trabaja de abogado en la compañía Marshaling S.A. por los alrededores de España";
       exec.initProperties();
       exec.findEntities(sentence);
       
       
    
    }



    

    private void findEntities(String text) throws IOException{
        
        KAFDocument kaf;

        // CREATES DE DOCUMENT
        InputStream is = new ByteArrayInputStream(text.getBytes());
        BufferedReader breader = new BufferedReader(new InputStreamReader(is));
        kaf = new KAFDocument(language, kafVersion);

        String version = CLI.class.getPackage().getImplementationVersion();
        String commit = CLI.class.getPackage().getSpecificationVersion();

        eus.ixa.ixa.pipe.tok.Annotate tokAnnotator = new eus.ixa.ixa.pipe.tok.Annotate(breader, annotatePosProperties);

        // Tokenize
        tokAnnotator.tokenizeToKAF(kaf);

        // PosTag
        KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor("terms", "ixa-pipe-pos-" + Files.getNameWithoutExtension(PosModel), version + "-" + commit);
        newLp.setBeginTimestamp();
        posAnnotator.annotatePOSToKAF(kaf);
        newLp.setEndTimestamp();

        // NER
        KAFDocument.LinguisticProcessor newLp2 = kaf.addLinguisticProcessor("entities", "ixa-pipe-nerc-" + Files.getNameWithoutExtension(NERModel), version + "-" + commit);
        newLp2.setBeginTimestamp();
        neAnnotator.annotateNEs(kaf);
        newLp2.setEndTimestamp();

        // Results
        for (Entity ent : kaf.getEntities()) {

            System.out.println(ent.getStr() + "\t" + ent.getType() + "\t");

        }

        breader.close();

    }
    
 

    private void initProperties() {
       
        String multiwords;
        String dictag;
        String noseg;

        String normalize; //Set normalization method according to corpus; the default option does not escape "brackets or forward slashes.
        String untokenizable; //Print untokenizable characters.
        String hardParagraph; //Do not segment paragraphs. Ever. 
        
        
        PosModel              = new File("resources/models/morph-models-1.5.0/es/es-pos-perceptron-autodict01-ancora-2.0.bin").getAbsolutePath();
        lemmatizerModel    = new File("resources/models/morph-models-1.5.0/es/es-lemma-perceptron-ancora-2.0.bin").getAbsolutePath();
        language           = "es";
        kafVersion         = "1.5.0";
        
        multiwords         = "false"; //true
        dictag             = new File("resources/models/tag").getAbsolutePath();
        normalize          = "true";
        untokenizable      = "false"; // false
        hardParagraph      = "false";
        noseg              = "false";


        this.annotatePosProperties = new Properties();

        annotatePosProperties.setProperty("normalize", normalize);
        annotatePosProperties.setProperty("untokenizable", untokenizable);
        annotatePosProperties.setProperty("hardParagraph", hardParagraph);
        annotatePosProperties.setProperty("noseg",noseg);
        annotatePosProperties.setProperty("model", PosModel);
        annotatePosProperties.setProperty("lemmatizerModel", lemmatizerModel);
        annotatePosProperties.setProperty("language", language);
        annotatePosProperties.setProperty("multiwords", multiwords);
        annotatePosProperties.setProperty("dictTag", dictag);
        annotatePosProperties.setProperty("dictPath", dictag);
        annotatePosProperties.setProperty("ruleBasedOption", dictag);

        try {
            this.posAnnotator    = new Annotate(annotatePosProperties);
        } catch (IOException e) {
            
        }
        
        ////////
        
        
        NERModel = new File("resources/models/morph-models-1.5.0/es/es-4-class-clusters-dictlbj-ancora.bin").getAbsolutePath();
        language = "es";
        dictag = new File("resources/models/tag").getAbsolutePath();

        

         this.annotateNEProperties = new Properties();
        annotateNEProperties.setProperty("model", NERModel);
        annotateNEProperties.setProperty("language", language);
        annotateNEProperties.setProperty("ruleBasedOption", Flags.DEFAULT_LEXER);
        annotateNEProperties.setProperty("dictTag", Flags.DEFAULT_DICT_OPTION);
        annotateNEProperties.setProperty("dictPath", Flags.DEFAULT_DICT_PATH);
        annotateNEProperties.setProperty("clearFeatures", Flags.DEFAULT_FEATURE_FLAG);
        
    
        
       try {
            this.neAnnotator    = new eus.ixa.ixa.pipe.nerc.Annotate(annotateNEProperties);
        } catch (IOException e) {
          //throw new RuntimeException("Error init",e);
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
