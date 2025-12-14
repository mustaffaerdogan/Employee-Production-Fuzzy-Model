package pkt;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.ArrayList;

/**
 * EpochGrapher - 10 farklı ağ topolojisinin epoch bazlı MSE değişimini
 * aynı grafik üzerinde görselleştiren sınıf.
 */
public class EpochGrapher extends JPanel {
    
    private List<ExperimentData> experiments;
    private static final int PADDING = 60;
    private static final int LEGEND_WIDTH = 200;
    
    // 10 farklı renk
    private static final Color[] COLORS = {
        new Color(255, 0, 0),      // Kırmızı
        new Color(0, 0, 255),      // Mavi
        new Color(0, 128, 0),      // Yeşil
        new Color(255, 165, 0),    // Turuncu
        new Color(128, 0, 128),    // Mor
        new Color(0, 128, 128),    // Camgöbeği
        new Color(255, 192, 203),  // Pembe
        new Color(128, 128, 0),    // Zeytin
        new Color(139, 69, 19),    // Kahverengi
        new Color(0, 0, 0)         // Siyah
    };
    
    public static class ExperimentData {
        String name;
        List<Double> trainErrors;
        List<Double> testErrors;
        
        public ExperimentData(String name) {
            this.name = name;
            this.trainErrors = new ArrayList<>();
            this.testErrors = new ArrayList<>();
        }
        
        public void addEpochData(double trainError, double testError) {
            trainErrors.add(trainError);
            testErrors.add(testError);
        }
    }
    
    public EpochGrapher() {
        this.experiments = new ArrayList<>();
        setPreferredSize(new Dimension(1200, 700));
        setBackground(Color.WHITE);
    }
    
    public void addExperiment(ExperimentData experiment) {
        experiments.add(experiment);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (experiments.isEmpty()) {
            g2.drawString("Veri yok", getWidth() / 2 - 30, getHeight() / 2);
            return;
        }
        
        int width = getWidth() - LEGEND_WIDTH;
        int height = getHeight();
        
        // Find max values for scaling
        int maxEpoch = 0;
        double maxError = 0;
        
        for (ExperimentData exp : experiments) {
            maxEpoch = Math.max(maxEpoch, exp.trainErrors.size());
            for (double err : exp.trainErrors) {
                maxError = Math.max(maxError, err);
            }
            for (double err : exp.testErrors) {
                maxError = Math.max(maxError, err);
            }
        }
        
        // Add some margin to max error
        maxError = maxError * 1.1;
        
        // Draw axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        
        // Y-axis
        g2.draw(new Line2D.Double(PADDING, PADDING, PADDING, height - PADDING));
        // X-axis
        g2.draw(new Line2D.Double(PADDING, height - PADDING, width - PADDING, height - PADDING));
        
        // Draw axis labels
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Epoch", width / 2 - 20, height - 10);
        
        // Rotate and draw Y-axis label
        Graphics2D g2d = (Graphics2D) g2.create();
        g2d.rotate(-Math.PI / 2);
        g2d.drawString("MSE (Mean Squared Error)", -height / 2 - 80, 20);
        g2d.dispose();
        
        // Draw title
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("10 Farklı Ağ Topolojisi - Epoch vs MSE Grafiği", width / 2 - 220, 30);
        
        // Draw grid and scale markers
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1));
        
        int graphWidth = width - 2 * PADDING;
        int graphHeight = height - 2 * PADDING;
        
        // Y-axis markers (10 divisions)
        for (int i = 0; i <= 10; i++) {
            int y = height - PADDING - (i * graphHeight / 10);
            double value = (maxError / 10) * i;
            
            g2.setColor(new Color(200, 200, 200));
            g2.draw(new Line2D.Double(PADDING, y, width - PADDING, y));
            
            g2.setColor(Color.BLACK);
            g2.drawString(String.format("%.4f", value), 5, y + 4);
        }
        
        // X-axis markers (10 divisions)
        for (int i = 0; i <= 10; i++) {
            int x = PADDING + (i * graphWidth / 10);
            int value = (maxEpoch / 10) * i;
            
            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(value), x - 10, height - PADDING + 20);
        }
        
        // Draw experiment lines
        g2.setStroke(new BasicStroke(2));
        
        for (int expIdx = 0; expIdx < experiments.size(); expIdx++) {
            ExperimentData exp = experiments.get(expIdx);
            Color color = COLORS[expIdx % COLORS.length];
            
            // Draw train error line (solid)
            g2.setColor(color);
            for (int i = 0; i < exp.trainErrors.size() - 1; i++) {
                double y1 = exp.trainErrors.get(i);
                double y2 = exp.trainErrors.get(i + 1);
                
                int x1 = PADDING + (int) ((i / (double) maxEpoch) * graphWidth);
                int x2 = PADDING + (int) (((i + 1) / (double) maxEpoch) * graphWidth);
                
                int py1 = height - PADDING - (int) ((y1 / maxError) * graphHeight);
                int py2 = height - PADDING - (int) ((y2 / maxError) * graphHeight);
                
                g2.draw(new Line2D.Double(x1, py1, x2, py2));
            }
            
            // Draw test error line (dashed)
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
                                        10.0f, new float[]{5.0f}, 0.0f));
            for (int i = 0; i < exp.testErrors.size() - 1; i++) {
                double y1 = exp.testErrors.get(i);
                double y2 = exp.testErrors.get(i + 1);
                
                int x1 = PADDING + (int) ((i / (double) maxEpoch) * graphWidth);
                int x2 = PADDING + (int) (((i + 1) / (double) maxEpoch) * graphWidth);
                
                int py1 = height - PADDING - (int) ((y1 / maxError) * graphHeight);
                int py2 = height - PADDING - (int) ((y2 / maxError) * graphHeight);
                
                g2.draw(new Line2D.Double(x1, py1, x2, py2));
            }
            
            g2.setStroke(new BasicStroke(2));
        }
        
        // Draw legend
        int legendX = width - PADDING + 10;
        int legendY = PADDING + 20;
        
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Legend:", legendX, legendY);
        
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        legendY += 20;
        
        for (int i = 0; i < experiments.size(); i++) {
            ExperimentData exp = experiments.get(i);
            Color color = COLORS[i % COLORS.length];
            
            // Draw color box
            g2.setColor(color);
            g2.fillRect(legendX, legendY - 8, 15, 10);
            
            // Draw text
            g2.setColor(Color.BLACK);
            g2.drawString(exp.name, legendX + 20, legendY);
            
            legendY += 15;
        }
        
        // Add legend for line styles
        legendY += 10;
        g2.drawString("—— Eğitim MSE", legendX, legendY);
        legendY += 15;
        g2.drawString("- - - Test MSE", legendX, legendY);
    }
    
    /**
     * Display the graph in a window
     */
    public void display() {
        JFrame frame = new JFrame("Epoch MSE Grafiği");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    /**
     * Save the graph to a PNG file
     */
    public void saveToFile(String filename) {
        try {
            int width = 1200;
            int height = 700;
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            this.setSize(width, height);
            this.paint(g2);
            g2.dispose();
            
            javax.imageio.ImageIO.write(image, "png", new java.io.File(filename));
            System.out.println("Grafik kaydedildi: " + filename);
        } catch (Exception e) {
            System.err.println("Grafik kaydetme hatası: " + e.getMessage());
        }
    }
}

