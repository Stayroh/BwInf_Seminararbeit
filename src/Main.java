import java.util.List;

/**
 * Hauptprogramm für den Bibertomograph-Solver.
 * 
 * Verwendung: java Main <eingabedatei.txt>
 */
public class Main {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Verwendung: java Main <eingabedatei.txt>");
            System.out.println("Beispiel: java Main tomograph00.txt");
            System.exit(1);
        }
        
        String filename = args[0];
        
        try {
            System.out.println("=== Bibertomograph Solver ===");
            System.out.println("Lade Datei: " + filename);
            System.out.println();
            
            // Eingabe parsen
            Constraints constraints = InputParser.parse(filename);
            System.out.println(constraints);
            
            // Heatmap anzeigen
            System.out.println("Berechnete Heatmap:");
            double[][] heatmap = constraints.computeHeatmap();
            printHeatmap(heatmap);
            System.out.println();
            
            // Solver starten
            System.out.println("Starte Solver...");
            long startTime = System.currentTimeMillis();
            
            HeuristicSolver solver = new HeuristicSolver(constraints);
            solver.setMaxSolutions(100); // Suche bis zu 100 Lösungen
            
            List<Grid> solutions = solver.solve();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Ergebnisse ausgeben
            System.out.println();
            System.out.println("=== Ergebnisse ===");
            System.out.println("Gefundene Lösungen: " + solutions.size());
            System.out.println("Durchsuchte Knoten: " + solver.getNodeCount());
            System.out.println("Laufzeit: " + duration + " ms");
            System.out.println();
            
            if (solutions.isEmpty()) {
                System.out.println("Keine Lösung gefunden!");
            } else if (solutions.size() == 1) {
                System.out.println("Eindeutige Lösung:");
                System.out.println(solutions.get(0));
            } else {
                System.out.println("Mehrere Lösungen gefunden. Kombinierte Ausgabe (? = mehrdeutig):");
                Grid combined = HeuristicSolver.combineSolutions(solutions);
                System.out.println(combined);
                
                System.out.println("Erste 3 Lösungen:");
                for (int i = 0; i < Math.min(3, solutions.size()); i++) {
                    System.out.println("Lösung " + (i + 1) + ":");
                    System.out.println(solutions.get(i));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Gibt die Heatmap formatiert aus.
     */
    private static void printHeatmap(double[][] heatmap) {
        int size = heatmap.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.printf("%.2f ", heatmap[i][j]);
            }
            System.out.println();
        }
    }
}
