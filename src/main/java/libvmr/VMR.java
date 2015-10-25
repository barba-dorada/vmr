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
 * Векторная машина Решетова - VMR
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
	 * Нормировщик
	 */
	private NormSetter normSetter = null;
	
	/**
	 * Обучающий алгоритм для искусственного нейрона
	 */
	private VectorMachine vectorMachine = null;

	/**
	 * Ядерное преобразование
	 */
	private KernelMachine kernelTrick = null;
	/**
	 * Весовые коэффициенты нейронной сети
	 */
	private double[] weights = null;
	/**
	 * Счётчик истинноположительных примеров
	 */
	private int truePositive = 0;
	/**
	 * Счётчик истинноотрицательных примеров
	 */
	private int trueNegative = 0;
	/**
	 * Счётчик ложноположительных примеров
	 */
	private int falsePositive = 0;
	/**
	 * Счётчик ложноотрицательных примеров
	 */
	private int falseNegative = 0;
	/**
	 * Наименования переменных
	 */
	private String[] variables = null;
	/**
	 * Чувствительность обобщающей способности
	 */
	private double se = 0d;

	/**
	 * Специфичность обобщающей способности
	 */
	private double sp = 0d;

	/**
	 * Показатель Решетова
	 */
	private double indicator = 0d;
	
	/**
	 * Количество ошибок вне обучающей выборки
	 */
	private int outSampleErrors = 0;

	/**
	 * Количество примеров вне обучающей выборки, по которым не учитывается статистика
	 */
	private int remainder = 0;
	
	/**
	 * @param Экземпляр класса для обучающего алгоритма
	 */
	public void setTrainer(VectorMachine trainer) {
		this.vectorMachine = trainer;
	}

	/**
	 * @param Экземпляр класса для нормировщика
	 */
	public void setNormSetter(NormSetter normSetter) {
		this.normSetter = normSetter;
	}

	/**
	 * @return Количество ошибок вне обучающей выборки
	 */
	public int getOutSampleErrors() {
		return outSampleErrors;
	}

	/**
	 * @return the indicator by Reshetov
	 */
	public double getIndicatorByReshetov() {
		return indicator;
	}

	/**
	 * Обобщающая способность в процентах
	 */
	private double ga = 0d;

	/**
	 * @param Задать
	 *            ядерное преобразование
	 */
	public void setKernelTrick(KernelMachine trick) {
		this.kernelTrick = trick;
	}

	/**
	 * @return Обобщающая способность в процентах
	 */
	public double getGeneralizationAbility() {
		return ga;
	}

	/**
	 * @return Чувствительность обобщающей способности в процентах
	 */
	public double getSensitivity() {
		return se;
	}

	/**
	 * @return Специфичность обобщающей способности в процентах
	 */
	public double getSpecificity() {
		return sp;
	}

	/**
	 * Получить количество истинноположительных исходов
	 * 
	 * @return количество истинноположительных исходов
	 */
	public int getTruePositives() {
		return this.truePositive;
	}

	/**
	 * Получить количество истинноотрицательных исходов
	 * 
	 * @return количество истинноотрицательных исходов
	 */
	public int getTrueNegatives() {
		return this.trueNegative;
	}

	/**
	 * Получить количество ложнооположительных исходов
	 * 
	 * @return количество ложноположительных исходов
	 */
	public int getFalsePositives() {
		return this.falsePositive;
	}

	/**
	 * Получить количество ложноноотрицательных исходов
	 * 
	 * @return количество ложноноотрицательных исходов
	 */
	public int getFalseNegatives() {
		return this.falseNegative;
	}

	/**
	 * Обучение нейронной сети по методу Решетова
	 * 
	 * @param samples
	 *            обучающая выборка
	 * @return true - полученная модель обладает обобщающей способностью, false
	 *         - не обладает
	 */
	public boolean train(double[][] samples) {
		Separator separator = new Separator(samples);
		this.remainder = separator.getFullTestSamples().length - separator.getStatisticalTestSamples().length;

		if (this.kernelTrick == null) {
			if (samples[0].length > 11) {
				if (samples[0].length > 45) {
					this.kernelTrick = new SimpleKernelMachine();
				} else {
					this.kernelTrick = new PairsKernelMachine();
				}
			} else {
				this.kernelTrick = new BigKernelMachine();
			}
		}
		
		if (this.normSetter == null) {
			this.normSetter = new SimpleNormSetter();
		}
		
		
		double[][] invariantSamples = this.getInvariant(this.kernelTrick.getTransformData(
				this.normSetter.normSetting(separator.getTrainSamples())));
		
		this.variables = this.kernelTrick.getVariables();
		
	
		
		if (this.vectorMachine == null) {
			this.vectorMachine = new BrownRobinsonReshetovAlgorithm();
		}
		
		
		this.weights = this.vectorMachine.train(invariantSamples, variables, this.kernelTrick.getAccounting());
		

		// Вычисляем показатель Решетова
		double[][] fullTestSamples = separator.getStatisticalTestSamples();
		this.indicator = 0d;
		for (int i = 0; i < fullTestSamples.length; i++) {
			double testResult = this.getDecision(fullTestSamples[i]);
			double ideal = 2d * (fullTestSamples[i][fullTestSamples[0].length - 1]) - 1d;
			double scores = testResult * ideal;
			this.indicator = this.indicator + scores;
			if (scores < 0) {
				this.outSampleErrors++;
			}
		}

		{
			double weightsSum = 0d;
			for (int i = 0; i < this.weights.length; i++) {
				weightsSum = weightsSum + Math.abs(this.weights[i]);
			}
			this.indicator = this.indicator / weightsSum;
		}

		// Вычисляем статистические показатели
		double[][] testSamples = separator.getStatisticalTestSamples();
		for (int i = 0; i < testSamples.length; i++) {
			double testResult= this.getDecision(testSamples[i]);
			double ideal = 2d * (testSamples[i][testSamples[0].length - 1]) - 1d;
			// Подводим итоги
			if (testResult * ideal > 0) {
				if (ideal > 0) {
					// Инкрементируем количество истинноположительных примеров
					this.truePositive++;
				} else {
					// Инкрементируем количество истинноотрицательных примеров
					this.trueNegative++;
				}
			} else {
				if (testResult > 0) {
					// Инкрементируем количество ложноположительных примеров
					this.falsePositive++;
				} else {
					// Инкрементируем количество ложноотрицательных примеров
					this.falseNegative++;
				}
			}
		}

		double tpd = (double) this.truePositive;
		double fpd = (double) this.falsePositive;
		double tnd = (double) this.trueNegative;
		double fnd = (double) this.falseNegative;
		// Вычисляем чувствительность
		this.se = tpd / (tpd + fpd);
		// Вычисляем специфичность
		this.sp = tnd / (tnd + fnd);
		// Вычисляем обобщающую способность
		this.ga = (this.se + this.sp - 1d) * 100d;
		// Вычисляем частоту положительных исходов
		double fp = (tpd + fnd) / (tpd + fnd + tnd + fpd);
		// Вычисляем частоту положительных исходов
		double fn = (tnd + fpd) / (tpd + fnd + tnd + fpd);

		// Проводим тесты на предмет наличия обобщающей способности
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
	 * Получение результатов обученного искусственного нейрона
	 * 
	 * @param pattern
	 *            ненормированные входные значения для предъявления нейрону
	 * @return результат
	 */
	public double getDecision(double[] pattern) {
		double[] sample = this.kernelTrick.getTransformData(this.normSetter.normSetting(pattern));

		double result = 0d;
		for (int i = 0; i < sample.length; i++) {
			result = result + sample[i] * this.weights[i];
		}
		return result;
	}


	/**
	 * Преобразование переменных из диапазона от минимального значения до
	 * максимального включительно к диапазону от -1 до 1 включительно
	 * 
	 * @param value
	 *            значение переменной
	 * @param maximmumvalue
	 *            максимальное значение переменной
	 * @param minimumvalue
	 *            минимальное значение переменной
	 * @return нормированное значение переменной в диапазоне от -1 до 1
	 *         включительно
	 */
	/*
	private double convertToRange(double value, double maximmumvalue,
			double minimumvalue) {
		return 2d * (value - minimumvalue) / (maximmumvalue - minimumvalue)
				- 1d;
	}
	*/

	/**
	 * Инвариантное преобразование выборки в результате чего два бинарных класса
	 * после преобразования становятся одним общим классом
	 * 
	 * @param sample
	 *            выборка для инварианта
	 * @return инвариантная выборка
	 */
	private double[][] getInvariant(double[][] sample) {
		double[][] result = new double[sample.length][sample[0].length];
		double[] ideals = this.normSetter.getIdeals();
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
		result = result + " * TruePositives: " + this.truePositive + "\n";
		result = result + " * TrueNegatives: " + this.trueNegative + "\n";
		result = result + " * FalsePositives: " + this.falsePositive + "\n";
		result = result + " * FalseNegatives: " + this.falseNegative + "\n";
		int total = this.truePositive + this.trueNegative + this.falsePositive + this.falseNegative;
		result = result + " * Total patterns in out of samples with statistics: " + total + "\n";
		result = result + " * The remainder patterns in out of samples without the statistics: " + this.remainder + "\n";
		result = result + " * Total errors in out of sample: " + this.outSampleErrors + "\n";
		result = result + " * Sensitivity of generalization ability: " + this.se
				+ "%\n";
		result = result + " * Specificity of generalization ability: "
				+ this.sp + "%\n";
		result = result + " * Generalization ability: " + this.ga + "%\n";
		result = result + " * Indicator by Reshetov: " + this.indicator + "\n";
		result = result + "*/\n";

		String formula = "double decision = ";
		if (this.vectorMachine.isConstantNonZero()) {
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
		result = this.normSetter.NormSettingToString(this.kernelTrick.getAccounting(), result);
		result = result + formula + ";\n";
		return result;
	}
}
