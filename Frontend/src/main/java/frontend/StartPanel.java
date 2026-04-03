package frontend;

import frontend.endpointApi.EndpointApi;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class StartPanel {
    private static final String IMG_PATH = "/images/start.jpg";

    public StartPanel() {
        // Usiamo BorderLayout per far occupare all'immagine tutto lo spazio del frame
        JPanel jPanel = new JPanel(new BorderLayout());
        try {
            ImagePanel imagePanel = loadImage();

            // Applichiamo il GridBagLayout per centrare i tasti nello "schermo" del Pip-Boy
            imagePanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            // CORREZIONE 1: Usa il metodo createCustomButton invece di new JButton
            JButton newGame = createCustomButton("Nuova Partita");
            JButton loadGame = createCustomButton("Carica Partita");

            newGame.addActionListener(e -> startActionPerformed("Nuova Partita"));
            loadGame.addActionListener(e -> startActionPerformed("Carica Partita"));

            // Configurazione posizionamento
            gbc.gridx = 0;
            gbc.gridy = 0;
            // CORREZIONE 2: Aumentiamo l'inset superiore per centrare i tasti nel monitor verde
            gbc.insets = new Insets(180, 0, 10, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            imagePanel.add(newGame, gbc);

            // CORREZIONE 3: Aggiungiamo il secondo bottone usando gbc per coerenza
            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            imagePanel.add(loadGame, gbc);

            jPanel.add(imagePanel, BorderLayout.CENTER);

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Si è verificato un errore: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
        RootFrame.setPanel(jPanel);
    }

    private JButton createCustomButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 45));
        button.setFont(new Font("Monospaced", Font.BOLD, 18));

        // Stile Fallout: Nero trasparente con bordi verdi
        button.setBackground(new Color(0, 0, 0, 160));
        button.setForeground(new Color(51, 255, 51));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(51, 255, 51), 2));
        button.setContentAreaFilled(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 255, 51));
                button.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 0, 160));
                button.setForeground(new Color(51, 255, 51));
            }
        });
        return button;
    }

    public void startActionPerformed(String buttonClicked) {
        if(buttonClicked.equals("Nuova Partita")) {
            RootFrame.showLoadVideo(() -> new StartNewGame(EndpointApi.startNewGame()));
        } else if(buttonClicked.equals("Carica Partita")) {
            new PannelMetchSaved(0, false);
        }
    }

    public ImagePanel loadImage() throws Exception {
        URL imgURL = StartPanel.class.getResource(IMG_PATH);
        if (imgURL == null) {
            throw new Exception("Impossibile trovare il file " + IMG_PATH);
        }

        ImagePanel imagePanel = new ImagePanel(imgURL);
        // CORREZIONE 4: Rimosso il vecchio layout BoxLayout che rompeva tutto
        imagePanel.setOpaque(false);
        return imagePanel;
    }
}