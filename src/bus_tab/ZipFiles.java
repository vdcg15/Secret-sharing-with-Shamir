package bus_tab;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
 
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
 
public class ZipFiles {
    private static File[] files;
    private static String pathZip;
    private static String passwordZip;
    private int n;
    private int k;
	
    public ZipFiles(File[] files, String pathZip, String passwordZip) {
	this.files = files;
	this.pathZip = pathZip;
	this.passwordZip = passwordZip;
    }
	
    public static void setZip() {
	try {/*
	    //This is name and path of zip file to be created
	    ZipFile zipFile = new ZipFile(pathZip);

	    List<File> tempList = Arrays.asList(files);
	    ArrayList<File> listFiles = new ArrayList<File>(tempList); 

	    //Initiate Zip Parameters which define various properties
	    ZipParameters parameters = new ZipParameters();
	    parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

	    //DEFLATE_LEVEL_FASTEST	- Lowest compression level but higher speed of compression
	    //DEFLATE_LEVEL_FAST   	- Low compression level but higher speed of compression
	    //DEFLATE_LEVEL_NORMAL  - Optimal balance between compression level/speed
	    //DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
	    //DEFLATE_LEVEL_ULTRA   - Highest compression level but low speed
	    parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

	    //Set the encryption flag to true
	    parameters.setEncryptFiles(true);
	    //Set the encryption method to AES Zip Encryption
	    parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
	    //AES_STRENGTH_128 - For both encryption and decryption
	    //AES_STRENGTH_192 - For decryption only
	    //AES_STRENGTH_256 - For both encryption and decryption
	    //Key strength 192 cannot be used for encryption. But if a zip file already has a
	    /file encrypted with key strength of 192, then Zip4j can decrypt this file
	    parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
	    //Set password
	    parameters.setPassword(passwordZip);

	    //Now add files to the zip file
	    zipFile.addFiles(listFiles, parameters);*/
		
	    //Adding a zip in a zip, avoiding leaking of information
	    //of the structure inside.
	    ZipFile zipFile = new ZipFile(pathZip.substring(0, pathZip.length() - 4) + "_here.zip");
	    ZipFile zipFile2 = new ZipFile(pathZip);
	
	    List<File> tempList = Arrays.asList(files);
	    ArrayList<File> listFiles = new ArrayList<File>(tempList); 

	    ZipParameters parameters = new ZipParameters();
	    parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
	    parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		
	    zipFile.addFiles(listFiles, parameters);
		
	    parameters.setEncryptFiles(true);
	    parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
	    parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
	    parameters.setPassword(passwordZip);

	    zipFile2.addFile(zipFile.getFile(), parameters);	
		
	    JOptionPane.showMessageDialog(null, 
		    "Successful!!!", 
		    "Zipping", JOptionPane.INFORMATION_MESSAGE);
	} catch (ZipException zEx) {
	    JOptionPane.showMessageDialog(null, 
		    "Fail!!!\n" + zEx.getMessage(), 
		    "Zipping", JOptionPane.ERROR_MESSAGE);	
	}
    }
}