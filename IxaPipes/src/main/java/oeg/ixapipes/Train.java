/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.ixapipes;


import java.io.IOException;
import java.io.PrintWriter;
import org.jdom2.JDOMException;
/**
 *
 * @author pcalleja
 */
public class Train {
    
  
    
    
    public static void main (String [] args) throws IOException, JDOMException{
    
        
        String [] myargs={"train","-p","resources/param/myprop.properties"};
     
        eus.ixa.ixa.pipe.nerc.CLI.main(myargs);
        
               
       
    
    }
    
    
      public void createPropertiesFile(String PropertiesFile, String CorpusTrain, String CorpusTest, String OutputModel) throws Exception{

        String res = "# Licensed to the Apache Software Foundation (ASF) under one or more\n"
                + "# contributor license agreements.  See the NOTICE file distributed with\n"
                + "# this work for additional information regarding copyright ownership.\n"
                + "# The ASF licenses this file to You under the Apache License, Version 2.0\n"
                + "# (the \"License\"); you may not use this file except in compliance with\n"
                + "# the License. You may obtain a copy of the License at\n"
                + "#\n"
                + "#     http://www.apache.org/licenses/LICENSE-2.0\n"
                + "#\n"
                + "# Unless required by applicable law or agreed to in writing, software\n"
                + "# distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                + "# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                + "# See the License for the specific language governing permissions and\n"
                + "# limitations under the License.\n"
                + "\n"
                + "# Sample machine learning properties file\n"
                + "\n"
                + "Algorithm=PERCEPTRON\n"
                + "Iterations=500\n"
                + "Cutoff=0\n"
                + "\n"
                + "##################################################\n"
                + "#### Custom parameters added by ixa-pipe-nerc ####\n"
                + "##################################################\n"
                + "\n"
                + "# Languages supported: de, en, es, eu, it, nl\n"
                + "Language=es\n"
                + "\n"
                + "# ClearAdaptiveFeatures: ONLY WORKS with CONLL formats!!\n"
                + "# Specify if adaptive features\n"
                + "# are to be cleared when no -DOCSTART- mark is available\n"
                + "# in the training and/or evaluation data. If commented out\n"
                + "# both values default to 'no'\n"
                + "ClearTrainingFeatures=no\n"
                + "ClearEvaluationFeatures=no\n"
                + "\n"
                + "# TrainingCorpus:\n"
                + "TrainSet=..\\\\corpus/esp.train\n"
                + "TestSet=..\\\\corpus/esp.testa\n"
                + "\n"
                + "# CorpusFormat: conll02, conll03, opennlp\n"
                + "CorpusFormat=conll02\n"
                + "\n"
                + "# OutputModel:\n"
                + "OutputModel=../models/mybin.bin\n"
                + "\n"
                + "# Named Entity types; if not active all ne types in the training corpus.\n"
                + "# Otherwise, separate with comma, eg., location,organization,person,misc\n"
                + "#Types=ORG,LOC,PER\n"
                + "\n"
                + "# Beamsize 1 amounts to greedy search\n"
                + "BeamSize=3\n"
                + "\n"
                + "#SequenceCodec=BIO\n"
                + "\n"
                + "##############\n"
                + "## FEATURES ##\n"
                + "##############\n"
                + "\n"
                + "# Window: left and right window range from the current token. TokenFeatures\n"
                + "# and TokenClassFeatures depend on the window range specified here.\n"
                + "Window=2:2\n"
                + "\n"
                + "# TokenFeatures: include current token (both in original and lowercase form)\n"
                + "TokenFeatures=yes\n"
                + "\n"
                + "# TokenClassFeatures: include token shape features (capitalization, digits,\n"
                + "# etc. see FastTokenClassFeatureGenerator class in ixa.pipe.nerc.train.features\n"
                + "# for details\n"
                + "TokenClassFeatures=yes\n"
                + "\n"
                + "# OutcomePriorFeatures: maps the underlying previous outcomes\n"
                + "OutcomePriorFeatures=yes\n"
                + "\n"
                + "# PreviousMapFeatures: takes into account previous decisions and adds them as\n"
                + "# features\n"
                + "PreviousMapFeatures=yes\n"
                + "\n"
                + "# SentenceFeatures: add first and last words of sentence as features.\n"
                + "SentenceFeatures=yes\n"
                + "\n"
                + "# PrefixFeatures: takes first 3rd and 4rd characters of current token as feature.\n"
                + "PrefixFeatures=yes\n"
                + "\n"
                + "# SuffixFeatures: takes last 4 characters of current token as feature.\n"
                + "SuffixFeatures=yes\n"
                + "\n"
                + "# BigramClassFeatures: adds bigram features based on tokens and their class\n"
                + "# features.\n"
                + "BigramClassFeatures=yes\n"
                + "\n"
                + "#DictionaryFeatures=/home/ragerri/resources/nerc-models/conll03/english/lbj\n"
                + "\n"
                + "# BrownClusterFeatures: add features using Brown clusters\n"
                + "# (http://metaoptimize.com/projects/wordreprs/). If yes, specify the location\n"
                + "# of the Brown clustering lexicon.\n"
                + "#BrownClusterFeatures=/tartalo01/users/ragerri/resources/clusters/periodico/brown/es-periodico-preclean.tok-c1000-p1.txt\n"
                + "\n"
                + "# ClarkClusterFeatures: add features using Clark (2003) clusters. If value is yes,\n"
                + "# specify the location of the clustering lexicon in Clark format.\n"
                + "#ClarkClusterFeatures=/tartalo01/users/ragerri/resources/clusters/wikipedia/es/clark/es-wiki-preclean.tok.punct.lower.400,/tartalo01/users/ragerri/resources/clusters/gigaword/spanish-3rd/clark/es-gigaword-preclean.tok.punct.lower.400\n"
                + "\n"
                + "# Word2VecClusterFeatures: add features using word2vec clusters. If value is\n"
                + "# yes, specify the location of the clustering lexicon in word2vec format.\n"
                + "#Word2VecClusterFeatures=/tartalo01/users/ragerri/resources/clusters/gigaword/spanish-3rd/word2vec/es-gigaword-s50-w5.400\n"
                + "\n"
                + "## END FEATURES ##";

        PrintWriter writer = new PrintWriter(PropertiesFile, "utf-8"); //"properties.prop"
        writer.append(res.trim());
        writer.close();

    }


}
