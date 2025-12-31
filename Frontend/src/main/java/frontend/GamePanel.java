package frontend;
import entity.Player;

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{
    // SCREEN SETTINGS
    // originalTitleSize: 16x16 è la dimensione dei .png del gioco (standard per i giochi in 2D)
    // tuttavia, le moderne risoluzioni dei pc renderebbero troppo piccoli i .png 16x16
    // occorre ridimensionare i .png (far sembrare i 16x16 in 48x48 a schermo)
    final int originalTileSize = 32;
    final int scale = 2;
    public final int tileSize = originalTileSize * scale; // diventano 32 * 2 = 64px

    // dimensione della nostra schermata di gioco: Quante tessere/.png il gioco può visualizzare sia orizzontalmente che verticalmente?
    // questa misura determinerà il numero di tessere visualizzabili: 16 orizzontalmente e 12 verticalmente
    // il rapporto è 4:3
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; // 64 * 16 = 1024 pixels
    final int screenHeight = tileSize * maxScreenRow; // 64*12 = 768 pixels
    /*
    * PROBLEMA ANIMAZIONE
    * Il gioco ha bisogno del suo "clock" per poter far si che le immagini possano "muoversi".
    * A tal proposito creo un Thread. Il Thread gestirà in parallelo questo problema una volta avviato
    * all'interno del programma.
    * */
    Thread gameThread;

    /*
    * PROBLEMA: CONTROLLO DEI PERSONAGGI DA TASTIERA
    * Il gioco ha bisogno di un keyHandler che riesca a stabilire quando il giocatore spinge dei tasti per muovere
    * i personaggi sullo schermo.
    * A tal proposito creo un oggetto della classe KeyHandler.
    * */
    KeyHandler keyH = new KeyHandler();
    Player player = new Player(this, keyH);

    // impostiamo la posizione di default del giocatore
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;

    // FPS
    int FPS = 60;


    // costruttore del pannello di gioco
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // imposta la dimensione del pannello (JPanel)
        this.setBackground(Color.black); // imposto il colore di sfondo del gioco (nero)
        this.setDoubleBuffered(true); // questo migliora le prestazioni di rendering
        this.addKeyListener(keyH); // metodo che gestisce gli input da tastiera
        this.setFocusable(true); // metodo che permette a GamePanel di "concentrarsi" sul ricevere input
    }


    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;


        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    // creo il metodo dedicato all'operazione 1 di aggiornamento
    // i valori X aumentano lo spostamento a dx
    // i valori Y aumentano lo spostamento in basso
    public void update() {
       player.update();
    }
    // creo il metodo dedicato all'operazione 1 di rappresentazione/disegno delle componenti/personaggi
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // pulisce il pannello prima di ridisegnare
        Graphics2D g2 = (Graphics2D)g; // trasforma l'oggetto Graphics in Graphics 2D

        player.draw(g2);

        g2.dispose(); // rilascia le risorse di sistema usate per disegnare subito dopo aver terminato

        Toolkit.getDefaultToolkit().sync(); // questo risolve il problema della fluidità del movimento
    }
}
