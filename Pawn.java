// package piece;
// import main.GamePanel;



public class Pawn extends Piece {
	
	public Pawn(int color, int col, int row) {
		super(color, col, row);

		if (color == GamePanel.WHITE) {
			image = getImage("wp");
		} 
		 else {
			image = getImage("bp");
		}

	}

	public boolean canMove(int targetCol, int targetRow) {
		
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {

			// Define the move based on its color 
			int moveValue;
			if(color == GamePanel.WHITE) {
				moveValue = -1;
			}
			else {
				moveValue = 1;
			}

			// checking the hitting piece 
			hittingP = getHitting(targetCol, targetRow);

			// 1 square movement 
			if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
				return true;
			}

			// 2 square movement 
			if(targetCol == preCol && targetRow == preRow + moveValue*2  &&  hittingP == null && moved == false &&
					pieceIsOnStraightLine(targetCol, targetRow) == false) {
				return true;
			}

		}
		return false;
	}

}