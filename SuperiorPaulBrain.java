
public class SuperiorPaulBrain implements Brain{
	/**
	 Given a piece and a board, returns a move object that represents
	 the best play for that piece, or returns null if no play is possible.
	 See the Brain interface for details.
	*/
	@Override
	public int bestMove(Board board, Piece piece, int pieceX, int pieceY, int limitHeight)  {
		
		double bestScore = 1e20;
		int bestX = 0;
		int bestY = 0;
		Piece bestPiece = null;
		Piece current = piece;
		
		// loop through all the rotations
		while (true) {
			final int yBound = limitHeight - current.getHeight()+1;
			final int xBound = board.getWidth() - current.getWidth()+1;
			
			// For current rotation, try all the possible columns
			for (int x = 0; x<xBound; x++) {
				int y = board.dropHeight(current, x);
				if (y<yBound) {	// piece does not stick up too far
					int result = board.place(current, x, y);
					if (result <= Board.PLACE_ROW_FILLED) {
						if (result == Board.PLACE_ROW_FILLED) board.clearRows();
						
						double score = rateBoard(board);
						
						if (score<bestScore) {
							bestScore = score;
							bestX = x;
							bestY = y;
							bestPiece = current;
						}
					}
					
					board.undo();	// back out that play, loop around for the next
				}
			}
			
			current = current.nextRotation();
			if (current == piece) break;	// break if back to original rotation
		}

		if (bestPiece == null) return(JTetris.DOWN);	// could not find a play at all!
		
		if(!piece.equals(bestPiece))
			return JTetris.ROTATE;
		if(bestX == pieceX)
			return JTetris.DROP;
		if(bestX < pieceX)
			return JTetris.LEFT;
		else
			return JTetris.RIGHT;
		
	}
	
	
	/*
	 A simple brain function.
	 Given a board, produce a number that rates
	 that board position -- larger numbers for worse boards.
	 This version just counts the height
	 and the number of "holes" in the board.
	 See Tetris-Architecture.html for brain ideas.
	*/
	public double rateBoard(Board board) {
		final int width = board.getWidth();
		final int maxHeight = board.getMaxHeight();
		
		int sumHeight = 0;
		int holes = 0;
		int doubleHoles = 0;
		double diff = 0;
		
		// Count the holes, and sum up the heights
		for (int x=0; x<width; x++) {
			final int colHeight = board.getColumnHeight(x);
			sumHeight += colHeight;
			int i = 5; //holes further down don't have a great impact
			
			int y = colHeight - 2;	// addr of first possible hole
			
			while (y>=0 && i>0) {
				if  (!board.getGrid(x,y)) {
					holes++;
				}
				y--;
				i--;
			}
		}
		double avgHeight = ((double)sumHeight)/width;
		
		// Count double holes
		for (int x=0; x<width; x++) {
			final int colHeight2 = board.getColumnHeight(x);
			int y = colHeight2 - 2;	// addr of first possible hole
			
			while (y>0) {
				if  (!board.getGrid(x,y)) {
					if(!board.getGrid(x,y-1)) {
						doubleHoles++;
					}
				}
				y=y-2;
			}
		}
		
		//skirt differences
		for (int x=0; x<width; x++) {
			final int colHeight3 = board.getColumnHeight(x);

			double diffCalc = avgHeight - colHeight3;
			diff += diffCalc * diffCalc;
			
		}
		
//		Double[] ML = JBrainTester.learning();
//		double a = ML[0];
//		double b = ML[1];
//		double c = ML[2];
//		double d = ML[3];
//		double e = ML[4];
		
		// Add up the counts to make an overall score
		// The weights, 8, 40, etc., are just made up numbers that appear to work
		return (8*maxHeight + 55*avgHeight + 1.25*holes + 1.25*doubleHoles + diff);	
	}

}
