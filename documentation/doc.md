## Nome gruppo
Bug-busters
## Membri del gruppo
| Nome              | Matricola | Email                         |
|-------------------|-----------|-------------------------------|
| Matteo Cervini    | 902225    | m.cervini1@campus.unimib.it   |
| Andrea Aivaliotis | 903571    | a.aivaliotis@campus.unimib.it |
| Le Yang Shi       | 894536    | l.shi1@campus.unimib.it       |

# Il dominio scelto

Abbiamo modellato una versione minimale di un sistema di gestione task simile a Trello/Notion, focalizzandoci sulle entità e sulle regole che governano assegnazioni, team e supervisione. Lo scopo è rappresentare in modo semplice:
- Come vengono create e viste le task,
- Come i team condividono le task,
- Come si struttura la gerarchia dei supervisori.

 
# Analisi di dominio
## Diagramma delle classi
![Diagramma delle classi](class_diagram.png)

## Analisi del diagramma delle classi

Breve panoramica delle entità principali e del loro ruolo nel modello:

- Person (astratta)
  - Offre attributi comuni a tutte le entità che modellano un attore che possiamo definire attivo(non 
    classe attiva),
    ossia l'effettivo utente di base: personId(che viene ereditato dalle entità concrete), nome, cognome,
    email.
  - Entità che estendono Persona: Dipendente e Supervisore.
  - Per il dominio, non si esclude che in futuro possano esserci altre entità che estendono Persona
   come potrebbe essere un esperto di dominio oppure un consulente che ha partita IVA che viene assunto
   momentaneamente, o altri possibili casi.

- Employee
  - Attributi: il suo ruolo, stipendio mensile, task assegnate e team del quale è membro
  - È una specializzazione di Person
  - Relazioni:
    - Ha assegnate a 1..n Task, ha una relazione molti a molti con un Task: 
      - una task può essere assegnata a più dipendenti
      - un dipendente può avere più task assegnate
    - Appartiene a 0..1 Team (un dipendente può non essere in un team).
    - È possibile promuovere un Employee a Supervisor

- Supervisor
  - Attributi: il proprio supervisore, una collezione di supervisori supervisionati e il team supervisionato
  - È una specializzazione di Employee (eredita quindi anche da Person)
  - Relazioni gerarchiche: 
    - Ha 0..1 capi (un supervisore può non avere un capo);
    - Ha 0..n subordinati (altri Supervisors) che supervisiona.
    - Ha 0..1 Team supervisionato (un supervisor supervisiona esattamente un team oppure nessuno).
  - Vincoli: la gerarchia deve essere aciclica; non è permesso che un supervisor sia suo proprio
  subordinato.

- Task
  - Attributi: il suo stato, una collezione di dipendenti ai quali è stata assegnata, e
    il team al quale è stata assegnata. Riporta inoltre l'Employee che l'ha accettata,
    la data di inizio e di chiusura.
  - Ha 0..1 Team ai quali è assegnata (una task può essere assegnata a un team oppure a nessuno):
  quello specifico task può essere assegnata a un solo team
  - Ha 1..n Employees ai quali è stata assegnata, ha una relazione molti a molti con Employee,
  ma una task può essere accettata da un solo Employee:
    - Una task può essere assegnata a più Employees
    - Un Employee può avere più task assegnate
  
- Team
  - Attributi: una collezione di dipendenti membri, il supervisore del team
  e una collezione di Task assegnate al Team.
  - Relazioni:
    - 1..n Employee (un team ha membri; attenzione: un Employee può appartenere ad al più un Team).
    - 0..* Task assegnate al team (visibili a tutti i membri).
    - Un Supervisor (un Team può avere un Supervisor oppure nessuno).

- Enum / Tipi di supporto
  - EmployeeRole 
  - TaskState

# Descrizione funzionale delle classi di persistenza JPA
Tutti i repository estendono `JpaRepository<ID, Long>`, che fornisce metodi CRUD di base. Non è stato
necessaria la creazione una repository di base `BaseRepository<ID, Long> extends JpaRepository<ID, Long>`
poiché abbiamo concluso che non ci sono metodi comuni necessari a tutte le entità.
Non verranno trattati tutti i metodi CRUD di base forniti da `JpaRepository`, ma solo i metodi
personalizzati implementati per soddisfare i requisiti specifici del dominio.

## 1. DipendenteRepository
La repository `DipendenteRepository` gestisce la persistenza dell’entità **Dipendente**.
### Metodi personalizzati
- Recupera i dipendenti con uno specifico **stipendio mensile**
- Recupera i dipendenti con uno specifico **stipendio mensile**, ordinati per **ruolo** (crescente)
- Recupera i dipendenti con uno specifico **stipendio mensile**, ordinati per **ruolo** (decrescente)
- Recupera i dipendenti con uno specifico **ruolo**
- Recupera i dipendenti con uno specifico **ruolo**, ordinati per **stipendio mensile** (crescente)
- Recupera i dipendenti con uno specifico **ruolo**, ordinati per **stipendio mensile** (decrescente)
- Recupera i **task** associati a un dipendente specifico, filtrati per **stato del task**

