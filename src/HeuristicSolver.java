import java.util.ArrayList;
import java.util.List;

/**
 * Heuristik-gesteuerter Backtracking-Solver für das Bibertomograph-Problem.
 * 
 * Verwendet:
 * - Heatmap-basierte Zellauswahl (Most Constrained + Confidence)
 * - Constraint Propagation (erzwungene Zellen erkennen)
 * - Forward Checking (unmögliche Zustände früh erkennen)
 */
public class HeuristicSolver {
    private final Constraints constraints;
    private final double[][] heatmap;
    private final int size;
    private List<Grid> solutions;
    private int maxSolutions;
    private long nodeCount;
    
    public HeuristicSolver(Constraints constraints) {
        this.constraints = constraints;
        this.size = constraints.getSize();
        this.heatmap = constraints.computeHeatmap();
        this.solutions = new ArrayList<>();
        this.maxSolutions = 100; // Limit für Lösungssuche
        this.nodeCount = 0;
    }
    
    /**
     * Setzt das Maximum der zu findenden Lösungen.
     */
    public void setMaxSolutions(int max) {
        this.maxSolutions = max;
    }
    
    /**
     * Löst das Problem und gibt alle gefundenen Lösungen zurück.
     */
    public List<Grid> solve() {
        solutions.clear();
        nodeCount = 0;
        
        Grid grid = new Grid(size);
        
        // Initiale Propagation
        if (propagate(grid)) {
            backtrack(grid);
        }
        
        return solutions;
    }
    
    /**
     * Gibt die Anzahl der durchsuchten Knoten zurück.
     */
    public long getNodeCount() {
        return nodeCount;
    }
    
    /**
     * Hauptbacktracking-Algorithmus.
     */
    private void backtrack(Grid grid) {
        if (solutions.size() >= maxSolutions) {
            return;
        }
        
        nodeCount++;
        
        // Wähle nächste Zelle basierend auf Heuristik
        int[] nextCell = selectNextCell(grid);
        
        // Wenn keine unzugewiesene Zelle mehr existiert, prüfe ob Lösung gültig
        if (nextCell == null) {
            if (isValidSolution(grid)) {
                solutions.add(grid.copy());
            }
            return;
        }
        
        int row = nextCell[0];
        int col = nextCell[1];
        
        // Bestimme Reihenfolge der Werte basierend auf Heatmap
        int[] valuesToTry;
        if (heatmap[row][col] >= 0.5) {
            valuesToTry = new int[]{1, 0};
        } else {
            valuesToTry = new int[]{0, 1};
        }
        
        for (int value : valuesToTry) {
            if (solutions.size() >= maxSolutions) {
                return;
            }
            
            // Forward Check: Ist dieser Wert überhaupt möglich?
            if (!isValueFeasible(grid, row, col, value)) {
                continue;
            }
            
            // Kopiere Grid und setze Wert
            Grid newGrid = grid.copy();
            newGrid.set(row, col, value);
            
            // Constraint Propagation
            if (propagate(newGrid)) {
                backtrack(newGrid);
            }
        }
    }
    
    /**
     * Wählt die nächste zu belegende Zelle basierend auf:
     * - Constraint Tightness (wie wenige Optionen bleiben)
     * - Heatmap Confidence (wie sicher sind wir uns)
     */
    private int[] selectNextCell(Grid grid) {
        int bestRow = -1;
        int bestCol = -1;
        double bestScore = Double.NEGATIVE_INFINITY;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid.isAssigned(i, j)) {
                    continue;
                }
                
                // Berechne Score für diese Zelle
                double score = computeCellScore(grid, i, j);
                
