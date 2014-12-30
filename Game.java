
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Game extends JFrame implements Runnable {
	
	public JLabel status;
	public JLabel score;
	public JLabel level;
	public boolean inControls = false;
	private JPanel infoBox;
	
	@Override
	public void run() {
		setResizable(false);
		final Court court = new Court(this);
		add(court, BorderLayout.CENTER);
		JPanel statusBar = new JPanel(new BorderLayout());
		status = new JLabel("Running...");
		statusBar.add(status, BorderLayout.NORTH);
		score = new JLabel("Score: 0");
		statusBar.add(score, BorderLayout.WEST);
		level = new JLabel("Level: 1");
		statusBar.add(level, BorderLayout.EAST);
		add(statusBar, BorderLayout.SOUTH);
		JPanel buttonBar = new JPanel();
		JButton reset = new JButton("Restart");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				court.start();
				court.requestFocus();
				if (inControls) {
					inControls = false;
					remove(infoBox);
				}
				updateLabel(score, "Score: 0");
				updateLabel(level, "Level: 1");
			}
		});
		JButton instructionWind = new JButton("Controls");
		instructionWind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!inControls && !court.paused) {
					inControls = true;
					if (court.isRunning())
						court.pause();
					infoBox = new JPanel();
					infoBox.add(new JLabel("Arrow Left - Move left\n"));
					infoBox.add(new JLabel("Arrow Right - Move right\n"));
					infoBox.add(new JLabel("Arrow Down - Move down\n"));
					infoBox.add(new JLabel("SPACE - Hard drop\n"));
					infoBox.add(new JLabel("A - Rotate clockwise\n"));
					infoBox.add(new JLabel("D - Rotate counterclockwise\n"));
					infoBox.add(new JLabel("P - Pause game\n"));
					JButton close = new JButton("Return to Game");
					infoBox.add(close, BorderLayout.SOUTH);
					add(infoBox);
					infoBox.requestFocus();
					close.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							remove(infoBox);
							inControls = false;
							court.pause();
							court.requestFocus();
							repaint();
						}
					});
				} else {
					court.requestFocus();
				}
			}
		});
		buttonBar.add(reset, BorderLayout.WEST);
		buttonBar.add(instructionWind, BorderLayout.EAST);
		add(buttonBar, BorderLayout.NORTH);
		court.start();
		
    setSize(200, 300);
    setTitle("Tetris");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocation(300, 0);
    setVisible(true);
    court.requestFocus(); // Set focus to the game
	}
	
	public JLabel getStatus() {
		return status;
	}
	
	public void updateLabel(JLabel label, String newTxt) {
		label.setText(newTxt);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}

}
