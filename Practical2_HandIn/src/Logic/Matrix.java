package Logic;

/**
 * Matrix class
 */
public class Matrix {
    private int[][] matrix;
    private int rows;
    private int columns;

    /**
     * Constructor for a Matrix object
     * @param rows Amount of rows in the matrix
     * @param columns Amount of columns in the matrix
     */
    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.matrix = new int[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.matrix[i][j] = 0;
            }
        }
    }

    /**
     * Creates a parity matrix.
     */
    public void makeParityMatrix() {
        int current = (int) Math.pow(2, columns) - 1;

        for (int i = 0; i < columns; i++) {
            matrix[(rows - columns) - 1][i] = 1;
        }

        current--;
        int identCount = rows - columns;
        int identColumnCount = 0;
        int parCount = 0;

        for (; current > 0; current--) {
            String bitStr = Integer.toBinaryString(current);
            if (bitStr.length() < columns) {
                bitStr = String.format("%" + columns + "s", bitStr).replace(' ', '0');
            }
            if ((current & (current - 1)) == 0) { //Checks current is power of 2. https://stackoverflow.com/questions/19383248/find-if-a-number-is-a-power-of-two-without-math-function-or-log-function
                matrix[identCount][identColumnCount] = 1;
                identCount++;
                identColumnCount++;
            } else {
                for (int i = 0; i < bitStr.length(); i++) {
                    matrix[parCount][i] = Integer.parseInt(Character.toString(bitStr.charAt(i)));
                }
                parCount++;
            }
        }
    }

    /**
     * Called on a parity matrix to build a generator matrix
     * @param r R value
     * @return A matrix object containing a generator matrix
     */
    public Matrix makeGeneratorMatrix(int r) {
        Matrix g = new Matrix((int) Math.pow(2, r) - r - 1, (int) Math.pow(2, r) - 1);
        int rowCount = 0;

        for (int i = 0; i < this.rows - this.columns; i++) {
            g.matrix[i][rowCount] = 1;
            System.arraycopy(this.matrix[i], 0, g.matrix[i], g.rows, this.columns);
            rowCount++;
        }
        return g;
    }

    /**
     * Method to multiply two matrices
     * @param m Matrix to be multiplied with
     * @return The result of the multiplication
     */
    public Matrix multiplyWith(Matrix m) {
        Matrix newMatrix = new Matrix(1, m.columns);

        int newMValue = 0;
        for (int i = 0; i < m.columns; i++) {
            for (int j = 0; j < m.rows; j++) {
                newMValue += (this.matrix[0][j] * m.matrix[j][i]);
            }
            newMatrix.matrix[0][i] = newMValue % 2;
            newMValue = 0;
        }

        return newMatrix;
    }

    /**
     * Inputs a character into a matrix at a given position
     * @param c Character to be inserted
     * @param row Row for insertion
     * @param column Column for insertion
     */
    public void inputBit(char c, int row, int column) {
        this.matrix[row][column] = Integer.parseInt(Character.toString(c));
    }

    /**
     * Converts a matrix into a string
     * @return Matrix converted into string
     */
    public String toString(){
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                str.append(matrix[i][j]);
            }
            if (rows != 1) {
                str.append("\n");
            }
        }

        return str.toString();
    }

    /**
     * Prints a matrix
     */
    public void printMatrix() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
