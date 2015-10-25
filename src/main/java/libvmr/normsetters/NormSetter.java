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

import java.io.*;
import libvmr.tools.Accounting;

/**
 * Интерфейс нормировщика входных и выходных данных
 * @author Yury V. Reshetov
 * @version 3.01
 */

public interface NormSetter extends Serializable {
	
	/**
	 * @return Значения зависимых переменных
	 */
	public double[] getIdeals();
	
	/**
	 * Нормировка входных и выходных данных перед запуском алгоритма на обучение
	 * @param samples данные для нормировки: 
	 * Начальные столбцы - значения регрессоров
	 * Последний столбец - значение зависимой переменной
	 * @return
	 */
	public double[][] normSetting(double[][] samples);
	
	/**
	 * Нормировка входных данных после обучения алгоритма
	 * @param samples входные данные для нормировки: 
	 * @return нормированные входные данные
	 */
	public double[] normSetting(double[] pattern);
	
	/**
	 * Преобразование нормированных данных в текстовый вид
	 * @param formula формула
	 * @param result предыдущие результаты в текстовом виде
	 * @return Нормированные данные в текстовом виде
	 */
	public String NormSettingToString(Accounting accounting, String result);
	
}
