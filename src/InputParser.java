import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Liest die Eingabedatei und erzeugt ein Constraints-Objekt.
 * 
 * Dateiformat:
 * 1. Zeile: n (Größe des Rasters)
 * 2. Zeile: Spaltensummen (von links nach rechts)
 * 3. Zeile: Zeilensummen (von oben nach unten)
 * 4. Zeile: Hauptdiagonalsummen (von oben links nach unten rechts)
 * 5. Zeile: Nebendiagonalsummen (von unten links nach oben rechts)
 */
public class InputParser {
    
    /**
     * Parst eine Eingabedatei und gibt die Constraints zurück.
     */
    public static Constraints parse(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // 1. Zeile: Größe n
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Datei ist leer");
            }
            int size = Integer.parseInt(line.trim());
            
            // 2. Zeile: Spaltensummen
            line = reader.readLine();
            if (line == null) {
                throw new IOException("Spaltensummen fehlen");
            }
            int[] colSums = parseIntArray(line, size);
            
            // 3. Zeile: Zeilensummen
            line = reader.readLine();
            if (line == null) {
                throw new IOException("Zeilensummen fehlen");
            }
            int[] rowSums = parseIntArray(line, size);
            
            // 4. Zeile: Hauptdiagonalsummen (2*n - 1 Werte)
            line = reader.readLine();
            if (line == null) {
                throw new IOException("Hauptdiagonalsummen fehlen");
            }
            int[] diagDownSums = parseIntArray(line, 2 * size - 1);
            
            // 5. Zeile: Nebendiagonalsummen (2*n - 1 Werte)
            line = reader.readLine();
            if (line == null) {
                throw new IOException("Nebendiagonalsummen fehlen");
            }
            int[] diagUpSums = parseIntArray(line, 2 * size - 1);
            
            return new Constraints(size, colSums, rowSums, diagDownSums, diagUpSums);
        }
    }
    
    /**
     * Parst eine Zeile mit durch Leerzeichen getrennten Zahlen.
     */
    private static int[] parseIntArray(String line, int expectedLength) throws IOException {
        String[] parts = line.trim().split("\\s+");
        if (parts.length != expectedLength) {
            throw new IOException("Erwartete " + expectedLength + " Werte, aber " + parts.length + " gefunden: " + line);
        }
        int[] result = new int[expectedLength];
        for (int i = 0; i < expectedLength; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }
}
