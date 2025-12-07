/**
 * Speichert die Summenbeschränkungen für Zeilen, Spalten und Diagonalen.
 */
public class Constraints {
    private final int size;
    private final int[] colSums;    // Summen pro Spalte (von links nach rechts)
    private final int[] rowSums;    // Summen pro Zeile (von oben nach unten)
    private final int[] diagDownSums; // Hauptdiagonalen (von oben links nach unten rechts)
    private final int[] diagUpSums;   // Nebendiagonalen (von unten links nach oben rechts)
    
    public Constraints(int size, int[] colSums, int[] rowSums, int[] diagDownSums, int[] diagUpSums) {
        this.size = size;
        this.colSums = colSums;
        this.rowSums = rowSums;
        this.diagDownSums = diagDownSums;
        this.diagUpSums = diagUpSums;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getColSum(int col) {
        return colSums[col];
    }
    
    public int getRowSum(int row) {
        return rowSums[row];
    }
    

    public int getDiagDownSum(int k) {
        return diagDownSums[k];
    }
    

    public int getDiagUpSum(int k) {
        return diagUpSums[k];
    }
    
    public int[] getColSums() {
        return colSums;
    }
    
    public int[] getRowSums() {
        return rowSums;
    }
    
    public int[] getDiagDownSums() {
        return diagDownSums;
    }
    
    public int[] getDiagUpSums() {
        return diagUpSums;
    }
    
    /**
     * Berechnet eine einfache Heatmap basierend auf den Summen.
     * Die Wahrscheinlichkeit für eine Zelle ergibt sich aus dem Durchschnitt
     * der "Dichte" aller Linien, die durch sie laufen.
     */
    public double[][] computeHeatmap() {
        double[][] heatmap = new double[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Berechne Dichte für jede Linie durch diese Zelle
                double rowDensity = (double) rowSums[i] / size;
                double colDensity = (double) colSums[j] / size;
                
                // Hauptdiagonale: k = i + j
                int diagDownK = i + j;
                int diagDownLen = size - Math.abs(diagDownK - (size - 1));
                double diagDownDensity = (double) getDiagDownSum(diagDownK) / diagDownLen;
                
                // Nebendiagonale: k = i + j
                int diagUpK = j - i + (size - 1);
                int diagUpLen = size - Math.abs(diagUpK - (size - 1));
                double diagUpDensity = (double) getDiagUpSum(diagUpK) / diagUpLen;
                
                // Durchschnitt aller Dichten
                heatmap[i][j] = (rowDensity + colDensity + diagDownDensity + diagUpDensity) / 4.0;
            }
        }
        
        return heatmap;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Constraints for ").append(size).append("x").append(size).append(" grid:\n");
        
        sb.append("Column sums: ");
        for (int s : colSums) sb.append(s).append(" ");
        sb.append("\n");
        
        sb.append("Row sums: ");
        for (int s : rowSums) sb.append(s).append(" ");
        sb.append("\n");
        
        sb.append("DiagDown sums: ");
        for (int s : diagDownSums) sb.append(s).append(" ");
        sb.append("\n");
        
        sb.append("DiagUp sums: ");
        for (int s : diagUpSums) sb.append(s).append(" ");
        sb.append("\n");
        
        return sb.toString();
    }
}
