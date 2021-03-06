/**
 * Example for libVMR
 *
 * @author Yury V. Reshetov
 * @version 1.0
 */

import libvmr.VMR;

public class Example {

    public static void main(String[] args) {
        // Create an array of data for the task - XOR
        // Columns in the beginning - the input data (v0 and v1)
        // The last column - the output values
        double[][] data = {
                {0, 0, 0},
                {0, 1, 1},
                {1, 0, 1},
                {1, 1, 0},
        };
        // Let's see the result
        double[][] testData = {
                {0, 0},
                {0, 1},
                {1, 0},
                {1, 1},
        };

        // Triple copy of the data
        // For big data reproduce copies are not necessary
        double[][] patterns = new double[data.length * 3][data[0].length];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < data.length; j++) {
                for (int n = 0; n < data[0].length; n++) {
                    patterns[i * 3 + j][n] = data[j][n];
                }
            }
        }

        // Create an instance of the class VMR
        VMR vmr = new VMR();

        //Try to train
        if (!vmr.train(patterns)) {
            System.out.println("Bad data");
            return;
        }

        printResult(vmr, testData);
    }

    private static void printResult(VMR vmr, double[][] testData) {
        for (int i = 0; i < testData.length; i++) {
            double result = (vmr.getDecision(testData[i]) + 1d) / 2d;
            System.out.println("v0 = " + testData[i][0] + ", v1 = " + testData[i][1] + ", result = " + result);
        }

        System.out.println();
        System.out.println("Sensitivity of generalization ability: " + vmr.getSensitivity() + "%");
        System.out.println("Specificity of generalization ability: " + vmr.getSpecificity() + "%");
        System.out.println("Generalization ability: " + vmr.getGeneralizationAbility() + "%");
        System.out.println();
        System.out.println("Formula:");
        System.out.println();
        System.out.println(vmr.getFormula());
    }

}