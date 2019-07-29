package bus_tab;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

public class Config {
	
    private static int factorLength;
    private static int primeLength;
    private static int certainty;
    private static int k;
    private static BigInteger prime;

    private static Vector<Shared> shared;

    private static String algorithm;
    private static String algorithmKey;
    private static String propsLink;

    private static String parameters[] = {
	"#factorLength", "#primeLength", "#certainty", "#algorithm", "#algorithmKey",
	"#prime", "#k", "#keyX", "#keyF"
    };
	
    private static String getParameter(String name) {

	File file = new File(propsLink);
	Scanner input = null;

	try {
	    input = new Scanner(file);
	} catch (FileNotFoundException fnfEx) {
	    fnfEx.printStackTrace();
	    return null;
	}
		
	String line = null;
		
	try {
	    while (input.hasNextLine()) {
		line = input.nextLine();
		if (line.equals(name)) {
		    line = input.nextLine();
		    break;
		}
	    }
	} catch (NoSuchElementException nsEx) {
	    try {
		nsEx.printStackTrace();
		input.close();
	    } catch (IllegalStateException isEx) {
		isEx.printStackTrace();
	    }
			
	    return null;
	}
		
	try {
	    input.close();
	} catch (IllegalStateException isEx) {
	    isEx.printStackTrace();
	    return null;
	}
		
	return line;
    }
	
    private static void setFactorLength() {
	String temp = getParameter(parameters[0]);
	factorLength = Integer.parseInt(temp);
    }
	
    public static int getFactorLength() {
	return factorLength;
    }
	
    private static void setPrimeLength() {
	String temp = getParameter(parameters[1]);
	primeLength = Integer.parseInt(temp);
    }
	
    public static int getPrimeLength() {
	return primeLength;
    }
	
    private static void setCertainty() {
	String temp = getParameter(parameters[2]);
	certainty = Integer.parseInt(temp);
    }
	
    public static int getCertainty() {
	return certainty;
    }
	
    private static void setAlgorithm() {
	algorithm = getParameter(parameters[3]);
    }
	
    public static String getAlgorithm() {   
	return algorithm;
    }
	
    private static void setAlgorithmKey() {
	algorithmKey = getParameter(parameters[4]);
    }
	
    public static String getAlgorithmKey() {
	return algorithmKey;
    }

    private static void setPrime() {
	String temp = getParameter(parameters[5]);
	prime = new BigInteger(temp);
    }

    public static BigInteger getPrime() {
	return prime;
    }

    private static void setK() {
	String temp = getParameter(parameters[6]);
	k = Integer.parseInt(temp);
    }

    public static int getK() {
	return k;
    }

    private static void setKeys() {
	shared = new Vector<Shared>();
	BigInteger x = BigInteger.ZERO;
	BigInteger f = BigInteger.ZERO;

	for (int i = 0; i < k; i++) {
	    x = new BigInteger(getParameter(parameters[7] + i));
	    f = new BigInteger(getParameter(parameters[8] + i));
	    shared.addElement(new Shared(x, f));
	}
    }
	
    public static Vector<Shared> getKeys() {
	return shared;
    }
	
    /*private static void setParameters() {
	String temp = getParameter(parameters[0]);
	factorLength = Integer.parseInt(temp);
	
	temp = getParameter(parameters[1]);
	primeLength = Integer.parseInt(temp);

	temp = getParameter(parameters[2]);
	certainty = Integer.parseInt(temp);
		
	algorithm = getParameter(parameters[3]);
	algorithmKey = getParameter(parameters[4]);
		
	temp = getParameter(parameters[5]);
	prime = new BigInteger(temp);
		
	temp = getParameter(parameters[6]);
	k = Integer.parseInt(temp);
    }*/

    public Config(String propsLink, String mode) {
	this.propsLink = propsLink;
	    
	if (mode.compareTo("cut") == 0) {
	    setFactorLength();
	    setPrimeLength();
	    setCertainty();
	} else if (mode.compareTo("combine") == 0) {
	    setPrime();
	    setK();
	    setKeys();
	} else if (mode.compareTo("algorithmKey") == 0) {
	    setAlgorithmKey();
	}
    }	
}
