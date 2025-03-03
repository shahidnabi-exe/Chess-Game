

public class King extends Piece {
		
	public King (int color, int col, int row) {
		super(color, col, row);

		type = Type.KING;

		if(color == GamePanel.WHITE) {
			image = getImage("wk");
		} 
		 else {
		 	image = getImage("bk");
		 }
	}

	public boolean canMove(int targetCol, int targetRow){

		if(isWithinBoard(targetCol, targetRow)) {
			//	 MOVEMENT 
			if (Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) ==  1 || 
					Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow)  == 1) 	{

				if(isValidSquare(targetCol,targetRow)){
					return true;
				}				
			}

			// CASTLING 
			if(moved == false) {

				//Right Castling
				
					

				// Left Castling
			}

		}
		return false;
	}
}