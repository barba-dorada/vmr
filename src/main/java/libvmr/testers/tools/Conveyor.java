/*
    jPrediction tools
    
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


package libvmr.testers.tools;




/**
 * Конвеер - синхронизатор
 * @author Yury V. Reshetov
 * @version 3.00
 */
public class Conveyor {
	
	/**
	 * Число 
	 */
	private int number = -1;
	/**
	 * Флаг готовности 
	 * Принимает истинное значение, если число находится на хранении
	 */
	private boolean ready = false;
	/*
	 * Хранилище лучшей модели векторной машины
	 */
	private Store store = new Store();
	
	/** 
	 * Передача числа
	 * @return число
	 */
	synchronized public int getNumber() {
		try {
			if (! ready) {
				wait();
			}
			ready = false;
			return this.number;
		} catch (InterruptedException ie) {
		} finally {
			notify();
		}
		return -1;
	}
	
	/**
	 * Приём числа
	 * @param number число
	 */
	synchronized public void setNumber(int number) {
		if (ready) {
			try {
				wait();
			} catch (InterruptedException ie) {				
			}
		}
		this.number = number;
		ready = true;
		notify();
	}
	
	/**
	 * @return Хранилище
	 */
	public Store getStore() {
		return store;
	}

}
