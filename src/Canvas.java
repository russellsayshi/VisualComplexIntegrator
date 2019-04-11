import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/* This is what is drawn on in the JFrame */

public class Canvas extends JPanel {
	public static final double maxX = 5;
	public static final double maxY = 5;
	public static final double minX = -5;
	public static final double minY = -5;

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
			g.drawLine(location, 0, location, getHeight());
			g.drawString(Integer.toString(y), location, (int)zeroX);
		}
	}
}
