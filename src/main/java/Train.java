import libvmr.VMR;
import libvmr.parsers.Parser;
import libvmr.testers.tools.Conveyor;
import libvmr.testers.tools.SearchEngine;
import libvmr.testers.tools.Store;

import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by vad on 02.11.2015 20:59.
 */
public class Train {
    public static void main(String[] args) {
        Parser parser = new Parser();
        File file = new File("C:\\dev\\projects\\java\\vmr\\src\\main\\resources\\test+1.csv");
        double[][] samples = parser.parsing(file);
        if (parser.getError() != null) {
            return;
        }

        Conveyor conveyor = new Conveyor();
        int threads = 4;
        for (int i = 0; i < threads; i++) {
            new SearchEngine(conveyor, samples);
        }
        Store store = conveyor.getStore();
        for (int i = 0; i < 100; i++) {
            conveyor.setNumber(i);
            System.out.println("Progress: " + store.getCounter() + "%\n");
            if (store.getVMR() != null) {
                System.out.println(store.getVMR().getFormula());
            }
        }

        for (int i = 0; i < threads; i++) {
            conveyor.setNumber(-1);
        }
        while (store.getCounter() < 100) {

        }

        VMR vmr = store.getVMR();
        StringBuffer ta = new StringBuffer();
        if (vmr.getGeneralizationAbility() > 0d) {
            ta.append("The quality of modeling:\n\n");
            ta.append("Sensitivity of generalization abiliy: "
                    + vmr.getSensitivity() + "%\n");
            ta.append("Specificity of generalization ability: "
                    + vmr.getSpecificity() + "%\n");
            ta.append("Generalization abiliy: "
                    + vmr.getGeneralizationAbility() + "%\n");
        } else {
            ta.append("Bad data");
        }
        System.out.println(ta.toString());
        save(vmr);
    }


    static public void save(VMR vmr) {
        String filename = "out";
        if (!filename.endsWith(".txt")) {
            filename = filename + ".txt";
        }
        try {
            File file = new File(filename);
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            String formula = vmr.getFormula();
            StringTokenizer st = new StringTokenizer(formula, "\n");
            while (st.hasMoreTokens()) {
                pw.println(st.nextToken());
            }
            pw.flush();
            pw.close();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("vmr.ser"));
            oos.writeObject(vmr);
            oos.flush();
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
