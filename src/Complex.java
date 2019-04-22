/* Represents an immutable complex number */

public class Complex {
	public static final Complex ZERO = new Complex(0, 0);
	public static final Complex IMAGINARY = new Complex(0, 1);
	public static final Complex ONE = new Complex(1, 0);
	public static final Complex INFINITY = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

	protected final double re, im;
	public Complex(double real, double imag) {
		if(real == Double.POSITIVE_INFINITY || imag == Double.POSITIVE_INFINITY) {
			this.re = Double.POSITIVE_INFINITY;
			this.im = Double.POSITIVE_INFINITY;
		} else {
			this.re = real;
			this.im = imag;
		}
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
			if(re > 0) {
				return Math.atan(im/re);
			} else {
				if(im > 0) {
					return Math.PI+Math.atan(im/re);
				} else {
					return -Math.PI+Math.atan(im/re);
				}
			}
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

	public Complex conj() {
		return new Complex(re, -im);
	}

	public Complex divide(Complex other) {
		double denominator = other.im*other.im + other.re*other.re;
		if(denominator == 0) return INFINITY;
		return new Complex((re*other.re + im*other.im)/denominator, (im*other.re - re*other.im)/denominator);
	}

	public Complex divide(double other) {
		if(other == 0) return INFINITY;
		return multiply(1/other);
	}

	public Complex exp() {
		double modulus = Math.exp(re);
		return new Complex(modulus * Math.cos(im), modulus * Math.sin(im));
	}

	public Complex sin() {
		Complex denominator = new Complex(0, 2);
		Complex iz = multiply(IMAGINARY);
		return iz.exp().minus(iz.negative().exp()).divide(denominator);
	}

	public Complex sinh() {
		Complex denominator = new Complex(0, 2);
		return exp().minus(negative().exp()).divide(denominator);
	}

	public Complex cos() {
		Complex iz = multiply(IMAGINARY);
		return iz.exp().add(iz.negative().exp()).divide(2);
	}

	public Complex cosh() {
		Complex denominator = new Complex(0, 2);
		return exp().add(negative().exp()).divide(denominator);
	}

	public boolean equals(Complex other) {
		return (re == other.re) && (im == other.im);
	}

	public boolean equals(double other) {
		return (im == 0) && (re == other);
	}

	public int hashCode() {
		return Double.hashCode(im) * Double.hashCode(re);
	}

	@Override
	public String toString() {
		return String.format("%.5f + %.5fi", re, im);
	}
}
