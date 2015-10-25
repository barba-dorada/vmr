/*
    libvmnext
    
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
 * Простой тестер
 * @author Yury V. Reshetov
 * @version 3.00
 */

public class SimpleTester implements Tester {
	

	/**
	 * Если следующая векторная машина лучше
	 * @param vmcurrent текущая векторная машина
	 * @param vmnext следующая векторная машина
	 * @return лучше?
	 */
	@Override
	public boolean isBestNextVectorMachine(VMR vmcurrent,
			VMR vmnext) {
		int errorscurrent = vmcurrent.getOutSampleErrors();
		int errorsnew = vmnext.getOutSampleErrors();
		if ((vmnext.getIndicatorByReshetov() > vmcurrent.getIndicatorByReshetov()) && (vmnext.getGeneralizationAbility() > 0d)) {
			if (errorscurrent >= errorsnew) {
				return true;
			}
		} else {
			if (errorscurrent > errorsnew) {
				return true;
			}
		}
		return false;
	}
}
