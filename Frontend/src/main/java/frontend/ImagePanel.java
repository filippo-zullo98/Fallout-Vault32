package frontend;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * JPanel personalizzato che visualizza un'immagine di sfondo.
 */
public class ImagePanel extends JPanel {
    private BufferedImage image; // Immagine di sfondo che verrà visualizzata nel pannello
    private static final int IMAGE_WIDTH = 600; // Larghezza predefinita dell'immagine nel pannello
    private static final int IMAGE_HEIGHT = 700; // Altezza predefinita dell'immagine nel pannello

    /**
     * Costruttore della classe ImagePanel.
     * Carica un'immagine specificata dall'URL e la memorizza per il disegno.
     * @param imgURL L'URL dell'immagine da caricare nel pannello.
     */
    public ImagePanel(URL imgURL) {
        try {
            // Carica l'immagine dall'URL specificato utilizzando ImageIO e la memorizza in 'image'
            image = ImageIO.read(imgURL);
        } // fine di if
        catch(IOException e){ // processa l'eccezione
            e.printStackTrace();
        } // fine del blocco catch
    } // fine del costruttore

    /**
     * Sovrascrive il metodo paintComponent per disegnare l'immagine di sfondo nel pannello.
     * @param g Il contesto grafico su cui disegnare l'immagine.
     */
    @Override
    protected void paintComponent(Graphics g) {
        // chiama il metodo paintComponent della superclasse per disegnare eventuali componenti aggiunti al pannello
        super.paintComponent(g);
        if(image != null) {
            // Disegna l'immagine utilizzando le dimensioni correnti del pannello
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        } // fine di if
    } // fine del metodo paintComponent

    /**
     * Sovrascrive il metodo getPreferredSize per specificare le dimensioni preferite del pannello.
     * @return Le dimensioni preferite del pannello basate sull'immagine caricata.
     */
    @Override
    public Dimension getPreferredSize() {
        if (image == null) {
            // Se l'immagine non è stata caricata correttamente, restituisce dimensioni di fallback
            return new Dimension(100, 100);
        } // fine di if
        else { // Altrimenti
            //  Restituisce le dimensioni predefinite dell'immagine
            return new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT);
        } // fine di else
    } // fine del metodo getPreferredSize

} // fine della classe ImagePanel