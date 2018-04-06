/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.ixapipes.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.google.common.io.Files;

//import eus.ixa.ixa.pipe.parse.ConstituentParsing;
//import eus.ixa.ixa.pipe.parse.Flags;
import eus.ixa.ixa.pipe.seg.RuleBasedSegmenter;
import ixa.kaflib.Annotation;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.KAFDocument.AnnotationType;
import ixa.kaflib.NonTerminal;
import ixa.kaflib.Statement;
import ixa.kaflib.Term;
import ixa.kaflib.Terminal;
import ixa.kaflib.Tree;
import ixa.kaflib.TreeNode;
import ixa.kaflib.WF;
import java.util.HashSet;
import java.util.Set;
import opennlp.tools.parser.Parse;
/**
 *
 * @author pcalleja
 */
public class IxaPipeHandler {
    /*
    
  OpenNLPAnswerHandler openNLPAnswerHandler;

    // tokenizer properties
    private final String tokenizerVersion = eus.ixa.ixa.pipe.tok.CLI.class.getPackage().getImplementationVersion();
    private final String tokenizerCommit = eus.ixa.ixa.pipe.tok.CLI.class.getPackage().getSpecificationVersion();
    private String lang = "es"; //It is REQUIRED to choose a language to perform annotation with ixa-pipe-tok. Default español, pero también puede ser ingles
    final String normalize = "default"; //Set normalization method according to corpus; the default option does not escape "brackets or forward slashes.
    final String untokenizable = "no"; //Print untokenizable characters.
    final String hardParagraph = "no"; //Do not segment paragraphs. Ever. 
    final String kafVersion = "v1.naf";

    //POS tagger properties
    private final String posTaggerVersion = eus.ixa.ixa.pipe.pos.CLI.class.getPackage().getImplementationVersion();
    private final String posTaggerCommit = eus.ixa.ixa.pipe.pos.CLI.class.getPackage().getSpecificationVersion();
    String posmodel;
    String lemmatizermodel;
    final String beamSize = "3";
    final String multiwords = "False";
    final String dictag = "False";
    eus.ixa.ixa.pipe.pos.Annotate annotatorPos;

    // Parse properties
    private final String parseVersion = eus.ixa.ixa.pipe.parse.CLI.class.getPackage().getImplementationVersion();
    private final String parseCommit = eus.ixa.ixa.pipe.parse.CLI.class.getPackage().getSpecificationVersion();
    String parsemodel;
    final String headFinderOption = Flags.DEFAULT_HEADFINDER;
    ConstituentParsing parserConstituent;

    //SentimentLexiconHandler
    SentimentLexiconHandler sentiLexHandler;

    public IxaPipeHandler() throws Exception {
        logger.info("Inicializando IxaPipes...");
        openNLPAnswerHandler = new OpenNLPAnswerHandler(Core.properties);
        posmodel = Core.properties.getPosmodel();
        lemmatizermodel = Core.properties.getLemmatizermodel();
        parsemodel = Core.properties.getParsemodel();

        lang = Core.properties.getLanguage();

        //Initializing pos annotator                 
        final Properties propertiesPos = setPosTaggerProperties(posmodel, lang, beamSize, multiwords, dictag, lemmatizermodel);
        annotatorPos = new eus.ixa.ixa.pipe.pos.Annotate(propertiesPos);

        //initializing parserConstituent
        final Properties propertiesParse = setParseProperties(parsemodel, lang, headFinderOption);
        //OpenNLP parserConstituent
        parserConstituent = new ConstituentParsing(propertiesParse);
//		   System.err.close(); //to avoid ixa-pipe tokenizer annoying messages

        //Sentilexicon initializer
        if (Core.properties.isUseSentiLexicons()) {
            sentiLexHandler = new SentimentLexiconHandler(Core.properties);
            sentiLexHandler.loadLexicons();
        }
        logger.info("Inicializado IxaPipes.");

    }

    public static void f() {
        RuleBasedSegmenter segmenter;
    }

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

    public final KAFDocument parseToKaf(KAFDocument inputKaf) throws IOException {
        String lang = inputKaf.getLang();
        final Properties properties = setParseProperties(parsemodel, lang, headFinderOption);
        final KAFDocument.LinguisticProcessor newLp = inputKaf.addLinguisticProcessor(
                "constituency",
                "ixa-pipe-parse-" + Files.getNameWithoutExtension(parsemodel), this.parseVersion
                + "-" + this.parseCommit);
        newLp.setBeginTimestamp();
        final eus.ixa.ixa.pipe.parse.Annotate annotator = new eus.ixa.ixa.pipe.parse.Annotate(properties);

        annotator.parseToKAF(inputKaf);
        //String kafToString = annotator.parseToOneline(inputKaf);
        newLp.setEndTimestamp();
//		   System.out.println("pos:"+inputKaf.toString());
        //System.out.println("pos:"+kafToString);
        return inputKaf;
    }

    public final List<Feature> parse(KAFDocument inputKaf) throws Exception {

        List<Feature> features = new ArrayList<Feature>();
        final List<List<WF>> sentences = inputKaf.getSentences();
        for (final List<WF> sentence : sentences) {
            // get array of token forms from a list of WF objects
            final String[] tokens = new String[sentence.size()];
            for (int i = 0; i < sentence.size(); i++) {
                tokens[i] = sentence.get(i).getForm();
            }
            // Constituent Parsing: OpenNLP
            final String sent = getSentenceFromTokens(tokens);
            final Parse[] parsedSentence = parserConstituent.parse(sent, 1);

            features.addAll(getFeaturesFromChunks(parsedSentence));
        }

//		   newLp.setEndTimestamp();
        //System.out.println("pos:"+inputKaf.toString());
        //System.out.println("features:"+features);
        return features;
    }

    private String getSentenceFromTokens(final String[] tokens) {
        final StringBuilder sb = new StringBuilder();
        for (final String token : tokens) {
            sb.append(token).append(" ");
        }
        final String sentence = sb.toString();
        return sentence;
    }

    private List<Feature> getFeaturesFromChunks(Parse[] parsedSentence) {
        List<Feature> features = new ArrayList<Feature>();
        List<Chunk> chunks = new ArrayList<Chunk>();
        boolean startedChunk = false;
        Chunk chunk = null;
        for (final Parse parsedSent : parsedSentence) {

            openNLPAnswerHandler.getChunksFromTree(parsedSent, chunks, chunk, startedChunk);

            features.addAll(openNLPAnswerHandler.convertChunksToFeatureList(chunks));
        }
        return features;
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

    private Properties setPosTaggerProperties(final String model, final String language, final String beamSize, final String multiwords, final String dictag, final String lemmatizermodel) {
        final Properties annotateProperties = new Properties();
        annotateProperties.setProperty("model", model);
        annotateProperties.setProperty("language", language);
        annotateProperties.setProperty("beamSize", beamSize);
        annotateProperties.setProperty("multiwords", multiwords);
        annotateProperties.setProperty("dictag", dictag);
        annotateProperties.setProperty("lemmatizerModel", lemmatizermodel);

        return annotateProperties;
    }

    private Properties setParseProperties(final String model, final String language, final String headFinder) {
        final Properties annotateProperties = new Properties();
        annotateProperties.setProperty("model", model);
        annotateProperties.setProperty("language", language);
        annotateProperties.setProperty("headFinder", headFinder);
        return annotateProperties;
    }

    public static int recursivo(TreeNode root, int nivel) {
        if (!root.isTerminal()) {
            NonTerminal nt = (NonTerminal) root;
            for (int i = 0; i < nivel; i++) {
                System.out.print("-");
            }
            System.out.println("" + nt.getId() + " " + nt.getLabel());
            List<TreeNode> children7 = root.getChildren();
            for (TreeNode child7 : children7) {
                recursivo(child7, nivel + 1);
            }
        } else {
            Terminal term = (Terminal) root;
            for (int i = 0; i < nivel; i++) {
                System.out.print("-");
            }
            System.out.println(term.getId() + " " + term.getStr());
            //    return;
        }
        return nivel;
    }

    //Obtiene las palabras de un texto
     
    public List<String> getGN(String text) {
        List<String> palabras = new ArrayList();
        try {
            KAFDocument kaf = tokenize(text);
            kaf = posTag(kaf);
            kaf = parseToKaf(kaf);

            tag(text);

            List<Annotation> ano = kaf.getAnnotations(AnnotationType.TREE);
            for (Annotation a : ano) {
                WF wf = (WF) a;
                palabras.add(wf.getForm());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return palabras;
    }

    //     * Obtiene las palabras de un texto
     
    public List<String> getPalabras(String text) {
        List<String> palabras = new ArrayList();
        try {
            KAFDocument kaf = tokenize(text);
            List<Annotation> ano = kaf.getAnnotations(AnnotationType.WF);
            for (Annotation a : ano) {
                WF wf = (WF) a;
                palabras.add(wf.getForm());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return palabras;
    }

    /*** Obtiene los lemmas de un texto
    
    public List<String> getLemmas(String text) {
        List<String> palabras = new ArrayList();
        try {
            KAFDocument kaf = tokenize(text);
            kaf = posTag(kaf);
//            kaf = parseToKaf(kaf);
            List<Annotation> ano = kaf.getAnnotations(AnnotationType.TERM);
            for (Annotation a : ano) {
                Term term = (Term) a;
                palabras.add(term.getLemma());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return palabras;
    }

    */
    /**
     * Part of speech (optional). Possible values are: - common noun (N) -
     * proper noun (R) - adjective (G) - verb (V) - preposition (P) - adverb (A)
     * - conjunction (C) - determiner (D) - other (O)
     *
     */
    /*
    public String postag(String text) throws Exception {
        String pos = "";
        KAFDocument kaf = tokenize(text);
        kaf = posTag(kaf);
        List<Annotation> a3 = kaf.getAnnotations(AnnotationType.TERM);
        for (Annotation a : a3) {
            Term term = (Term) a;
            Term head = term.getHead();
//            WF wf = term.getHeadWF();
            pos += term.getStr() + " " + term.getLemma() + " " + term.getPos() + " " + term.getMorphofeat() + "\n";
//            System.out.println(term.getId() + " " + term.getStr() + " " + term.getLemma() + " " + term.getPos() + " " + term.getMorphofeat() + " " + term.getType() + " " + term.getCase() + " " + term.getCompound());
        }

        return pos;
    }

    public String postagLikeFreeling(String text) throws Exception {
        String pos = " ";
        KAFDocument kaf = tokenize(text);
        kaf = posTag(kaf);
        List<Annotation> a3 = kaf.getAnnotations(AnnotationType.TERM);
        for (Annotation a : a3) {
            Term term = (Term) a;
            Term head = term.getHead();
//            WF wf = term.getHeadWF();
            pos += term.getLemma().toUpperCase() + "#" + term.getMorphofeat() + " ";
//            System.out.println(term.getId() + " " + term.getStr() + " " + term.getLemma() + " " + term.getPos() + " " + term.getMorphofeat() + " " + term.getType() + " " + term.getCase() + " " + term.getCompound());
        }

        return pos;
    }
    
    
    public String tag(String text) throws Exception {

        String pos = "";

        KAFDocument kaf = tokenize(text);
        kaf = posTag(kaf);
        kaf = parseToKaf(kaf);
      

        List<Annotation> a1 = kaf.getAnnotations(AnnotationType.TREE);

        List<Annotation> a2 = kaf.getAnnotations(AnnotationType.WF);

        List<Annotation> a3 = kaf.getAnnotations(AnnotationType.TERM);
        for (Annotation a : a3) {
            Term term = (Term) a;
            Term head = term.getHead();
            WF wf = term.getHeadWF();
            System.out.println(term.getId() + " " + term.getStr() + " " + term.getLemma() + " " + term.getPos() + " " + term.getMorphofeat() + " " + term.getType() + " " + term.getCase() + " " + term.getCompound());
        }

        List<Tree> constituents = kaf.getConstituents();
        for (Tree arbol : constituents) {
            String id = arbol.getGroupID();
            String tipo = arbol.getType();
            String k = arbol.toString();
            System.out.println(id + " " + tipo + " " + k);
            TreeNode root = arbol.getRoot();

            recursivo(root, 0);
//            System.exit(0);

            if (!root.isTerminal()) {
                NonTerminal nt = (NonTerminal) root;
                String label = nt.getLabel();
                System.out.println("-     " + nt.getId() + " " + label);
            }
            List<TreeNode> children = root.getChildren();
            for (TreeNode child : children) {
                String edge = child.getEdgeId();
                String id2 = child.getId();
                if (!child.isTerminal()) {
                    List<TreeNode> children2 = child.getChildren();
                    for (TreeNode child2 : children2) {
                        String id3 = child2.getId();
                        if (!child2.isTerminal()) {
                            NonTerminal nt = (NonTerminal) child2;
                            System.out.println("--    " + nt.getId() + " " + nt.getLabel());
                            List<TreeNode> children3 = nt.getChildren();
                            for (TreeNode child3 : children3) {
                                if (!child3.isTerminal()) {
                                    NonTerminal nt2 = (NonTerminal) child3;
                                    System.out.println("---   " + nt2.getId() + " " + nt2.getLabel());
                                    List<TreeNode> children4 = nt2.getChildren();
                                    for (TreeNode child4 : children4) {
                                        if (!child4.isTerminal()) {
                                            NonTerminal n7 = (NonTerminal) child4;
                                            System.out.println("----  " + n7.getId() + " " + n7.getLabel());
                                            List<TreeNode> tn7 = n7.getChildren();
                                            for (TreeNode child5 : tn7) {
                                                if (!child5.isTerminal()) {
                                                    NonTerminal tn9 = (NonTerminal) child5;
                                                    System.out.println("----- " + tn9.getId() + " " + tn9.getLabel());
                                                    List<TreeNode> children10 = tn9.getChildren();
                                                    for (TreeNode child6 : children10) {
                                                        if (!child6.isTerminal()) {
                                                            NonTerminal ntx = (NonTerminal) child6;
                                                            System.out.println("------" + ntx.getId() + " " + ntx.getLabel());
                                                            List<TreeNode> children7 = child6.getChildren();
                                                            for (TreeNode child7 : children7) {
                                                                if (!child7.isTerminal()) {
                                                                    NonTerminal nty = (NonTerminal) child7;
                                                                    System.out.println("------" + ntx.getId() + " " + ntx.getLabel());
                                                                } else {
                                                                    Terminal term = (Terminal) child7;
                                                                    System.out.println(term.getId() + " " + term.getStr());

                                                                }

                                                            }
                                                        } else {
                                                            Terminal term = (Terminal) child6;
                                                            System.out.println(term.getId() + " " + term.getStr());
                                                        }
                                                    }
                                                } else {
                                                    Terminal term = (Terminal) child5;
                                                    System.out.println(term.getId() + " " + term.getStr());
                                                }
                                            }
                                        } else {
                                            Terminal term = (Terminal) child4;
                                            System.out.println(term.getId() + " " + term.getStr());
                                        }
                                    }

                                } else {
                                    Terminal term = (Terminal) child3;
                                    System.out.println(term.getId() + " " + term.getStr());

                                }
                            }

                        } else {
                            Terminal term = (Terminal) child2;
                            System.out.println(term.getId() + " " + term.getStr());
                        }
                    }
                } else {
                    Terminal term = (Terminal) child;
                    System.out.println(term.getId() + " " + term.getStr());
                }
            }
        }
        List<Statement> statements = kaf.getStatements();
        final List<List<WF>> sentences = kaf.getSentences();

        for (List<WF> sentence : sentences) {

            // get array of token forms from a list of WF objects
            final String[] tokens = new String[sentence.size()];
            for (int i = 0; i < sentence.size(); i++) {
                WF fragmento = sentence.get(i);
                String idu = fragmento.getId();
                //   System.out.println("IDIDID: " + idu);
                tokens[i] = fragmento.getForm();
                //    System.out.println(tokens[i]);
            }
            // Constituent Parsing: OpenNLP
            final String sent = getSentenceFromTokens(tokens);
            final Parse[] parsedSentence = parserConstituent.parse(sent, 1);
            for (Parse p : parsedSentence) {
                Parse[] parseList = p.getChildren();
                for (Parse pp : parseList) {
                    System.out.println("Hijo: " + pp);
                    Parse[] hijos = pp.getChildren();
                    for (Parse hijo : hijos) {
                        System.out.println("Nieto: " + hijo);
                        Parse[] nietos = hijo.getChildren();
                        for (Parse nieto : nietos) {
                            System.out.println("BisNieto: " + nieto);
                        }

                    }

                    pp.show();
                    pp.showCodeTree();
                    //       System.out.println(pp);
                }
            }

        }

        return pos;
    }

    public List<String> getConstituents(String texto) {
        List<String> con = new ArrayList();
        String res1 = "";
        String res2 = "";
        String res3 = "";
        String res4 = "";
        String res5 = "";
        String res6 = "";
        Parse[] parses = parserConstituent.parse(texto, 3);
        for (Parse parse : parses) {
            String s1 = parse.getType();
            String s2 = parse.getCoveredText();
            if (s1.equals("NEG"))
                res1+=s2+" ";
            if (s1.equals("GRUP.VERB"))
                res1+=s2+" ";
            
//            System.out.println(s1 + " " + s2);
            Parse[] parses2 = parse.getChildren();
            for (Parse parse2 : parses2) {
                s1 = parse2.getType();
                s2 = parse2.getCoveredText();
                if (s1.equals("NEG"))
                    res2+=s2+" ";
                if (s1.equals("GRUP.VERB"))
                    res2+=s2+" ";
 //               System.out.println("-- " + s1 + " " + s2);
                Parse[] parses3 = parse2.getChildren();
                for (Parse parse3 : parses3) {
                    s1 = parse3.getType();
                    s2 = parse3.getCoveredText();
                    if (s1.equals("NEG"))
                        res3+=s2+" ";
                    if (s1.equals("GRUP.VERB"))
                        res3+=s2+" ";
                    
   //                 System.out.println("---- " + s1 + " " + s2);
                    Parse[] parses4 = parse3.getChildren();
                    for (Parse parse4 : parses4) {
                        s1 = parse4.getType();
                        s2 = parse4.getCoveredText();
                        if (s1.equals("NEG"))
                            res4+=s2+" ";
                        if (s1.equals("GRUP.VERB"))
                            res4+=s2+" ";
     //                   System.out.println("------ " + s1 + " " + s2);
                        Parse[] parses5 = parse4.getChildren();
                        for (Parse parse5 : parses5) {
                            s1 = parse5.getType();
                            s2 = parse5.getCoveredText();
                            if (s1.equals("NEG"))
                                res5+=s2+" ";
                            if (s1.equals("GRUP.VERB"))
                                res5+=s2+" ";
                            
    //                        System.out.println("-------- " + s1 + " " + s2);
                            Parse[] parses6 = parse5.getChildren();
                            for (Parse parse6 : parses6) {
                                s1 = parse6.getType();
                                s2 = parse6.getCoveredText();
                                if (s1.equals("NEG"))
                                    res6+=s2+" ";
                                if (s1.equals("GRUP.VERB"))
                                    res6+=s2+" ";
    //                            System.out.println("---------- " + s1 + " " + s2);
                            }
                        }
                    }
                }
            }
             break; //solo queremos un analisis
        }
        res1=res1.trim();
        if(!res1.isEmpty())
            con.add(res1);
        res2=res2.trim();
        if(!res2.isEmpty())
            con.add(res2);
        res3=res3.trim();
        if(!res3.isEmpty())
            con.add(res3);
        res4=res4.trim();
        if(!res4.isEmpty())
            con.add(res4);
        res5=res5.trim();
        if(!res5.isEmpty())
            con.add(res5);
        res6=res6.trim();
        if(!res6.isEmpty())
            con.add(res6);
        return con;
    }

    //     * Recibe una cadena de texto ("como me gusta el pam bimbo") y genera una
    // lista de características (Feature).
     
    public List<Feature> processText(String text) throws Exception {
        KAFDocument kaf = tokenize(text);
        posTag(kaf);

        //1. CARACTERISTICAS BÁSICAS: PALABRAS Y LEMAS
        List<Feature> features = getBasicFeatures(kaf);

        //2. CHUNKS
        if (Core.properties.isUseChunks()) {
            features.addAll(parse(kaf));
        }

        //3. TIEMPOS VERBALES
        if (Core.properties.isUseTenses()) {
            Set<String> tiempos = getTiempos(kaf);
            for (String tiempo : tiempos) {
                Feature f = new Feature(tiempo, "tiempo", tiempo);
                features.add(f);
            }
        }

        return features;
    }

    //https://talp-upc.gitbooks.io/freeling-user-manual/content/tagsets/tagset-es.html
    private Set<String> getTiempos(KAFDocument kaf) {
        Set<String> set = new HashSet();
        for (Term term : kaf.getTerms()) {
            String s = term.getMorphofeat();
            if (s.startsWith("V")) {
                if (s.contains("VMIP")) {
                    set.add("@@presente");
                }
                if (s.contains("VMIF")) {
                    set.add("@@futuro");
                }
                if (s.contains("VMIS")) {
                    set.add("@@pasado");
                }
                if (s.contains("VMIC")) {
                    set.add("@@condicional");
                }
                if (s.contains("VMII")) {
                    set.add("@@imperfecto");
                }
            }
        }
        return set;

    }

    private List<Feature> getBasicFeatures(KAFDocument kaf) {
        List<Feature> features = new ArrayList<Feature>();
        Feature feat;
        for (Term term : kaf.getTerms()) {
            if (!isStopword(term.getForm(), term.getMorphofeat())) {
                if (!Core.properties.isUseLemmasAsFeatures()) {
                    feat = new Feature(term.getForm(), Constants.TokenType, term.getMorphofeat());
                    features.add(feat);
                } else {
                    feat = new Feature(term.getLemma(), Constants.TokenType, term.getMorphofeat());
                    features.add(feat);
                }

                if (Core.properties.isUseSentiLexicons()) {
                    features.addAll(getSentiFeatures(term));
                }
            }
        }
        return features;
    }

    private List<Feature> getSentiFeatures(Term term) {
        List<Feature> sentiFeatures = new ArrayList<Feature>();
        Feature feat;
        for (Map.Entry<String, SentimentLexicon> entry : sentiLexHandler.lexicons.entrySet()) {
            String lexiconLabel = entry.getKey();
            SentimentLexicon sentiLex = entry.getValue();
            List<String> annotations = sentiLex.getAnnotations(term.getLemma());
            if (annotations != null) {
                for (String annotation : annotations) {
                    feat = new Feature("@@" + lexiconLabel + annotation, Constants.SentiLexType, term.getMorphofeat());
                    sentiFeatures.add(feat);
                }
            }
        }

        return sentiFeatures;
    }

    private boolean isStopword(String termText, String tag) {
        if (tag.matches("^D[IDPTEA].*")) //Determinantes
        {
            return true;
        } else if (tag.startsWith("P")) //Pronombres
        {
            return true;
        } else if (tag.startsWith("I")) //Interjecciones
        {
            return true;
        } else if (tag.startsWith("S")) //Preposiciones
        {
            return true;
        } else if (tag.startsWith("F")) //Signos de puntuación			
        {
            return true;
        } else if (tag.startsWith("C")) //Conjunciones			
        {
            return true;
        } else if (tag.startsWith("NP")) //Noun Proper			
        {
            return true;
        } else if (termText.startsWith("@")) //Numerales
        {
            return true;
        } else if (termText.startsWith("http://")) //Numerales
        {
            return true;
        }
//			else if (termText.toLowerCase().contains("carrefour")||termText.toLowerCase().contains("mercadona")
//					||termText.toLowerCase().contains("ikea")||termText.toLowerCase().contains("corte")) //Numerales
//				return true;
//			else if (tag.startsWith("W")) //Fechas y Horas
//				return true;
        return false;
    }

    public static void main(final String[] args) throws Exception {
        Core.init();
        Core.initLoggerDebug();
        Core.initDefaultConfig();
        Core.properties.setLanguage("es");
        final IxaPipeHandler ixapipe = new IxaPipeHandler();
        String txt = "";
        String mensaje = "Yo estoy comiendo las manzanas de mi abuela";
//        String mensaje = "Vosotros os disparáis si queréis y yo bebo Cruzcampo hasta que se me derritan las entrañas.";
//        String mensaje = "Entrar a una \"Cervecería\" y que te pongan una Cruzcampo. Ahora les pago Yo con dinero del Monopoly, ¿no?";
//	    List<Feature> features=ixapipe.processText("Perfectas zapatillas para profesionales del fútbol");
//        List<Feature> features = ixapipe.processText("Just a perfect day drink sangria in the park and then later, when it gets dark we go home");
        txt = ixapipe.tag(mensaje);

        System.out.println(txt);
    }
*/
}
