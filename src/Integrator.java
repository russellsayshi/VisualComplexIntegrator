import java.util.ArrayList;

/* This class performs the actual numerical integration */

public final class Integrator {
	private Integrator() {} // no instantiation
	public static final int DEFAULT_RIEMANN_POINTS = 200000;

	public static Complex integrate(ArrayList<Complex> c) {
		Complex result = Complex.ZERO;
		for(int i = 0; i < c.size()-1; i++) {
			result = result.add(integrateLine(c.get(i), c.get(i+1)));
		}
		return result;
	}

	public static Complex integrateLine(Complex start, Complex end, int num_pts_per_len) {
		Complex diff = end.minus(start);
		Complex running_sum = Complex.ZERO;
		int num_pts = (int)Math.ceil(diff.modulus()*num_pts_per_len);
		Complex dz = diff.multiply(1.0/num_pts);
		for(int i = 0; i < num_pts; i++) {
			Complex point = start.add(dz.multiply(i));
			Complex value = Operation.perform(point); //take value of func
			Complex integrand = value.multiply(dz);
			running_sum = running_sum.add(integrand);
		}
		return running_sum;
	}

	public static Complex integrateLine(Complex start, Complex end) {
		return integrateLine(start, end, DEFAULT_RIEMANN_POINTS);
	}
}
