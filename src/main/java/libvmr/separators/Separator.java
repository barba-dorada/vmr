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


package libvmr.separators;

import java.io.*;
import java.util.*;

/**
 * ���������, ����������� ������� �� ��������� � �������� �����
 * @author Yury V. Reshetov
 * @version 3.01
 */
public class Separator implements Serializable {
	
	private static final long serialVersionUID = 301L;
	/**
	 * �������� �������
	 */
	private double[][] samples = null;
	/**
	 * ��������� �������
	 */
	private double[][] trainsamples = null;
	/**
	 * �������� �������, ��������� ������ ��� ����������
	 */
	private double[][] testsamples = null;
	/**
	 * ������ �������� �������
	 */
	private double[][] fulltestsamples = null;

	/**
	 * @return ������ �������� �����
	 */
	public double[][] getFullTestSamples() {
		return this.fulltestsamples;
	}

	/**
	 * �������� �������� ����� ��� ����������
	 * @return �������� ����� ��� ����������
	 */
	public double[][] getStatisticalTestSamples() {
		return this.testsamples;
	}

	/**
	 * �������� ��������� �����
	 * @return ��������� �����
	 */
	public double[][] getTrainSamples() {
		return this.trainsamples;
	}

	/**
	 * �����������
	 * @param ����� ������� � ���������
	 */
	public Separator(double[][] patterns) {
		this.samples = patterns;

		ArrayList<double[]> trues = new ArrayList<double[]>();
		ArrayList<double[]> falses = new ArrayList<double[]>();
		for (int i = 0; i < samples.length; i++) {
			if (samples[i][samples[i].length - 1] > 0.5) {
				trues.add(samples[i]);
			} else {
				falses.add(samples[i]);
			}
		}
		Collections.shuffle(trues);
		Collections.shuffle(falses);

		int lenmin = Math.min(trues.size(), falses.size());
		int lenmax = Math.max(trues.size(), falses.size());
		
		this.fulltestsamples = new double[lenmax][samples[0].length];
		
		lenmax = (lenmax - lenmin) / 2 + lenmin;

		this.trainsamples = new double[lenmin][samples[0].length];
		this.testsamples = new double[lenmax][samples[0].length];
		for (int i = 0; i < lenmin; i++) {
			if ((i % 2) == 0) {
				this.trainsamples[i] = trues.get(i);
				this.testsamples[i] = falses.get(i);
				this.fulltestsamples[i] = falses.get(i);
			} else {
				this.trainsamples[i] = falses.get(i);
				this.testsamples[i] = trues.get(i);
				this.fulltestsamples[i] = trues.get(i);
			}
		}

		if (lenmax != lenmin) {
			ArrayList<double[]> r = new ArrayList<double[]>();
			if (trues.size() > lenmin) {
				r = trues;
			} else {
				r = falses;
			}
			for (int i = lenmin; i < lenmax; i++) {
				this.testsamples[i] = r.get(i);
			}
			for (int i = lenmin; i < r.size(); i++) {
				this.fulltestsamples[i] = r.get(i);
			}
		}
	}
}
