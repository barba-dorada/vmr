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

import libvmr.VMR;

/**
 * Поиск наилучшей модели
 * @author Yury V. Reshetov
 * @version 3.00
 */


public class SearchEngine extends Thread {
	/**
	 * Конвейер
	 */
	private Conveyor conveyor = null;
	/**
	 * Выборка с примерами
	 */
	private double[][] samples = null;
	

	/**
	 * Конструктор
	 * @param conveyor конвейер
	 * @param samples выборка с примерами
	 */
	public SearchEngine(Conveyor conveyor, double[][] samples) {
		this.conveyor = conveyor;
		this.samples = samples;
		this.start();
	}
	

	/**
	 * Параллельные вычисления
	 */
	public void run() {
		Store store = this.conveyor.getStore();
		while (this.conveyor.getNumber() != -1) {
			//System.out.println("Get number = " + number);
			VMR vmr = new VMR();
			vmr.train(samples);
			store.setVMR(vmr);
			//number = this.conveyor.getNumber();
		} 
		//System.out.println("Get number = " + number);
	}
}
