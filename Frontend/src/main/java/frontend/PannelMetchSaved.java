package frontend;

import frontend.endpointApi.EndpointApi;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe che gestisce il pannello per la visualizzazione dei salvataggi e le operazioni correlate.
 */
public class PannelMetchSaved {
    private final int N_SAVES = 4; // Numero massimo di salvataggi consentiti
    private JTextArea saveNameTextArea; // Area di testo per il nome del salvataggio
    private JButton sendSaveNameButton; // Pulsante per inviare il nome del salvataggio
    private final GridBagConstraints gbc = new GridBagConstraints(); // Impostazioni di layout
    private final JPanel panel = new JPanel(new GridBagLayout()); // Pannello principale con layout a griglia
    private JButton backButton; // Pulsante per tornare indietro
    private final boolean saveGame; // Flag per determinare se salvare il gioco o meno
    private int idMetch; // ID del salvataggio corrente

    /**
     * Costruttore della classe `PannelMetchSaved`.
     * Gestisce l'inizializzazione del pannello e il recupero dei dati dei salvataggi dal backend.
     * @param new_port Porta da utilizzare per la comunicazione
     * @param save_game Flag che indica se il gioco deve essere salvato o meno
     */
    public PannelMetchSaved(int new_port, boolean save_game) {
        this.saveGame = save_game;
        gbc.insets = new Insets(10, 10, 10, 10); // Imposta i margini per i componenti

        // Gestione delle eccezioni per la richiesta al backend
        JSONObject response;
        try {
            response = EndpointApi.getListMetchSaved(); // Recupera la lista delle partite salvate dal backend
            System.out.println("Risposta dal backend: " + response);
        } // fine del blocco try
        catch (Exception e) { // processa l'eccezione
            // Mostra un messaggio di errore in caso di fallimento nella connessione
            JOptionPane.showMessageDialog(null, "Errore nel recupero delle partite salvate:\n" + e.getMessage(),
                    "Errore di Connessione", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        } // fine del blocco catch

        // Creazione del dizionario per memorizzare i nomi dei salvataggi in base all'ID
        Map<Integer, String> saveNameMap = new HashMap<>();

        // iterazione per creare i bottoni dei salvataggi
        for (int i = 0; i < N_SAVES; i++) {
            JButton button = new JButton(); // crea un nuovo bottone
            String name = "empty"; // Nome di default per il bottone

            // Itera sulla risposta del backend per ottenere i dati dei salvataggi
            for (String key : response.keySet()) {
                if (Integer.parseInt(key) == i) {
                    name = response.get(key).toString();
                } // fine di if
            } // fine di for interno

            // Se il nome non è vuoto
            if (!name.equals("empty")){
                try {
                    // Aggiorna il testo del bottone con i dati del salvataggio
                    JSONObject text = new JSONObject(name);
                    String nomePartita = (String) text.get("Nome");
                    String ultimaStanza = (String) text.get("NomeUltimaStanza");
                    // Formatta il testo per includere il nome della partita e l'ultima stanza
                    name = "<html>Nome partita: " + nomePartita + "<br>Ultima stanza: " + ultimaStanza + "</html>";
                } // fine del blocco try
                catch (Exception e){
                    System.out.println("error");
                } // fine del blocco catch
            } // fine di if

            button.setText(name); // Imposta il testo del bottone con il nome del salvataggio
            int index = i; // Memorizza l'indice corrente per l'azione del bottone
            saveNameMap.put(index, name); // Memorizza il nome del salvataggio nel dizionario

            // Aggiunge un action listener per gestire il click del bottone
            button.addActionListener(e -> {
                if (button.isEnabled()) {
                    // Se il gioco deve essere salvato
                    if (saveGame) {
                        // mostra e gestisce l'area di testo e il pulsante di invio
                        sendSaveNameButton.setVisible(true);
                        saveNameTextArea.setVisible(true);
                        idMetch = index; // Memorizza l'ID del salvataggio selezionato
                    } // fine di if
                    else { // altrimenti
                        try {
                            // Avvia un nuovo gioco con l'ID del salvataggio specificato
                            new StartNewGame(EndpointApi.startNewGame(index));
                        } // fine del blocco try
                        catch (Exception exception) { // processa l'eccezione
                            // Mostra un messaggio di errore in caso di problemi nell'avvio del gioco
                            JOptionPane.showMessageDialog(null, "Partita inesistente:\n" + exception.getMessage(),
                                    "Errore", JOptionPane.ERROR_MESSAGE);
                            exception.printStackTrace();
                        } // fine del blocco catch
                    } // fine di else
                } // fine di if
            }); // fine dell'action listener su button

            button.setPreferredSize(new Dimension(210, 200)); // Imposta le dimensioni del bottone
            // Imposta la posizione del bottone nella griglia del pannello principale
            gbc.gridx = i % 2;
            gbc.gridy = i / 2;
            panel.add(button, gbc); // Aggiunge il bottone al pannello principale
        } // fine del for

        // Disegna gli elementi aggiuntivi nel pannello se il gioco deve essere salvato
        if (save_game) {
            drawElementInPanel(new_port); // Metodo per disegnare gli elementi aggiuntivi
            sendSaveNameButton.setVisible(false);
            saveNameTextArea.setVisible(false);
        } // fine di if

        // Aggiunge il pulsante "Indietro" alla fine della griglia
        backButton = new JButton("Indietro");
        backButton.setPreferredSize(new Dimension(80, 30));
        backButton.addActionListener(e -> RootFrame.setPrecPannel()); // Azione per tornare indietro

        // Imposta la posizione del pulsante "Indietro" nella griglia
        gbc.gridx = 0;
        gbc.gridy = 3; // Riga sotto la griglia dei bottoni
        gbc.anchor = GridBagConstraints.SOUTHWEST; // Ancoraggio in basso a sinistra
        panel.add(backButton, gbc);

        RootFrame.setPanel(panel); // Imposta il pannello principale nel frame radice
    } // fine del costruttore PannelMetchSaved


    /**
     * Metodo per disegnare elementi aggiuntivi nel pannello quando il gioco deve essere salvato.
     * @param new_port La porta da utilizzare per la comunicazione.
     */
    public void drawElementInPanel(int new_port) {
        // Creazione della JTextArea per l'inserimento del nome del salvataggio
        saveNameTextArea = new JTextArea();
        saveNameTextArea.setLineWrap(true); // Abilitazione del wrap del testo
        saveNameTextArea.setWrapStyleWord(true); // Wrap delle parole
        saveNameTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        saveNameTextArea.setPreferredSize(new Dimension(80, 30)); // Dimensioni personalizzate per la JTextArea
        saveNameTextArea.setToolTipText("Inserici il nome della partita"); // Suggerimento per l'utente

        // Creazione del pulsante "Invio" per inviare il nome del salvataggio
        sendSaveNameButton = new JButton("Invio");
        sendSaveNameButton.setPreferredSize(new Dimension(80, 30)); // Dimensioni personalizzate per il pulsante "Invio"

        // Configurazione della posizione della JTextArea nella griglia
        gbc.gridx = 1;  // Colonna 1 (destra rispetto alla griglia dei bottoni)
        gbc.gridy = N_SAVES / 2 + 1; // Riga sotto la griglia di bottoni di salvataggio
        gbc.gridwidth = 1; // Larghezza della JTextArea (1 cella)
        gbc.gridheight = 1; // Altezza della JTextArea (1 cella)
        gbc.fill = GridBagConstraints.HORIZONTAL; // Riempi lo spazio sia in larghezza che in altezza
        gbc.anchor = GridBagConstraints.SOUTHEAST; // Ancoraggio in basso a destra
        gbc.weightx = 1.0; // Peso per l'espansione orizzontale
        gbc.weighty = 1.0; // Peso per l'espansione verticale
        panel.add(saveNameTextArea, gbc); // Aggiungi la JTextArea al pannello principale

        // Configurazione della posizione del pulsante "Invio" nella griglia del pannello
        gbc.gridx = N_SAVES % 2; // Colonna destra rispetto alla griglia dei bottoni
        gbc.gridy = N_SAVES / 2 + 1; // Riga sotto la griglia dei bottoni di salvataggio
        gbc.gridheight = 1; // Altezza del pulsante "Invio"
        gbc.fill = GridBagConstraints.NONE; // Non riempire lo spazio
        gbc.weightx = 0.0; // Nessun peso per l'espansione orizzontale
        gbc.weighty = 0.0; // Nessun peso per l'espansione verticale
        panel.add(sendSaveNameButton, gbc); // Aggiunge il pulsante "Invio" al pannello principale

        // Azione del pulsante "Invio" per salvare il nome della partita
        sendSaveNameButton.addActionListener(e -> {
            try {
                // Invia la richiesta al backend per salvare la partita con il nome inserito
                JSONObject res = EndpointApi.saveMetch(new_port, idMetch, saveNameTextArea.getText());
                RootFrame.setPrecPannel(); // torna al pannello principale
                System.out.println(saveNameTextArea.getText());
            } // fine del blocco try
            catch (Exception ex) { // processa l'eccezione
                // Mostra un messaggio di errore in caso di problemi durante il salvataggio
                JOptionPane.showMessageDialog(null, "Errore durante il salvataggio della partita:\n" + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } // fine del blocco catch
        }); // fine dell'action listener

        // Aggiungi il pulsante "Indietro" alla fine della griglia
        backButton = new JButton("Indietro");
        backButton.setPreferredSize(new Dimension(80, 30));
        backButton.addActionListener(e -> RootFrame.setPrecPannel()); // Azione per tornare indietro

        // Imposta la posizione del pulsante "Indietro" nella griglia
        gbc.gridx = 0;
        gbc.gridy = 3; // Riga sotto la griglia di bottoni
        gbc.anchor = GridBagConstraints.SOUTHWEST; // Ancoraggio in basso a sinistra
        panel.add(backButton, gbc);
    } // fine del metodo drawElementInPanel

}  // fine della classe PannelMetchSaved