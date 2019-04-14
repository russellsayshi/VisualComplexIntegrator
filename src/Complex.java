/* Represents an immutable complex number */

public class Complex {
	public static final Complex ZERO = new Complex(0, 0);

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

	public Complex negative() {
		return new Complex(-re, -im);
	}

	public Complex add(Complex other) {
		return new Complex(re + other.re, im + other.im);
	}

	public Complex add(double other) {
		return new Complex(re + other, im);
	}

	public Complex minus(Complex other) {
		return add(other.negative());
	}

	public Complex minus(double other) {
		return new Complex(re - other, im);
	}

	public Complex multiply(Complex other) {
		return new Complex(re * other.re - im * other.im, re * other.im + other.re * im);
	}

	public Complex multiply(double other) {
		return new Complex(re * other, im * other);
	}

	@Override
	public String toString() {
		return String.format("%.5f + %.5fi", re, im);
	}
}
