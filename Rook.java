// package piece;	
// import main.GamePanel;


public class Rook extends Piece {

 	public Rook(int color, int col, int row) {
 		super(color, col, row);

 		if(color == GamePanel.WHITE){
 			image = getImage("wr");
 		}
 		 else {
 		 	image = getImage("br");
 		}
 	}

 	public boolean canMove(int targetCol, int targetRow) {
 		
 		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false ) {
 			// Rook can move as long as either its Col or Row is the same 
 			if(targetCol == preCol || targetRow == preRow) {
 				if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
}
