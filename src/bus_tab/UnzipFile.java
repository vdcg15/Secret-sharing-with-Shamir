package bus_tab;

import java.io.File;
import java.security.InvalidKeyException;
import javax.swing.JOptionPane;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class UnzipFile {

    private static String fileUnzip;
    private static String dst;
    private static String pwd;
	
    public UnzipFile(String fileUnzip, String dst, String pwd) {
	this.fileUnzip = fileUnzip;
	this.dst = dst; 
	this.pwd = pwd; 
    }
	
    public static void extract() {
	try {
	    ZipFile zip = new ZipFile(fileUnzip);
	    
	    if (zip.isEncrypted()) {
		zip.setPassword(pwd);
	    }
	    
	    String temp = zip.getFile().getName();
	    temp = temp.substring(0, temp.length() - 4);
	    
	    File folder = new File(dst + "\\" + temp);
	    folder.mkdir();
	    zip.extractAll(folder.getPath());			
	    JOptionPane.showMessageDialog(null, 
		"Successful!!!", 
		"Unzipping", JOptionPane.INFORMATION_MESSAGE);
	} catch (ZipException zEx) {
	    JOptionPane.showMessageDialog(null, 
		"Fail!!!\n" + zEx.getMessage(), 
		"Unzipping", JOptionPane.ERROR_MESSAGE);
	} catch (NullPointerException npEx) {
	    JOptionPane.showMessageDialog(null, 
		"Fail!!!\n" + npEx.getMessage(), 
		"Unzipping", JOptionPane.ERROR_MESSAGE);
	}
    }
}
