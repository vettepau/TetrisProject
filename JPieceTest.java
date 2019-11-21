
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;


/**
 Debugging client for the Piece class.
 The JPieceTest component draws all the rotations of a tetris piece.
 JPieceTest.main()  creates a frame  with one JPieceTest for each
 of the 7 standard tetris pieces.
 
 This is the starter file version -- 
 The outer shell is done. You need to complete paintComponent()
 and drawPiece().
*/
class JPieceTest extends JComponent {
	protected Piece root;	
	

	public JPieceTest(Piece piece, int width, int height) {
		super();
		
		setPreferredSize(new Dimension(width, height));

		root = piece;
	}

	/**
	 Draws the rotations from left to right.
	 Each piece goes in its own little box.
	*/
	public final int MAX_ROTATIONS = 4;
	public void paintComponent(Graphics g) {
		int startX = 0;
		Piece temp = root;
		int i = 0;
		
		do {
			drawPiece(g, temp, new Rectangle(startX,0,getWidth()/4,getHeight()));
			startX += getWidth()/4;
			temp = temp.nextRotation();	
		}while(!root.equals(temp));
	}
	
	/**
	 Draw the piece inside the given rectangle.
	*/
	private void drawPiece(Graphics g, Piece piece, Rectangle r) {
		Point[] body = piece.getBody();
		int[] skirt = piece.getSkirt();
		int blockSize = (int)(Math.min(r.width, r.height)/4);
		for(Point p: body) {
			g.setColor(Color.BLACK);
			if(skirt[p.x] == p.y)
				g.setColor(Color.YELLOW);
			g.fillRect(r.x + (blockSize) * p.x, 3*(blockSize) - (blockSize) * p.y, blockSize-1, blockSize-1);
		}

		g.setColor(Color.RED);
		g.setFont(new Font("Arial",Font.BOLD,blockSize));
		g.drawString("w:" + piece.getWidth() + " h:" + piece.getHeight(), r.x, 4*blockSize);
		

//		g.setColor(Color.BLUE);
//		g.drawRect(r.x, r.y, r.width, r.height-1);
	}	


	/**
	 Draws all the pieces by creating a JPieceTest for
	 each piece, and putting them all in a frame.
	*/
	static public void main(String[] args)
	
	{
		JFrame frame = new JFrame("Piece Tester");
		JComponent container = (JComponent)frame.getContentPane();
		
		// Put in a BoxLayout to make a vertical list
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		Piece[] pieces = Piece.getPieces();
		
		for (int i=0; i<pieces.length; i++) {
			JPieceTest test = new JPieceTest(pieces[i], 375, 75);
			container.add(test);
		}
		
		// Size the window and show it on screen
		frame.pack();
		frame.setVisible(true);
		
		// Quit on window close
		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
	}
}
