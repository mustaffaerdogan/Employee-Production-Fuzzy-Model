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
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

public class RaporTest {

    public static void main(String[] args) {
        System.out.println("Rapor icin testler baslatiliyor...");
        System.out.println("Her deney icin epoch bazli hata degerleri kaydediliyor...\n");
        
        // 1. Load Dataset
        DataSet fullSet = loadDataSet("data_set.txt");
        if (fullSet == null) {
            System.out.println("Veri seti yuklenemedi!");
            return;
        }

        // 2. Split Dataset (75% Train, 25% Test)
        DataSet[] split = splitDataSet(fullSet, 0.75);
        DataSet trainSet = split[0];
        DataSet testSet = split[1];
        
        System.out.println("Veri seti: " + fullSet.size() + " satir.");
        System.out.println("Egitim Seti: " + trainSet.size() + ", Test Seti: " + testSet.size());
        System.out.println("\n--------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-15s | %-13s | %-8s | %-10s | %-10s%n", 
                          "Exp No", "Hidden Neurons", "Learning Rate", "Momentum", "Train MSE", "Test MSE");
        System.out.println("--------------------------------------------------------------------------------------");

        // 3. Define Experiments
        // Format: {Hidden Neurons, Learning Rate, Momentum}
        double[][] experiments = {
            {4, 0.2, 0.7},
            {8, 0.2, 0.7},
            {15, 0.2, 0.7},
            {30, 0.2, 0.7},
            {8, 0.1, 0.7},
            {8, 0.5, 0.7},
            {8, 0.2, 0.2},
            {8, 0.2, 0.9},
            {15, 0.5, 0.9}, // Complex
            {4, 0.1, 0.2}   // Simple
        };

        // Create grapher
        EpochGrapher grapher = new EpochGrapher();

        // 4. Run Experiments and collect epoch data
        for (int i = 0; i < experiments.length; i++) {
            int hiddenNeurons = (int) experiments[i][0];
            double learningRate = experiments[i][1];
            double momentum = experiments[i][2];

            EpochGrapher.ExperimentData expData = runExperimentWithEpochTracking(
                i + 1, trainSet, testSet, hiddenNeurons, learningRate, momentum);
            grapher.addExperiment(expData);
        }
        
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.println("Tum testler tamamlandi.");
        System.out.println("\nEpoch bazli grafik olusturuluyor...");
        
        // Display graph
        grapher.display();
        
        // Save graph to file
        grapher.saveToFile("docs/img/epoch_mse_grafik.png");
        
        System.out.println("Grafik goruntulendi ve kaydedildi.");
    }

    private static EpochGrapher.ExperimentData runExperimentWithEpochTracking(
            int expNo, DataSet trainSet, DataSet testSet, 
            int hiddenNeurons, double learningRate, double momentum) {
        
        // Create experiment data container
        String expName = String.format("Exp%d(H:%d,LR:%.1f,M:%.1f)", 
                                       expNo, hiddenNeurons, learningRate, momentum);
        EpochGrapher.ExperimentData expData = new EpochGrapher.ExperimentData(expName);
        
        // Create Network
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(
            TransferFunctionType.SIGMOID, 3, hiddenNeurons, 1);

        // Configure Learning Rule
        MomentumBackpropagation learningRule = new MomentumBackpropagation();
        learningRule.setLearningRate(learningRate);
        learningRule.setMomentum(momentum);
        learningRule.setMaxError(0.0001);
        learningRule.setMaxIterations(100); // Reduced for graph clarity
        
        // Add listener to track epoch errors
        learningRule.addListener(new LearningEventListener() {
            @Override
            public void handleLearningEvent(LearningEvent event) {
                MomentumBackpropagation bp = (MomentumBackpropagation) event.getSource();
                double trainError = bp.getTotalNetworkError();
                double testError = calculateMSE(neuralNet, testSet);
                expData.addEpochData(trainError, testError);
            }
        });

        neuralNet.setLearningRule(learningRule);

        // Train
        neuralNet.learn(trainSet);

        // Calculate final MSE
        double trainMSE = calculateMSE(neuralNet, trainSet);
        double testMSE = calculateMSE(neuralNet, testSet);

        // Print Result
        System.out.printf("%-8d | %-15d | %-13.2f | %-8.2f | %-10.6f | %-10.6f%n", 
                          expNo, hiddenNeurons, learningRate, momentum, trainMSE, testMSE);
        
        return expData;
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
}
