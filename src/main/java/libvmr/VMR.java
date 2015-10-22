/*
    libVMR
    
    Copyright (C) 2014,  Yury V. Reshetov

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 
*/

package libvmr;

import libvmr.kernelmachines.*;
import libvmr.normsetters.*;
import libvmr.separators.*;
import libvmr.vectormachines.*;
import java.io.*;


/**
 * ��������� ������ �������� - VMR
 * 
 * @author Yury V. Reshetov
 * @version 3.01
 */
public class VMR implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 301L;

	/**
	 * �����������
	 */
	private NormSetter normsetter = null;
	
	/**
	 * ��������� �������� ��� �������������� �������
	 */
	private VectorMachine vectormachine = null;

	/**
	 * ������� ��������������
	 */
	private KernelMachine kerneltrick = null;
	/**
	 * ������� ������������ ��������� ����
	 */
	private double[] weights = null;
	/**
	 * ������� �������������������� ��������
	 */
	private int truepositive = 0;
	/**
	 * ������� �������������������� ��������
	 */
	private int truenegative = 0;
	/**
	 * ������� ������������������ ��������
	 */
	private int falsepositive = 0;
	/**
	 * ������� ������������������ ��������
	 */
	private int falsenegative = 0;
	/**
	 * ������������ ����������
	 */
	private String[] variables = null;
	/**
	 * ���������������� ���������� �����������
	 */
	private double se = 0d;

	/**
	 * ������������� ���������� �����������
	 */
	private double sp = 0d;

	/**
	 * ���������� ��������
	 */
	private double indicator = 0d;
	
	/**
	 * ���������� ������ ��� ��������� �������
	 */
	private int outsampleerrors = 0;
	
	/**
	 * ���������� �������� ��� ��������� �������, �� ������� �� ����������� ����������
	 */
	private int remainder = 0;
	
	/**
	 * @param ��������� ������ ��� ���������� ���������
	 */
	public void setTrainer(VectorMachine trainer) {
		this.vectormachine = trainer;
	}

	/**
	 * @param ��������� ������ ��� ������������
	 */
	public void setNormsetter(NormSetter normsetter) {
		this.normsetter = normsetter;
	}

	/**
	 * @return ���������� ������ ��� ��������� �������
	 */
	public int getOutSampleErrors() {
		return outsampleerrors;
	}

	/**
	 * @return the indicator by Reshetov
	 */
	public double getIndicatorByReshetov() {
		return indicator;
	}

	/**
	 * ���������� ����������� � ���������
	 */
	private double ga = 0d;

	/**
	 * @param ������
	 *            ������� ��������������
	 */
	public void setKernelTrick(KernelMachine trick) {
		this.kerneltrick = trick;
	}

	/**
	 * @return ���������� ����������� � ���������
	 */
	public double getGeneralizationAbility() {
		return ga;
	}

	/**
	 * @return ���������������� ���������� ����������� � ���������
	 */
	public double getSensitivity() {
		return se;
	}

	/**
	 * @return ������������� ���������� ����������� � ���������
	 */
	public double getSpecificity() {
		return sp;
	}

	/**
	 * �������� ���������� �������������������� �������
	 * 
	 * @return ���������� �������������������� �������
	 */
	public int getTruePositives() {
		return this.truepositive;
	}

	/**
	 * �������� ���������� �������������������� �������
	 * 
	 * @return ���������� �������������������� �������
	 */
	public int getTrueNegatives() {
		return this.truenegative;
	}

	/**
	 * �������� ���������� ������������������� �������
	 * 
	 * @return ���������� ������������������ �������
	 */
	public int getFalsePositives() {
		return this.falsepositive;
	}

	/**
	 * �������� ���������� �������������������� �������
	 * 
	 * @return ���������� �������������������� �������
	 */
	public int getFalseNegatives() {
		return this.falsenegative;
	}

	/**
	 * �������� ��������� ���� �� ������ ��������
	 * 
	 * @param samples
	 *            ��������� �������
	 * @return true - ���������� ������ �������� ���������� ������������, false
	 *         - �� ��������
	 */
	public boolean train(double[][] samples) {
		Separator separator = new Separator(samples);
		this.remainder = separator.getFullTestSamples().length - separator.getStatisticalTestSamples().length;

		if (this.kerneltrick == null) {
			if (samples[0].length > 11) {
				if (samples[0].length > 45) {
					this.kerneltrick = new SimpleKernelMachine();
				} else {
					this.kerneltrick = new PairsKernelMachine();
				}
			} else {
				this.kerneltrick = new BigKernelMachine();
			}
		}
		
		if (this.normsetter == null) {
			this.normsetter = new SimpleNormSetter();
		}
		
		
		double[][] invariantsamples = this.getInvariant(this.kerneltrick.getTransformData(
				this.normsetter.normSetting(separator.getTrainSamples())));
		
		this.variables = this.kerneltrick.getVariables();
		
	
		
		if (this.vectormachine == null) {
			this.vectormachine = new BrownRobinsonReshetovAlgorithm();
		}
		
		
		this.weights = this.vectormachine.train(invariantsamples, variables, this.kerneltrick.getAccounting());
		

		// ��������� ���������� ��������
		double[][] fulltestsamples = separator.getStatisticalTestSamples();
		this.indicator = 0d;
		for (int i = 0; i < fulltestsamples.length; i++) {
			double testresult = this.getDecision(fulltestsamples[i]);
			double ideal = 2d * (fulltestsamples[i][fulltestsamples[0].length - 1]) - 1d;
			double scores = testresult * ideal;
			this.indicator = this.indicator + scores;
			if (scores < 0) {
				this.outsampleerrors++;
			}
		}

		{
			double weightssum = 0d;
			for (int i = 0; i < this.weights.length; i++) {
				weightssum = weightssum + Math.abs(this.weights[i]);
			}
			this.indicator = this.indicator / weightssum;
		}

		// ��������� �������������� ����������
		double[][] testsamples = separator.getStatisticalTestSamples();
		for (int i = 0; i < testsamples.length; i++) {
			double testresult = this.getDecision(testsamples[i]);
			double ideal = 2d * (testsamples[i][testsamples[0].length - 1]) - 1d;
			// �������� �����
			if (testresult * ideal > 0) {
				if (ideal > 0) {
					// �������������� ���������� �������������������� ��������
					this.truepositive++;
				} else {
					// �������������� ���������� �������������������� ��������
					this.truenegative++;
				}
			} else {
				if (testresult > 0) {
					// �������������� ���������� ������������������ ��������
					this.falsepositive++;
				} else {
					// �������������� ���������� ������������������ ��������
					this.falsenegative++;
				}
			}
		}

		double tpd = (double) this.truepositive;
		double fpd = (double) this.falsepositive;
		double tnd = (double) this.truenegative;
		double fnd = (double) this.falsenegative;
		// ��������� ����������������
		this.se = tpd / (tpd + fpd);
		// ��������� �������������
		this.sp = tnd / (tnd + fnd);
		// ��������� ���������� �����������
		this.ga = (this.se + this.sp - 1d) * 100d;
		// ��������� ������� ������������� �������
		double fp = (tpd + fnd) / (tpd + fnd + tnd + fpd);
		// ��������� ������� ������������� �������
		double fn = (tnd + fpd) / (tpd + fnd + tnd + fpd);

		// �������� ����� �� ������� ������� ���������� �����������
		boolean output = ((this.se > fp) && (sp > fn));

		if ((this.ga < 0d) || (!output)) {
			output = false;
			this.ga = 0d;
		}
		this.se = this.se * 100d;
		this.sp = this.sp * 100d;
		return output;
	}

	/**
	 * ��������� ����������� ���������� �������������� �������
	 * 
	 * @param pattern
	 *            ��������������� ������� �������� ��� ������������ �������
	 * @return ���������
	 */
	public double getDecision(double[] pattern) {
		
		

		double[] sample = this.kerneltrick.getTransformData(this.normsetter.normSetting(pattern));

		double result = 0d;
		for (int i = 0; i < sample.length; i++) {
			result = result + sample[i] * this.weights[i];
		}
		return result;
	}


	/**
	 * �������������� ���������� �� ��������� �� ������������ �������� ��
	 * ������������� ������������ � ��������� �� -1 �� 1 ������������
	 * 
	 * @param value
	 *            �������� ����������
	 * @param maximmumvalue
	 *            ������������ �������� ����������
	 * @param minimumvalue
	 *            ����������� �������� ����������
	 * @return ������������� �������� ���������� � ��������� �� -1 �� 1
	 *         ������������
	 */
	/*
	private double convertToRange(double value, double maximmumvalue,
			double minimumvalue) {
		return 2d * (value - minimumvalue) / (maximmumvalue - minimumvalue)
				- 1d;
	}
	*/

	/**
	 * ������������ �������������� ������� � ���������� ���� ��� �������� ������
	 * ����� �������������� ���������� ����� ����� �������
	 * 
	 * @param sample
	 *            ������� ��� ����������
	 * @return ������������ �������
	 */
	private double[][] getInvariant(double[][] sample) {
		double[][] result = new double[sample.length][sample[0].length];
		double[] ideals = this.normsetter.getIdeals();
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				result[i][j] = sample[i][j] / ideals[i];
			}
		}
		return result;
	}


	public String getFormula() {
		String result = "/**\n";

		result = result + " * The quality of modeling in out of sample:\n";
		result = result + " *\n";
		result = result + " * TruePositives: " + this.truepositive + "\n";
		result = result + " * TrueNegatives: " + this.truenegative + "\n";
		result = result + " * FalsePositives: " + this.falsepositive + "\n";
		result = result + " * FalseNegatives: " + this.falsenegative + "\n";
		int total = this.truepositive + this.truenegative + this.falsepositive + this.falsenegative;
		result = result + " * Total patterns in out of samples with statistics: " + total + "\n";
		result = result + " * The remainder patterns in out of samples without the statistics: " + this.remainder + "\n";
		result = result + " * Total errors in out of sample: " + this.outsampleerrors + "\n";
		result = result + " * Sensitivity of generalization abiliy: " + this.se
				+ "%\n";
		result = result + " * Specificity of generalization ability: "
				+ this.sp + "%\n";
		result = result + " * Generalization ability: " + this.ga + "%\n";
		result = result + " * Indicator by Reshetov: " + this.indicator + "\n";
		result = result + "*/\n";

		String formula = "double decision = ";
		if (this.vectormachine.isConstantNonZero()) {
			formula = formula +  + this.weights[0];
		}
		for (int i = 1; i < this.variables.length; i++) {
			if (!variables[i].equals("")) {
				if (variables[i].startsWith("-")) {
					formula = formula + " " + variables[i];
				} else {
					formula = formula + " + " + variables[i];
				}
			}
		}
        /*
		for (int i = 0; i < this.maxs.length; i++) {
			String variablesname = "x" + i + " ";
			String f = formula + " ";
			if (f.indexOf(variablesname) > 0) {
				double sub = this.maxs[i] - this.mins[i];
				if (this.mins[i] < 0) {
					result = result + "double x" + i + " = 2.0 * (v" + i
							+ " + " + Math.abs(this.mins[i]) + ") / " + sub
							+ " - 1.0;\n";
				} else {
					result = result + "double x" + i + " = 2.0 * (v" + i
							+ " - " + this.mins[i] + ") / " + sub + " - 1.0;\n";
				}
			} else {
				result = result + "//Variable v" + i + " got under reduction\n";
			}
		}
		*/
		result = this.normsetter.NormSettingToString(this.kerneltrick.getAccounting(), result);
		result = result + formula + ";\n";
		return result;
	}
}
