/*
 * This class represents a game board 9x9 grid
 * The value will be stored as an integer
 */
public class Board {
	private int[][] grid;
	private int size;

	public Board(int size) {
		super();
		this.grid = new int[size][size];
		this.size = size;
		//Initialize the board to avoid NullPointerExceptions
		for(int y = 0; y < this.grid.length; y++) {
			for(int x = 0; x < this.grid.length; x++) {
				this.grid[y][x] = 0;
			}
		}
	}

	public int[][] getGrid() {
		return grid;
	}

	public void setGrid(int[][] grid) {
		this.grid = grid;
	}
	
	/* HELPER METHODS */
	//Referring each element inside 2d array as a cell
	public int getValueFromCell(int row, int col) {
		return this.grid[row][col];
	}
	
	public void setValueToCell(int row, int col, int value) {
		this.grid[row][col] = value;
	}
	
	public int getSize() {
		return this.size;
	}
	
	//Getting the size of one sub grid i.e 3x3 grid
	public int getSubSize() {
		return this.size/3;
	}

}
