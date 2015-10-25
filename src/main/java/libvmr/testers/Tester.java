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

package libvmr.testers;

import libvmr.*;

/**
 * Тестер
 * @author Yury V. Reshetov
 * @version 3.00
 */

public interface Tester {
	
	
	
	/**
	 * Если следующая векторная машина лучше
	 * @param vmCurrent текущая векторная машина
	 * @param vmNext следующая векторная машина
	 * @return лучше?
	 */
	boolean isBestNextVectorMachine(VMR vmCurrent, VMR vmNext);
}
