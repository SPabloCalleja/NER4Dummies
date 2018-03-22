/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.crfmallet;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

import cc.mallet.fst.*;
import cc.mallet.optimize.Optimizable;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.pipe.tsf.*;
import cc.mallet.types.*;
import cc.mallet.util.*;
/**
 *
 * @author pcalleja
 */
public class ExampleImplementation {
    
    
    public ExampleImplementation(String trainingFilename, String testingFilename) throws IOException {
		
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();

		int[][] conjunctions = new int[2][];
		conjunctions[0] = new int[] { -1 };
		conjunctions[1] = new int[] { 1 };

		pipes.add(new SimpleTaggerSentence2TokenSequence());
		pipes.add(new OffsetConjunctions(conjunctions));
		pipes.add(new TokenTextCharSuffix("C1=", 1));
		pipes.add(new TokenTextCharSuffix("C2=", 2));
		pipes.add(new TokenTextCharSuffix("C3=", 3));
		pipes.add(new RegexMatches("CAPITALIZED", Pattern.compile("\\p{Lu}.*")));
		pipes.add(new RegexMatches("STARTSNUMBER", Pattern.compile("[0-9].*")));
		pipes.add(new RegexMatches("HYPHENATED", Pattern.compile(".*\\-.*")));
		pipes.add(new RegexMatches("DOLLARSIGN", Pattern.compile(".*\\$.*")));
		pipes.add(new TokenFirstPosition("FIRSTTOKEN"));
		pipes.add(new TokenSequence2FeatureVectorSequence());

		Pipe pipe = new SerialPipes(pipes);

		InstanceList trainingInstances = new InstanceList(pipe);
		InstanceList testingInstances = new InstanceList(pipe);

		trainingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(trainingFilename)))), Pattern.compile("^\\s*$"), true));
		testingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(testingFilename)))), Pattern.compile("^\\s*$"), true));
		
		CRF crf = new CRF(pipe, null);
		//crf.addStatesForLabelsConnectedAsIn(trainingInstances);
		crf.addStatesForThreeQuarterLabelsConnectedAsIn(trainingInstances);
		crf.addStartState();

		CRFTrainerByLabelLikelihood trainer = new CRFTrainerByLabelLikelihood(crf);
		trainer.setGaussianPriorVariance(10.0);

		//CRFTrainerByStochasticGradient trainer = 
		//new CRFTrainerByStochasticGradient(crf, 1.0);

		//CRFTrainerByL1LabelLikelihood trainer = 
		//	new CRFTrainerByL1LabelLikelihood(crf, 0.75);

		//trainer.addEvaluator(new PerClassAccuracyEvaluator(trainingInstances, "training"));
		trainer.addEvaluator(new PerClassAccuracyEvaluator(testingInstances, "testing"));
		trainer.addEvaluator(new TokenAccuracyEvaluator(testingInstances, "testing"));
		trainer.train(trainingInstances);
		
	}
    
    
     public void run (InstanceList trainingData, InstanceList testingData) throws Exception {
      // setup:
      //    CRF (model) and the state machine
      //    CRFOptimizableBy* objects (terms in the objective function)
      //    CRF trainer
      //    evaluator and writer

      // model
      CRF crf = new CRF(trainingData.getDataAlphabet(),
                        trainingData.getTargetAlphabet());
      // construct the finite state machine
      crf.addFullyConnectedStatesForLabels();
      // initialize model's weights
      crf.setWeightsDimensionAsIn(trainingData, false);

      //  CRFOptimizableBy* objects (terms in the objective function)
      // objective 1: label likelihood objective
      CRFOptimizableByLabelLikelihood optLabel =
          new CRFOptimizableByLabelLikelihood(crf, trainingData);

      // CRF trainer
      Optimizable.ByGradientValue[] opts =
          new Optimizable.ByGradientValue[]{optLabel};
      // by default, use L-BFGS as the optimizer
      CRFTrainerByValueGradients crfTrainer =
          new CRFTrainerByValueGradients(crf, opts);

      // *Note*: labels can also be obtained from the target alphabet
      String[] labels = new String[]{"I-PER", "I-LOC", "I-ORG", "I-MISC"};
      TransducerEvaluator evaluator = new MultiSegmentationEvaluator(
          new InstanceList[]{trainingData, testingData},
          new String[]{"train", "test"}, labels, labels) {
        @Override
        public boolean precondition(TransducerTrainer tt) {
          // evaluate model every 5 training iterations
          return tt.getIteration() % 5 == 0;
        }
      };
      crfTrainer.addEvaluator(evaluator);

      CRFWriter crfWriter = new CRFWriter("ner_crf.model") {
        @Override
        public boolean precondition(TransducerTrainer tt) {
          // save the trained model after training finishes
          return tt.getIteration() % Integer.MAX_VALUE == 0;
        }
      };
      crfTrainer.addEvaluator(crfWriter);

      // all setup done, train until convergence
      crfTrainer.setMaxResets(0);
      crfTrainer.train(trainingData, Integer.MAX_VALUE);
      // evaluate
      evaluator.evaluate(crfTrainer);

      // save the trained model (if CRFWriter is not used)
       FileOutputStream fos = new FileOutputStream("ner_crf.model");
       ObjectOutputStream oos = new ObjectOutputStream(fos);
       oos.writeObject(crf);
    }
    
    public static void main (String[] args) throws Exception {
		ExampleImplementation trainer = new ExampleImplementation("esp.train.gz", "esp.testa.gz");

	}
    
}
