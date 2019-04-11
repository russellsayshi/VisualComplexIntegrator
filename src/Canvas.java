import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/* This is what is drawn on in the JFrame */

public class Canvas extends JPanel {
	public static final double maxX = 5;
	public static final double maxY = 5;
	public static final double minX = -5;
	public static final double minY = -5;

	public Canvas() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if(me.getButton() == MouseEvent.BUTTON1) {
					// Left click, add a point to the path
					int x = me.getX();
					int y = me.getY();
					mousePoints.add(new Complex(x, y));
				} else {
					// Right click, close path, take integral, and clear
					if(mousePoints.size() > 0) {
						// Create closed path
						mousePoints.add(mousePoints.get(mousePoints.size()-1));
					}
				}
				updateIntegral();
				SwingUtilities.invokeLater(() -> repaint());
			}
		});
	}

	private Complex integralValue = new Complex(0, 0);
	private ArrayList<Complex> mousePoints = new ArrayList<>();

	private void updateIntegral() {
		integralValue = Integrator.integrate(mousePoints);
	}

	private double mathToScreenX(double x) {
		return (x-minX)/(maxX-minX)*getWidth();
	}

	private double mathToScreenY(double y) {
		return (y-minY)/(maxY-minY)*getHeight();
	}

	private double screenToMathX(double x) {
		return x/getWidth()*(maxX-minX)+minX;
	}

	private double screenToMathY(double y) {
		return y/getHeight()*(maxY-minY)+minY;
	}

	private Complex mathToScreen(Complex z) {
		return new Complex(mathToScreenX(z.real()), mathToScreenY(z.imag()));
	}

	private Complex screenToMath(Complex z) {
		return new Complex(screenToMathX(z.real()), screenToMathY(z.imag()));
	}

	@Override
	public void paintComponent(Graphics gOld) {
		Graphics2D g = (Graphics2D) gOld;

		// Clear screen
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Draw grid lines
		g.setColor(Color.BLACK);

		// Find coordinates of origin
		double zeroX = mathToScreenX(0);
		double zeroY = mathToScreenY(0);

		// Fill in lines
		for(int x = (int)Math.ceil(minX); x < (int)Math.floor(maxX); x++) {
			int location = (int)mathToScreenX(x);
			g.drawLine(location, 0, location, getHeight());
			g.drawString(Integer.toString(x), location, (int)zeroY);
		}
		for(int y = (int)Math.ceil(minY); y < (int)Math.floor(maxY); y++) {
			int location = (int)mathToScreenY(y);
			g.drawLine(0, location, getWidth(), location);
			g.drawString(Integer.toString(y), (int)zeroX, location);
		}

		// Give integral value
		g.setColor(Color.RED);
		g.drawString("Integral value: " + integralValue, 30, 30);
	}
}
