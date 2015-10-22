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
 * ��������� ������������ ������� � �������� ������
 * @author Yury V. Reshetov
 * @version 3.01
 */

public interface NormSetter extends Serializable {
	
	/**
	 * @return �������� ��������� ����������
	 */
	public double[] getIdeals();
	
	/**
	 * ���������� ������� � �������� ������ ����� �������� ��������� �� ��������
	 * @param samples ������ ��� ����������: 
	 * ��������� ������� - �������� �����������
	 * ��������� ������� - �������� ��������� ����������
	 * @return
	 */
	public double[][] normSetting(double[][] samples);
	
	/**
	 * ���������� ������� ������ ����� �������� ���������
	 * @param samples ������� ������ ��� ����������: 
	 * @return ������������� ������� ������
	 */
	public double[] normSetting(double[] pattern);
	
	/**
	 * �������������� ������������� ������ � ��������� ���
	 * @param formula �������
	 * @param result ���������� ���������� � ��������� ����
	 * @return ������������� ������ � ��������� ����
	 */
	public String NormSettingToString(Accounting accounting, String result);
	
}