                if (score > bestScore) {
                    bestScore = score;
                    bestRow = i;
                    bestCol = j;
                }
            }
        }
        
        if (bestRow == -1) {
            return null;
        }
        return new int[]{bestRow, bestCol};
    }
    
    /**
     * Berechnet einen Score für die Zellauswahl.
     * Höhere Scores = sollte früher belegt werden.
     */
    private double computeCellScore(Grid grid, int row, int col) {
        // Constraint Tightness: Wie "voll" sind die Linien durch diese Zelle?
        double tightness = 0;
        
        // Zeile
        int rowRemaining = constraints.getRowSum(row) - grid.getRowSum(row);
        int rowUnassigned = grid.getRowUnassigned(row);
        if (rowUnassigned > 0) {
            tightness += 1.0 - (double) rowRemaining / rowUnassigned;
        }
        
        // Spalte
        int colRemaining = constraints.getColSum(col) - grid.getColSum(col);
        int colUnassigned = grid.getColUnassigned(col);
        if (colUnassigned > 0) {
            tightness += 1.0 - (double) colRemaining / colUnassigned;
        }
        
        // Hauptdiagonale: k = row + col
        int diagDownK = row + col;
        int diagDownRemaining = constraints.getDiagDownSum(diagDownK) - grid.getDiagDownSum(diagDownK);
        int diagDownUnassigned = grid.getDiagDownUnassigned(diagDownK);
        if (diagDownUnassigned > 0) {
            tightness += 1.0 - (double) diagDownRemaining / diagDownUnassigned;
        }
        
        // Nebendiagonale: k = col - row + (size-1)
        int diagUpK = col - row + (size - 1);
        int diagUpRemaining = constraints.getDiagUpSum(diagUpK) - grid.getDiagUpSum(diagUpK);
        int diagUpUnassigned = grid.getDiagUpUnassigned(diagUpK);
        if (diagUpUnassigned > 0) {
            tightness += 1.0 - (double) diagUpRemaining / diagUpUnassigned;
        }
        
        // Heatmap Confidence: Wie weit von 0.5 entfernt?
        double confidence = Math.abs(heatmap[row][col] - 0.5) * 2;
        
        // Kombinierter Score (Gewichtung kann angepasst werden)
        return tightness * 0.7 + confidence * 0.3;
    }
    
    /**
     * Prüft ob ein Wert für eine Zelle noch möglich ist (Forward Checking).
     */
    private boolean isValueFeasible(Grid grid, int row, int col, int value) {
        if (value == 1) {
            // Prüfe ob wir noch Platz für eine 1 haben
            if (grid.getRowSum(row) >= constraints.getRowSum(row)) return false;
            if (grid.getColSum(col) >= constraints.getColSum(col)) return false;
            
            int diagDownK = row + col;
            if (grid.getDiagDownSum(diagDownK) >= constraints.getDiagDownSum(diagDownK)) return false;
            
            int diagUpK = col - row + (size - 1);
            if (grid.getDiagUpSum(diagUpK) >= constraints.getDiagUpSum(diagUpK)) return false;
        } else {
            // Prüfe ob wir genug verbleibende Zellen haben für die benötigten 1en
            int rowRemaining = constraints.getRowSum(row) - grid.getRowSum(row);
            if (grid.getRowUnassigned(row) - 1 < rowRemaining) return false;
            
            int colRemaining = constraints.getColSum(col) - grid.getColSum(col);
            if (grid.getColUnassigned(col) - 1 < colRemaining) return false;
            
            int diagDownK = row + col;
            int diagDownRemaining = constraints.getDiagDownSum(diagDownK) - grid.getDiagDownSum(diagDownK);
            if (grid.getDiagDownUnassigned(diagDownK) - 1 < diagDownRemaining) return false;
            
            int diagUpK = col - row + (size - 1);
            int diagUpRemaining = constraints.getDiagUpSum(diagUpK) - grid.getDiagUpSum(diagUpK);
            if (grid.getDiagUpUnassigned(diagUpK) - 1 < diagUpRemaining) return false;
        }
        
        return true;
    }
    
    /**
     * Constraint Propagation: Findet und setzt erzwungene Zellen.
     * Gibt false zurück wenn ein Konflikt erkannt wird.
     */
    private boolean propagate(Grid grid) {
        boolean changed = true;
        
        while (changed) {
            changed = false;
            
            // Prüfe Zeilen
            for (int i = 0; i < size; i++) {
                int result = propagateLine(grid, i, true);
                if (result == -1) return false;
                if (result == 1) changed = true;
            }
            
            // Prüfe Spalten
            for (int j = 0; j < size; j++) {
                int result = propagateLine(grid, j, false);
                if (result == -1) return false;
                if (result == 1) changed = true;
            }
            
            // Prüfe Hauptdiagonalen (k = row + col, range 0 to 2*(size-1))
            for (int k = 0; k <= 2 * (size - 1); k++) {
                int result = propagateDiagDown(grid, k);
                if (result == -1) return false;
                if (result == 1) changed = true;
            }
            
            // Prüfe Nebendiagonalen (k = col - row + (size-1), range 0 to 2*(size-1))
            for (int k = 0; k <= 2 * (size - 1); k++) {
                int result = propagateDiagUp(grid, k);
                if (result == -1) return false;
                if (result == 1) changed = true;
            }
        }
        
        return true;
    }
    
    /**
     * Propagiert Constraints für eine Zeile oder Spalte.
     * Gibt zurück: -1 = Konflikt, 0 = keine Änderung, 1 = Änderung
     */
    private int propagateLine(Grid grid, int index, boolean isRow) {
        int sum = isRow ? grid.getRowSum(index) : grid.getColSum(index);
        int unassigned = isRow ? grid.getRowUnassigned(index) : grid.getColUnassigned(index);
        int target = isRow ? constraints.getRowSum(index) : constraints.getColSum(index);
        
        int remaining = target - sum;
        
        // Konflikt: Mehr gefüllt als erlaubt
        if (remaining < 0) return -1;
        
        // Konflikt: Nicht genug Platz für benötigte Felder
        if (remaining > unassigned) return -1;
        
        // Wenn keine Änderung möglich
        if (unassigned == 0) return 0;
        
        boolean changed = false;
        
        // Alle verbleibenden müssen 1 sein
        if (remaining == unassigned) {
            for (int k = 0; k < size; k++) {
                int i = isRow ? index : k;
                int j = isRow ? k : index;
                if (!grid.isAssigned(i, j)) {
                    grid.set(i, j, 1);
                    changed = true;
                }
            }
        }
        
        // Summe erreicht: alle verbleibenden müssen 0 sein
        if (remaining == 0) {
            for (int k = 0; k < size; k++) {
                int i = isRow ? index : k;
                int j = isRow ? k : index;
                if (!grid.isAssigned(i, j)) {
                    grid.set(i, j, 0);
                    changed = true;
                }
            }
        }
        
        return changed ? 1 : 0;
    }
    
    /**
     * Propagiert Constraints für eine Hauptdiagonale (k = row + col).
     */
    private int propagateDiagDown(Grid grid, int k) {
        int sum = grid.getDiagDownSum(k);
        int unassigned = grid.getDiagDownUnassigned(k);
        int target = constraints.getDiagDownSum(k);
        
        int remaining = target - sum;
        
        if (remaining < 0) return -1;
        if (remaining > unassigned) return -1;
        if (unassigned == 0) return 0;
        
        boolean changed = false;
        
        if (remaining == unassigned) {
            for (int i = 0; i < size; i++) {
                int j = k - i;
                if (j >= 0 && j < size && !grid.isAssigned(i, j)) {
                    grid.set(i, j, 1);
                    changed = true;
                }
            }
        }
        
        if (remaining == 0) {
            for (int i = 0; i < size; i++) {
                int j = k - i;
                if (j >= 0 && j < size && !grid.isAssigned(i, j)) {
                    grid.set(i, j, 0);
                    changed = true;
                }
            }
        }
        
        return changed ? 1 : 0;
    }
    
    /**
     * Propagiert Constraints für eine Nebendiagonale (k = col - row + (size-1)).
     */
    private int propagateDiagUp(Grid grid, int k) {
        int sum = grid.getDiagUpSum(k);
        int unassigned = grid.getDiagUpUnassigned(k);
        int target = constraints.getDiagUpSum(k);
        
        int remaining = target - sum;
        
        if (remaining < 0) return -1;
        if (remaining > unassigned) return -1;
        if (unassigned == 0) return 0;
        
        boolean changed = false;
        
        if (remaining == unassigned) {
            for (int i = 0; i < size; i++) {
                int j = k - (size - 1) + i;
                if (j >= 0 && j < size && !grid.isAssigned(i, j)) {
                    grid.set(i, j, 1);
                    changed = true;
                }
            }
        }
        
        if (remaining == 0) {
            for (int i = 0; i < size; i++) {
                int j = k - (size - 1) + i;
                if (j >= 0 && j < size && !grid.isAssigned(i, j)) {
                    grid.set(i, j, 0);
                    changed = true;
                }
            }
        }
        
        return changed ? 1 : 0;
    }
    
    /**
     * Prüft ob eine vollständige Belegung alle Constraints erfüllt.
     */
    private boolean isValidSolution(Grid grid) {
        // Prüfe Zeilen
        for (int i = 0; i < size; i++) {
            if (grid.getRowSum(i) != constraints.getRowSum(i)) return false;
        }
        
        // Prüfe Spalten
        for (int j = 0; j < size; j++) {
            if (grid.getColSum(j) != constraints.getColSum(j)) return false;
        }
        
        // Prüfe Hauptdiagonalen (k = row + col)
        for (int k = 0; k <= 2 * (size - 1); k++) {
            if (grid.getDiagDownSum(k) != constraints.getDiagDownSum(k)) return false;
        }
        
        // Prüfe Nebendiagonalen (k = col - row + (size-1))
        for (int k = 0; k <= 2 * (size - 1); k++) {
            if (grid.getDiagUpSum(k) != constraints.getDiagUpSum(k)) return false;
        }
        
        return true;
    }
    
    /**
     * Kombiniert mehrere Lösungen zu einer Ausgabe mit '?' für mehrdeutige Felder.
     */
    public static Grid combineSolutions(List<Grid> solutions) {
        if (solutions.isEmpty()) {
            return null;
        }
        
        if (solutions.size() == 1) {
            return solutions.get(0);
        }
        
        int size = solutions.get(0).getSize();
        Grid combined = new Grid(size);
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int firstValue = solutions.get(0).get(i, j);
                boolean allSame = true;
                
                for (Grid solution : solutions) {
                    if (solution.get(i, j) != firstValue) {
                        allSame = false;
                        break;
                    }
                }
                
                if (allSame) {
                    combined.set(i, j, firstValue);
                }
                // Sonst bleibt -1 (wird als '?' ausgegeben)
            }
        }
        
        return combined;
    }
}
