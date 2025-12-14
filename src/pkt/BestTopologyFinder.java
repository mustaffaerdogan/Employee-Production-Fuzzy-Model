package pkt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

/**
 * BestTopologyFinder - 10 farklı ağ topolojisini test edip en iyisini belirler
 */
public class BestTopologyFinder {
    
    public static class TopologyConfig {
        public int hiddenNeurons;
        public double learningRate;
        public double momentum;
        public double testMSE;
        public double trainMSE;
        
        public TopologyConfig(int hiddenNeurons, double learningRate, double momentum) {
            this.hiddenNeurons = hiddenNeurons;
            this.learningRate = learningRate;
            this.momentum = momentum;
        }
        
        @Override
        public String toString() {
            return String.format("Hidden: %d, LR: %.2f, Momentum: %.2f (Test MSE: %.6f)", 
                                 hiddenNeurons, learningRate, momentum, testMSE);
        }
    }
    
    public static TopologyConfig findBestTopology() {
        System.out.println("En iyi ag topolojisi arastiriliyor...\n");
        
        // Load Dataset
        DataSet fullSet = loadDataSet("data_set.txt");
        if (fullSet == null) {
            System.out.println("Veri seti yuklenemedi!");
            return null;
        }

        // Split Dataset (75% Train, 25% Test)
        DataSet[] split = splitDataSet(fullSet, 0.75);
        DataSet trainSet = split[0];
        DataSet testSet = split[1];

        // Define Experiments
        TopologyConfig[] experiments = {
            new TopologyConfig(4, 0.2, 0.7),
            new TopologyConfig(8, 0.2, 0.7),
            new TopologyConfig(15, 0.2, 0.7),
            new TopologyConfig(30, 0.2, 0.7),
            new TopologyConfig(8, 0.1, 0.7),
            new TopologyConfig(8, 0.5, 0.7),
            new TopologyConfig(8, 0.2, 0.2),
            new TopologyConfig(8, 0.2, 0.9),
            new TopologyConfig(15, 0.5, 0.9),
            new TopologyConfig(4, 0.1, 0.2)
        };

        TopologyConfig bestConfig = null;
        double bestTestMSE = Double.MAX_VALUE;

        // Run Experiments
        for (int i = 0; i < experiments.length; i++) {
            TopologyConfig config = experiments[i];
            
            // Create Network
            MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(
                TransferFunctionType.SIGMOID, 3, config.hiddenNeurons, 1);

            // Configure Learning Rule
            MomentumBackpropagation learningRule = new MomentumBackpropagation();
            learningRule.setLearningRate(config.learningRate);
            learningRule.setMomentum(config.momentum);
            learningRule.setMaxError(0.0001);
            learningRule.setMaxIterations(1000);

            neuralNet.setLearningRule(learningRule);

            // Train
            neuralNet.learn(trainSet);

            // Calculate MSE
            config.trainMSE = calculateMSE(neuralNet, trainSet);
            config.testMSE = calculateMSE(neuralNet, testSet);

            System.out.printf("Deney %2d: %s%n", (i + 1), config);
            
            // Track best
            if (config.testMSE < bestTestMSE) {
                bestTestMSE = config.testMSE;
                bestConfig = config;
            }
        }
        
        System.out.println("\n=== EN IYI TOPOLOJI ===");
        System.out.println(bestConfig);
        
        return bestConfig;
    }

    private static double calculateMSE(NeuralNetwork<?> net, DataSet dataSet) {
        double totalError = 0;
        for (DataSetRow row : dataSet.getRows()) {
            net.setInput(row.getInput());
            net.calculate();
            double[] networkOutput = net.getOutput();
            double[] desiredOutput = row.getDesiredOutput();

            double error = desiredOutput[0] - networkOutput[0];
            totalError += error * error;
        }
        return totalError / dataSet.size();
    }

    private static DataSet loadDataSet(String filename) {
        DataSet ds = new DataSet(3, 1);
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                double[] inputs = new double[3];
                double[] outputs = new double[1];

                inputs[0] = Double.parseDouble(parts[0]);
                inputs[1] = Double.parseDouble(parts[1]);
                inputs[2] = Double.parseDouble(parts[2]);
                outputs[0] = Double.parseDouble(parts[3]);

                ds.addRow(new DataSetRow(inputs, outputs));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Dosya bulunamadi: " + filename);
            return null;
        }
        return ds;
    }

    private static DataSet[] splitDataSet(DataSet ds, double trainRatio) {
        List<DataSetRow> rows = new ArrayList<>(ds.getRows());
        Collections.shuffle(rows);

        int trainSize = (int) (rows.size() * trainRatio);

        DataSet trainSet = new DataSet(3, 1);
        DataSet testSet = new DataSet(3, 1);

        for (int i = 0; i < rows.size(); i++) {
            if (i < trainSize) {
                trainSet.addRow(rows.get(i));
            } else {
                testSet.addRow(rows.get(i));
            }
        }
        return new DataSet[]{trainSet, testSet};
    }
    
    public static void main(String[] args) {
        findBestTopology();
    }
}

