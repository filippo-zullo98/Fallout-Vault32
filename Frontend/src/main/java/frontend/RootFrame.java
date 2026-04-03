package frontend;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class RootFrame {
    private static final int WIDTH = 580;
    private static final int HEIGHT = 560;
    private static JFrame rootFrame = new JFrame();
    private static JPanel currentPanel = new JPanel();
    private static JPanel previousPanel = null;
    private static Clip MenuTheme;

    public RootFrame() {
        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootFrame.setSize(WIDTH, HEIGHT);
        rootFrame.setLayout(new BorderLayout());
        rootFrame.setResizable(false);
        rootFrame.setLocationRelativeTo(null); // Centra la finestra

        startMenuTheme();

        // Supponendo che StartPanel chiami RootFrame.setPanel() al suo interno
        new StartPanel();

        rootFrame.add(currentPanel, BorderLayout.CENTER);
        rootFrame.setVisible(true);
    }

    public static void setPanel(JPanel newPanel){
        if (currentPanel != null) {
            rootFrame.remove(currentPanel);
        }
        previousPanel = currentPanel;
        currentPanel = newPanel;
        rootFrame.add(currentPanel, BorderLayout.CENTER);
        rootFrame.revalidate();
        rootFrame.repaint();
    }

    public static void setPrecPannel(){
        if(previousPanel != null) {
            setPanel(previousPanel);
        } else {
            System.out.println("Nessun pannello precedente da ripristinare.");
        }
    }

    public static void startMenuTheme() {
        try {
            // Nota: Usiamo getResource invece di getResourceAsStream per coerenza con le GIF
            URL soundUrl = RootFrame.class.getResource("/music/main_title.wav");
            if (soundUrl != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundUrl);
                MenuTheme = AudioSystem.getClip();
                MenuTheme.open(audioInputStream);
                MenuTheme.loop(Clip.LOOP_CONTINUOUSLY);
                MenuTheme.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopMenuTheme() {
        if (MenuTheme != null && MenuTheme.isRunning()) {
            MenuTheme.stop();
            MenuTheme.close();
        }
    }

    /**
     * Versione Swing con GIF del caricamento
     */
    public static void showLoadVideo(Runnable onVideoEnd) {
        // Creiamo il pannello per la GIF
        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setBackground(Color.BLACK);

        URL resource = RootFrame.class.getResource("/video/Caricamento.gif");

        if (resource != null) {
            ImageIcon loadingIcon = new ImageIcon(resource);
            JLabel gifLabel = new JLabel(loadingIcon);
            loadingPanel.add(gifLabel, BorderLayout.CENTER);
        } else {
            JLabel errorLabel = new JLabel("CARICAMENTO...", SwingConstants.CENTER);
            errorLabel.setForeground(Color.GREEN);
            loadingPanel.add(errorLabel, BorderLayout.CENTER);
        }

        // Impostiamo il pannello di caricamento
        RootFrame.setPanel(loadingPanel);

        // Poiché le GIF loopano all'infinito, usiamo un Timer per decidere
        // quanto deve durare la schermata di caricamento (es. 5 secondi)
        Timer timer = new Timer(5000, e -> {
            onVideoEnd.run(); // Esegui l'azione successiva (es. vai alla schermata di gioco)
        });
        timer.setRepeats(false);
        timer.start();
    }
}