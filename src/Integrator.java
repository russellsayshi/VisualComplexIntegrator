import java.util.ArrayList;

/* This class performs the actual numerical integration */

public final class Integrator {
	private Integrator() {} // no instantiation
	public static final int RIEMANN_POINTS = 1000000;

	public static Complex integrate(ArrayList<Complex> c) {
		Complex result = Complex.ZERO;
		for(int i = 0; i < c.size()-1; i++) {
			result = result.add(integrateLine(c.get(i), c.get(i+1)));
		}
		return result;
	}

	private static Complex integrateLine(Complex start, Complex end) {
		Complex diff = end.minus(start);
		Complex running_sum = Complex.ZERO;
		Complex dz = diff.multiply(1.0/RIEMANN_POINTS);
		for(int i = 0; i < RIEMANN_POINTS; i++) {
			Complex point = start.add(dz.multiply(i));
			Complex value = Operation.perform(point); //take value of func
			Complex integrand = value.multiply(dz);
			running_sum = running_sum.add(integrand);
		}
		return running_sum;
	}
}
