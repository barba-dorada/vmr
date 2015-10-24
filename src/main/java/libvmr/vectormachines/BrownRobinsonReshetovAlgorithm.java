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
 * Обучение искусственного нейрона по методу Брауна, Робинсон, Решетова
 * 
 * @author Yury V. Reshetov
 * @version 3.01
 */
public class BrownRobinsonReshetovAlgorithm implements VectorMachine {

	private static final long serialVersionUID = 301L;

	/**
	 * Не является ли константа редуцированной?
	 */
	private boolean constantnonzero = false;

	/**
	 * Генератор случайных чисел
	 */
	private Random rand = new Random();
	/**
	 * Весовые коэффициенты искусственного нейрона
	 */
	private double[] weights = null;

	/**
	 * @return Не является ли константа редуцированной?
	 */
	public boolean isConstantNonZero() {
		return constantnonzero;
	}

	/**
	 * Обучение нейронной сети по методу Брауна, Робинсон, Решетова
	 * 
	 * @param samples
	 *            обучающая выборка
	 * @return true - полученная модель обладает обобщающей способностью, false
	 *         - не обладает
	 * @param accounting
	 *            учёт используемых входных переменных
	 */
	@Override
	public double[] train(double[][] samples, String[] variables,
			Accounting accounting) {
		double[][] doublesamples = this.doublling(samples);

		// Количество строк
		int rows = doublesamples.length;
		// Количество столбцов
		int columns = doublesamples[0].length;
		// Счётчик для игрока по строкам - оппонента
		int[] counterrows = new int[rows];
		// Инициализация счётчика
		Arrays.fill(counterrows, 0);
		// Счётчик для игрока по столбцам - генератор гипотез
		int[] countercolumns = new int[columns];
		// Инициализация счётчика
		Arrays.fill(countercolumns, 0);
		// Накопитель для строк
		double[] rowsadder = new double[rows];
		Arrays.fill(rowsadder, 0d);
		// Накопитель для столбцов
		double[] collsadder = new double[columns];
		Arrays.fill(collsadder, 0d);
		// Индекс выбранной строки
		int selectedrow = rand.nextInt(rows);
		// Индекс выбранного столбца
		int seletctedcolumn = 0;
		// Количество холостых итераций
		int delta = doublesamples[0].length / 2;
		if (delta == 0) {
			delta = 1;
		}
		for (int u = 0; u < (doublesamples[0].length); u++) {
			if (u == delta) {
				// После завершения холостых итераций очищаем счётчик столбцов
				Arrays.fill(countercolumns, 0);
			}
			for (int t = 0; t < 1000000; t++) {
				for (int j = 0; j < columns; j++) {
					collsadder[j] = collsadder[j]
							+ doublesamples[selectedrow][j];
				}
				seletctedcolumn = 0;
				// Ищем лучший столбец
				for (int j = 1; j < columns; j++) {
					if ((collsadder[j] == collsadder[seletctedcolumn])
							&& (countercolumns[j] < countercolumns[seletctedcolumn])) {
						seletctedcolumn = j;
					}
					if (collsadder[j] > collsadder[seletctedcolumn]) {
						seletctedcolumn = j;
					}
				}
				// Улучшаем гипотезу
				countercolumns[seletctedcolumn] = countercolumns[seletctedcolumn] + 1;
				// Ищем пример, наименее соответствующий гипотезе
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
				// Увеличиваем счётчик для найденного примера
				counterrows[selectedrow] = counterrows[selectedrow] + 1;
			}
		}

		int count = columns / 2;
		// Результаты подсчёта для столбцов
		int[] result = new int[count];
		// Весовые коэффициенты искусственного нейрона
		this.weights = new double[count];
		// Максимальное значение для нормировки весовых коэффициентов
		double max = 0d;
		for (int i = 0; i < count; i++) {
			// Вычисляем разницу счётчика i-го столбца между ЗА и ПРОТИВ
			result[i] = countercolumns[i] - countercolumns[i + count];
			// Присваиваем разницу i-му весовому коэффициенту искусственного
			// нейрона
			this.weights[i] = result[i];
			// Если найдено максимальное значение для нормировки
			if (Math.abs(this.weights[i]) > max) {
				// Запоминаем это значение
				max = Math.abs(this.weights[i]);
			}
		}

		this.constantnonzero = Math.abs(result[0]) > 0;

		for (int i = 0; i < count; i++) {
			// Нормируем весовые коэффициенты искусственного нейрона
			this.weights[i] = this.weights[i] / max;
			// Записываем результаты в текстовом виде
			// Если весовой коэффициент не нулевой
			if (Math.abs(result[i]) > 0d) {
				// То указываем его
				variables[i] = this.weights[i] + variables[i];
			} else {
				// Обнулённые весовые коэффициенты нам не нужны
				variables[i] = "";
				accounting.clearPredictor(i);
			}
		}
		return this.weights;
	}

	/**
	 * Удваиватель строк массива
	 * 
	 * @param samples
	 *            входной массив
	 * @return выходной массив, каждая строка которого расширена вдвое за счёт
	 *         добавления отрицательных значений из оригинала
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
