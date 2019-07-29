package bus_tab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import com.sun.mail.util.BASE64EncoderStream;
import javax.swing.JOptionPane;

public class CutPwd {

    private static int factorLength = 0;
    private static int primeLength = 0;
    private static int certainty = 0;
    private static String algorithm = null;
    private static String algorithmKey = null;
    
    private static final String path = "src/resource/CutPwd.properties";
    private static final String pathPrint = "src/resource/CutInfo.txt";
	 
    private static BigInteger pwdSecret = BigInteger.ZERO;
    private static int n = 0;
    private static int k = 0;
    private static BigInteger prime = BigInteger.ZERO;

    private static Config config;
	
    private static Vector<BigInteger> factors;
    private static Vector<Shared> shared;
	
    public CutPwd(String passwordZip, int n, int k, String algorithm) {
	config = new Config(path, "cut");
		
	factorLength = config.getFactorLength(); 
	primeLength = config.getPrimeLength();
	certainty = config.getCertainty();
	//algorithm = config.getAlgorithm();
	//algorithmKey = config.getAlgorithmKey();
	
	this.n = n;
	this.k = k;
	this.algorithm = algorithm;	
	this.pwdSecret = encryptPwd(passwordZip);
	
	//this.algorithmKey is set in encryptPwd
    }
	
    private static BigInteger encryptPwd(String temp) {
	Cipher enCipher;
	SecretKey key;
		
	try {
	    //generate secret key using our algorithm
	    key = KeyGenerator.getInstance(algorithm).generateKey();
	    algorithmKey = Base64.getEncoder().encodeToString(key.getEncoded());
			
	    enCipher = Cipher.getInstance(algorithm);
			
	    //initialize the ciphers with the given key
	    enCipher.init(Cipher.ENCRYPT_MODE, key);

	    //encode the string into a sequence of bytes using the named charset
	    //storing the result into a new byte array.
	    byte[] utf8 = temp.getBytes("UTF8");
	    byte[] en = enCipher.doFinal(utf8);

	    en = BASE64EncoderStream.encode(en);

	    return new BigInteger(en);
	} catch (NoSuchAlgorithmException nsaEx) {
	    JOptionPane.showMessageDialog(null, 
		"Fail!!!\n" + nsaEx.getMessage(), 
		"Cut", JOptionPane.ERROR_MESSAGE);
	  
	    return null;
	} catch (NoSuchPaddingException nspEx) {
	    JOptionPane.showMessageDialog(null, 
		"Fail!!!\n" + nspEx.getMessage(), 
		"Cut", JOptionPane.ERROR_MESSAGE);
	    return null;
	} catch (InvalidKeyException ivEx) {
	    JOptionPane.showMessageDialog(null, 
		"Fail!!!\n" + ivEx.getMessage(), 
		"Cut", JOptionPane.ERROR_MESSAGE);
	    return null;
	} catch (Exception ex) {
	    JOptionPane.showMessageDialog(null, 
		"Fail!!!\n" + ex.getMessage(), 
		"Cut", JOptionPane.ERROR_MESSAGE);
	    return null;
	}
    }
	
    private static void createFactor(SecureRandom sr) {
	factors = new Vector<BigInteger>();
		
	for (int i = 1; i < k; i++) {
	    BigInteger temp = BigInteger.ZERO;
			
	    while (true) {
		temp = new BigInteger(factorLength, certainty, sr);
						
		if (temp.compareTo(BigInteger.ZERO) > 0) {
		    factors.add(temp);
		    break;
		}
	    }			
	}
    }
	
    private static void createPrime(SecureRandom sr) {
	prime = BigInteger.ZERO;
		
	while (true) {
	    prime = new BigInteger(primeLength, certainty, sr);
					
	    if (prime.compareTo(BigInteger.ZERO) > 0) {
		break;
	    }
	}			
    }
	
    public static BigInteger getPrime()	{
	return prime;
    }
	
    private static BigInteger fromXtoFX(BigInteger tempX) {
	int size = factors.size();
	BigInteger result = BigInteger.ZERO;
		
	for (int i = 0; i < size; i++) {
	    result = result.add((tempX.pow(i)).multiply(factors.elementAt(i)));
	    result = result.mod(prime);
	}
		
	return result;
    }
	
    private static void createShared(SecureRandom sr) {
	shared = new Vector<Shared>();
	BigInteger tempX = BigInteger.ZERO;
		
	for (int i = 0; i < n; i++) {
	    tempX = new BigInteger(factorLength, 0, sr);
	    shared.add(new Shared(tempX, fromXtoFX(tempX)));
	}
    }
	
    public static void cutPwd()	{
	SecureRandom sr = new SecureRandom();
		
	//Randomly choose k-1 numbers, from a[1] to a[k-1]
	//Recommended a big integer
	createFactor(sr);	
	factors.add(0, pwdSecret);
				
	//Randomly choose a prime number, which is bigger than all factors
	createPrime(sr);
				
	//Create n points (x, f(x) mod p)		
	//2 ways to select x
	//Way 1: randomly n times to select x
	//Way 2: just select from 0, 1, 2, ..., n (simpler)
	createShared(sr);
		
	print();
    }
	
    public static Vector<Shared> getShared() {
	return shared;
    }
	
    private static void print() {
	FileWriter fw = null;
	BufferedWriter bw = null;

	try {
	    //System.out.println(k);
	    fw = new FileWriter(pathPrint);
	    bw = new BufferedWriter(fw);
		
	    bw.write("#algorithm");
	    bw.newLine();
	    bw.write(algorithm);
	    bw.newLine();
	    bw.write("#algorithmKey");
	    bw.newLine();
	    bw.write(algorithmKey);
	    bw.newLine();
	    bw.write("#prime");
	    bw.newLine();
	    bw.write(prime.toString());
	    bw.newLine();
	    bw.write("#k");
	    bw.newLine();
	    bw.write(Integer.toString(k));
	    bw.newLine();

	    int size = shared.size();
	    bw.write("#n");
	    bw.newLine();
	    bw.write(Integer.toString(size));
	    bw.newLine();
			
	    for (int i = 0; i < size; i++) {
		bw.write("#keyX" + i);
		bw.newLine();
		bw.write(shared.elementAt(i).getX().toString());
		bw.newLine();
		bw.write("#keyF" + i);
		bw.newLine();
		bw.write(shared.elementAt(i).getF().toString());
		bw.newLine();
	    }
	} catch (IOException ioEx) {
	    JOptionPane.showMessageDialog(null, 
		"Fail!!!\n" + ioEx.getMessage(), 
		"Cut", JOptionPane.ERROR_MESSAGE);
	    
	    return;
	} finally {
	    try {
		if (bw != null) {
		    bw.flush();
		    bw.close();
		}
					
				
		if (fw != null) {
		    fw.close();
		}
	    } catch (IOException ioEx) {
		JOptionPane.showMessageDialog(null, 
		    "Fail!!!\n" + ioEx.getMessage(), 
		    "Cut", JOptionPane.ERROR_MESSAGE);
			
		return;
	    }
	}
    }
}
