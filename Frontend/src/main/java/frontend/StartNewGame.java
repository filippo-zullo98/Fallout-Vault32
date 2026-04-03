package frontend;

import frontend.endpointApi.EndpointApi;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;


public class StartNewGame {
    // Dichiarazione delle variabili di istanza e costanti
    private final int port;

    private final JPanel panel = new JPanel(new BorderLayout());
    private final JTextArea textArea = new JTextArea();
    private final JTextField textField = new JTextField(); // Campo di testo per input utente
    private final JButton sendButton = new JButton("Invio"); // Bottone di invio
    private final JButton buttonSali = new JButton("<html><span style='color: green; font-family: Arial, sans-serif;'>&#9650;</span></html>"); // Frecce verso l'alto con font Arial o sans-serif
    private final JButton buttonScendi = new JButton("<html><span style='color: green; font-family: Arial, serif;'>&#9660;</span></html>"); // Frecce verso il basso con font Times New Roman o serif
    private final JToolBar toolBar = new JToolBar();
    private final JButton buttonGame = new JButton("Game");
    private final JButton buttonHelp = new JButton("Help");
    private final JButton buttonInventory = new JButton("Inventario");
    private final JButton backButton = new JButton("Indietro");
    private final String PATH_IMG = "/img_stanze/id_";
    private JLabel imageLabel;
    private JSONObject currentroom = null;

    private JLabel timerLabel;
    private int counter = 15;

    public StartNewGame(int new_port) {
        port = new_port;
        boolean flag = false;
        RootFrame.stopMenuTheme();

        addToolBar();// aggiunta della toolbar

        // Ottenimento e visualizzazione della stanza corrente  DA CAPIRE
        while (!flag){
            try (Socket socket = new Socket("localhost", port)) {
                currentroom = EndpointApi.getCurrentRoom(new_port);
                flag = true;
            } catch (IOException e) {
                flag = false;
            }
        }

        sendButton.addActionListener(e -> {
            JSONObject results = EndpointApi.sendParser(new_port,textField.getText());
            if (results != null){
                textField.setText("");
                textArea.setText(results.get("descrizione").toString());

            }
        });

        drawGamePanel();

        RootFrame.setPanel(panel);
    }

    private void drawGamePanel() {
        // Dimensionamento dei componenti
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        textField.setPreferredSize(new Dimension(300, 30));
        sendButton.setPreferredSize(new Dimension(80, 30));

        // Creazione dello JScrollPane con la textArea all'interno
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(400, 100)); // Impostazione della dimensione preferita dello JScrollPane

        // Creazione del pannello inferiore con i componenti
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        bottomPanel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        bottomPanel.add(textField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        bottomPanel.add(sendButton, gbc);

        // Creazione e aggiunta dei bottoni direzionali
        JPanel directionalPanel = new JPanel(new GridBagLayout());
        addDirectionalButton(directionalPanel);
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        bottomPanel.add(directionalPanel, gbc);

        textArea.setText("Ti trovi all'interno di: " + currentroom.get("name"));

        viewButton(Integer.parseInt(currentroom.get("type").toString()) == 1);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        backButton.setPreferredSize(new Dimension(80, 30));
        backButton.addActionListener(e12 -> RootFrame.setPrecPannel());

        imageLabel = new JLabel(drawRoomImg());
        JPanel imagePanel = new JPanel(new FlowLayout());
        imagePanel.add(imageLabel);
        panel.add(imagePanel, BorderLayout.CENTER);
    }
    private void showTimer(int count){
        timerLabel.setVisible(true);
        this.counter = count;
        Timer countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (counter > 0) {
                    counter--;
                    timerLabel.setText("Time: " + counter);
                } else {
                    ((Timer)e.getSource()).stop();
                    timerLabel.setText("Time's up!");

                    // Creazione del secondo timer (attesa di 10 secondi)
                    Timer hideTimer = new Timer(10000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            timerLabel.setVisible(false);
                            ((Timer)e.getSource()).stop();
                        }
                    });



