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

import libvmr.tools.Accounting;


/**
 * Сложное полиминальное ядерное преобразование
 * @author Yury V. Reshetov
 * @version 3.01
 */
public class BigKernelMachine implements KernelMachine {
	
	private static final long serialVersionUID = 301L;
	/**
	 * Идентификаторы используемых переменных
	 */
	private String[] variables = null;
	/**
	 * Экземпляр класса для учёта используемых переменных
	 */
	private Accounting accounting = null;
	
	/**
	 * Возвращает идентификаторы используемых переменных
	 * @return идентификаторы переменных
	 */
	@Override
	public String[] getVariables() {
		return this.variables;
	}

	/**
	 * Преобразовать двумерный массив
	 * @return преобразованный массив
	 */
	@Override
    public double[][] getTransformData(double[][] samples) {
        int count = 1;
        for (int i = 0; i < (samples[0].length); i++) {
            count = count * 2;
        }
        double[][] result = new double[samples.length][count];
        this.variables = new String[count];
		this.accounting = new Accounting(this.variables.length, samples[0].length);
        for (int i = 0; i < samples.length; i++) {
            for (int j = 0; j < count; j++) {
            	this.variables[j] = "";
                double pp = 1d;
                int z = j;
                for (int n = 0; n < samples[0].length; n++) {
                    if ((z % 2) == 1) {
                        pp = pp * samples[i][n];
                        this.variables[j] = this.variables[j] + " * x" + n;
                        this.accounting.setValue(j, n);
                    }
                    z = z / 2;
                }
                result[i][j] = pp;                
            }
        }
        return result;
    }

	/**
	 * Преобразовать одномерный массив
	 * @return
	 */
	@Override
    public double[] getTransformData(double[] sample) {
        int count = 1;
        for (int i = 0; i < (sample.length); i++) {
            count = count * 2;
        }
        double[] result = new double[count];
        for (int i = 0; i < count; i++) {
            double pp = 1d;
            int z = i;
            for (int n = 0; n < sample.length; n++) {
                if ((z % 2) == 1) {
                    pp = pp * sample[n];
                } 
                z = z / 2;
            }
            result[i] = pp;
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
