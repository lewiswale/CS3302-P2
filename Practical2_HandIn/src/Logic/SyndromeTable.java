package Logic;

/**
 * Class for syndrome tables
 */
public class SyndromeTable {
    private String[][] table;
    private int rows;
    private int r;

    /**
     * Syndrome table constructor
     * @param r R value
     */
    public SyndromeTable(int r) {
        this.r = r;
        this.rows = (int) Math.pow(2, r);
        this.table = new String[rows][2];
        this.fillTable();
    }

    /**
     * Fills the syndrome table
     */
    public void fillTable() {
        int rowCount = rows - 1;
        int powerBoundary = rows - r - 1;
        table[0][0] = String.format("%" + (rows - 1) + "s", "").replace(" ", "0");
        table[0][1] = String.format("%" + r + "s", "").replace(" ", "0");

        table[powerBoundary][1] = String.format("%" + r + "s", "").replace(" ", "1");
        rowCount--;

        int afterBoundaryCount = powerBoundary + 1;
        int beforeBoundaryCount = 1;

        for (; rowCount >= 0 ; rowCount--) {
            String bitStr = Integer.toBinaryString(rowCount);
            if (bitStr.length() < r) {
                bitStr = String.format("%" + r + "s", bitStr).replace(" ", "0");
            }
            if ((rowCount & (rowCount - 1)) == 0) {
                if (rowCount != 0) {
                    table[afterBoundaryCount][1] = bitStr;
                }
                table[rows - rowCount - 1][0] = buildE(rows - 1 - rowCount, rows - 1);
                afterBoundaryCount++;
            } else {
                if (!bitStr.matches("1+")) {
                    table[beforeBoundaryCount][1] = bitStr;
                }
                table[rows - rowCount - 1][0] = buildE(rows - 1 - rowCount, rows - 1);
                beforeBoundaryCount++;
            }
        }
    }

    /**
     * Builds syndrome string
     * @param n Column number
     * @param l Length of syndrome string
     * @return  Syndrome string
     */
    public String buildE(int n, int l) {
        StringBuilder str = new StringBuilder();

        for (int i = 1; i <= l; i++) {
            if (i == n) {
                str.append("1");
            } else {
                str.append("0");
            }
        }

        return str.toString();
    }

    /**
     * Returns syndrome string with respect to syndrome code
     * @param syndromeCode Syndrome code
     * @return Syndrome string corresponding to syndrome code
     */
    public String getSyndromeString(String syndromeCode) {
        for (int i = 0; i < rows; i++) {
            if (table[i][1].equals(syndromeCode)) {
                return table[i][0];
            }
        }
        return null;
    }

    /**
     * Converts a syndrome table to a single string
     * @return string representing syndrome table
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            str.append(table[i][0]).append(" | ").append(table[i][1]).append("\n");
        }
        return str.toString();
    }
}