## 2. SupervisoreRepository
La repository `SupervisoreRepository` gestisce la persistenza dell’entità **Supervisore**.
### Metodi personalizzati
- Recupera i **supervisori che non hanno un supervisore** (livello più alto della gerarchia)
- Recupera i **supervisori senza subordinati**
- Recupera i **supervisori che non supervisionano alcun team**

# Descrizione funzionale delle classi di servizio
Le classi service offrono metodi "complessi" che combinano più operazioni di persistenza e
implementano la logica di business specifica del dominio. Questi metodi orchestrano le chiamate ai
repository e applicano le regole di business necessarie. Seppure avvengano operazioni di controllo
null sugli input, alcuni metodi dei service sono reputabili **wrapper** dei repository.

## 1. DipendenteService

La classe `DipendenteService` gestisce le operazioni di business relative all’entità **Dipendente**, 
inclusi ruoli, stipendi e task.

### Controlli preliminari
Tutti i parametri in input devono essere validati per garantire che non siano null.

### CRUD di base
- Salvataggio di un dipendente
- Salvataggio di una lista di dipendenti
- Recupero di un dipendente per ID (normale o reference)
- Recupero di tutti i dipendenti
- Rimozione di un dipendente per ID

### CRUD avanzato e funzionalità specifiche
- Licenziamento di un dipendente per Id o lista di Dipendenti(accessibile solo se il dipendente che
richiede l'operazione è un MANAGER)
- Cerca tutti i dipendenti con stipendio mensile uguale, maggiore, minore o in un intervallo di un 
certo valore e ordinato per ruolo(accessibile solo se il dipendente che richiede l'operazione è un 
(`MANAGER`)
- Cerca tutti i dipendenti con ruolo uguale, maggiore, minore o in un intervallo di un
  certo valore e ordinato per stipendio mensile(accessibile solo se il dipendente che richiede
l'operazione è un (`MANAGER`))
- Si può modificare il salario mensile e il ruolo di un dipendente fornito il corretto ID
(accessibile solo se il dipendente
che richiede l'operazione è un (`MANAGER`))
- Cerca un insieme di task dato l'Id del dipendente e un filtro sullo stato della task
- Due metodi helper controllano che gli ID e gli oggetti passati non siano Null tramite
    Optional.requireNonNull. Forniscono inoltre il controllo per i (`MANAGER`).
Se un oggetto non è (`MANAGER`)(e dovrebbe esserlo) oppure non esiste un dipendente con l'Id
fornito, viene lanciata l'eccezione: (`IllegalArgumentException`). Vengono forniti
messaggi di errore memorizzati nel file: EmployeeContants.
- 
## 2. SupervisoreService

La classe `SupervisoreService` gestisce le operazioni di business relative all’entità **Supervisore** e alle **relazioni gerarchiche** tra supervisori.

### Controlli preliminari
Tutti i parametri in input devono essere validati per garantire che non siano null.

### CRUD di base
- Salvataggio di un supervisore
- Ricerca di un supervisore per ID
- Recupero di tutti i supervisori
- Eliminazione di un supervisore per ID 

### CRUD avanzato e funzionalità specifiche
- Assegnazione di un subordinato a un supervisore 
- Rimozione di un subordinato da un supervisore
- Ricerca di supervisori che non hanno supervisore (radici della gerarchia)
- Ricerca di tutti i supervisori che non hanno subordinati (foglie della gerarchia oppure utile per
a quali supervisori non è stato assegnato nessun subordinato)
- Ricerca di tutti i supervisori non che supervisionano un team (foglie della gerarchia oppure utile per
  a quali supervisori non è stato assegnato nessun team)
- - Due metodi helper controllano che gli ID e gli oggetti passati non siano Null tramite
    Optional.requireNonNull. Forniscono inoltre il controllo per i (`SUPERVISORI`) ai quali si sta
cercando di assegnare un subordinato che creerebbe un ciclo nella gerarchia dei supervisori.
    Se un oggetto (`SUPERVISORE`) che è in fase di assegnamento come subordinato crea un loop,
    oppure non esiste un dipendente con l'Id fornito, viene lanciata l'eccezione: 
- (`IllegalArgumentException`). Vengono forniti messaggi di errore memorizzati nel file: 
SupervisorContants.
---

## 3. TaskService

## 4. TeamService

# Facade
- La classe `Facade` è un **wrapper** verso i layer di **service**, esponendo un punto di accesso
unificato alle funzionalità dell’applicazione.
- Non è stata definita una **interfaccia comune della Facade**, poiché la classe è utilizzata 
**esclusivamente a supporto dei test** e non come API pubblica dell’applicazione.
- L’assenza di un’interfaccia non compromette **robustezza e manutenibilità**, dato il ruolo limitato 
della Facade nel progetto.
- Una versione a **interfaccia** sarebbe risultata utile solo nel caso di una **suddivisione della 
Facade in più Facade specifiche per servizio** (es. EmployeeFacade, SupervisorFacade, TaskFacade e TeamFacade).



