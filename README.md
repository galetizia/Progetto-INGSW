# 🐞 BugBoard26

**BugBoard26** è una piattaforma client-server sviluppata per la gestione collaborativa delle issue all'interno di progetti software.

Il sistema permette agli utenti autorizzati di creare, visualizzare e gestire le segnalazioni, tenendo traccia del loro stato, della priorità, della tipologia e dell'assegnazione. Sono inoltre presenti funzionalità per il filtraggio e l'ordinamento delle issue, la gestione degli allegati, l'archiviazione e l'individuazione delle issue duplicate.

Il progetto è stato sviluppato nell'ambito del corso di **Ingegneria del Software** presso l'**Università degli Studi di Napoli Federico II**, nell'anno accademico 2025/2026.

---

## 🚀 Funzionalità

Le principali funzionalità implementate sono:

- 🔐 autenticazione degli utenti;
- 👤 gestione degli utenti da parte dell'amministratore;
- 🐞 creazione e gestione delle issue;
- 📋 visualizzazione delle issue;
- 🔎 filtraggio e ordinamento delle issue;
- 🏷️ gestione della priorità e della tipologia;
- 🔄 gestione dello stato delle issue;
- 👨‍💻 assegnazione delle issue agli utenti;
- 📎 visualizzazione degli allegati;
- 🗄️ archiviazione delle issue;
- 🔗 gestione delle issue duplicate;
- 🛡️ gestione degli accessi in base al ruolo dell'utente.

---

## 👥 Ruoli

BugBoard26 prevede tre tipologie di utenti:

| Ruolo | Descrizione |
|---|---|
| **Admin** | Gestisce gli utenti e dispone delle funzionalità amministrative del sistema. |
| **Internal User** | Utente interno al progetto che può visualizzare e gestire le issue secondo i permessi previsti. |
| **External User** | Utente esterno al progetto che può accedere alle funzionalità previste per il proprio ruolo. |

---

## 🏗️ Architettura

BugBoard26 adotta un'architettura **client-server** composta da un front-end sviluppato in JavaFX, un back-end realizzato con Spring Boot e un database PostgreSQL.

Il front-end comunica con il back-end attraverso API REST, mentre il back-end si occupa della gestione della logica applicativa e dell'accesso al database.

---

## 🛠️ Tecnologie

### Front-end
- Java
- JavaFX
- FXML
- CSS

### Back-end
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT

### Database
- PostgreSQL

### Build e deployment
- Maven
- Docker
- Docker Compose

### Testing e analisi
- JUnit 5
- Mockito
- SonarQube

---

## ⚙️ Installazione e avvio

### Prerequisiti

Per eseguire BugBoard26 sono necessari:

- **JDK 17** o versione compatibile;
- **Maven**;
- **Docker** e **Docker Compose**.

### 1. Clonazione del repository

Clonare il repository tramite:

    git clone <URL_DEL_REPOSITORY>
    cd BugBoard26

### 2. Configurazione

Prima di avviare l'applicazione, è necessario configurare le variabili d'ambiente utilizzate dal back-end.

Creare un file `.env` nella directory `BugBoard26-backend`, prendendo come riferimento il file `.env.example` presente nel repository.

Il file deve contenere le variabili necessarie per la configurazione del database e dell'applicazione.

> **Nota:** il file `.env` può contenere credenziali e altre informazioni sensibili e non deve essere pubblicato nel repository.

### 3. Avvio del back-end

Accedere alla directory del back-end:

    cd BugBoard26-backend

Avviare il back-end e il database PostgreSQL tramite Docker Compose:

    docker compose up -d

Per verificare che i container siano stati avviati correttamente:

    docker compose ps

Una volta avviati correttamente i servizi, il back-end sarà disponibile all'indirizzo configurato nell'applicazione.

### 4. Avvio del front-end

Accedere alla directory del front-end:

    cd ../BugBoard26-frontend

Il front-end viene fornito come file `.jar`, presente nella directory `target`.

Per avviare l'applicazione è sufficiente eseguire il file `.jar` tramite doppio clic.

In alternativa, è possibile avviare il client da terminale tramite:

    java -jar target/<nome-file>.jar

Una volta avviato, il client JavaFX si collega al back-end attraverso le API REST.
