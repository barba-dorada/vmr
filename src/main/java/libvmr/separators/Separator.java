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
 * Сепаратор, разделяющий выборку на обучающую и тестовую части
 * @author Yury V. Reshetov
 * @version 3.01
 */
public class Separator implements Serializable {
	
	private static final long serialVersionUID = 301L;
	/**
	 * Исходная выборка
	 */
	private double[][] samples = null;
	/**
	 * Обучающая выборка
	 */
	private double[][] trainSamples = null;
	/**
	 * Тестовая выборка, пригодная только для статистики
	 */
	private double[][] testSamples = null;
	/**
	 * Полная тестовая выборка
	 */
	private double[][] fullTestSamples = null;

	/**
	 * @return полная тестовая часть
	 */
	public double[][] getFullTestSamples() {
		return this.fullTestSamples;
	}

	/**
	 * Получить тестовую часть для статистики
	 * @return тестовая часть для статистики
	 */
	public double[][] getStatisticalTestSamples() {
		return this.testSamples;
	}

	/**
	 * Получить обучающую часть
	 * @return обучающая часть
	 */
	public double[][] getTrainSamples() {
		return this.trainSamples;
	}

	/**
	 * Конструктор
	 * @param Общая выборка с примерами
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

		int lenMin = Math.min(trues.size(), falses.size());
		int lenMax = Math.max(trues.size(), falses.size());
		
		this.fullTestSamples = new double[lenMax][samples[0].length];
		
		lenMax = (lenMax - lenMin) / 2 + lenMin;

		this.trainSamples = new double[lenMin][samples[0].length];
		this.testSamples = new double[lenMax][samples[0].length];
		for (int i = 0; i < lenMin; i++) {
			if ((i % 2) == 0) {
				this.trainSamples[i] = trues.get(i);
				this.testSamples[i] = falses.get(i);
				this.fullTestSamples[i] = falses.get(i);
			} else {
				this.trainSamples[i] = falses.get(i);
				this.testSamples[i] = trues.get(i);
				this.fullTestSamples[i] = trues.get(i);
			}
		}

		if (lenMax != lenMin) {
			ArrayList<double[]> r = new ArrayList<double[]>();
			if (trues.size() > lenMin) {
				r = trues;
			} else {
				r = falses;
			}
			for (int i = lenMin; i < lenMax; i++) {
				this.testSamples[i] = r.get(i);
			}
			for (int i = lenMin; i < r.size(); i++) {
				this.fullTestSamples[i] = r.get(i);
			}
		}
	}
}
