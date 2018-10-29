package GUI;

import Exceptions.LoopNumberTooSmallException;
import Logic.BinaryStringBuilder;
import Logic.Matrix;
import Logic.SyndromeTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MainForm {
    private JTextArea Display;
    private JPanel mainPanel;
    private JButton goButton;
    private JTextField probabilityEntry;
    private JLabel label1;
    private JTextField loopNumberInput;
    private JLabel label2;
    private int errorCount = 0;
    private int errorsCorrected = 0;

    /**
     * Action Listener for pressing the go button.
     */
    public MainForm() {
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                double errProb;
                int loopNumber;
                Display.setText("");

                try {
                    errProb = Double.parseDouble(probabilityEntry.getText());   //Retrieves user input.

                    loopNumber = Integer.parseInt(loopNumberInput.getText());
                    if (loopNumber < 1)
                        throw new LoopNumberTooSmallException();

                    for (int r = 2; r <= 6; r++) {                  //Loops through each R value
                        Display.append("===========================================================\n");
                        BinaryStringBuilder generator = new BinaryStringBuilder();
                        int wordLength = (int) Math.pow(2, r) - r - 1;
                        int encodedWordLength = (int) Math.pow(2, r) - 1;

                        Display.append("R value: " + r + "\n");
                        Display.append("Generating a random string of 32,604 bits long, encoding it, introducing errors and " +
                                "attempting to \ndecode it " + loopNumber + " times...\n\n");

                        Matrix parity = new Matrix(encodedWordLength, r);   //Builds parity matrix
                        parity.makeParityMatrix();
                        Display.append("Parity matrix built:\n");
                        Display.append(parity.toString() + "\n");

                        Matrix g = parity.makeGeneratorMatrix(r);           //Builds generator matrix
                        Display.append("Generator matrix built:\n");
                        Display.append(g.toString() + "\n");
                        if (r == 2) {
                            Display.append("\n");
                        }

                        SyndromeTable sTable = new SyndromeTable(r);        //Builds syndrome table
                        Display.append("Syndrome table built:\n");
                        Display.append(sTable.toString() + "\n");

                        double averageCorrectionAccuracy = 0.0;
                        double averageErrorCorrectionRate = 0.0;

                        for (int i = 0; i < loopNumber; i++) {              //Builds average statistics
                            errorCount = 0;
                            errorsCorrected = 0;

                            StringBuilder originalString = new StringBuilder(generator.generate());     //Generates random bit string

                            String encodedString = hammingEncode(g, originalString.toString(), wordLength);     //Encodes string

                            String erroneousString;
                            if (errProb != 0.0) {
                                erroneousString = createErrors(encodedString, errProb);      //Introduces errors
                            } else {
                                erroneousString = encodedString;
                            }

                            String decodedString = hammingDecode(parity, sTable, erroneousString, encodedWordLength, wordLength);   //Decodes string

                            int decodedErrorCount = 0;
                            for (int j = 0; j < originalString.length(); j++) {
                                if (originalString.charAt(j) == decodedString.charAt(j)) {
                                    decodedErrorCount++;
                                }
                            }
                            averageCorrectionAccuracy += (double) (decodedErrorCount * 100) / (double) originalString.length();
                            averageErrorCorrectionRate += ((double) errorsCorrected / errorCount) * 100;
                        }

                        averageCorrectionAccuracy = averageCorrectionAccuracy / loopNumber;
                        averageErrorCorrectionRate = averageErrorCorrectionRate / loopNumber;
                        double informationRate = (double) wordLength / encodedWordLength;

                        System.out.println("With an r value of " + r + ":");

                        System.out.println("Average decoding accuracy was " + averageCorrectionAccuracy + "% for "
                                + loopNumber + " loops.");
                        System.out.println("Percentage of introduced errors corrected: " + averageErrorCorrectionRate
                                + "%");

                        System.out.println("Information rate = " + informationRate);

                        System.out.println();
                        System.out.println("=========================================================================");

                        Display.append("Average percentage for decoding accuracy: " + averageCorrectionAccuracy
                                + "%\n");
                        Display.append("Average percentage for the amount of errors actually corrected: "
                                + averageErrorCorrectionRate + "%\n");
                        Display.append("Information rate for R value of " + r + ": " + informationRate + "\n");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Error probability should be a double " +
                            "and number of loops should be an Integer.");
                } catch (LoopNumberTooSmallException e) {
                    JOptionPane.showMessageDialog(null, "Number of loops must be greater " +
                            "than 1.");
                }
            }
        });
    }

    /**
     * Encodes a given bit string using Hamming codes
     * @param g Generator matrix
     * @param str Bit string to be encoded
     * @param wordLength Size of blocks to be encoded
     * @return A string of encoded bits
     */
    public String hammingEncode(Matrix g, String str, int wordLength) {
        StringBuilder encodedString = new StringBuilder();

        for (int i = 0; i < str.length() / (wordLength); i++) {     //Inspects each block of bits
            Matrix tempWord = new Matrix(1, wordLength);
            for (int j = 0; j < wordLength; j++) {
                tempWord.inputBit(str.charAt((i*wordLength) + j), 0, j);    //Converts string into matrix
            }
            Matrix encodedWord = tempWord.multiplyWith(g);  //Encodes block
            String encodedBlock = encodedWord.toString();
            encodedString.append(encodedBlock);             //Builds encoded string
        }

        return encodedString.toString();
    }

    /**
     * Decodes encoded string of bits
     * @param parity Parity Matrix
     * @param sTable Syndrome table
     * @param erroneousString String to be decoded
     * @param encodedWordLength Block size for encoded string
     * @param wordLength Block size of original string
     * @return A string of decoded bits
     */
    public String hammingDecode(Matrix parity, SyndromeTable sTable, String erroneousString, int encodedWordLength,
                                int wordLength) {
        StringBuilder decodedString = new StringBuilder();
        String errorCorrectBitSting;

        for (int i = 0; i < (erroneousString.length() / encodedWordLength); i++) {  //Inspects each block in encoded string
            Matrix tempCodeWord = new Matrix(1, encodedWordLength);
            for (int j = 0; j < encodedWordLength; j++) {
                tempCodeWord.inputBit(erroneousString.charAt((i*encodedWordLength) + j), 0, j); //Converts block into matrix
            }

            Matrix syndrome = tempCodeWord.multiplyWith(parity);    //Calculates syndrome code
            String syndromeCode = syndrome.toString();
            if (!syndromeCode.matches("0+")) {          //Checks if error has been detected
                errorsCorrected++;
                String syndromeString = sTable.getSyndromeString(syndromeCode);
                errorCorrectBitSting = Long.toBinaryString((Long.parseLong(tempCodeWord.toString(), 2))     //Corrects error
                        ^ (Long.parseLong(syndromeString, 2)));
                errorCorrectBitSting = String.format("%" + encodedWordLength + "s", errorCorrectBitSting).replace(' ', '0');
            } else {
                errorCorrectBitSting = tempCodeWord.toString();
            }

            for (int j = 0; j < wordLength; j++) {
                decodedString.append(errorCorrectBitSting.charAt(j));       //Retrieves decoded bits
            }
        }

        return  decodedString.toString();
    }

    /**
     * Introduces errors into encoded string
     * @param encodedString String to have errors introduced
     * @param prob Probability of error occuring for each bit
     * @return A string of bits with errors introduced
     */
    public String createErrors(String encodedString, Double prob) {
        StringBuilder erroneousString = new StringBuilder();
        Random rand = new Random();
        double boundary = prob;

        for (int i = 0; i < encodedString.length(); i++) {
            double n = rand.nextDouble();
            if (n > boundary) {
                erroneousString.append(encodedString.charAt(i));
            } else {
                if (encodedString.charAt(i) == '0') {
                    erroneousString.append("1");
                } else {
                    erroneousString.append("0");
                }
                errorCount++;
            }
        }

        return erroneousString.toString();
    }

    /**
     * Main method that displays GUI
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("CS3302 Practical 2");
        frame.setContentPane(new MainForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(650, 400);
        frame.setVisible(true);
    }
}
