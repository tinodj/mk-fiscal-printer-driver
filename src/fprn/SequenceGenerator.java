package fprn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

/**
 * @author tinodj
 * 
 */
public class SequenceGenerator {
    private static int currSeq = 0;

    private static Random random = new Random();

    public static byte getNewSeq() throws Exception {
        int newSeq = 0;
        if (currSeq == 0) {
            try {
                BufferedReader reader = new BufferedReader(
                        new FileReader("seq"));
                currSeq = (reader.readLine().charAt(0));
                reader.close();
            } catch (Exception e) {
                currSeq =0;
            }
        }
        while (newSeq < 40 || newSeq == currSeq)
            newSeq = random.nextInt(120);
        currSeq = newSeq;
        BufferedWriter writer = new BufferedWriter(new FileWriter("seq"));
        writer.write(currSeq);
        writer.close();
        return (byte)newSeq;
    }

}
