
public class PaulBrain implements Brain {

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
		int partTrench = 0;
		int Trench = 0;
		int rightHeight = 20;
		int leftHeight = 20;
		int trenchHeight = 3;
		
		// Count the holes, and sum up the heights
		for (int x=0; x<width; x++) {
			final int colHeight = board.getColumnHeight(x);
			if(x != 9) {
				rightHeight = board.getColumnHeight(x+1);	
			}
			
			if(x != 0) {
				leftHeight = board.getColumnHeight(x-1);
			}
			
			if(leftHeight > rightHeight) {
				trenchHeight = rightHeight;
			}
			else {
				trenchHeight = leftHeight;
			}
			
			sumHeight += colHeight;
			
			int y = colHeight - 2;	// addr of first possible hole
			int i = 5;	//holes further down don't really matter
			
			while (y>=0 && i>0) {
				if  (!board.getGrid(x,y)) {
					holes++;
					if(x>0 && x<9 && trenchHeight>4) {
						if(board.getGrid(x+1, trenchHeight-i) && board.getGrid(x-1, trenchHeight-i)) {
							partTrench++;
						}
					}
					else if(x == 0 && trenchHeight>4) {
						if(board.getGrid(x+1, trenchHeight-i)) {
							partTrench++;
						}
					}
					else if(x == 9 && trenchHeight>4) {
						if(board.getGrid(x-1, trenchHeight-i)) {
							partTrench++;
						}
					}
				}
				y--;
				i--;
			}
			if(partTrench > 2) {
				Trench = partTrench-2;
				partTrench = 0;
			}
			else {
				partTrench = 0;
			}
		}
		
		double avgHeight = ((double)sumHeight)/width;
		
		// Add up the counts to make an overall score
		// The weights, 8, 40, etc., are just made up numbers that appear to work
		return (8*maxHeight + 40*avgHeight + 1.25*holes + 25*Trench);	
	}
	
}
