import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.RenderingHints;


public class GamePanel extends JPanel implements Runnable {

	public static final int WIDTH = 900;
	public static final int HEIGHT = 700;
	final int FPS = 60;
	Thread gameThread;
	Board board = new Board();
	mouse m = new mouse();


	//PIECES
	public static ArrayList<Piece> pieces = new ArrayList<>();
	public static ArrayList<Piece> simPieces = new ArrayList<>();
	ArrayList<Piece>promoPieces = new ArrayList<>();
	Piece activeP, checkingP;

	//COLOR
	public static final int WHITE = 0;
	public static final int BLACK = 1; 
	int currentColor = WHITE;

	// BOOLEANS 
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	boolean gameOver;
	

	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);
		addMouseMotionListener(m);
		addMouseListener(m);

		// setPieces();
		// testPromotion();
		testIllegal();
		copyPieces(pieces, simPieces);

	}

	public void launchGame() {
		gameThread = new  Thread(this);
		gameThread.start();
		testPromotion();
		// setPieces();
		// copyPieces(pieces, simPieces);

	}

	public void setPieces() {

		// WHITE TEAM
		pieces.add(new Pawn(WHITE, 0, 6));
		pieces.add(new Pawn(WHITE, 1, 6));
		pieces.add(new Pawn(WHITE, 2, 6));
		pieces.add(new Pawn(WHITE, 3, 6));
		pieces.add(new Pawn(WHITE, 4, 6));
		pieces.add(new Pawn(WHITE, 5, 6));
		pieces.add(new Pawn(WHITE, 6, 6));
		pieces.add(new Pawn(WHITE, 7, 6));
		pieces.add(new Rook(WHITE, 0, 7));
		pieces.add(new Rook(WHITE, 7, 7));
		pieces.add(new Knight(WHITE, 1, 7));
		pieces.add(new Knight(WHITE, 6, 7));
		pieces.add(new Bishop(WHITE, 2, 7));
		pieces.add(new Bishop(WHITE, 5, 7));
		pieces.add(new Queen(WHITE, 3, 7));
		pieces.add(new King(WHITE, 4, 4));
				// pieces.add(new King(WHITE, 4, 7));

		// BLACK TEAM
		pieces.add(new Pawn(BLACK, 0, 1));
		pieces.add(new Pawn(BLACK, 1, 1));
		pieces.add(new Pawn(BLACK, 2, 1));
		pieces.add(new Pawn(BLACK, 3, 1));
		pieces.add(new Pawn(BLACK, 4, 1));
		pieces.add(new Pawn(BLACK, 5, 1));
		pieces.add(new Pawn(BLACK, 6, 1));
		pieces.add(new Pawn(BLACK, 7, 1));
		pieces.add(new Rook(BLACK, 0, 0));
		pieces.add(new Rook(BLACK, 7, 0));
		pieces.add(new Knight(BLACK, 1, 0));
		pieces.add(new Knight(BLACK, 6, 0));
		pieces.add(new Bishop(BLACK, 2, 0));
		pieces.add(new Bishop(BLACK, 5, 0));
		pieces.add(new Queen(BLACK, 3, 0));
		pieces.add(new King(BLACK, 4, 0));

	}

	public void testPromotion() {
		pieces.add(new Pawn(WHITE, 0, 5));
		pieces.add(new Pawn(BLACK, 6, 1));
	}

	public void testIllegal() {
		pieces.add(new Pawn(WHITE,7,6));
		pieces.add(new King(WHITE,3,7));
		pieces.add(new King(BLACK,0,3));
		pieces.add(new Bishop(BLACK,1,4));
		pieces.add(new Queen(BLACK,4,5));
	}

	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {

		target.clear();
		for(int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}

	@Override
	public void run() {

		//Game Loop
		double drawInterval = 1000000000 / FPS;	
		double delta = 0;
		long lastTime =  System.nanoTime();
		long currentTime;

		while(gameThread != null) {

			currentTime = System.nanoTime();

			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;

			if (delta >= 1){
				update();
				repaint();
				delta --;
			}
		}
	}

	private void update() {

		if (promotion) {
			promoting();
			
		}
		else{
			/// Mouse Pressed ///
			if(m.pressed) {
				if(activeP == null) {
					// if the active piece is null, check if you can pick up a piece

					for(Piece piece : simPieces) {

						// if the mouse is on an ally piece, pick it up as the activeP
						if(piece.color == currentColor  && 
								piece.col == m.x / Board.SQUARE_SIZE &&
								piece.row == m.y / Board.SQUARE_SIZE) {
							activeP = piece;
						}
					}
				}
				else {
					// if the plyr is holding a piece, simulate the piece 
					simulate();
				}		
			}

			// MOUSE BUTTON PREESSED //
			if(m.pressed == false) {

				if (activeP != null) {

					if(validSquare ){

						// MOVE CONFIRMED

						//Update the piece list in case a piece has been captured and removed during the simulation
						copyPieces(simPieces, pieces);
						activeP.updatePosition();

						if(isKingInCheck()) {
							// TODO :	gameOver


						} 

						if(canPromote()) {

							promotion = true;
						} else  {
							changePlayer();
						}	
						

					}
					else {
						// the move is not valid so reset everything 
						copyPieces(pieces, simPieces);
						activeP.resetPosition();
						activeP = null;
					}
					// activeP.updatePosition();
					// activeP = null;
				}
			}
		}		
	}


	private void simulate() {

		 canMove = false;
		 validSquare = false;

		 // Reset the piece list in every loop 
		 // this is basically for restoring the removed pieces dring simulation
		 copyPieces(simPieces, pieces);

		// if the piece is being held, update it position 
		activeP.x = m.x - Board.HALF_SQUARE_SIZE;
		activeP.y = m.y - Board.HALF_SQUARE_SIZE;
		activeP.col = activeP.getCol(activeP.x);
		activeP.row = activeP.getRow(activeP.y);	

		// check if the piece is hovering over a reachable square
		if(activeP.canMove(activeP.col, activeP.row)) {
    		canMove = true;

			// If hitting a piece, remove it from the list 
			if(activeP.hittingP != null) {
			    simPieces.remove(activeP.hittingP.getIndex());

			        // Update the active piece's position after capturing
			        // activeP.updatePosition();
			}
			
			if (isKingInCheck()) {
    			validSquare = false; // Block illegal moves
			}

			if(isIllegal(activeP) == false && oponentCanCaptureKing() == false) {
				validSquare = true;
			}
			
		}	

	}

	private boolean isIllegal(Piece King) {

		if(King.type == Type.KING) {
			for(Piece p : simPieces) {
				if(p != King && p.color != King.color && p.canMove(King.col, King.row)) {
					return true;
				}
			}
		}
		return false;
	}


	private boolean oponentCanCaptureKing() {

		Piece king = getKing(false);

		for(Piece p : simPieces) {
			if(p.color != king.color && p.canMove(king.col, king.row)) {
				return true;
			}
		}

		return false;
	}

	private boolean isCheckMate(){

		Piece king = getKing(true);

		if(KingCanMove(king)) {
			return false;
		}
		else {
			// but you still have chance!!
			// check if can block the attack with your piece

			// check the position of the checking piece and the king in check
			int colDiff = Math.abs(checkingP.col - king.col);
			int rowDiff = Math.abs(checkingP.row - king.row);


			if(colDiff == 0) {
				// the checking piece is attacking verticallly 
				
				if (checkingP.row < king.row) {
					//the checking piece is above the king
				
					for(int row = checkingP.row; row < king.row; row++) {
						for(Piece p : simPieces) {
							if(p != king && p.color != currentColor && p.canMove(checkingP.col, row){
								return false;
							}
						}
					}
				}
				
				if(checkingP.row > king.row ){
					// the checking piece is below the king 
				
					for(int row = checkingP.row; row > king.row; row--) {
						for(Piece p : simPieces) {
							if(p != king && p.color != currentColor && p.canMove(checkingP.col, row){
								return false;
							}
						}
					}
				}
			}
			else if(rowDiff == 0) {
				// the checking piece is attacking horizontally

				if(checkingP.col < king.col) {
					// the checking piece is to the left 

					for(int col = checkingP.col; col < king.col; col++) {
						for(Piece p : simPieces) {
							if(p != king && p.color != currentColor && p.canMove(col, checkingP.row){
								return false;
							}
						}
					}
				}

				if(checkingP.col > king.col) {
					// the checking piece is to the right

					for(int col = checkingP.col; col > king.col; col--) {
						for(Piece p : simPieces) {
							if(p != king && p.color != currentColor && p.canMove(col, checkingP.row){
								return false;
							}
						}
					}
				}
			}
			else if(colDiff == rowDiff) {
				// the checking piece is attacking diagonally
			}
			else {
				// the checking piece is knight

			}

		}

		return true;
	}

	private boolean KingCanMove(Piece king){
		//Simulate if there is any square where the king can move to 
		if(isValidMove(king, -1, -1)) { return true;}
		if(isValidMove(king, 0, -1)) { return true;}
		if(isValidMove(king, 1, -1)) { return true;}
		if(isValidMove(king, -1, 0)) { return true;}
		if(isValidMove(king, 1, 0)) { return true;}
		if(isValidMove(king, -1, 1)) { return true;}
		if(isValidMove(king, 0, 1)) { return true;}
		if(isValidMove(king, 1, 1)) { return true;}

		return false;
	}

	private boolean isValidMove(Piece king, int colPlus, int rowPlus){
		
		boolean isValidMove = false;

		//update the king's position for a second
		king.col += colPlus;
		king.row += rowPlus;

		if(king.canMove(king.col, king.row)) {

			if(king.hittingP != null) {
				simPieces.remove(king.hittingP.getIndex());
			}
			if(isIllegal(king) == false) {
				isValidMove = true;
			}
		}
		//Reset the king's position and restore the removed piece
		king.resetPosition();
		copyPieces(pieces, simPieces);

		return isValidMove;
	}

	private boolean isKingInCheck() {

		Piece king = getKing(true);

		if(activeP.canMove(king.col, king.row)) {
			checkingP = activeP;
			return true;
		} 
		else {
			checkingP = null;
		}

		return false;
	}

	private Piece getKing(boolean opponent) {

		Piece king = null;

		for(Piece p : simPieces) {
			if(opponent) {
				if(p.type == Type.KING && p.color != currentColor) {
					king = p;
				}
			}
			else {
				if(p.type == Type.KING && p.color == currentColor) {
					king = p;
				}
			}
		}
		return king;
	}

	private void changePlayer() {

		if(currentColor == WHITE) {
			currentColor = BLACK;
		}
		else {
			currentColor = WHITE;
		}
		activeP = null;
	}

	private boolean canPromote() {

		if(activeP.type == Type.PAWN) {
			if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7 ) {
				promoPieces.clear();
				promoPieces.add(new Rook(currentColor, 9, 2));
				promoPieces.add(new Knight(currentColor, 9, 3));
				promoPieces.add(new Bishop(currentColor, 9, 4));
				promoPieces.add(new Queen(currentColor, 9, 5));
				return true;
			}
		}

		return false;
	}

	public void promoting() {
		
		if(m.pressed) {
			for(Piece p : promoPieces) {
				if(p.col == m.x / Board.SQUARE_SIZE && p.row == m.y /Board.SQUARE_SIZE) {
					switch (p.type) {
						case ROOK: simPieces.add(new Rook(currentColor, activeP.col, activeP.row));break;
						case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row));break;
						case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));break;
						case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row));break;
						default: break;
					}
					simPieces.remove(activeP.getIndex());
					copyPieces(simPieces, pieces);
					activeP = null;
					promotion = false;
					changePlayer();
				}
			}
		}
	}

	public void paintComponent( Graphics g){
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		
		//BOARD
		board.draw(g2);

		//PIECES 
		for(Piece p : simPieces){
			p.draw(g2);
		}

		

		if(activeP != null) {
			if(canMove) 
			{
				if(isIllegal(activeP) || oponentCanCaptureKing() )	{
					g2.setColor(Color.gray);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
				else {
					g2.setColor(Color.white);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			}
			

			//Draw the active piece in the end so it won't be hidden by the board or the colored square
			activeP.draw(g2);
		}

		//STATUS MESSAGES
		g2.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font ("Book Antiqua", Font.PLAIN, 40));	
		g2.setColor(Color.white);

		if (promotion) {
			g2.drawString(" Promoto to:", 840, 150);
			for(Piece p : promoPieces ) {
				g2.drawImage(p.image, p.getX(p.col), p.getY(p.row) , Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}
		else {
			if(currentColor == WHITE) {
				g2.drawString("White's turn ", 840, 550);
				if(checkingP != null && checkingP.color == BLACK) {
					g2.setColor(Color.red);
					g2.drawString(" The King ", 840, 650);
					g2.drawString(" is in Check!!", 840, 700);
				}
			}
			else {
				g2.drawString(" Black's turn ", 840, 250);
				if(checkingP != null && checkingP.color == WHITE) {
					g2.setColor(Color.red);
					g2.drawString(" The King ", 840, 100);
					g2.drawString(" is in Check!!", 840, 150);
				}
			}
		}
		
	}
}
