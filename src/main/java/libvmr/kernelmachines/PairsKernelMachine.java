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


package libvmr.kernelmachines;

import libvmr.tools.*;


/**
 * Парное ядерное преобразование
 * @author Yury V. Reshetov
 * @version 3.01
 */

public class PairsKernelMachine implements KernelMachine {

	private static final long serialVersionUID = 301L;

	/**
	 * Экземпляр класса для учёта используемых переменных
	 */
	private Accounting accounting = null;

	private String[] variables = null;

	/**
	 * Возвращает идентификаторы используемых переменных
	 * 
	 * @return идентификаторы переменных
	 */
	public String[] getVariables() {
		return this.variables;
	}

	/**
	 * Преобразовать двумерный массив данных
	 * 
	 * @return преобразованный массив
	 */
	public double[][] getTransformData(double[][] samples) {
		String[] tempvariables = new String[samples[0].length + 1];
		tempvariables[0] = "";
		for (int i = 0; i < samples[0].length; i++) {
			tempvariables[i + 1] = " * x" + i;
		}

		double tempresult[][] = new double[samples.length][samples[0].length + 1];
			for (int i = 0; i < samples.length; i++) {
				tempresult[i][0] = 1d;
				for (int j = 0; j < samples[0].length; j++) {
					tempresult[i][j + 1] = samples[i][j];
				}
			}

		this.variables = new String[(samples[0].length + 1) * (samples[0].length + 2) / 2];
		this.accounting = new Accounting(this.variables.length, samples[0].length);
		int n = 0;
		for (int i = 0; i < (samples[0].length + 1); i++) {
			for (int j = 0; j < (samples[0].length + 1); j++) {
				if (j >= i) {
					if ((i > 0) && (i == j)) {
						this.variables[n] = " * (2.0" + tempvariables[i] + tempvariables[j] + " - 1.0)";
						
					} else {
						this.variables[n] = tempvariables[i] + tempvariables[j];
					}
					this.accounting.setValue(n, i - 1);
					this.accounting.setValue(n, j - 1);
					n++;
				}
			}
		}

		double result[][] = new double[samples.length][this.variables.length];
		for (int k = 0; k < samples.length; k++) {
			n = 0;
			for (int i = 0; i < (samples[0].length + 1); i++) {
				for (int j = 0; j < (samples[0].length + 1); j++) {
					// Проверка наличия дубликатов
					if (j >= i) {
						if ((i > 0) && (i == j)) {
							result[k][n] = 2d * tempresult[k][i] * tempresult[k][j] - 1d;
						} else {
							result[k][n] = tempresult[k][i] * tempresult[k][j];
						}
						n++;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Преобразовать одномерный массив
	 * 
	 * @return одномерный массив
	 */
	public double[] getTransformData(double[] sample) {

		double tempresult[] = new double[sample.length + 1];
		for (int i = 0; i < sample.length; i++) {
			tempresult[0] = 1d;
			for (int j = 0; j < sample.length; j++) {
				tempresult[j + 1] = sample[j];
			}
		}
		double result[] = new double[(sample.length + 1) * (sample.length + 2) / 2];
		int n = 0;
		for (int i = 0; i < (sample.length + 1); i++) {
			for (int j = 0; j < (sample.length + 1); j++) {
				// Проверка наличия дубликатов
				if (j >= i) {
					if ((i > 0) && (i == j)) {
						result[n] = 2d * tempresult[i] * tempresult[j] - 1d;
					} else {
						result[n] = tempresult[i] * tempresult[j];
					}
					n++;
				}
			}
		}
		return result;
	}
	
	/**
	 * Вернуть экземпляр класса учёта используемых переменных
	 * @return Экземпляр класса учёта используемых переменных
	 */
	@Override
	public Accounting getAccounting() {
		return this.accounting;
	}

}