                    // Avvio del secondo timer
                    hideTimer.setRepeats(false); // Esegue una sola volta
                    hideTimer.start();
                }
            }
        });

        // Avvio del primo timer
        countdownTimer.start();
    }
    private ImageIcon drawRoomImg() {
        ImageIcon newRoom = null;
        if (Integer.parseInt(currentroom.get("type").toString()) == 1){
            URL imgURL = StartNewGame.class.getResource(PATH_IMG + "ascensore" +".jpg"); // creo un oggetto di tipo URL contenente l'imagePath

            assert imgURL != null;
            newRoom = new ImageIcon(imgURL); // Cambia il percorso dell'immagine

        }else {
            URL imgURL = StartNewGame.class.getResource(PATH_IMG + currentroom.get("id") +".jpg"); // creo un oggetto di tipo URL contenente l'imagePath

            assert imgURL != null;
            newRoom = new ImageIcon(imgURL); // Cambia il percorso dell'immagine
            System.out.println(PATH_IMG + currentroom.get("id") +".jpg");
        }
        Image newImage = newRoom.getImage();
        Image resizedNewImage = newImage.getScaledInstance(360, 340, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedNewImage);
    }

    private void viewButton(boolean visible) {
        buttonScendi.setVisible(visible);
        buttonSali.setVisible(visible);
    }

    private void addToolBar() {
        //aggiunta pulsanti alla toolbar
        toolBar.add(buttonGame);
        toolBar.add(buttonHelp);
        toolBar.add(buttonInventory);

        panel.add(toolBar, BorderLayout.NORTH);  //aggiunta della toolbar

        // action listener che gestisce il bottone della toolbar "Game"
        buttonGame.addActionListener(e -> {
            JPopupMenu SavePopUpMenu = new JPopupMenu();
            JMenuItem itemSave = new JMenuItem("Salva Partita");
            itemSave.addActionListener(e1 -> new PannelMetchSaved(port, true));
            SavePopUpMenu.add(itemSave);
            SavePopUpMenu.show(buttonGame, 0, buttonGame.getHeight());
        });

        // action listener che gestisce il bottone della toolbar "Help"
        buttonHelp.addActionListener(e -> drawHelpPanel());

        // action listener che gestisce il bottone "Inventario" della toolbar
        buttonInventory.addActionListener(e -> drawInventarioPanel());
    }

    private void drawInventarioPanel() {
        JPanel inventarioPanel = new JPanel(new BorderLayout());

        JSONObject inventario = EndpointApi.getInventario(port);

        if (inventario == null) {
            JLabel label = new JLabel("<html><div style='text-align: center; color: #008000; font-size: 16px; font-weight: bold;'>INVENTARIO VUOTO</div></html>");
            label.setVerticalAlignment(JLabel.CENTER);
            label.setHorizontalAlignment(JLabel.CENTER);

            // Aggiunge il JLabel al centro del pannello inventarioPanel
            inventarioPanel.add(label, BorderLayout.CENTER);
        } else {
            Object[][] data = new Object[inventario.keySet().size()][2];
            int len = data.length;
            for (String key : inventario.keySet()) {
                JSONObject furnitureDettagli = (JSONObject) inventario.get(key);
                // Formattazione HTML per ogni riga della tabella
                data[len-1][0] = "<html><div style='color: #008000; font-size: 14px;'>" + furnitureDettagli.get("name") + "</div></html>";
                data[len-1][1] = "<html><div style='color: #008000; font-size: 8px;'>" + furnitureDettagli.get("descrizione") + "</div></html>";

                len--;
            }
            drawTable(data, inventarioPanel);
        }

        inventarioPanel.add(backButton, BorderLayout.SOUTH);
        RootFrame.setPanel(inventarioPanel); // Aggiorna il pannello corrente solo una volta
    }

    private void drawHelpPanel(){
        JPanel helpPanel = new JPanel(new BorderLayout());
        JTextArea textCommandArea = new JTextArea();
        textCommandArea.setEditable(false);
        textCommandArea.setText(getCommandList());
        JScrollPane scrollPane = new JScrollPane(textCommandArea);
        helpPanel.add(scrollPane, BorderLayout.CENTER);


        helpPanel.add(backButton, BorderLayout.SOUTH);
        RootFrame.setPanel(helpPanel);
    }

    private void drawTable(Object[][] data,JPanel inventarioPanel ) {
        String[] columnNames = {"name", "descrizione"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        // Adjust row height dynamically
        table.setRowHeight(0, 50);
        table.setRowHeight(1, 30);

        // Crea un JPanel e aggiunge il JScrollPane
        inventarioPanel.add(scrollPane, BorderLayout.CENTER);


    }

    public void addDirectionalButton(JPanel frame) {
        JButton buttonUp = new JButton("↑");
        JButton buttonDown = new JButton("↓");
        JButton buttonLeft = new JButton("←");
        JButton buttonRight = new JButton("→");


        // Dimensioni per i pulsanti direzionali sinistra e destra
        Dimension directionalButtonSize = new Dimension(40, 40);
        buttonLeft.setPreferredSize(directionalButtonSize);
        buttonRight.setPreferredSize(directionalButtonSize);

        // Dimensioni per i pulsanti direzionali su e giù
        Dimension upDownButtonSize = new Dimension(40, 40);
        buttonUp.setPreferredSize(upDownButtonSize);
        buttonDown.setPreferredSize(upDownButtonSize);

        // Dimensioni per buttonSali e buttonScendi
        Dimension saliScendiButtonSize = new Dimension(80, 30);
        buttonSali.setPreferredSize(saliScendiButtonSize);
        buttonScendi.setPreferredSize(saliScendiButtonSize);

        // Creazione dell'etichetta per il timer
        timerLabel = new JLabel();
        timerLabel.setBounds(100, 50, 100, 30);
        timerLabel.setVisible(false);
        frame.add(timerLabel);

        // Aumenta la dimensione del font per il testo delle frecce
        buttonUp.setFont(new Font("Arial", Font.BOLD, 18)); // Esempio di aumentare la dimensione del font
        buttonDown.setFont(new Font("Arial", Font.BOLD, 18));
        buttonLeft.setFont(new Font("Arial", Font.BOLD, 18));
        buttonRight.setFont(new Font("Arial", Font.BOLD, 18));


        buttonSali.setVisible(false);
        buttonScendi.setVisible(false);

        // Configurazione per i bottoni direzionali con GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3); // Insets più piccoli per ridurre lo spazio

        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(buttonUp, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(buttonDown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(buttonLeft, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        frame.add(buttonRight, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        frame.add(buttonSali, gbc);

        gbc.gridx = 3;
        gbc.gridy = 2;
        frame.add(buttonScendi, gbc);

        // Aggiungo spazio aggiuntivo tra i bottoni direzionali
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL; // Riempi lo spazio verticalmente
        gbc.weighty = 1.0; // Peso per l'altezza (espande verticalmente)
        frame.add(Box.createRigidArea(new Dimension(10, 10)), gbc); // Spazio aggiuntivo tra i bottoni

        // Aggiungo i listener agli ascoltatori di azione per i bottoni direzionali
        buttonUp.addActionListener(e -> handleButtonClick("/up"));
        buttonDown.addActionListener(e -> handleButtonClick("/down"));
        buttonLeft.addActionListener(e -> handleButtonClick("/left"));
        buttonRight.addActionListener(e -> handleButtonClick("/right"));
        buttonSali.addActionListener(e -> handleButtonClick("/sali"));
        buttonScendi.addActionListener(e -> handleButtonClick("/scendi"));
    }


    // metodo che inoltra le richieste del client al server per utilizzare il giocatore
    private void handleButtonClick(String endpoint) {
        JSONObject res = EndpointApi.sendRequestDirection(endpoint,port);
        System.out.println("Risposta JSON dal backend:");
        //System.out.println(jsonResponse);
        //JSONObject res = EndpoinApi.getCurrentRoom(port);;
        assert res != null;
        currentroom = res;

        try {
            viewButton(Integer.parseInt(res.get("type").toString()) == 1);
            imageLabel.setIcon(drawRoomImg());
        } catch (Exception e){
            System.out.println("movimento non effettuato");
        }
        try {
            textArea.setText("Ti trovi all'interno di: " + res.get("name"));
            if (res.has("time")) {
                showTimer(Integer.parseInt(res.get("time").toString()));
            }
        }catch (Exception e){
            textArea.setText(res.get("descrizione").toString());

        }

    }

    private String getCommandList() {
        StringBuilder commands = new StringBuilder();
        commands.append("ELENCO DEI COMANDI\n");
        commands.append("   - click su freccia ↑ per muoversi verso Nord\n");
        commands.append("   - click su freccia ↓ per muoversi verso Sud\n");
        commands.append("   - click su freccia → per muoversi verso destra\n");
        commands.append("   - click su freccia ← per muoversi vesro sinistra\n");
        commands.append("   - click su S: Quando sei in ascensore per salire\n");
        commands.append("   - click su G: Quando sei in ascensore per scendere\n");
        commands.append("   - digitare Osserva per avere una descrizione della stanza\n");
        commands.append("   - digitare Esamina per ispezionare un mobile\n");
        commands.append("   - digitare Usa per utilizzare un oggetto su qualcosa\n");
        commands.append("   - click su invio per inviare un comando testuale\n");
        commands.append("   - click su Nuova Partita per avviare una nuova sessione di gioco\n");
        commands.append("   - click su Game nella toolbar: fare click su Game e poi su Salva partita per effettuare un nuovo salvataggio\n");
        commands.append("   - click su Carica Partita per accede ai salvataggi di gioco\n");
        commands.append("   - click su Inventario per visualizzare gli oggetti raccolti\n");
        return commands.toString();
    }


}