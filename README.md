# Bibertomograph
```text
    ____  _ __              __                                               __  
   / __ )(_) /_  ___  _____/ /_____  ____ ___  ____  ____ __________ _____  / /_ 
  / __  / / __ \/ _ \/ ___/ __/ __ \/ __ `__ \/ __ \/ __ `/ ___/ __ `/ __ \/ __ \
 / /_/ / / /_/ /  __/ /  / /_/ /_/ / / / / / / /_/ / /_/ / /  / /_/ / /_/ / / / /
/_____/_/_.___/\___/_/   \__/\____/_/ /_/ /_/\____/\__, /_/   \__,_/ .___/_/ /_/ 
                                                  /____/          /_/            
```
Seminararbeit zur Aufgabe 4 des Bundeswettbewerbs Informatik (BwInf).

## Worum gehts?

Man hat ein n×n Raster und kennt nur die Summen der gefüllten Felder in jeder Zeile, Spalte und Diagonale. Daraus soll das Programm die ursprüngliche Figur rekonstruieren. Falls es mehrere Lösungen gibt, werden die unsicheren Felder mit `?` markiert.

## Wie funktionierts?

Der Solver nutzt Backtracking mit ein paar Tricks:
- Wenn klar ist was in eine Zelle muss, wird sie direkt gesetzt (Constraint Propagation)
- Eine Heatmap schätzt für jede Zelle wie wahrscheinlich sie gefüllt ist
- Bevor ein Wert gesetzt wird, wird geprüft ob er überhaupt noch möglich ist

## Projektstruktur

```
├── src/
│   ├── Main.java
│   ├── Grid.java
│   ├── Constraints.java
│   ├── InputParser.java
│   └── HeuristicSolver.java
├── testdata/
│   ├── tomograph00.txt
│   ├── tomograph01.txt
│   └── ...
└── README.md
```

## Ausführen

```bash
cd src
javac *.java
java Main ../testdata/tomograph02.txt
```

## Ausgabe

- `#` = gefüllt
- `.` = leer
- `?` = mehrdeutig

## Dokumentation

Die vollständige Seminararbeit mit theoretischen Grundlagen, Algorithmusbeschreibung und Beispielen findest du in [docs/main.pdf](docs/main.pdf).