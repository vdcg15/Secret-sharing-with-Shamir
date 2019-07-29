package bus_tab;

import java.math.BigInteger;
import java.util.Vector;

public class Shared {

    private BigInteger valueX;
    private BigInteger valueF;
	
    public Shared(BigInteger X, BigInteger F) {
	this.valueX = X;
	this.valueF = F;
    }

    public void setX(BigInteger X) {
	this.valueX = X;
    }

    public BigInteger getX() {
	return valueX;
    }

    public void setF(BigInteger F) {
	this.valueF = F;
    }

    public BigInteger getF() {
	return valueF;
    }

    public void print() {
	System.out.println("Key shared: ");
	System.out.println("x = " + valueX);
	System.out.println("f(x) = " + valueF + "\n");
    }
}
