/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.ixapipes.old;

import oeg.ixapipes.*;
import oeg.ixapipes.old.CLI;
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
    
    private final String tokenizerVersion = eus.ixa.ixa.pipe.tok.CLI.class.getPackage().getImplementationVersion();
    private final String tokenizerCommit = eus.ixa.ixa.pipe.tok.CLI.class.getPackage().getSpecificationVersion();
    private String lang = "es"; //It is REQUIRED to choose a language to perform annotation with ixa-pipe-tok. Default español, pero también puede ser ingles
     String normalize = "default"; //Set normalization method according to corpus; the default option does not escape "brackets or forward slashes.
     String untokenizable = "no"; //Print untokenizable characters.
     String hardParagraph = "no"; //Do not segment paragraphs. Ever. 
     String kafVersion = "v1.naf";
    eus.ixa.ixa.pipe.pos.Annotate annotatorPos;
    
    public final KAFDocument tokenize(String text) throws IOException {
        //final BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));		   
        KAFDocument kaf = new KAFDocument(lang, kafVersion);
        final Properties properties = setTokenizerProperties(lang, normalize, untokenizable, hardParagraph);

        final StringReader stringReader = new StringReader(text);
        BufferedReader breader = new BufferedReader(stringReader);
        final eus.ixa.ixa.pipe.tok.Annotate annotator = new eus.ixa.ixa.pipe.tok.Annotate(breader, properties);
        final KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor("text", "ixa-pipe-tok-" + lang, tokenizerVersion + "-" + tokenizerCommit);
        newLp.setBeginTimestamp();
        annotator.tokenizeToKAF(kaf);
        newLp.setEndTimestamp();
        //System.out.println("tokens:"+kaf.toString());
        breader.close();
        return kaf;
        //bwriter.write(kaf.toString());

        // bwriter.close();
    }
 
    private Properties setTokenizerProperties(final String lang, final String normalize, final String untokenizable, final String hardParagraph) {
        final Properties annotateProperties = new Properties();
        // tokenizer properties
        annotateProperties.setProperty("language", lang);
        annotateProperties.setProperty("normalize", normalize);
        annotateProperties.setProperty("untokenizable", untokenizable);
        annotateProperties.setProperty("hardParagraph", hardParagraph);

        return annotateProperties;
    }
    
    
    // cat file.txt | java -jar target/ixa-pipe-tok-$version-exec.jar tok -l en | 
    //java -jar ixa-pipe-pos-1.5.0-exec.jar tag -m en-pos-perceptron-autodict01-conll09.bin -lm en-lemma-perceptron-conll09.bin |
    //java -jar $PATH/target/ixa-pipe-nerc-${version}-exec.jar tag -m model.bin
    
    

     private final String posTaggerVersion = eus.ixa.ixa.pipe.pos.CLI.class.getPackage().getImplementationVersion();
     private final String posTaggerCommit = eus.ixa.ixa.pipe.pos.CLI.class.getPackage().getSpecificationVersion();
    
  /*  
    public void initParam(){
    
        final Properties propertiesPos = setPosTaggerProperties(posmodel, lang, beamSize, multiwords, dictag, lemmatizermodel);
       
    
    }
    
    
    public final KAFDocument posTag(KAFDocument inputKaf) throws IOException {
//          Increiblemente, la siguiente sentencia falla. No usar. debe de ser que se equivoca al resolver la clase "Files"oq ue la libreria de google está mal.
//              String xx=Files.getNameWithoutExtension(posmodel);
        String xx = posmodel.substring(posmodel.lastIndexOf("/") + 1, posmodel.length() - 4);
        final KAFDocument.LinguisticProcessor newLp = inputKaf.addLinguisticProcessor("terms", "ixa-pipe-pos-" + xx, this.posTaggerVersion + "-" + this.posTaggerCommit);
        newLp.setBeginTimestamp();
        annotatorPos.annotatePOSToKAF(inputKaf);
        newLp.setEndTimestamp();
//	      System.out.println("pos:"+inputKaf.toString());
        return inputKaf;
    }

*/
    
   // private static final Logger LOG = LoggerFactory.getLogger(IXAService.class);

  
    String resourceFolder;

    String model              ;
    String lemmatizerModel    ;
     String PosModel    ;
    String language           ;
    String multiwords         ;
    String dictag             ;
    
    String noseg              ;

    private Properties annotateProperties;

    private Annotate posAnnotator;


    public Execution(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }
    
     public Execution() {
     
         
         
    }
    
    
    /*
    String kafVersion         ;
    String normalize          ;
    String untokenizable      ;
    String hardParagraph      ;
    */

    public void setup() {

        model              = Paths.get(resourceFolder,"morph-models-1.5.0/es/es-pos-perceptron-autodict01-ancora-2.0.bin").toFile().getAbsolutePath();
        lemmatizerModel    = Paths.get(resourceFolder,"morph-models-1.5.0/es/es-lemma-perceptron-ancora-2.0.bin").toFile().getAbsolutePath();
        language           = "es";
        multiwords         = "false"; //true
        dictag             = Paths.get(resourceFolder,"tag").toFile().getAbsolutePath();
        kafVersion         = "1.5.0";
        normalize          = "true";
        untokenizable      = "false"; // false
        hardParagraph      = "false";
        noseg              = "false";


        this.annotateProperties = new Properties();

        annotateProperties.setProperty("normalize", normalize);
        annotateProperties.setProperty("untokenizable", untokenizable);
        annotateProperties.setProperty("hardParagraph", hardParagraph);
        annotateProperties.setProperty("noseg",noseg);
        annotateProperties.setProperty("model", model);
        annotateProperties.setProperty("lemmatizerModel", lemmatizerModel);
        annotateProperties.setProperty("language", language);
        annotateProperties.setProperty("multiwords", multiwords);
        annotateProperties.setProperty("dictTag", dictag);
        annotateProperties.setProperty("dictPath", dictag);
        annotateProperties.setProperty("ruleBasedOption", dictag);

        try {
            this.posAnnotator    = new Annotate(annotateProperties);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing ixa service",e);
        }
    }

    
    /*
 
    public String process(String text, List<PoS> filter, Form form)  {

        return analyze(text,filter).stream()
                    .map(term-> {
                        switch (form){
                            case LEMMA: return Strings.isNullOrEmpty(term.getLemma())? term.getStr() : term.getLemma().toLowerCase();
                            default: return term.getStr().toLowerCase();
                        }
                    })
                    .collect(Collectors.joining(" "));
    }


    public List<Annotation> annotate(String text, List<PoS> filter) throws AvroRemoteException {
        List<Term> terms = analyze(text, filter);
        return terms.stream()
                .map(term -> {

                    Annotation annotation = new Annotation();
                    annotation.setTarget(term.getStr());

                    annotation.setTermcase(!Strings.isNullOrEmpty(term.getCase())?term.getCase():"");
                    annotation.setLemma(!Strings.isNullOrEmpty(term.getLemma())?term.getLemma():"");
                    annotation.setForm(!Strings.isNullOrEmpty(term.getForm())?term.getForm():"");
                    annotation.setMorphoFeat(!Strings.isNullOrEmpty(term.getMorphofeat())?term.getMorphofeat():"");
                    annotation.setSentiment("");
                    annotation.setForm(!Strings.isNullOrEmpty(term.getForm())?term.getForm():"");
                    annotation.setType(!Strings.isNullOrEmpty(term.getType())?term.getType():"");
                    annotation.setPos(!Strings.isNullOrEmpty(term.getPos())?PoSTranslator.toPoSTag(term.getPos()).name():"");
                    annotation.setPara("");
                    annotation.setOffset("");

                    return annotation;
                })
                .collect(Collectors.toList());
    }
    
    */

    private List<Term> analyze(String text){
        List<Term> terms = Collections.emptyList();

        final KAFDocument kaf;
        try {

            InputStream is = new ByteArrayInputStream(text.getBytes());

            BufferedReader breader = new BufferedReader(new InputStreamReader(is));

            kaf = new KAFDocument(language, kafVersion);

//            annotateProperties.setProperty("resourcesDirectory","src/main/resources");


            final String version        = CLI.class.getPackage().getImplementationVersion();
            final String commit         = CLI.class.getPackage().getSpecificationVersion();

            final eus.ixa.ixa.pipe.tok.Annotate tokAnnotator = new eus.ixa.ixa.pipe.tok.Annotate(breader, annotateProperties);

            // Tokenization
            tokAnnotator.tokenizeToKAF(kaf);


            // PosTagging

            final KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor("terms", "ixa-pipe-pos-" + Files.getNameWithoutExtension(model), version + "-" + commit);
            
            
            newLp.setBeginTimestamp();
            posAnnotator.annotatePOSToKAF(kaf);
            newLp.setEndTimestamp();

            // Debug
            //            kaf.getAnnotations(KAFDocument.AnnotationType.TERM).stream().map( annotation -> (Term) annotation).forEach(term -> System.out.println(term.getStr() + "\t " + term.getLemma() +"\t " + term.getPos()));
            //System.out.println(posAnnotator.annotatePOSToCoNLL(kaf));

            //            // Named-Entity Annotator

            
             KAFDocument.LinguisticProcessor newLp2 = kaf.addLinguisticProcessor( "entities", "ixa-pipe-nerc-" + Files.getNameWithoutExtension(model2),    version + "-" + commit);
             newLp2.setBeginTimestamp();
            neAnnotator.annotateNEs(kaf);
            newLp2.setEndTimestamp();

            for (Entity ent : kaf.getEntities()) {

                System.out.println(ent.getStr() + "\t" + ent.getType());

            }

            /*
            for(KAFDocument.AnnotationType aType : KAFDocument.AnnotationType.values()){
               List<Annotation> out = kaf.getAnnotations(aType);
                System.out.println("Annotations '" + aType.name()+"' found: " + out.size());
            }
            
            for(KAFDocument.AnnotationType aType : KAFDocument.AnnotationType.values()){
               List<Annotation> out = kaf.getAnnotations(aType);
                System.out.println("Annotations '" + aType.name()+"' found: " + out.size());
                for(Annotation anot:  out){System.out.println(anot);}
            }
            */


            /*
            // Filtering
            List<String> postags = filter.stream().flatMap( type -> PoSTranslator.toTermPoS(type).stream()).collect(Collectors.toList());

            terms = kaf.getAnnotations(KAFDocument.AnnotationType.TERM).stream()
                    .map(annotation -> (Term) annotation)
                    .filter(term -> postags.isEmpty() || postags.contains(term.getPos()))
                    .collect(Collectors.toList());
            */
            
            breader.close();
            
            kaf.save("hellooo.txt");
        } catch (IOException e) {
            System.out.println("faallososos");
            // LOG.error("Error analyzing text", e);
        }

        return terms;
    }
    
    
    private Properties annotateProperties2;
    private String model2;
    
    eus.ixa.ixa.pipe.nerc.Annotate neAnnotator;
    
    
    
    public void setup2(){
    
        model2 = Paths.get(resourceFolder, "morph-models-1.5.0/es/es-4-class-clusters-dictlbj-ancora.bin").toFile().getAbsolutePath();
        language = "es";
        dictag = Paths.get(resourceFolder, "tag").toFile().getAbsolutePath();

        annotateProperties2 = new Properties();
        annotateProperties2.setProperty("model", model2);
        annotateProperties2.setProperty("clearFeatures", "no");
        annotateProperties2.setProperty("language", language);
        annotateProperties2.setProperty("dictTag", "off");
        annotateProperties2.setProperty("dictPath", "off");
        annotateProperties2.setProperty("lexer", "off");

         Properties annotateProperties = new Properties();
        annotateProperties.setProperty("model", model2);
        annotateProperties.setProperty("language", language);
        annotateProperties.setProperty("ruleBasedOption", Flags.DEFAULT_LEXER);
        annotateProperties.setProperty("dictTag", Flags.DEFAULT_DICT_OPTION);
        annotateProperties.setProperty("dictPath", Flags.DEFAULT_DICT_PATH);
        annotateProperties.setProperty("clearFeatures", Flags.DEFAULT_FEATURE_FLAG);
        
        
        // annotateProperties2.setProperty("clearFeatures", "no");
        // annotateProperties2.setProperty("language", language);
        //annotateProperties2.setProperty("dictTag", "off");
        // annotateProperties2.setProperty("dictPath", "off");
        
        
       try {
            this.neAnnotator    = new eus.ixa.ixa.pipe.nerc.Annotate(annotateProperties);
        } catch (IOException e) {
            throw new RuntimeException("Error init",e);
        }

        //  annotateParser.addArgument("-m", "--model").required(true)        .help("Pass the model to do the tagging as a parameter.\n");
        //  annotateParser.addArgument("--clearFeatures").required(false)     .choices("yes", "no", "docstart").   ///setDefault(Flags.DEFAULT_FEATURE_FLAG) "Reset the adaptive features every sentence; defaults to 'no'; if -DOCSTART- marks"
        //  annotateParser.addArgument("-l", "--language").required(false)   .choices("ca", "de", "en", "es", "eu", "fr", "gl", "it", "nl", "pt",  "ru")        .help(       "Choose language; it defaults to the language value in incoming NAF file.\n");
        //NOT annotateParser.addArgument("-o", "--outputFormat").required(false)      .choices("conll03", "conll02", "naf")     .setDefault(Flags.DEFAULT_OUTPUT_FORMAT)   .help("Choose output format; it defaults to NAF.\n");
        // NOT  annotateParser.addArgument("--lexer").choices("numeric")    .setDefault(Flags.DEFAULT_LEXER).required(false)  .help("Use lexer rules for NERC tagging; it defaults to false.\n");
        /* annotateParser.addArgument("--dictTag").required(false)
        .choices("tag", "post").setDefault(Flags.DEFAULT_DICT_OPTION).help(
            "Choose to directly tag entities by dictionary look-up; if the 'tag' option is chosen, "
                + "only tags entities found in the dictionary; if 'post' option is chosen, it will "
                + "post-process the results of the statistical model.\n");

        annotateParser.addArgument("--dictPath").required(false)
        .setDefault(Flags.DEFAULT_DICT_PATH).help(
            "Provide the path to the dictionaries for direct dictionary tagging; it ONLY WORKS if --dictTag "
                + "option is activated.\n");
    
         */
    }
    
    
    public static void main (String [] args) throws IOException{
    
       Execution exec= new Execution("src/main/bin");
       //KAFDocument doc=  exec.tokenize("Hola Mundo moderfockers");
       //doc.save("hellooo.txt");
       
       exec.setup();
       
       exec.setup2();
       
       
       exec.analyze("Mi amigo Juan trabaja de abogado por los alrededores de España");
       
       
    
    }


}
