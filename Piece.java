
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics2D;


public class Piece {

	public BufferedImage image;
	public int x, y;
	public int col, row, preCol, preRow;
	public int color;
	public Piece hittingP;

	public Piece(int color, int col, int row) {
		this.color = color;
		this.row = row; 
		this.col = col;
		x = getX(col);
		y = getY(row);
		preCol = col;
		preRow = row;

	}

	public BufferedImage getImage(String imagePath) {

		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));			
		} 
		 catch (IOException e) {
    		e.printStackTrace();
		}
		
		return image;
	}

	public int getX(int col) {
		return col * Board.SQUARE_SIZE;
	}

	public int getY(int row) {
		return row * Board.SQUARE_SIZE;
	}

	public int getCol(int x) {
		 return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}

	public int getRow(int x) {
		 return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}

	public int getIndex() {
		for(int i = 0; i < GamePanel.simPieces.size(); i++) {
			if(GamePanel.simPieces.get(i) == this) {
				return i;
			}
		}
		return 0;
	}

	public void updatePosition() {
		x = getX(col);
		y = getY(row);
		preCol = getCol(x);
		preRow = getRow(x);
	}

	public void resetPosition() {
		col = preCol;
		row = preRow;
		x = getX(col);
		y = getY(row);
	}

	public boolean canMove(int targetCol, int targetRow) {
		return false;
	}

	public boolean isWithinBoard(int targetCol, int targetRow) {
		if(targetCol >= 0 && targetCol <=7 && targetRow >= 0 && targetRow <= 7) {
			return true;
		}
		return false;
	}

	public boolean isSameSquare(int targetCol, int targetRow) {

		if(targetCol == preCol && targetRow == preRow) {
			return true;
		}
		return false;
	}

	public Piece getHitting(int targetCol, int targetRow) {
		for (Piece piece : GamePanel.simPieces) {
			if(piece.col == targetCol && piece.row == targetRow && piece != this) {
				return piece;
			}
		}
		return null;
	}

	boolean isValidSquare(int targetCol, int targetRow) {
		hittingP = getHitting(targetCol, targetRow);

		if(hittingP == null) { // the square is VACANT
			return true;
		}
		else { // the square is OCCUPIED
			if(hittingP.color != this.color) { // if the color is different then it can be captured
				return true;	
			} 
			else {
				hittingP = null;
			}
		}
		return false;
	}

	public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {

		// When piece is moving on to the left 
		for(int c = preCol + 1; c < targetCol; c++){
			for (Piece piece : GamePanel.simPieces ) {
				if(piece.col == c && piece.row == targetRow ) {
					hittingP = piece;
					return true;
				}
				
			}
		}

		// When piece is moving on to the moving up 
		for(int r = preRow - 1; r > targetRow; r--){
			for (Piece piece : GamePanel.simPieces ) {
				if(piece.row == r && piece.col == targetCol ) {
					hittingP = piece;
					return true;
				}
				
			}
		}

		// When piece is moving on to the moving down 
		for(int r = preCol + 1; r < targetCol; r++){
			for (Piece piece : GamePanel.simPieces ) {
				if(piece.row == r && piece.col == targetCol ) {
					hittingP = piece;
					return true;
				}
				
			}
		}

		// When piece is moving on to the left 
		for(int c = preRow - 1; c > targetCol; c--){
			for (Piece piece : GamePanel.simPieces ) {
				if(piece.col == c && piece.row == targetRow ) {
					hittingP = piece;
					return true;
				}
				
			}
		}



		return false;
	}

	public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {

		if(targetRow < preRow) {
			// Up Left 
			for(int c = preCol - 1; c > targetCol; c-- ) {
				int diff = Math.abs(c - preCol);
				for(Piece piece : GamePanel.simPieces) {
					if(piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return  true;
					}
				}
			}

			// Up Right 
			for(int c = preCol + 1; c < targetCol; c++ ) {
				int diff = Math.abs(c - preCol);
				for(Piece piece : GamePanel.simPieces) {
					if(piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return  true;
					}
				}
			}
		}

		if(targetRow > preRow) {
			// Down Left 
			for(int c = preCol - 1; c > targetCol; c-- ) {
				int diff = Math.abs(c - preCol);
				for(Piece piece : GamePanel.simPieces) {
					if(piece.col == c && piece.row == preRow + diff) {
						hittingP = piece;
						return  true;
					}
				}
			}

			// Down Right 
			for(int c = preCol + 1; c < targetCol; c++ ) {
				int diff = Math.abs(c - preCol);
				for(Piece piece : GamePanel.simPieces) {
					if(piece.col == c && piece.row == preRow + diff) {
						hittingP = piece;
						return  true;
					}
				}
			}
		}

		return  false;
	}

	public void draw(Graphics2D g2) {
		g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}

} 