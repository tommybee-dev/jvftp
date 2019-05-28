/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.io;

import java.util.Date;
import java.util.Vector;

/**
 * Orders, filters and splits arrays of CoFile objects.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see CoFile
 */
public class CoSort {
	private int sorttype;

	/** Order Option - Order by name. */
	public static final int ORDER_BY_NAME = 1;
	/** Order Option - Order by extension. */
	public static final int ORDER_BY_TYPE = 2;
	/** Order Option - Order by size. */
	public static final int ORDER_BY_SIZE = 3;
	/** Order Option - Order by date. */
	public static final int ORDER_BY_DATE = 4;
	/** Order Option - Order by Path. */
	public static final int ORDER_BY_PATH = 5;
	/** Order Option - Skip ordering. */
	public static final int ORDER_BY_NONE = 6;
	/** Order Flag - Inverse flag. */
	public static final int ORDER_INVERSE = 8;
	
	CoSort(int sorttype) {
		this.sorttype = sorttype;
	}

	private boolean Pause() throws Exception {
		return true;
	}

	private void Swap(CoFile a[], int i, int j) {
		CoFile T;
		T = a[i];
		a[i] = a[j];
		a[j] = T;
	}

	private boolean AgreaterB(CoFile a, CoFile b) {
		boolean compare = false;
		switch (sorttype) {
			case ORDER_BY_NAME :
				compare = (a.compareNameToIgnoreCase(b) > 0);
				break;
			case ORDER_BY_TYPE :
				compare = (a.compareExtToIgnoreCase(b) > 0);
				break;
			case ORDER_BY_SIZE :
				compare = (a.length() > b.length());
				break;
			case ORDER_BY_DATE :
				compare =
					(new Date(a.lastModified())
						.after(new Date(b.lastModified())));
				break;
			case ORDER_BY_PATH :
				compare = (a.compareTo(b) > 0);
				break;
		}
		return compare;
	}

	private boolean AsmallerB(CoFile a, CoFile b) {
		boolean compare = false;
		switch (sorttype) {
			case ORDER_BY_NAME :
				compare = (a.compareNameToIgnoreCase(b) < 0);
				break;
			case ORDER_BY_TYPE :
				compare = (a.compareExtToIgnoreCase(b) < 0);
				break;
			case ORDER_BY_SIZE :
				compare = (a.length() < b.length());
				break;
			case ORDER_BY_DATE :
				compare =
					(new Date(a.lastModified())
						.before(new Date(b.lastModified())));
				break;
			case ORDER_BY_PATH :
				compare = (a.compareTo(b) < 0);
				break;
		}
		return compare;
	}

	/** This is a generic version of C.A.R Hoare's Quick Sort 
	 * algorithm.  This will handle arrays that are already
	 * Sorted, and arrays with duplicate keys.
	 * If you think of a one dimensional array as going from
	 * the lowest index on the left to the highest index on the right
	 * then the parameters to this function are lowest index or
	 * left and highest index or right.  The first time you call
	 * this function it will be with the parameters 0, a.length - 1.
	 *
	 * @param a	   an CoFile array
	 * @param lo0	 left boundary of array partition
	 * @param hi0	 right boundary of array partition */
	private void QuickSort(CoFile a[], int lo0, int hi0) throws Exception {
		int lo = lo0;
		int hi = hi0;
		CoFile mid;

		if (hi0 > lo0) {

			/* Arbitrarily establishing partition element as the midpoint of
			 * the array. */
			mid = a[(lo0 + hi0) / 2];

			// loop through the array until indices cross
			while (lo <= hi) {
				/* find the first element that is greater than or equal to 
				 * the partition element starting from the left Index. */
				while ((lo < hi0) && Pause() && AsmallerB(a[lo], mid))
					++lo;

				/* find an element that is smaller than or equal to 
				 * the partition element starting from the right Index. */
				while ((hi > lo0) && Pause() && AgreaterB(a[hi], mid))
					--hi;

				// if the indexes have not crossed, Swap
				if (lo <= hi) {
					Swap(a, lo, hi);
					++lo;
					--hi;
				}
			}

			/* If the right index has not reached the left side of array
			 * must now Sort the left partition. */
			if (lo0 < hi)
				QuickSort(a, lo0, hi);

			/* If the left index has not reached the right side of array
			 * must now Sort the right partition. */
			if (lo < hi0)
				QuickSort(a, lo, hi0);

		}
	}

	private void Sort(CoFile a[]) throws Exception {
		QuickSort(a, 0, a.length - 1);
		if ((sorttype & ORDER_INVERSE) > 0)
			for (int n = 0; n < (a.length / 2); n++)
				Swap(a, n, a.length - 1 - n);
	}

	/** Returns an array of abstract pathnames filtered by specified filter list. */
	static public CoFile[] listFilter(CoFile list[], String[] filter) {
		if (filter.length != 0) {
			Vector<CoFile> ffv = new Vector<CoFile>();
			for (int i = 0; i < list.length; i++)
				if (list[i].isDirectory()
					|| list[i].isLink()
					|| list[i].equalsExtTo(filter))
					ffv.addElement(list[i]);
			CoFile[] ffs = new CoFile[ffv.size()];
			ffv.copyInto(ffs);
			return ffs;
		} else
			return list;
	}

	/** Returns an array of abstract pathnames ordered by specified sorttype. */
	static public CoFile[] listOrder(CoFile list[], int sorttype) {
		if ((sorttype & (~ORDER_INVERSE)) != ORDER_BY_NONE) {
			CoSort s = new CoSort(sorttype);
			try {
				s.Sort(list);
			} catch (Exception e) {
			}
			return list;
		} else
			return list;
	}

	/** Returns an array of abstract pathnames splited to directory and file part. */
	static public CoFile[] listSplit(CoFile list[]) {
		Vector<CoFile> ffv = new Vector<CoFile>();
		for (int i = 0; i < list.length; i++)
			if (list[i].isDirectory() || list[i].isLink())
				ffv.addElement(list[i]);
		for (int i = 0; i < list.length; i++)
			if (!list[i].isDirectory() && !list[i].isLink())
				ffv.addElement(list[i]);
		CoFile[] ffs = new CoFile[ffv.size()];
		ffv.copyInto(ffs);
		return ffs;
	}
}
