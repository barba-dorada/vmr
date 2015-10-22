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

package libvmr.normsetters;

import libvmr.tools.*;


/**
 * ����� �������� ������������ ������� � �������� ������ ��� �������� ������������� 
 * @author Yury V. Reshetov
 * @version 3.01
 */
public class SimpleNormSetter implements NormSetter {
		

	private static final long serialVersionUID = 301L;

	/**
	 * ��������� �������� ��������� �������
	 */
	private double[] maxs = null;

	/**
	 * �������� �������� ��������� �������
	 */
	private double[] mins = null;
	
	
	/**
	 * �������� ��������� ����������
	 */
	private double[] ideals = null;
	
	/**
	 * @return �������� ��������� ����������
	 */
	public double[] getIdeals() {
		return ideals;
	}
	

	/**
	 * ���������� ������� � �������� ������ ����� �������� ��������� �� ��������
	 * ��������� ������� - �������� �����������
	 * ��������� ������� - �������� ��������� ����������
	 * @param samples ������ ��� ����������: 
	 * @return ������������� ������
	 */
	public double[][] normSetting(double[][] samples) {
		// ����������� ��������� ������� ��������� ������� - ����������
		this.ideals = new double[samples.length];
		for (int i = 0; i < samples.length; i++) {
			this.ideals[i] = this.convertToRange(
					samples[i][samples[0].length - 1], 1d, 0d);
		}
		double[][] normsamples = new double[samples.length][samples[0].length - 1];

		// ���� ��������� � ��������
		this.maxs = new double[normsamples[0].length];
		this.mins = new double[normsamples[0].length];
		for (int j = 0; j < normsamples[0].length; j++) {
			double max = samples[0][j];
			double min = max;
			for (int i = 1; i < normsamples.length; i++) {
				if (samples[i][j] > max) {
					max = samples[i][j];
				}
				if (samples[i][j] < min) {
					min = samples[i][j];
				}
			}
			this.maxs[j] = max;
			this.mins[j] = min;
		}
		// �����������
		for (int i = 0; i < normsamples.length; i++) {
			for (int j = 0; j < normsamples[0].length; j++) {
				normsamples[i][j] = this.convertToRange(samples[i][j],
						this.maxs[j], this.mins[j]);
			}
		}
		return normsamples;
	}
	
	/**
	 * ���������� ������� ������ ����� �������� ���������
	 * @param samples ������� ������ ��� ����������: 
	 * @return ������������� ������� ������
	 */
	public double[] normSetting(double[] pattern) {
		double[] sample = new double[this.maxs.length];
		for (int i = 0; i < sample.length; i++) {
			sample[i] = this.convertToRange(pattern[i], this.maxs[i],
					this.mins[i]);
			if (sample[i] > 1d) {
				sample[i] = 1d;
			}
			if (sample[i] < -1d) {
				sample[i] = -1d;
			}
		}
		return sample;
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
	private double convertToRange(double value, double maximmumvalue,
			double minimumvalue) {
		return 2d * (value - minimumvalue) / (maximmumvalue - minimumvalue)
				- 1d;
	}
	
	/**
	 * �������������� ������������� ������ � ��������� ���
	 * @param formula �������
	 * @param result ���������� ���������� � ��������� ����
	 * @return ������������� ������ � ��������� ����
	 */
	public String NormSettingToString(Accounting accounting, String prevstring) {
		String result = prevstring;
		for (int i = 0; i < this.maxs.length; i++) {
			if (accounting.isUseVariable(i)) {
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
		return result;
	}
		
}
