import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
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
	public static final int DESIRED_FPS = 60;
	public static final int SLEEP_TIME_MS = 1000/DESIRED_FPS;
	public static final Font font = new Font("Courier", Font.PLAIN, 15);
	public static final int CHECKBOX_SIZE = 10;
	public static final int CHECKBOX_PADDING = 10;

	static {
		// Initialize color grid
		for(int y = 0; y < EVAL_GRID_Y; y++) {
			for(int x = 0; x < EVAL_GRID_X; x++) {
				Complex point = new Complex(((double)x)/EVAL_GRID_X*(maxX-minX)+minX, ((double)y+1)/EVAL_GRID_Y*(maxY-minY)+minY);
				Complex value = Operation.perform(point);
				double arg = value.arg()/(2*Math.PI)+0.5;
				if(arg < 0 || arg > 1) System.out.println(arg);
				double mod = Math.atan(value.modulus()) * 2 / Math.PI;
				Color c = Color.getHSBColor((float)arg, 0.5f, (float)mod);
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
					// or check the checkbox
					int x = me.getX();
					int y = me.getY();
					if(x >= CHECKBOX_PADDING && x <= CHECKBOX_PADDING + CHECKBOX_SIZE
							&& y >= getHeight()-CHECKBOX_PADDING-CHECKBOX_SIZE && y <= getHeight()-CHECKBOX_PADDING) {
						// Change value of checkbox
						showExtraInfo = !showExtraInfo;
					} else {
						// Add mouse point
						synchronized(mousePointsSynchronizer) {
							mousePoints.add(new Complex(screenToMathX(x, getWidth()), screenToMathY(y, getHeight())));
						}
					}
				} else {
					// Right click, close path, take integral, and clear
					synchronized(mousePointsSynchronizer) {
						if(mousePoints.size() > 0) {
							// Create closed path
							mousePoints.add(mousePoints.get(0));
						}
					}
					synchronized(mousePointsSynchronizer) {
						mousePoints.clear();
					}
				}
				requestRepaint();
			}
			@Override
			public void mouseExited(MouseEvent me) {
				mouseInFrame = false;
				synchronized(mousePointsSynchronizer) {
					mousePoint = null;
				}
				synchronized(mouseNotifier) {
					mouseNotifier.notifyAll();
				}
			}
			@Override
			public void mouseEntered(MouseEvent me) {
				mouseInFrame = true;
				synchronized(mouseNotifier) {
					mouseNotifier.notifyAll();
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent me) {
				synchronized(mousePointsSynchronizer) {
					mousePoint = new Complex(screenToMathX(me.getX(), getWidth()), screenToMathY(me.getY(), getHeight()));
				}
				valueAtMouse = Operation.perform(mousePoint);
				if(showExtraInfo) {
					mousePoints.setVolatileEndpoint(mousePoint);
				}
				requestRepaint();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent ce) {
				background = null;
			}
		});
		new Thread(() -> {
			try {
				repaintThread();
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}).start();
	}
	
	private void repaintThread() throws InterruptedException {
		long lastFrameRequestedAt = 0;
		while(true) {
			while(requestRepaintFlag || 1 == 1) {
				if(showExtraInfo) {
					approximateIntegral = mousePoints.getVolatileIntegral();
					approximateClosedIntegral = mousePoints.getVolatileClosedIntegral();
				}
				SwingUtilities.invokeLater(() -> repaint());
				long currentFrameRequestedAt = System.currentTimeMillis();
				long timeDiff = (currentFrameRequestedAt - lastFrameRequestedAt);
				long timeToSleep = (timeDiff > SLEEP_TIME_MS ? 0 : SLEEP_TIME_MS - timeDiff);
				requestRepaintFlag = false;
				Thread.sleep(timeToSleep);
				lastFrameRequestedAt = currentFrameRequestedAt;
			}
			synchronized(mouseNotifier) {
				mouseNotifier.wait();
			}
		}
	}

	private transient IntegrationPath mousePoints = new IntegrationPath();
	private volatile transient Complex mousePoint = null;
	private transient final Object mousePointsSynchronizer = new Object();
	private transient final Object mouseNotifier = new Object();
	private transient BufferedImage background = null;
	private transient volatile boolean mouseInFrame = false;
	private transient volatile boolean requestRepaintFlag = false;
	private transient volatile boolean showExtraInfo = false;
	private transient volatile Complex valueAtMouse = Complex.ZERO;
	private transient volatile Complex approximateIntegral = Complex.ZERO;
	private transient volatile Complex approximateClosedIntegral = Complex.ZERO;

	private void requestRepaint() {
		requestRepaintFlag = true;
		synchronized(mouseNotifier) {
			mouseNotifier.notifyAll();
		}
	}

	private double mathToScreenX(double x, double width) {
		return (x-minX)/(maxX-minX)*width;
	}

	private double mathToScreenY(double y, double height) {
		return height-(y-minY)/(maxY-minY)*height;
	}

	private double screenToMathX(double x, double width) {
		return x/width*(maxX-minX)+minX;
	}

	private double screenToMathY(double y, double height) {
		return (height-y)/height*(maxY-minY)+minY;
	}

	private Complex mathToScreen(Complex z, double width, double height) {
		return new Complex(mathToScreenX(z.real(), width), mathToScreenY(z.imag(), height));
	}

	private Complex screenToMath(Complex z, double width, double height) {
		return new Complex(screenToMathX(z.real(), width), screenToMathY(z.imag(), height));
	}

	private BufferedImage rebuildBackground() {
		int width = getWidth();
		int height = getHeight();
		BufferedImage bg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bg.createGraphics();

		// Find coordinates of origin
		double zeroX = mathToScreenX(0, width);
		double zeroY = mathToScreenY(0, height);

		// Initial calculations
		int rectWidth = (int)Math.ceil(width/EVAL_GRID_X+1);
		int rectHeight = (int)Math.ceil(height/EVAL_GRID_Y+1);

		// Fill in squares 
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Complex point = new Complex(((double)x)/width*(maxX-minX)+minX, ((double)y)/height*(maxY-minY)+minY);
				Complex value = Operation.perform(point);
				double arg = value.arg()/(2*Math.PI)+0.5;
				if(arg < 0 || arg > 1) System.out.println(arg);
				double mod = Math.atan(value.modulus()) * 2 / Math.PI;
				Color c = Color.getHSBColor((float)arg, 0.5f, (float)mod);
				Complex screenPoint = mathToScreen(point, width, height);
				g.setColor(c);
				g.fillRect(x, y, 1, 1);
			}
		}

		// Draw grid lines
		g.setColor(Color.BLACK);

		// Fill in lines
		for(int x = (int)Math.ceil(minX); x < (int)Math.floor(maxX); x++) {
			int location = (int)mathToScreenX(x, width);
			g.drawLine(location, 0, location, height);
			g.drawString(Integer.toString(x), location, (int)zeroY);
		}
		for(int y = (int)Math.ceil(minY); y < (int)Math.floor(maxY); y++) {
			int location = (int)mathToScreenY(y, height);
			g.drawLine(0, location, width, location);
			g.drawString(Integer.toString(y), (int)zeroX, location);
		}

		return bg;
	}

	@Override
	public void paintComponent(Graphics gOld) {
		Graphics2D g = (Graphics2D) gOld;
		int width = getWidth();
		int height = getHeight();

		BufferedImage bg = background;
		if(bg == null) {
			bg = rebuildBackground();
			background = bg;
			g.drawImage(bg, 0, 0, null);
		} else {
			g.drawImage(bg, 0, 0, null);
		}

		// Set font
		g.setFont(font);

		// Give integral value
		g.setColor(Color.WHITE);
		g.drawString("Integral value: " + mousePoints.integrate(), 5, 15);
		if(showExtraInfo) g.drawString("Closed path integral: " + mousePoints.integrateClosed(), 5, 30);

		// Draw checkbox
		if(!showExtraInfo) {
			g.drawRect(CHECKBOX_PADDING, height-CHECKBOX_PADDING-CHECKBOX_SIZE, CHECKBOX_SIZE, CHECKBOX_SIZE);
		} else {
			g.fillRect(CHECKBOX_PADDING, height-CHECKBOX_PADDING-CHECKBOX_SIZE, CHECKBOX_SIZE, CHECKBOX_SIZE);
		}
		g.drawString("Extra?", CHECKBOX_PADDING*2 + CHECKBOX_SIZE, height-10);

		// Draw integration path
		g.setColor(Color.BLUE);
		synchronized(mousePointsSynchronizer) {
			for(int i = 0; i < mousePoints.size()-1; i++) {
				Complex current = mousePoints.get(i);
				Complex next = mousePoints.get(i+1);
				g.drawLine((int)mathToScreenX(current.real(), width),
						(int)mathToScreenY(current.imag(), height),
						(int)mathToScreenX(next.real(), width),
						(int)mathToScreenY(next.imag(), height));
			}
			if(mousePoint != null && mousePoints.size() > 0) {
				Complex current = mousePoints.get(mousePoints.size()-1);
				g.setColor(Color.GREEN);
				g.drawLine((int)mathToScreenX(current.real(), width),
						(int)mathToScreenY(current.imag(), height),
						(int)mathToScreenX(mousePoint.real(), width),
						(int)mathToScreenY(mousePoint.imag(), height));
				g.setColor(Color.WHITE);
				if(showExtraInfo) {
					g.drawString("Function value: " + valueAtMouse, 5, height-60);
					g.drawString("Approximate closed integral: " + approximateClosedIntegral, 5, height-30);
					g.drawString("Approximate integral: " + approximateIntegral, 5, height-45);
				}
			} else if(showExtraInfo) {
				g.setColor(Color.WHITE);
				g.drawString("Function value: " + valueAtMouse, 5, height-30);
			}
		}

	}
}
