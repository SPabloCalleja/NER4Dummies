/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.ixapipes;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.google.common.io.Files;
import eus.ixa.ixa.pipe.ml.utils.Flags;
import eus.ixa.ixa.pipe.pos.Annotate;
import eus.ixa.ixa.pipe.pos.CLI;

//import eus.ixa.ixa.pipe.parse.ConstituentParsing;
//import eus.ixa.ixa.pipe.parse.Flags;
import eus.ixa.ixa.pipe.seg.RuleBasedSegmenter;
import ixa.kaflib.Annotation;
import ixa.kaflib.Entity;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.NonTerminal;
import ixa.kaflib.Statement;
import ixa.kaflib.Term;
import ixa.kaflib.Terminal;
import ixa.kaflib.Tree;
import ixa.kaflib.TreeNode;
import ixa.kaflib.WF;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import opennlp.tools.parser.Parse;
/**
 *
 * @author pcalleja
 */
public class Execution {
    

 
    String resourceFolder;

    String model;
    String lemmatizerModel;
    String PosModel;
    String language;
    String multiwords;
    String dictag;
    String noseg;

    String normalize; //Set normalization method according to corpus; the default option does not escape "brackets or forward slashes.
    String untokenizable; //Print untokenizable characters.
    String hardParagraph; //Do not segment paragraphs. Ever. 
    String kafVersion;

    String NERModel;
    
    eus.ixa.ixa.pipe.nerc.Annotate neAnnotator;
    eus.ixa.ixa.pipe.pos.Annotate posAnnotator;
    
    private Properties annotatePosProperties;
    private Properties annotateNEProperties;
  



     //private final String posTaggerVersion = eus.ixa.ixa.pipe.pos.CLI.class.getPackage().getImplementationVersion();
     //private final String posTaggerCommit = eus.ixa.ixa.pipe.pos.CLI.class.getPackage().getSpecificationVersion();
     
     public Execution(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }
    

    private void findEntities(String text){
        


        KAFDocument kaf;
        try {

            InputStream is = new ByteArrayInputStream(text.getBytes());

            BufferedReader breader = new BufferedReader(new InputStreamReader(is));

            kaf = new KAFDocument(language, kafVersion);




            String version        = CLI.class.getPackage().getImplementationVersion();
            String commit         = CLI.class.getPackage().getSpecificationVersion();
            
            
            eus.ixa.ixa.pipe.tok.Annotate tokAnnotator = new eus.ixa.ixa.pipe.tok.Annotate(breader, annotatePosProperties);

            // Tokenize
            tokAnnotator.tokenizeToKAF(kaf);


            // PosTag
            KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor("terms", "ixa-pipe-pos-" + Files.getNameWithoutExtension(model), version + "-" + commit);
            
            
            newLp.setBeginTimestamp();
            posAnnotator.annotatePOSToKAF(kaf);
            newLp.setEndTimestamp();


            // NER
            KAFDocument.LinguisticProcessor newLp2 = kaf.addLinguisticProcessor( "entities", "ixa-pipe-nerc-" + Files.getNameWithoutExtension(NERModel),    version + "-" + commit);
            newLp2.setBeginTimestamp();
            neAnnotator.annotateNEs(kaf);
            newLp2.setEndTimestamp();
            
            // Results

            for (Entity ent : kaf.getEntities()) {

                System.out.println(ent.getStr() + "\t" + ent.getType());

            }

         
            
            breader.close();
            
           
        } catch (IOException e) {
            
            // LOG.error("Error analyzing text", e);
        }

    }
    
 

    private void initProperties() {
       
        
        
        model              = Paths.get(resourceFolder,"models/morph-models-1.5.0/es/es-pos-perceptron-autodict01-ancora-2.0.bin").toFile().getAbsolutePath();
        lemmatizerModel    = Paths.get(resourceFolder,"models/morph-models-1.5.0/es/es-lemma-perceptron-ancora-2.0.bin").toFile().getAbsolutePath();
        language           = "es";
        multiwords         = "false"; //true
        dictag             = Paths.get(resourceFolder,"models/tag").toFile().getAbsolutePath();
        kafVersion         = "1.5.0";
        normalize          = "true";
        untokenizable      = "false"; // false
        hardParagraph      = "false";
        noseg              = "false";


        this.annotatePosProperties = new Properties();

        annotatePosProperties.setProperty("normalize", normalize);
        annotatePosProperties.setProperty("untokenizable", untokenizable);
        annotatePosProperties.setProperty("hardParagraph", hardParagraph);
        annotatePosProperties.setProperty("noseg",noseg);
        annotatePosProperties.setProperty("model", model);
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
        
        
        NERModel = Paths.get(resourceFolder, "models/morph-models-1.5.0/es/es-4-class-clusters-dictlbj-ancora.bin").toFile().getAbsolutePath();
        language = "es";
        dictag = Paths.get(resourceFolder, "models/tag").toFile().getAbsolutePath();

        

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

    
       

    
  
    
    
    public static void main (String [] args) throws IOException{
    
       Execution exec= new Execution("resources");

       exec.initProperties();
       exec.findEntities("Mi amigo Juan trabaja de abogado en la compañía Marshaling S.A. por los alrededores de España");
       
       
    
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
