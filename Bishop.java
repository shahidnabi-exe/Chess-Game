
public class Bishop extends Piece {
		
	public Bishop (int color, int col, int row) {
		super(color, col, row);

		type = Type.BISHOP;

		if(color == GamePanel.WHITE) {
			image = getImage("wb");
		} 
		 else {
		 	image = getImage("bb");
		 }
	}

	public boolean canMove(int targetCol, int targetRow) {

		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false ) {

			if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
				if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		return false;
	}
}