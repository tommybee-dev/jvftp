jvftp

The source code is originally from the sourceforge site. 
Check the original source site for the jvftp <http://jvftp.sourceforge.net/>

Modified:
- <b>unexpected characters in the file list.</b>
I've added a label and a text field for the encoding like a setControlEncoding method in the apache commons.
So, you can change the character set easily if you've got an unexpected characters displaying on a ftp file list.

I have tested only one case that is an euc-kr.  

- sun.net.ftp package
The GUI with this library used a sun.net.ftp package which is not recommaned by Oracle.
Some old source code  of the package needed to be fixed  compatible with jdk 1.6/1.7/1.8. 

Benefit:
No needed any of dependent libraries for the compilation and building the jvftp.

I always appreciate to the developers of that project.

![Alt text](img/change1.png)

