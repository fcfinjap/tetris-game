tetris-game
===========
Game: Tetris

Normal rules of Tetris, controls are displayed by pausing the game and by
	clicking controls.
	
I have the class Tetronimo, which is the class that creates the Tetris shapes.
	I used a 3-D array to store the coordinates of the squares in the shape
	relative to the shape itself. I decided to use a 3-D array instead of a 
	Map because I wanted to experiment with enumerations in Java, and also
	because I feel that it was just much easier to initialize this array. 
	However, I could have done this by using a 
	Map<Tetronimo.Shapes, Integer[][]> structure I think, and it would work 
	just as well. 

I have the Court class, which represents the tetris grid, where the gameplay
	occurs. This is the class that handles all of the tetris functions, and 
	the controls are in here as well. I did not implement wall kicks, but on
	Piazza, Tiernan said it was fine if we just did something in the case where
	the piece can't rotate. I change the status when the piece can not rotate,
	to tell the user "Invalid Rotation", and it shows that until the piece 
	moves down again. 

The Game class is the JFrame that basically takes care of the UI. 
