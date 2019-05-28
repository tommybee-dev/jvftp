/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.io;

import java.io.File;

/**
 * PS: Comparable is only implemented since JAVA 2.
 */
public interface CoComparable 
    /* Emulation for Java v1.0 and v1.1 = Remove Comparable!*/
    extends Comparable<File> { 
}
