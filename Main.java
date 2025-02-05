// package main;
import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		JFrame window = new JFrame(" Simple Chess Game");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);

		// Add GamePanel to Window 
		GamePanel gp = new GamePanel();
		window.add(gp);
		window.pack();


		window.setLocationRelativeTo(null);
		window.setVisible(true);

		gp.launchGame();

	}
}