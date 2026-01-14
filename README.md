## Nome gruppo
Bug-busters
## Membri del gruppo


| Nome              | Matricola | Email                         |
|-------------------|-----------|-------------------------------|
| Matteo Cervini    | 902225    | m.cervini1@campus.unimib.it   |
| Andrea Aivaliotis | 903571    | a.aivaliotis@campus.unimib.it |
| Le Yang Shi       | 894536    | l.shi1@campus.unimib.it       |
## Come testare il progetto
Per testare il progetto, seguire questi passaggi:

Clonare il repository:
```bash
git clone https://gitlab.com/gruppo10-bug-busters/2025_assignment3_gruppo10-bug-busters
```
Prerequisiti:
Assicurarsi di avere installato Maven e la versione 21 del Java Development Kit (JDK) sul proprio sistema:

Eseguire tutti i test:
```bash
mvn clean test
```   

Se si volesse avviare il programma principale, utilizzare:
```bash
mvn spring-boot:run
```

Il progetto utilizza sia per production che per test il database H2 in memoria.
