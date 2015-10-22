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

package libvmr.tools;

import java.io.*;
import java.util.*;

/**
 * Учёт используемых переменных
 * @author Yury V. Reshetov
 * @version 3.01
 */
public class Accounting implements Serializable {
	

	private static final long serialVersionUID = 301L;

	/**
	 * Массив учёта используемых переменных
	 */
	private boolean[][] account = null;
	
	/**
	 * Конструктор
	 * @param predictors Количество предикторов
	 * @param variables Количество переменных
	 */
	public Accounting(int predictors, int variables) {
		this.account = new boolean[predictors][variables];
		for (int i = 0; i < predictors; i++) {
			Arrays.fill(account[i], false);
		}
	}
	
	/**
	 * Поставить переменную на учёт
	 * @param predictorsindex Индекс предиктора в котором используется переменная
	 * @param variablesindex Индекс используемой переменной
	 */
	public void setValue(int predictorsindex, int variablesindex) {
		this.account[predictorsindex][variablesindex] = true;
	}
	
	/**
	 * Очистка неиспользуемого предиктора
	 * @param index Индекс предиктора
	 */
	public void clearPredictor(int index) {
		Arrays.fill(account[index], false);
	}
	
	/**
	 * Поиск используемой переменной в предикторах
	 * @param index Индекс используемой переменной
	 * @return
	 */
	public boolean isUseVariable(int index) {
		for (int i = 0; i < this.account.length; i++) {
			if (this.account[i][index]) {
				return true;
			}
		}
		return false;
	}

}
