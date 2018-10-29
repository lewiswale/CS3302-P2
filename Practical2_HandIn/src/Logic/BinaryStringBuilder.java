package Logic;

import java.util.Random;

/**
 * Class to generate a random string of bits
 */
public class BinaryStringBuilder {
    /**
     * Generates a string of random bits 32,604 bits long
     * @return String of bits
     */
    public String generate() {
        Random rand = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 32604; i++) {

            int n = rand.nextInt(100);
            if (n < 50) {
                str.append("0");
            } else {
                str.append("1");
            }
        }
        return str.toString();
    }
}
