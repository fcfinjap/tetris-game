
public class Tetronimo {
	
	enum Shapes {
		Empty, Z, InvZ, Line, T, Square, L, InvL
	}

	private int[][] coor;
	private int[][][] coorTable;
	private Shapes currentPiece;
	
	public Tetronimo() {
		coor = new int[4][2];
		setShape(Shapes.Empty);
	}

	/** method to set type to Tetronimo and assign relative coordinates
	 *  of bottom left corners of each square in the Tetronimo.
	 * 
	 * @param shape type
	 */
	public void setShape(Shapes shape) {
		// Stores relative initial coordinates of bottom left corners order of enum
		coorTable = new int[][][] {
				{{0, 0}, {0, 0}, {0, 0}, {0, 0}},
				{{0, 1}, {0, 0}, {1, 0}, {-1, 1}},
				{{1, 1}, {0, 1}, {0, 0}, {-1, 0}},
				{{2, 0}, {1, 0}, {0, 0}, {-1, 0}},
				{{1, 0}, {-1, 0}, {0, -1}, {0, 0}},
				{{0, 0}, {1, 0}, {1, 1}, {0, 1}},
				{{0, 0}, {1, 0}, {-1, 0}, {-1, -1}},
				{{0, 0}, {1, 0}, {-1, 0}, {1, -1}}
		};
		
		// search the coordinate table for the initial coordinates
		// by using ordinal() to get the index of enum
		for (int i = 0; i < coor.length; i++) {
			for (int j = 0; j < coor[i].length; j++) {
				coor[i][j] = coorTable[shape.ordinal()][i][j];
			}
		}
		
		currentPiece = shape;
		
	}
	
	/** 
	 * Creates a random piece 
	 */
	public void createRandPiece() {
		// Round up so random shape can never be Empty
		int shape = (int) Math.ceil(Math.random() * 7);
		if (shape == 0) {
			shape++;
		}
		Shapes[] types = Shapes.values();
		setShape(types[shape]);
	}
	
	// Getters and Setters
	private void setX(int square, int x) {
		coor[square][0] = x;
	}
	private void setY(int square, int y) {
		coor[square][1] = y;
	}
	public int getX(int square) {
		return coor[square][0];
	}
	public int getY(int square) {
		return coor[square][1];
	}
	public Shapes getPiece() {
		return currentPiece;
	}
	public int getMinX() {
		int minX = coor[0][0];
		for (int i = 0; i < coor.length; i++) {
			minX = Math.min(minX, coor[i][0]);
		}
		return minX;
	}
	public int getMinY() {
		int minY = coor[0][1];
		for (int i = 0; i < coor.length; i++) {
			minY = Math.min(minY, coor[i][1]);
		}
		return minY;
	}
	
	/**
	 * Rotates piece clockwise. If it is a square, rotation is not necessary.
	 */
	public Tetronimo clockwise() {
		if (currentPiece == Shapes.Square) {
			return this;
		}
		
		Tetronimo rotated = new Tetronimo();
		rotated.currentPiece = currentPiece;
		
		for (int i = 0; i < coor.length; i++) {
			rotated.setX(i, getY(i));
			rotated.setY(i, getX(i) * -1);
		}
		return rotated;
	}
	
	/**
	 * Rotates piece clockwise. If it is a square, rotation is not necessary.
	 */
	public Tetronimo counterClock() {
		if (currentPiece == Shapes.Square) {
			return this;
		}
		
		Tetronimo rotated = new Tetronimo();
		rotated.currentPiece = currentPiece;
		
		for (int i = 0; i < coor.length; i++) {
			rotated.setX(i, -1 * getY(i));
			rotated.setY(i, getX(i));
		}
		return rotated;
	}

}
