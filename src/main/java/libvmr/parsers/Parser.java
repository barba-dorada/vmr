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

package libvmr.parsers;

import java.io.*;

/**
 * РџР°СЂСЃРёРЅРі РІС…РѕРґРЅРѕРіРѕ С„Р°Р№Р»Р° РІ С„РѕСЂРјР°С‚Рµ CSV
 * 
 * @author Yury V. Reshetov
 * @version 3.00
 */
public class Parser {

	/**
	 * РќР°Р·РІР°РЅРёСЏ РІС…РѕРґРЅС‹С… РїСЂРёР·РЅР°РєРѕРІ
	 * 
	 */
	private String[] ids = null;

	/**
	 * Р•РґРёРЅРёС†С‹ РёР·РјРµСЂРµРЅРёСЏ РґР»СЏ РІС…РѕРґРЅС‹С… РїСЂРёР·РЅР°РєРѕРІ
	 */
	private String[] units = null;

	/**
	 * РЎРѕРѕР±С‰РµРЅРёРµ РѕР± РѕС€РёР±РєРµ
	 */
	private String error = null;

	/**
	 * РџР°СЂСЃРёРЅРі
	 * 
	 * @param file
	 *            Р�РјСЏ РІС…РѕРґРЅРѕРіРѕ С„Р°Р№Р»Р°
	 * @return РњР°СЃСЃРёРІ РѕР±СѓС‡Р°СЋС‰РµР№ РІС‹Р±РѕСЂРєРё
	 */
	public double[][] parsing(File file) {
		double[][] samples = null;
		try {
			// Р—Р°РіСЂСѓР¶Р°РµРј РІРµСЃСЊ С‚РµРєСЃС‚ РІ РћР—РЈ
			BufferedReader bufferedreader = new BufferedReader(new FileReader(
					file));
			String s = "";
			String strings = "";
			while ((s = bufferedreader.readLine()) != null) {
				if (!s.trim().equals("")) {
					strings = strings + s + "\n";
				}
			}
			bufferedreader.close();

			String[] rows = strings.split("\n");
			if (rows.length > 2) {
				String[] names = rows[0].split(";");
				String[] unitstext = rows[1].split(";");
				String[] temp = rows[2].split(";");
				if (((names.length + 1) == temp.length)
						&& (names.length == unitstext.length)) {
					this.ids = new String[names.length - 1];
					this.units = new String[names.length - 1];
					for (int i = 1; i < names.length; i++) {
						this.ids[i - 1] = names[i].replace('\"', ' ').trim();
						this.units[i - 1] = unitstext[i].replace('\"', ' ')
								.trim();
					}
				}
				samples = new double[rows.length - 2][temp.length - 1];
				for (int i = 2; i < rows.length; i++) {
					String[] row = rows[i].split(";");
					if (row.length != temp.length) {
						int linenumber = i + 1;
						this.error = "Error in " + linenumber + " line\nFile: "
								+ file.getName();
						return null;
					} else {
						for (int j = 1; j < row.length; j++) {
							samples[i - 2][j - 1] = Double.parseDouble(row[j]
									.replace('\"', ' ').trim()
									.replace(',', '/'));
						}
					}
				}
			}
		} catch (NumberFormatException nfe) {
			this.error = "Number format error: " + nfe.getLocalizedMessage()
					+ "\nFile: " + file.getName();
			return null;
		} catch (Exception ex) {
			this.error = ex.getLocalizedMessage() + "\nFile: " + file.getName();
			return null;
		}
		return samples;
	}

	/**
	 * РњРµС‚РѕРґ РІРѕР·РІСЂР°С‰Р°РµС‚ РЅР°Р·РІР°РЅРёСЏ РІС…РѕРґРЅС‹С… РїР°СЂР°РјРµС‚СЂРѕРІ
	 * 
	 * @return РЅР°Р·РІР°РЅРёСЏ РІС…РѕРґРЅС‹С… РїР°СЂР°РјРµС‚СЂРѕРІ
	 */
	public String[] getIDs() {
		return this.ids;
	}

	/**
	 * РњРµС‚РѕРґ РІРѕР·РІСЂР°С‰Р°РµС‚ РЅР°РёРјРµРЅРѕРІР°РЅРёСЏ РµРґРёРЅРёС† РёР·РјРµСЂРµРЅРёСЏ
	 * 
	 * @return РЅР°РёРјРµРЅРѕРІР°РЅРёСЏ РµРґРёРЅРёС† РёР·РјРµСЂРµРЅРёСЏ
	 */
	public String[] getUnits() {
		return this.units;
	}

	/**
	 * РњРµС‚РѕРґ РІРѕР·РІСЂР°С‰Р°РµС‚ СЃРѕРѕР±С‰РµРЅРёРµ РѕР± РѕС€РёР±РєРµ
	 * 
	 * @return СЃРѕРѕР±С‰РµРЅРёРµ РѕР± РѕС€РёР±РєРµ, Р»РёР±Рѕ null РµСЃР»Рё РѕС€РёР±РєРё РѕС‚СЃСѓС‚СЃС‚РІСѓСЋС‚
	 */
	public String getError() {
		return this.error;
	}

}
