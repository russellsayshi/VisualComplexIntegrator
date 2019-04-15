/* Modify this to change which complex function
 * we are graphing. Simply takes a complex input
 * and gives a complex output, in two parts.
 */

public final class Operation {
	private Operation() {} //no instantiation

	public static Complex performExponentiation(Complex in) {
		double modulus = Math.exp(in.real());
		return new Complex(modulus * Math.cos(in.imag()), modulus * Math.sin(in.imag()));
	}

	// Whichever one is named perform is the one
	// that is graphed. The above is just to
	// be renamed in case that is desired
	public static Complex perform(Complex in) {
		//Take 1/z
		return new Complex(1, 0).divide(in);
	}
}
