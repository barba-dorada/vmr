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
 * Простое ядерное преобразование
 * @author Yury V. Reshetov
 * @version 3.01
 */
public class SimpleKernelMachine implements KernelMachine {
	
	private static final long serialVersionUID = 301L;

	/**
	 * Экземпляр класса для учёта используемых переменных
	 */
	private Accounting accounting = null;
	
	private String[] variables = null;
	
	/**
	 * Возвращает идентификаторы используемых переменных
	 * @return идентификаторы переменных
	 */
	public String[] getVariables() {
		return this.variables;
	}
	
	/**
	 * Преобразовать двумерный массив данных
	 * @return преобразованный массив
	 */
    public double[][] getTransformData(double[][] samples) {
    	this.variables = new String[samples[0].length + 1];
    	this.accounting = new Accounting(this.variables.length, samples[0].length);
    	variables[0] = "";
    	for (int i = 0; i < samples[0].length; i++) {
    		this.variables[i + 1] = " * x" + i;
    		this.accounting.setValue(i + 1, i);
    	}
    	double result[][] = new double[samples.length][samples[0].length + 1];
    	for (int i = 0; i < samples.length; i++) {
    		result[i][0] = 1d;
    		for (int j = 0; j < samples[0].length; j++) {
    			result[i][j + 1] = samples[i][j];
    		}
    	}
    	return result;
    }

	/**
	 * Преобразовать одномерный массив
	 * @return одномерный массив
	 */
    public double[] getTransformData(double[] sample) {
    	
    	double result[] = new double[sample.length + 1];
    	for (int i = 0; i < sample.length; i++) {
    		result[0] = 1d;
    		for (int j = 0; j < sample.length; j++) {
    			result[j + 1] = sample[j];
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
