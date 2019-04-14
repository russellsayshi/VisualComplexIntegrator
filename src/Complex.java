/* Represents an immutable complex number */

public class Complex {
	protected final double re, im;
	public Complex(double real, double imag) {
		this.re = real;
		this.im = imag;
	}

	public double real() {
		return re;
	}

	public double imag() {
		return im;
	}

	public double modulus() {
		return Math.sqrt(re * re + im * im);
	}

	public double arg() {
		//account for vertical angles
		if(Math.abs(re) < 0.000001) {
			if(im > 0) return Math.PI/2;
			else return -Math.PI/2;
		} else {
			return Math.atan(im/re);
		}
	}

	@Override
	public String toString() {
		return Double.toString(re) + " + " + Double.toString(im) + "i";
	}
}
