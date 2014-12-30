
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class Court extends JPanel implements ActionListener {
	
	// Court Dimensions (rows and columns)
	final int GRID_WIDTH = 10;
	final int GRID_HEIGHT = 22;
	
	// checks if piece has stopped falling
	private boolean stoppedFalling = false;
	// checks if game has started
	private boolean isStarted = false;
	// checks if game is paused
	public boolean paused = false;
	private Tetronimo refPiece;
	private int refPX = 0;
	private int refPY = 0;
	private Tetronimo.Shapes[] grid;
	private Timer actTime;
	private int score = 0;
	private Game refGame;
	private int actionInc = 700;
	private int level = 1;
	private KeyAdapter controls;
	private KeyAdapter controlsWhenPaused = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_P)
				pause();
		}
	};
	private JPanel hider;
	
	

	public Court(Game game) {
		setSize(100, 220);
		setFocusable(true);
		refPiece = new Tetronimo();
		// Set timer for action calls
		actTime = new Timer(actionInc, this);
		actTime.start();
		isStarted = true;
		grid = new Tetronimo.Shapes[GRID_WIDTH * GRID_HEIGHT];
		reset();
		refGame = game;
		controls = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					moveInBounds(refPX - 1, refPY, refPiece);
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					moveInBounds(refPX + 1, refPY, refPiece);
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					moveInBounds(refPX, refPY - 1, refPiece);
				else if (e.getKeyCode() == KeyEvent.VK_D) {
					if (!moveInBounds(refPX, refPY, refPiece.counterClock()))
						refGame.updateLabel(refGame.status, "Invalid Rotation");
					else
						refGame.updateLabel(refGame.status, "Running...");
				}	else if (e.getKeyCode() == KeyEvent.VK_A) {
					if (!moveInBounds(refPX, refPY, refPiece.clockwise()))
						refGame.updateLabel(refGame.status, "Invalid Rotation");
					else
						refGame.updateLabel(refGame.status, "Running...");
				}	else if (e.getKeyCode() == KeyEvent.VK_SPACE) 
					hardDrop(refPX, refPY, refPiece);
				else if (e.getKeyCode() == KeyEvent.VK_P)
					pause();
				
			}
		};
	}

	/**
	 * Clears grid to hold only empty shapes.
	 */
	private void reset() {
		for (int i = 0; i < grid.length; i++) {
			grid[i] = Tetronimo.Shapes.Empty;
		}
		removeKeyListener(controls);
		removeKeyListener(controlsWhenPaused);
		if (paused) {
			paused = false;
			refGame.remove(hider);
		}
		score = 0;
		level = 1;
		actionInc = 700;
		actTime.setDelay(actionInc);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (stoppedFalling) {
			stoppedFalling = false;
			spawnPiece();
		} else {
			moveDownOne();
		}
	}
	
	// Getters
	public int getTileWidth() {
		return (int) getSize().getWidth() / GRID_WIDTH;
	}
	public int getTileLength() {
		return (int) getSize().getWidth() / GRID_HEIGHT;
	}
	public Tetronimo.Shapes getShape(int x, int y) {
		return grid[y * GRID_WIDTH + x];
	}
	public boolean isRunning() {
		return isStarted;
	}
	
	public void pause() {
		hider = new JPanel();
		hider.add(new JLabel("PRESS 'P' TO RESUME PLAY!\n"));
		hider.add(new JLabel("Arrow Left - Move left\n"));
		hider.add(new JLabel("Arrow Right - Move right\n"));
		hider.add(new JLabel("Arrow Down - Move down\n"));
		hider.add(new JLabel("SPACE - Hard drop\n"));
		hider.add(new JLabel("A - Rotate clockwise\n"));
		hider.add(new JLabel("D - Rotate counterclockwise\n"));
		hider.add(new JLabel("P - Pause game\n"));
		if (paused) {
			paused = false;
			actTime.start();
			refGame.remove(hider);
			refGame.repaint();
			refGame.updateLabel(refGame.status, "Running...");
			removeKeyListener(controlsWhenPaused);
			addKeyListener(controls);
		} else {
			paused = true;
			actTime.stop();
			refGame.add(hider);
			refGame.updateLabel(refGame.status, "Paused.");
			removeKeyListener(controls);
			addKeyListener(controlsWhenPaused);
		}
		
	}
	/**
	 * Moves Piece down a line, if it is a valid move.
	 */
	private void moveDownOne() {
		if (!moveInBounds(refPX, refPY - 1, refPiece)) {
			updateBoard();
		}
	}
	
	/**
	 * Puts piece in grid and updates the grid accordingly.
	 */
	private void updateBoard() {
		for (int i = 0; i < 4; i++) {
			int x = refPX + refPiece.getX(i);
			int y = refPY - refPiece.getY(i);
			grid[(y * GRID_WIDTH) + x] = refPiece.getPiece();
		}
		
		delFullRows();
		
		if (!stoppedFalling) {
			spawnPiece();
		}
		
	}
	
	/**
	 * Deletes rows of squares in the grid that are full. 
	 */
	private void delFullRows() {
		boolean fullRowExists = false;
		// Starts check from top
		for (int i = GRID_HEIGHT - 1; i >= 0; i--) {
			boolean rowCheck = true;
			for (int j = 0; j < GRID_WIDTH; j++) {
				if (getShape(j, i) == Tetronimo.Shapes.Empty) {
					rowCheck = false;
					break;
				}
			}
			// Move everything above the full row down one row,
			// deleting the full line.
			if (rowCheck) {
				score++;
				if (score % 10 == 0) {
					actionInc = actionInc / 2 + 50;
					actTime.setDelay(actionInc);
					refGame.updateLabel(refGame.status, "Level Up!");
					level++;
				}
				fullRowExists = true;
				for (int k = i; k < GRID_HEIGHT - 1; k++) {
					for (int l = 0; l < GRID_WIDTH; l++) {
						grid[(k * GRID_WIDTH) + l] = getShape(l, k + 1);
					}
				}
			}
		}
		if (fullRowExists) {
			stoppedFalling = true;
			refPiece.setShape(Tetronimo.Shapes.Empty);
			fullRowExists = false;
			refGame.updateLabel(refGame.score, "Score: " + Integer.toString(score));
			refGame.updateLabel(refGame.level, "Level: " + Integer.toString(level));
			repaint();
		}
	}

	/**
	 * Checks whether or not the piece will be moving in bounds. Moves the piece
	 * if it can be moved, return false if otherwise.
	 * 
	 * @param potX - potential X position
	 * @param potY - potential Y position
	 * @param check - piece to be moved
	 * @return true if moved within bounds, false otherwise
	 */
	private boolean moveInBounds(int potX, int potY, Tetronimo check) {
		for (int i = 0; i < 4; i++) {
			int checkX = potX + check.getX(i);
			int checkY = potY - check.getY(i);
			if (checkX < 0 || checkX >= GRID_WIDTH 
													|| checkY < 0 || checkY >= GRID_HEIGHT) {
				return false;
			}
			if (getShape(checkX, checkY) != Tetronimo.Shapes.Empty) {
				return false;
			}
		}
		refPiece = check;
		refPX = potX;
		refPY = potY;
		repaint();
		return true;
	}
	
	/**
	 * Moves piece to the lowest point it can go, then updates the grid.
	 * 
	 * @param potX X position
	 * @param potY Y position
	 * @param piece Piece to be hard dropped
	 */
	private void hardDrop(int potX, int potY, Tetronimo piece) {
		int newY = potY;
		while (moveInBounds(potX, newY, piece)) {
			newY--;
		}
		updateBoard();
	}

	/**
	 * Spawns a random shape at the top of the grid.
	 */
	private void spawnPiece() {
		refPiece.createRandPiece();
		refPX = GRID_WIDTH / 2 - 1;
		refPY = GRID_HEIGHT - 1 + refPiece.getMinY();
		refGame.updateLabel(refGame.status, "Running...");
		// You lose if you can not spawn a piece!
		if (!moveInBounds(refPX, refPY, refPiece)) {
			refPiece.setShape(Tetronimo.Shapes.Empty);
			actTime.stop();
			isStarted = false;
			removeKeyListener(controls);
			refGame.updateLabel(refGame.status, "You Lose.");
		}
	}
	
	/**
	 * Draws the board
	 */
	public void paint(Graphics g) {
		super.paint(g);
		
		Dimension gridSize = getSize();
		int topOfGrid = (int) gridSize.getHeight() - GRID_HEIGHT * getTileLength();
		
		// Draw all tiles already on grid, starting from top
		for (int i = 0; i < GRID_HEIGHT; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				Tetronimo.Shapes tile = getShape(j, GRID_HEIGHT - i - 1);
				if (tile != Tetronimo.Shapes.Empty) {
					drawTile(g, tile, getTileWidth() * j, 
										getTileLength() * i + topOfGrid);
				}
			}
		}
		
		// Draw actual piece
		if (refPiece.getPiece() != Tetronimo.Shapes.Empty) {
			for (int i = 0; i < 4; i++) {
				int x = refPiece.getX(i) + refPX;
				int y = refPY - refPiece.getY(i);
				drawTile(g, refPiece.getPiece(), x * getTileWidth(), 
						topOfGrid + (GRID_HEIGHT - y - 1) * getTileLength());
			}
		}
	}

	private void drawTile(Graphics g, Tetronimo.Shapes tile, int x, int y) {
		// Create array of colors that corresponds to type of Piece
		Color[] palette = {new Color(0, 0, 0), new Color(255, 0, 0),
				new Color(27, 255, 1), new Color(1, 255, 239),
				new Color(166, 18, 161), new Color(255, 247, 0),
				new Color(0, 0, 255), new Color(255, 128, 0)
		};
		
		// Get color for shape
		Color tileCol = palette[tile.ordinal()];
		
		g.setColor(tileCol);
		g.fillRect(x + 1, y + 1, getTileWidth() - 2, getTileLength() - 2);
	}

	/**
	 * Initializes fields to beginning of game and resets grid to beginning.
	 */
	public void start() {
		isStarted = true;
		stoppedFalling = false;
		reset();
		spawnPiece();
		addKeyListener(controls);
		refGame.updateLabel(refGame.status, "Running...");
		actTime.start();
	}
}
