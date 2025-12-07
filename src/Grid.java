/**
 * Repräsentiert das n×n Raster für den Bibertomographen.
 * Jede Zelle kann den Wert -1 (nicht zugewiesen), 0 (leer) oder 1 (gefüllt) haben.
 */
public class Grid {
    private final int size;
    private final int[][] cells;
    
    public Grid(int size) {
        this.size = size;
        this.cells = new int[size][size];
        // Initialisiere alle Zellen als nicht zugewiesen
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = -1;
            }
        }
    }
    
    /**
     * Kopiert das Grid (für Backtracking).
     */
    public Grid copy() {
        Grid copy = new Grid(size);
        for (int i = 0; i < size; i++) {
            System.arraycopy(cells[i], 0, copy.cells[i], 0, size);
        }
        return copy;
    }
    
    public int getSize() {
        return size;
    }
    
    public int get(int row, int col) {
        return cells[row][col];
    }
    
    public void set(int row, int col, int value) {
        cells[row][col] = value;
    }
    
    public boolean isAssigned(int row, int col) {
        return cells[row][col] != -1;
    }
    
    /**
     * Gibt die Anzahl der zugewiesenen Zellen zurück.
     */
    public int countAssigned() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] != -1) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * Gibt die Summe der gefüllten Zellen in einer Zeile zurück (nur zugewiesene).
     */
    public int getRowSum(int row) {
        int sum = 0;
        for (int j = 0; j < size; j++) {
            if (cells[row][j] == 1) {
                sum++;
            }
        }
        return sum;
    }
    
    /**
     * Gibt die Anzahl der nicht zugewiesenen Zellen in einer Zeile zurück.
     */
    public int getRowUnassigned(int row) {
        int count = 0;
        for (int j = 0; j < size; j++) {
            if (cells[row][j] == -1) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gibt die Summe der gefüllten Zellen in einer Spalte zurück.
     */
    public int getColSum(int col) {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            if (cells[i][col] == 1) {
                sum++;
            }
        }
        return sum;
    }
    
    /**
     * Gibt die Anzahl der nicht zugewiesenen Zellen in einer Spalte zurück.
     */
    public int getColUnassigned(int col) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (cells[i][col] == -1) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gibt die Summe der gefüllten Zellen in einer Hauptdiagonale zurück.
     * Index k = row + col, Bereich: 0 bis 2*(size-1)
     */
    public int getDiagDownSum(int k) {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            int j = k - i;
            if (j >= 0 && j < size && cells[i][j] == 1) {
                sum++;
            }
        }
        return sum;
    }
    
    /**
     * Gibt die Anzahl der nicht zugewiesenen Zellen in einer Hauptdiagonale zurück.
     */
    public int getDiagDownUnassigned(int k) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            int j = k - i;
            if (j >= 0 && j < size && cells[i][j] == -1) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gibt die Summe der gefüllten Zellen in einer Nebendiagonale zurück.
     * Index k = col - row + (size-1), Bereich: 0 bis 2*(size-1)
     */
    public int getDiagUpSum(int k) {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            int j = k - (size - 1) + i;
            if (j >= 0 && j < size && cells[i][j] == 1) {
                sum++;
            }
        }
        return sum;
    }
    
    /**
     * Gibt die Anzahl der nicht zugewiesenen Zellen in einer Nebendiagonale zurück.
     */
    public int getDiagUpUnassigned(int k) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            int j = k - (size - 1) + i;
            if (j >= 0 && j < size && cells[i][j] == -1) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gibt das Grid als String aus.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] == 1) {
                    sb.append('#');
                } else if (cells[i][j] == 0) {
                    sb.append('.');
                } else {
                    sb.append('?');
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
