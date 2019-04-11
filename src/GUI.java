import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Entry point of the application. Creates
 * JFrame with the Canvas inside of it.
 */

public class GUI {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Complex Integration");
		frame.setContentPane(new Canvas());
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
