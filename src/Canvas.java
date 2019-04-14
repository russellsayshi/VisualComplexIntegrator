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
	public static final int EVAL_GRID_X = 400;
	public static final int EVAL_GRID_Y = 400;
	public static final Color[][] color_grid = new Color[EVAL_GRID_Y][EVAL_GRID_X];

	static {
		// Initialize color grid
		for(int y = 0; y < EVAL_GRID_Y; y++) {
			for(int x = 0; x < EVAL_GRID_X; x++) {
				Complex point = new Complex(((double)x)/EVAL_GRID_X*(maxX-minX)+minX, ((double)y+1)/EVAL_GRID_Y*(maxY-minY)+minY);
				Complex value = Operation.perform(point);
				double arg = point.arg() / Math.PI;
				if(arg < 0) arg += 2;
				double mod = Math.atan(point.modulus()) * 2 / Math.PI;
				Color c = Color.getHSBColor((float)arg, 0.5f, 1-(float)mod);
				color_grid[y][x] = c;
			}
		}
	}

	public Canvas() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if(me.getButton() == MouseEvent.BUTTON1) {
					// Left click, add a point to the path
					int x = me.getX();
					int y = me.getY();
					synchronized(mousePointsSynchronizer) {
						mousePoints.add(new Complex(screenToMathX(x), screenToMathY(y)));
					}
					updateIntegral();
				} else {
					// Right click, close path, take integral, and clear
					synchronized(mousePointsSynchronizer) {
						if(mousePoints.size() > 0) {
							// Create closed path
							mousePoints.add(mousePoints.get(0));
						}
					}
					updateIntegral();
					synchronized(mousePointsSynchronizer) {
						mousePoints.clear();
					}
				}
				SwingUtilities.invokeLater(() -> repaint());
			}
		});
	}

	private Complex integralValue = new Complex(0, 0);
	private ArrayList<Complex> mousePoints = new ArrayList<>();
	private Object mousePointsSynchronizer = new Object();

	private void updateIntegral() {
		synchronized(mousePointsSynchronizer) {
			integralValue = Integrator.integrate(mousePoints);
		}
	}

	private double mathToScreenX(double x) {
		return (x-minX)/(maxX-minX)*getWidth();
	}

	private double mathToScreenY(double y) {
		return getHeight()-(y-minY)/(maxY-minY)*getHeight();
	}

	private double screenToMathX(double x) {
		return x/getWidth()*(maxX-minX)+minX;
	}

	private double screenToMathY(double y) {
		return (getHeight()-y)/getHeight()*(maxY-minY)+minY;
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
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, getWidth(), getHeight());

		// Find coordinates of origin
		double zeroX = mathToScreenX(0);
		double zeroY = mathToScreenY(0);

		// Initial calculations
		int rectWidth = (int)Math.ceil(getWidth()/EVAL_GRID_X+1);
		int rectHeight = (int)Math.ceil(getHeight()/EVAL_GRID_Y+1);

		// Fill in squares 
		for(int y = 0; y < EVAL_GRID_Y; y++) {
			for(int x = 0; x < EVAL_GRID_X; x++) {
				Complex startPoint = new Complex(((double)x)/EVAL_GRID_X*(maxX-minX)+minX, ((double)y)/EVAL_GRID_Y*(maxY-minY)+minY);
				Complex screenStartPoint = mathToScreen(startPoint);
				g.setColor(color_grid[y][x]);
				g.fillRect((int)screenStartPoint.real(), (int)(screenStartPoint.imag()-rectHeight), (int)(rectWidth), (int)(rectHeight));
			}
		}

		// Draw grid lines
		g.setColor(Color.BLACK);

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
		g.setColor(Color.WHITE);
		g.drawString("Integral value: " + integralValue, 5, 15);

		// Draw integration path
		g.setColor(Color.BLUE);
		synchronized(mousePointsSynchronizer) {
			for(int i = 0; i < mousePoints.size()-1; i++) {
				Complex current = mousePoints.get(i);
				Complex next = mousePoints.get(i+1);
				g.drawLine((int)mathToScreenX(current.real()), (int)mathToScreenY(current.imag()), (int)mathToScreenX(next.real()), (int)mathToScreenY(next.imag()));
			}
		}
	}
}
