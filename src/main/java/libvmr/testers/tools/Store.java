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
import libvmr.testers.SimpleTester;
import libvmr.testers.Tester;

/**
 * Хранилище - чистилище
 * @author Yury V. Reshetov
 * @version 3.01
 */
public class Store {
	
	/**
	 * Тестер
	 */
	private Tester tester = null;
		

	/**
	 * Векторная машина Решетова
	 */
	public VMR vmr = null;
	/**
	 * Счётчик
	 */
	public int counter = 0;
	
	/**
	 * @param Задаёт тестер
	 */
	public void setTester(Tester tester) {
		this.tester = tester;
	}
	
	/** 
	 * @return Состояние счётчика
	 */
	synchronized public int getCounter() {
		try {
			if (counter < 100) {
				wait();
			}
			return this.counter;
		} catch (InterruptedException ie) {
		} finally {
			notify();
		}
		return -1;
	}
	
	/**
	 * Приём векторной машины в хранилище
	 * @param vmr Векторная машина Решетова
	 */
	synchronized public void setVMR(VMR vmr) {
		if (this.vmr == null) {
			this.vmr = vmr;
		} else {
			if (this.tester == null) {
				this.tester = new SimpleTester();
			}
			if (this.tester.isBestNextVectorMachine(this.vmr, vmr)) {
				this.vmr = vmr;
			}
		}
		this.counter++;
		notify();
	}
	
	public VMR getVMR() {
		return this.vmr;
	}
}
