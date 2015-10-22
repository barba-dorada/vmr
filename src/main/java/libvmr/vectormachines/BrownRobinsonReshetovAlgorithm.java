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

package libvmr.vectormachines;

import java.util.*;

import libvmr.tools.Accounting;

/**
 * �������� �������������� ������� �� ������ ������, ��������, ��������
 * 
 * @author Yury V. Reshetov
 * @version 3.01
 */
public class BrownRobinsonReshetovAlgorithm implements VectorMachine {

	private static final long serialVersionUID = 301L;

	/**
	 * �� �������� �� ��������� ��������������?
	 */
	private boolean constantnonzero = false;

	/**
	 * ��������� ��������� �����
	 */
	private Random rand = new Random();
	/**
	 * ������� ������������ �������������� �������
	 */
	private double[] weights = null;

	/**
	 * @return �� �������� �� ��������� ��������������?
	 */
	public boolean isConstantNonZero() {
		return constantnonzero;
	}

	/**
	 * �������� ��������� ���� �� ������ ������, ��������, ��������
	 * 
	 * @param samples
	 *            ��������� �������
	 * @return true - ���������� ������ �������� ���������� ������������, false
	 *         - �� ��������
	 * @param accounting
	 *            ���� ������������ ������� ����������
	 */
	@Override
	public double[] train(double[][] samples, String[] variables,
			Accounting accounting) {
		double[][] doublesamples = this.doublling(samples);

		// ���������� �����
		int rows = doublesamples.length;
		// ���������� ��������
		int columns = doublesamples[0].length;
		// ������� ��� ������ �� ������� - ���������
		int[] counterrows = new int[rows];
		// ������������� ��������
		Arrays.fill(counterrows, 0);
		// ������� ��� ������ �� �������� - ��������� �������
		int[] countercolumns = new int[columns];
		// ������������� ��������
		Arrays.fill(countercolumns, 0);
		// ���������� ��� �����
		double[] rowsadder = new double[rows];
		Arrays.fill(rowsadder, 0d);
		// ���������� ��� ��������
		double[] collsadder = new double[columns];
		Arrays.fill(collsadder, 0d);
		// ������ ��������� ������
		int selectedrow = rand.nextInt(rows);
		// ������ ���������� �������
		int seletctedcolumn = 0;
		// ���������� �������� ��������
		int delta = doublesamples[0].length / 2;
		if (delta == 0) {
			delta = 1;
		}
		for (int u = 0; u < (doublesamples[0].length); u++) {
			if (u == delta) {
				// ����� ���������� �������� �������� ������� ������� ��������
				Arrays.fill(countercolumns, 0);
			}
			for (int t = 0; t < 1000000; t++) {
				for (int j = 0; j < columns; j++) {
					collsadder[j] = collsadder[j]
							+ doublesamples[selectedrow][j];
				}
				seletctedcolumn = 0;
				// ���� ������ �������
				for (int j = 1; j < columns; j++) {
					if ((collsadder[j] == collsadder[seletctedcolumn])
							&& (countercolumns[j] < countercolumns[seletctedcolumn])) {
						seletctedcolumn = j;
					}
					if (collsadder[j] > collsadder[seletctedcolumn]) {
						seletctedcolumn = j;
					}
				}
				// �������� ��������
				countercolumns[seletctedcolumn] = countercolumns[seletctedcolumn] + 1;
				// ���� ������, �������� ��������������� ��������
				for (int i = 0; i < rows; i++) {
					rowsadder[i] = rowsadder[i]
							+ doublesamples[i][seletctedcolumn];
				}
				selectedrow = 0;
				for (int i = 1; i < rows; i++) {
					if ((rowsadder[i] == rowsadder[selectedrow])
							&& (counterrows[i] < counterrows[selectedrow])) {
						selectedrow = i;
					}
					if (rowsadder[i] < rowsadder[selectedrow]) {
						selectedrow = i;
					}
				}
				// ����������� ������� ��� ���������� �������
				counterrows[selectedrow] = counterrows[selectedrow] + 1;
			}
		}

		int count = columns / 2;
		// ���������� �������� ��� ��������
		int[] result = new int[count];
		// ������� ������������ �������������� �������
		this.weights = new double[count];
		// ������������ �������� ��� ���������� ������� �������������
		double max = 0d;
		for (int i = 0; i < count; i++) {
			// ��������� ������� �������� i-�� ������� ����� �� � ������
			result[i] = countercolumns[i] - countercolumns[i + count];
			// ����������� ������� i-�� �������� ������������ ��������������
			// �������
			this.weights[i] = result[i];
			// ���� ������� ������������ �������� ��� ����������
			if (Math.abs(this.weights[i]) > max) {
				// ���������� ��� ��������
				max = Math.abs(this.weights[i]);
			}
		}

		this.constantnonzero = Math.abs(result[0]) > 0;

		for (int i = 0; i < count; i++) {
			// ��������� ������� ������������ �������������� �������
			this.weights[i] = this.weights[i] / max;
			// ���������� ���������� � ��������� ����
			// ���� ������� ����������� �� �������
			if (Math.abs(result[i]) > 0d) {
				// �� ��������� ���
				variables[i] = this.weights[i] + variables[i];
			} else {
				// ��������� ������� ������������ ��� �� �����
				variables[i] = "";
				accounting.clearPredictor(i);
			}
		}
		return this.weights;
	}

	/**
	 * ����������� ����� �������
	 * 
	 * @param samples
	 *            ������� ������
	 * @return �������� ������, ������ ������ �������� ��������� ����� �� ����
	 *         ���������� ������������� �������� �� ���������
	 */
	private double[][] doublling(double[][] samples) {
		double[][] result = new double[samples.length][samples[0].length * 2];
		for (int i = 0; i < samples.length; i++) {
			for (int j = 0; j < samples[0].length; j++) {
				result[i][j] = samples[i][j];
				result[i][j + samples[0].length] = -samples[i][j];
			}
		}
		return result;
	}

}
