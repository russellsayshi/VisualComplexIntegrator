import java.util.ArrayList;

public class IntegrationPath {
	private final ArrayList<Complex> list = new ArrayList<Complex>();
	private final ArrayList<Complex> lineIntegrals = new ArrayList<Complex>();
	private Complex integral = Complex.ZERO;
	private Complex closedIntegral = Complex.ZERO;

	// Allows a temporary endpoint to be set
	private transient Complex volatileEndpoint = null;
	private transient Complex volatileClosedIntegral = null;
	private transient Complex volatileIntegral = null;

	public IntegrationPath() {}

	public void add(Complex point) {
		if(list.size() > 0) {
			Complex partialSum = Integrator.integrateLine(list.get(list.size()-1), point);
			lineIntegrals.add(partialSum);
			integral = integral.add(partialSum);
			closedIntegral = integral.add(Integrator.integrateLine(point, list.get(0)));
		}
		list.add(point);
	}

	public void setVolatileEndpoint(Complex endpoint) {
		volatileEndpoint = endpoint;
		volatileClosedIntegral = null;
		volatileIntegral = null;
	}

	public Complex getVolatileIntegral() {
		if(list.size() == 0 || volatileEndpoint == null) return Complex.ZERO;
		if(volatileIntegral != null) return volatileIntegral;
		Complex approximateEndSum = Integrator.integrateLine(list.get(list.size()-1), volatileEndpoint, 1000);
		return (volatileIntegral = integral.add(approximateEndSum));
	}

	public Complex getVolatileClosedIntegral() {
		if(list.size() == 0 || volatileEndpoint == null) return Complex.ZERO;
		if(volatileClosedIntegral != null) return volatileClosedIntegral;
		Complex volatileIntegralSoFar = getVolatileIntegral();
		Complex approximateSum = Integrator.integrateLine(volatileEndpoint, list.get(0), 1000);
		return (volatileClosedIntegral = volatileIntegralSoFar.add(approximateSum));
	}

	public void clear() {
		list.clear();
		lineIntegrals.clear();
		integral = Complex.ZERO;
	}

	public Complex integrate() {
		return integral;
	}

	public Complex integrateClosed() {
		return closedIntegral;
	}

	public int size() {
		return list.size();
	}

	public Complex get(int i) {
		return list.get(i);
	}

	public Complex getLineIntegral(int i) {
		return lineIntegrals.get(i);
	}
}
