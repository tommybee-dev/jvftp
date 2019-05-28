/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.util;

public class CoActiveMap extends CoHandleMap {
	public static final CoActiveMap progress = new CoActiveMap();
	public static boolean terminate = false;

	public void remove(Object key) {
		super.remove(key);
		if (terminate && used == 0) {
			System.exit(0);
		}
	}
}