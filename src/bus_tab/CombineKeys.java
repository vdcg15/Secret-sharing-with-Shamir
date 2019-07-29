package bus_tab;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.sun.mail.util.BASE64DecoderStream;
import java.io.File;
import javax.swing.JOptionPane;

public class CombineKeys {
	
    private static int k;
    private static BigInteger prime;
    private static Vector<Shared> keys;
	
    private static String algorithm;
    private static String algorithmKey;
    private static String path = "src/resource/CombineKeys.properties";
    private static BigInteger pwdFound;
    private static Config config;

    public CombineKeys(File fileKeysToExtract, File fileDecryptKey, String algorithm) {
	if (fileKeysToExtract != null) {
	    config = new Config(fileKeysToExtract.toString(), "combine");
	} else {
	    config = new Config(path, "combine");
	}

	prime = config.getPrime(); 
	k = config.getK(); 
	keys = config.getKeys();

	config = new Config(fileDecryptKey.toString(), "algorithmKey");
	algorithmKey = config.getAlgorithmKey();
	this.algorithm = algorithm;
    }
	
    private static BigInteger combineKeys() {
	//y0 * 	(-x1) / (x0 � x1) * 	(-x2) / (x0 � x2)
	//y1 * 	(-x0) / (x1 � x0) * 	(-x2) / (x1 � x2)
	//y2 * 	(-x0) / (x2 � x0) * 	(-x1) / (x2 � x1)
	//mod p
		
	BigInteger result = BigInteger.ZERO;
	int size = keys.size();
		
	for (int i = 0; i < size; i++) {
	    BigInteger numerator = BigInteger.ONE;		//up
	    BigInteger denominator = BigInteger.ONE;	//down
			
	    Shared tempKey = keys.elementAt(i);
	    BigInteger tempMain = tempKey.getX();
			
	    for (int j = 0; j < size; j++) {
		if (i == j) {
		    continue;
		}
				
		BigInteger tempExtra = keys.elementAt(j).getX();
				
		numerator = numerator.multiply(tempExtra.negate()).mod(prime);
		denominator = denominator.multiply(tempMain.subtract(tempExtra)).mod(prime);
	    }
			
	    BigInteger tempF = tempKey.getF();
	    BigInteger tempBig = tempF.multiply(numerator).multiply(denominator.modInverse(prime));
	    result = result.add(tempBig).mod(prime);
	}

	return result;
    }
	
    public static String findPwd() {
	pwdFound = combineKeys();
		
	Cipher deCipher;
	SecretKey key;
		
	try {
	    // generate secret key using our algorithm
	    byte[] temp = Base64.getDecoder().decode(algorithmKey);
	    key = new SecretKeySpec(temp, 0, temp.length, algorithm);
	    deCipher = Cipher.getInstance(algorithm);

	    // initialize the ciphers with the given key
	    deCipher.init(Cipher.DECRYPT_MODE, key);
	
	    // decode with base64 to get bytes
	    byte[] dec = BASE64DecoderStream.decode(pwdFound.toByteArray());
	    byte[] utf8 = deCipher.doFinal(dec);
						
	    // create new string based on the specified charset
	    return new String(utf8, "UTF8");
	} catch (NoSuchAlgorithmException nsaEx) {
	    JOptionPane.showMessageDialog(null, 
		    "Fail!!!\n" + nsaEx.getMessage(), 
		    "Combine", JOptionPane.ERROR_MESSAGE);
		    
	    return null;	
	} catch (NoSuchPaddingException nspEx) {
	    JOptionPane.showMessageDialog(null, 
		    "Fail!!!\n" + nspEx.getMessage(), 
		    "Combine", JOptionPane.ERROR_MESSAGE);
		    
	    return null;	
	} catch (InvalidKeyException ivEx) {
	    JOptionPane.showMessageDialog(null, 
		    "Fail!!!\n" + ivEx.getMessage(), 
		    "Combine", JOptionPane.ERROR_MESSAGE);
		    
	    return null;	
	} catch (Exception ex) {
	    JOptionPane.showMessageDialog(null, 
		    "Fail!!!\n" + ex.getMessage(), 
		    "Combine", JOptionPane.ERROR_MESSAGE);
		    
	    return null;	
	}	
    }
}
