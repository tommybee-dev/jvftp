/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.util;

public class CoHandleMap {
	private Entry map[] = new Entry[5];
	int used = 0;

	private class Entry {
		Object key, value;
		Entry(Object key, Object value) {
			this.key = key;
			this.value = value;
		}
	}

	private void resize() {
		Entry old[] = map;
		map = new Entry[old.length + 5];
		System.arraycopy(old, 0, map, 0, old.length);
	}

	private int getIndex(Object key) {
		for (int i = 0; i < used; i++)
			if (map[i].key == key)
				return i;
		return -1;
	}

	private void clearIndex(int i) {
		map[i].key = null;
		map[i].value = null;
		map[i] = null;
	}

	public CoHandleMap() {
	}

	public void put(Object key, Object value) {
		int i = getIndex(key);
		if (i != -1) {
			map[i].key = key;
			map[i].value = value;
		} else {
			map[used] = new Entry(key, value);
			used++;
			if (map.length == used)
				resize();
		}
	}

	public Object get(Object key) {
		int i = getIndex(key);
		if (i != -1)
			return map[i].value;
		else
			return null;
	}

	public void remove(Object key) {
		int i = getIndex(key);
		if (i != -1) {
			clearIndex(i);
			for (int m = i; m < (used - 1); m++)
				map[m] = map[m + 1];
			used--;
		}
	}

	public void removeAll() {
		for (int i = 0; i < used; i++)
			clearIndex(i);
		used = 0;
	}
}