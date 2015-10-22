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


package libvmr.kernelmachines;

import java.io.*;
import libvmr.tools.*;

/**
 * ��������� ������� ��������������
 * @author Yury V. Reshetov
 * @version 3.01
 */

public interface KernelMachine extends Serializable {
	
	
	/**
	 * ������������� ��������� ������
	 * @return ��������������� ������
	 */
    public double[][] getTransformData(double[][] samples);

	/**
	 * ������������� ���������� ������
	 * @return ��������������� ������
	 */
    public double[] getTransformData(double[] sample);
    
	/**
	 * ���������� �������������� ������������ ����������
	 * @return �������������� ����������
	 */
	public String[] getVariables();
	
	/**
	 * ������� ��������� ������ ����� ������������ ����������
	 * @return ��������� ������ ����� ������������ ����������
	 */
	public Accounting getAccounting();

}
