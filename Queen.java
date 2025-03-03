
public class Queen extends Piece {
		
	public Queen (int color, int col, int row) {
		super(color, col, row);

		type = Type.QUEEN;

		if(color == GamePanel.WHITE) {
			image = getImage("wq");
		} 
		 else {
		 	image = getImage("bq");
		 }
	}

	public boolean canMove(int targetCol, int targetRow){
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){

			// Vertical & Horizontal 
			if(targetCol == preCol || targetRow == preRow) {
				if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					return true;
				}
			}

			// Diagnol 
			if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow))  {
				if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		return false;
	}
}