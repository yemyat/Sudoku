import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/*
 * This class controls the game logic
 */
public class Controller {
	private Board gameBoard;
	protected boolean isSolved;
	protected Stack<Board> undoStack;
	protected Stack<Board> redoStack;

	public Controller() {
		super();
		this.isSolved = false;
		// Initializing the board
		this.gameBoard = new Board(9);
		this.importBoardFromFile();
		// For game control
		this.undoStack = new Stack<Board>();
		this.redoStack = new Stack<Board>();
	}

	/* PUZZLE SOLVING */
	public Board generateSolution() {
		solveCurrentBoard(0);
		return this.gameBoard;
	}

	/* THIS METHOD CHECKS IF A USER HAS ENTERED A VALID PUZZLE */
	public boolean checkSolution(Board submittedBoard) {
		for (int row = 0; row < submittedBoard.getSize(); row++) {
			for (int col = 0; col < submittedBoard.getSize(); col++) {
				if (!isValidNumber(row, col,
						submittedBoard.getValueFromCell(row, col),
						submittedBoard)) {
					return false;
				}
			}
		}
		return true;
	}

	/* THIS RECURSIVE METHOD UTILIZES BACKTRACKING TO SOLVE THE BOARD */
	private void solveCurrentBoard(int trial) {
		// stop condition
		if (trial >= this.gameBoard.getSize() * this.gameBoard.getSize()) {
			this.isSolved = true;
			return;
		}

		// Getting row and col by the number of trials
		int row = trial % this.gameBoard.getSize();
		int col = trial / this.gameBoard.getSize();

		// If the cell has not yet filled up
		if (this.gameBoard.getValueFromCell(row, col) == 0) {
			// List out all the possibilities of that particular cell
			ArrayList<Integer> possibilities = getPossibilities(row, col,
					this.gameBoard);
			// Randomizing may improve the game's performance
			Collections.shuffle(possibilities);

			for (int possibility : possibilities) {
				// Don't do anything if the board has already been solved
				if (this.isSolved) {
					break;
				}
				// Try filling up one possibility and solve the board again to
				// check if that was the right number
				this.gameBoard.setValueToCell(row, col, possibility);
				solveCurrentBoard(trial + 1);
			}
			// If it cannot be solved yet, leave a backtrack.
			if (!this.isSolved) {
				this.gameBoard.setValueToCell(row, col, 0);
			}
		} else {
			solveCurrentBoard(trial + 1);
		}
	}

	/* THIS METHOD GENERATES ALL THE POSSIBILITIES OF A CELL IN A BOARD */
	public ArrayList<Integer> getPossibilities(int row, int col, Board board) {
		ArrayList<Integer> possibilities = new ArrayList<Integer>();
		for (int i = 1; i <= board.getSize(); i++) {
			if (isValidNumber(row, col, i, board)) {
				possibilities.add(i);
			}
		}
		return possibilities;
	}

	/* CHECKS IF THERE WERE ANY DUPLICATIONS IN A ROW */
	private boolean checkRowViolation(int row, int col, int value, Board board) {
		for (int j = 0; j < board.getSize(); j++) {
			if (j != col && board.getValueFromCell(row, j) == value) {
				return true;
			}
		}
		return false;
	}

	/* CHECKS IF THERE WERE ANY DUPLICATIONS IN A COL */
	private boolean checkColViolation(int row, int col, int value, Board board) {
		for (int j = 0; j < board.getSize(); j++) {
			if (j != row && board.getValueFromCell(j, col) == value) {
				return true;
			}
		}
		return false;
	}

	/* CHECKS IF THERE WERE ANY DUPLICATIONS IN A SUB GRID */
	private boolean checkSubViolation(int row, int col, int value, Board board) {
		// Get row and column of that grid
		// This will always return you 3x3 grid
		int rowBase = row - row % (board.getSubSize());
		int colBase = col - col % (board.getSubSize());

		// Look for duplicates in the grid
		for (int i = rowBase; i < (board.getSubSize()) + rowBase; i++) {
			for (int j = colBase; j < (board.getSubSize()) + colBase; j++) {
				if (i != row && j != col
						&& board.getValueFromCell(i, j) == value) {
					return true;
				}
			}
		}
		return false;
	}

	/* COMBINATION OF ALL THE VIOLATION RULES */
	public boolean isValidNumber(int row, int col, int value, Board board) {
		if (value <= 0) {
			return false;
		} else {
			return !checkRowViolation(row, col, value, board)
					&& !checkColViolation(row, col, value, board)
					&& !checkSubViolation(row, col, value, board);
		}
	}

	/* GAME VERSION CONTROLS */

	/*
	 * SAVING CURRENT STATE OF THE GAME IF AND ONLY IF IT DOES NOT RESEMBLE THE
	 * OLD ONE
	 */
	public void saveCurrentGameState(Board currentBoard) {
		if (this.undoStack.size() > 0) {
			Board previousBoard = this.undoStack.peek();
			if (!areIdentical(currentBoard, previousBoard)) {
				this.undoStack.push(currentBoard);
			}
		} else {
			this.undoStack.push(currentBoard);
		}
	}

	/* WHEN WE UNDO, WE NEED TO PUSH CURRENT STATE INTO REDO STACK OR VICE VERSA */

	public Board undo(Board currentBoard) {
		if (this.undoStack.size() != 0) {
			if (areIdentical(currentBoard, this.undoStack.peek())) {
				this.undoStack.pop();
			}

			Board pastBoard = this.undoStack.pop();
			this.redoStack.push(currentBoard);
			return pastBoard;
		} else {
			return this.gameBoard;
		}
	}

	public Board redo(Board currentBoard) {
		if (this.redoStack.size() > 0) {
			Board pastBoard = this.redoStack.pop();
			this.undoStack.push(currentBoard);
			return pastBoard;
		} else {
			return null;
		}
	}

	/* ACCESSOR METHODS */
	public Board getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(Board gameBoard) {
		this.gameBoard = gameBoard;
	}

	public int getUndoSize() {
		return this.undoStack.size();
	}

	public int getRedoSize() {
		return this.redoStack.size();
	}

	/* PRIVATE METHODS */
	private void importBoardFromFile() {
		String puzzle = null;
		String fileName = "res/puzzle.txt";
		// Random number should be below 244 (total number of lines in the file)
		int random = (int) Math.round(Math.random() * 243);
		try {
			LineNumberReader lReader = new LineNumberReader(new FileReader(
					fileName));
			while (lReader.getLineNumber() != random) {
				puzzle = lReader.readLine();
			}

			int row = 0;
			int col = 0;
			for (int i = 0; i < (this.gameBoard.getSize() * this.gameBoard
					.getSize()); i++) {
				if (this.gameBoard.getValueFromCell(row, col) == 0) {
					this.gameBoard.setValueToCell(row, col,
							charToInt(puzzle.charAt(i)));
					col++;
				}

				if (col % this.gameBoard.getSize() == 0) {
					row++;
					col = 0;
				}
			}
			lReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method converts a character to an integer
	 */
	private int charToInt(char charAt) {
		return Integer.parseInt(String.valueOf(charAt));
	}

	/*
	 * This method checks if two boards are identical to each other
	 */
	private boolean areIdentical(Board originalBoard, Board newBoard) {
		for (int y = 0; y < originalBoard.getSize(); y++) {
			for (int x = 0; x < originalBoard.getSize(); x++) {
				if (originalBoard.getValueFromCell(y, x) != newBoard
						.getValueFromCell(y, x)) {
					return false;
				}
			}
		}
		return true;
	}

}
