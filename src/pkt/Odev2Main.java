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
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

public class Odev2Main {
    
    // Normalization constants
    private static final double MAX_TECRUBE = 35.0;
    private static final double MAX_YAS = 60.0;
    private static final double MAX_OUTPUT = 500.0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // 2. Load the 'data_set.txt' file
        System.out.println("Veri seti yukleniyor...");
        DataSet dataSet = loadDataSet("data_set.txt");

        if (dataSet == null || dataSet.isEmpty()) {
            System.out.println("Veri seti yuklenemedi veya bos!");
            return;
        }
        System.out.println("Veri seti yuklendi. Toplam satir: " + dataSet.size());

        while (true) {
            System.out.println("\n------------------------------------------------");
            System.out.println("       YSA (Yapay Sinir Agi) Odev Menusu");
            System.out.println("------------------------------------------------");
            System.out.println("1- Agi Egit ve Test Et (Momentumlu) [LR: 0.2, M: 0.7]");
            System.out.println("2- Agi Egit ve Test Et (Momentumsuz) [LR: 0.2]");
            System.out.println("3- Agi Egit Epoch Goster");
            System.out.println("4- Agi Egit ve Tekli Test");
            System.out.println("5- K-Fold Test");
            System.out.println("0- Cikis");
            System.out.print("Seciminiz: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            if (choice == 0) {
                System.out.println("Cikis yapiliyor...");
                break;
            }

            switch (choice) {
                case 1:
                    trainAndTestMomentum(dataSet);
                    break;
                case 2:
                    trainAndTestNoMomentum(dataSet);
                    break;
                case 3:
                    trainAndShowEpochs(dataSet);
                    break;
                case 4:
                    trainAndSingleTest(dataSet, scanner);
                    break;
                case 5:
                    kFoldTest(dataSet, scanner);
                    break;
                default:
                    System.out.println("Gecersiz secim, tekrar deneyin.");
            }
        }
        scanner.close();
    }

    private static DataSet loadDataSet(String filename) {
        // 3. Create a 'TrainingSet' (3 inputs, 1 output)
        DataSet ds = new DataSet(3, 1);
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                double[] inputs = new double[3];
                double[] outputs = new double[1];

                inputs[0] = Double.parseDouble(parts[0]); // Norm_Exp
                inputs[1] = Double.parseDouble(parts[1]); // Norm_Age
                inputs[2] = Double.parseDouble(parts[2]); // Norm_Gender
                outputs[0] = Double.parseDouble(parts[3]); // Norm_Output

                ds.addRow(new DataSetRow(inputs, outputs));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Dosya bulunamadi: " + filename);
            return null;
        } catch (Exception e) {
            System.err.println("Veri okuma hatasi: " + e.getMessage());
            return null;
        }
        return ds;
    }

    // Helper to split dataset manually (shuffles data)
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

    // 1- Agi Egit ve Test Et (Momentumlu)
    private static void trainAndTestMomentum(DataSet fullSet) {
        System.out.println("\n--- Momentumlu Egitim ---");
        DataSet[] split = splitDataSet(fullSet, 0.75);
        DataSet trainSet = split[0];
        DataSet testSet = split[1];
        System.out.println("Veri seti bolundu: %75 Egitim (" + trainSet.size() + "), %25 Test (" + testSet.size() + ")");

        // 4. Create MLP (3 Input, 8 Hidden, 1 Output, Sigmoid)
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 8, 1);

        MomentumBackpropagation learningRule = new MomentumBackpropagation();
        learningRule.setLearningRate(0.2);
        learningRule.setMomentum(0.7);
        learningRule.setMaxError(0.0001);
        learningRule.setMaxIterations(5000);

        neuralNet.setLearningRule(learningRule);

        System.out.println("Egitim basliyor...");
        neuralNet.learn(trainSet);
        System.out.println("Egitim tamamlandi.");

        double mse = calculateMSE(neuralNet, testSet);
        System.out.println("Test Seti MSE (Mean Squared Error): " + String.format("%.6f", mse));
    }

    // 2- Agi Egit ve Test Et (Momentumsuz)
    private static void trainAndTestNoMomentum(DataSet fullSet) {
        System.out.println("\n--- Momentumsuz Egitim ---");
        DataSet[] split = splitDataSet(fullSet, 0.75);
        DataSet trainSet = split[0];
        DataSet testSet = split[1];
        System.out.println("Veri seti bolundu: %75 Egitim (" + trainSet.size() + "), %25 Test (" + testSet.size() + ")");

        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 8, 1);

        BackPropagation learningRule = new BackPropagation();
        learningRule.setLearningRate(0.2);
        learningRule.setMaxError(0.0001);
        learningRule.setMaxIterations(5000);

        neuralNet.setLearningRule(learningRule);

        System.out.println("Egitim basliyor...");
        neuralNet.learn(trainSet);
        System.out.println("Egitim tamamlandi.");

        double mse = calculateMSE(neuralNet, testSet);
        System.out.println("Test Seti MSE (Mean Squared Error): " + String.format("%.6f", mse));
    }

    // 3- Agi Egit Epoch Goster
    private static void trainAndShowEpochs(DataSet fullSet) {
        System.out.println("\n--- Epoch Gosterimli Egitim ---");
        // Use full set or split? Usually full set for demo or split. Using full set here as implied.
        
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 8, 1);
        BackPropagation learningRule = new BackPropagation();
        learningRule.setLearningRate(0.2);
        learningRule.setMaxError(0.0001);
        learningRule.setMaxIterations(100); // Limit iterations for demo visibility

        learningRule.addListener(new LearningEventListener() {
            @Override
            public void handleLearningEvent(LearningEvent event) {
                BackPropagation bp = (BackPropagation) event.getSource();
                System.out.println("Epoch: " + bp.getCurrentIteration() + " | Hata: " + bp.getTotalNetworkError());
            }
        });

        neuralNet.setLearningRule(learningRule);
        
        System.out.println("Egitim basliyor (Max 100 epoch)...");
        neuralNet.learn(fullSet);
        System.out.println("Egitim tamamlandi.");
    }

    // 4- Agi Egit ve Tekli Test
    private static void trainAndSingleTest(DataSet fullSet, Scanner scanner) {
        System.out.println("\n--- Tekli Test Modu ---");
        // Train first
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 8, 1);
        BackPropagation learningRule = new BackPropagation();
        learningRule.setLearningRate(0.2);
        learningRule.setMaxError(0.001);
        learningRule.setMaxIterations(2000);
        neuralNet.setLearningRule(learningRule);

        System.out.println("Ag egitiliyor (Lutfen bekleyin)...");
        neuralNet.learn(fullSet);
        System.out.println("Ag egitildi.");

        // User Input
        System.out.print("Tecrube giriniz (0-35): ");
        double tecrube = scanner.nextDouble();
        
        System.out.print("Yas giriniz (18-60): ");
        double yas = scanner.nextDouble();
        
        System.out.print("Cinsiyet giriniz (0=Kadin, 1=Erkek): ");
        double cinsiyet = scanner.nextDouble();

        // Normalize
        double normTecrube = tecrube / MAX_TECRUBE;
        double normYas = yas / MAX_YAS;
        double normCinsiyet = cinsiyet; // Already 0-1

        // Set Input
        neuralNet.setInput(normTecrube, normYas, normCinsiyet);
        neuralNet.calculate();
        
        // Get Output
        double[] output = neuralNet.getOutput();
        double normOutput = output[0];
        
        // De-normalize
        double result = normOutput * MAX_OUTPUT;

        System.out.println("\n----------------SONUC----------------");
        System.out.println("Girdi -> Tecrube: " + tecrube + ", Yas: " + yas + ", Cinsiyet: " + cinsiyet);
        System.out.println("Ag Ciktisi (Normalize): " + normOutput);
        System.out.println("Tahmini Parca Sayisi (Gercek): " + result);
        System.out.println("-------------------------------------");
    }

    // 5- K-Fold Test
    private static void kFoldTest(DataSet fullSet, Scanner scanner) {
        System.out.println("\n--- K-Fold Cross Validation ---");
        System.out.print("K değerini giriniz (ör. 5 veya 10): ");
        int k = scanner.nextInt();
        if (k < 2) k = 2;

        List<DataSetRow> rows = new ArrayList<>(fullSet.getRows());
        Collections.shuffle(rows); // Randomize

        int foldSize = rows.size() / k;
        double totalMSE = 0;

        for (int i = 0; i < k; i++) {
            DataSet trainData = new DataSet(3, 1);
            DataSet testData = new DataSet(3, 1);

            int start = i * foldSize;
            int end = (i == k - 1) ? rows.size() : (i + 1) * foldSize;

            for (int j = 0; j < rows.size(); j++) {
                if (j >= start && j < end) {
                    testData.addRow(rows.get(j));
                } else {
                    trainData.addRow(rows.get(j));
                }
            }

            // Train
            MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 8, 1);
            BackPropagation learningRule = new BackPropagation();
            learningRule.setLearningRate(0.2);
            learningRule.setMaxError(0.001);
            learningRule.setMaxIterations(1000);
            neuralNet.setLearningRule(learningRule);

            neuralNet.learn(trainData);

            // Test
            double mse = calculateMSE(neuralNet, testData);
            System.out.println("Fold " + (i + 1) + " MSE: " + String.format("%.6f", mse));
            totalMSE += mse;
        }

        System.out.println("-------------------------------------");
        System.out.println("Ortalama MSE: " + String.format("%.6f", (totalMSE / k)));
    }

    private static double calculateMSE(NeuralNetwork net, DataSet testSet) {
        double totalError = 0;
        for (DataSetRow row : testSet.getRows()) {
            net.setInput(row.getInput());
            net.calculate();
            double[] networkOutput = net.getOutput();
            double[] desiredOutput = row.getDesiredOutput();

            double error = desiredOutput[0] - networkOutput[0];
            totalError += error * error;
        }
        return totalError / testSet.size();
    }
}
