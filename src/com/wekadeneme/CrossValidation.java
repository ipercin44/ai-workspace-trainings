package com.wekadeneme;

import java.io.File;
import java.io.IOException;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

public class CrossValidation {

	public static void main(String[] args) throws Exception, IOException {

		DataSource dataSource = new DataSource("./sources/datasets/glass.arff");

		Instances data = dataSource.getDataSet();

		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);

		int folds = 10;
		
		int size = data.numInstances() / folds;
		int begin = 0;
		int end = size - 1;
		

		double precision = 0;
		double recall = 0;
		double fmeasure = 0;
		double error = 0;


		for (int i = 0; i <= folds; i++) {
			System.out.println("=========================\nIteration: " + i + "\n-------------------------");
			Instances trainingInstances = new Instances(data);
			Instances testIntances = new Instances(data, begin, (end - begin));
			for (int j = 0; j < (end - begin); j++) {
				trainingInstances.delete(begin);
			}

			NaiveBayes naiveBayes = new NaiveBayes();
			naiveBayes.buildClassifier(trainingInstances);

			J48 j48 = new J48();
			j48.buildClassifier(trainingInstances);

			Evaluation evaluation = new Evaluation(testIntances);
			evaluation.evaluateModel(j48, testIntances);

			System.out.println("Precision: " + evaluation.precision(1));
			System.out.println("Recall: " + evaluation.recall(1));
			System.out.println("FMeasure: " + evaluation.fMeasure(1));
			System.out.println("ErroRate: " + evaluation.errorRate());
			System.out.println("Kappa: " + evaluation.kappa());

			precision += evaluation.precision(1);
			recall += evaluation.recall(1);
			fmeasure += evaluation.fMeasure(1);
			error += evaluation.errorRate();

			// UPDATE
			begin = end + 1;
			end += size;
			if (i == (folds - 1)) {
				end = data.numInstances();
			}
		}
		System.out.println("\n=========== Results =============\n");
		System.out.println("Precision: " + precision / folds);
		System.out.println("Recall: " + recall / folds);
		System.out.println("FMeasure: " + fmeasure / folds);
		System.out.println("Error: " + error / folds);

		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File("./sources/results/crossvalidation.arff"));
		saver.writeBatch();
	}

}
