# ☢️ Fallout-Vault32 ☢️
Progetto per l'esame di Metodi Avanzati di Programmazione (MAP)
Università degli Studi di Bari "Aldo Moro"

## 📝 Descrizione del Progetto

Fallout-Vault32 è un'avventura testuale a interfaccia grafica ispirata all'universo post-apocalittico di Fallout. Il progetto implementa un'architettura Client-Server per la gestione di un gioco di ruolo in cui l'utente deve esplorare stanze, raccogliere oggetti e interagire con l'ambiente per fuggire dal Vault 32.

Il software è stato sviluppato come progetto finale per il corso di Metodi Avanzati di Programmazione, focalizzandosi sull'utilizzo della programmazione concorrente, programmazione di rete e gestione dei dati tramite database SQL.

## 🛠️ Tecnologie Utilizzate
 

  - Linguaggio: Java 21

  - Interfaccia Grafica: Java Swing 

  - Backend:RESTful API

  - Database: H2 Database (per il salvataggio delle partite e gestione inventario)

  - Build Tool: Maven

## 🏛️ Architettura e Design Pattern

Il progetto segue una struttura modulare suddivisa in:

  Frontend: Gestisce la visualizzazione delle immagini delle stanze, l'inventario e l'interazione utente tramite pulsanti direzionali personalizzati.

  Server: Espone gli endpoint per la logica di gioco e comunica con il DB.

  Pattern Implementati:

  - Observer: Per aggiornare l'interfaccia dell'inventario in tempo reale.

  - Singleton: Per la gestione della connessione al database e dell'Engine di gioco.

  - Strategy/Command: Per il parsing dei comandi utente.

## 🚀 Come Iniziare

**Prerequisiti**

- JDK 21 o superiore.

- Maven installato sul sistema.

**Configurazione**
1. Clona il repository:
   <pre>
     git clone https://github.com/tuo-username/Fallout-Vault32.git
     cd Fallout-Vault32
    </pre>
3. Configurazione Database:
   Assicurati che il database H2 sia configurato correttamente nella classe JDBC.java. (Nota: Le credenziali sono gestite localmente e non incluse nel repository per sicurezza).

**Esecuzione**
1. Avvia il Server:
   Apri il tuo IDE preferito (intellij, netbeans, vs-code...)
   Naviga nella cartella del server a avvia il file engine.java
   Oppure, naviga nella cartella Server ed esegui nel terminale:
    <pre>
     mvn clean compile
     mvn exec:java -Dexec.mainClass="app.MainServer" 
    </pre>
     
3. Avvia il Client (Frontend):
   Apri il tuo IDE preferito (intellij, netbeans, vs-code...)
   Naviga nella cartella Frontend a avvia il file engine.java
   Oppure, naviga nella cartella Frontend ed esegui nel terminale:
    <pre>
     mvn exec:java -Dexec.mainClass="frontend.RootFrame"
    </pre>
## ✒️ Autori
A cura di:
- Palmisano Domenico
- Zullo Filippo
- Marchese Gabriele
    
