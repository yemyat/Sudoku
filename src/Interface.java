import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/* THIS CLASS CONTROLS THE UI */
public class Interface {
	private JPanel[][] board;

	final static String BUTTONCARD = "Buttons"; //For CardLayout
	final static String LABELCARD = "Labels"; //For CardLayout
	private Controller game; //Game controller
	private boolean checkedAnswer = false; //For disabling some UI parts

	public Interface() {
		drawBoard();
		newGame();
	}

	private void newGame() {
		game = new Controller();
		this.checkedAnswer = false;
		updateBoard(game.getGameBoard());
	}

	/* Updating the user interface after getting data from controller */
	private void updateBoard(Board gameBoard) {
		CardLayout cl = null;
		for (int y = 0; y < this.board.length; y++) {
			for (int x = 0; x < this.board.length; x++) {
				cl = (CardLayout) (this.board[y][x].getLayout());
				JPanel temp = (JPanel) this.board[y][x].getComponent(1);
				JLabel tempLabel = (JLabel) temp.getComponent(0);
				tempLabel.setText("0");
				cl.show(this.board[y][x], BUTTONCARD);

				if (gameBoard.getValueFromCell(y, x) != 0) {
					cl.show(this.board[y][x], LABELCARD);
					tempLabel.setText(String.valueOf(gameBoard
							.getValueFromCell(y, x)));
				}
			}
		}
		updatePossibilities();
	}

	/* Updating possibilities of each cell */
	private void updatePossibilities() {
		ArrayList<Integer> possibilities = null;
		CardLayout cl = null;
		Board currentBoard = getCurrentBoard();
		for (int y = 0; y < this.board.length; y++) {
			for (int x = 0; x < this.board.length; x++) {
				
				//Firstly, get all the possibilities
				possibilities = game.getPossibilities(y, x, currentBoard);
				
				//Make the cardlayout to show 9 buttons
				cl = (CardLayout) (this.board[y][x].getLayout());
				JPanel temp = (JPanel) this.board[y][x].getComponent(0);
				
				//Make the buttons invisible and empty first
				for (int j = 0; j < 9; j++) {
					JButton button = (JButton) temp.getComponent(j);
					button.setText("");
					button.setVisible(false);
				}
				
				//Show the necessary buttons
				for (int i = 0; i < possibilities.size(); i++) {
					JButton button = (JButton) temp.getComponent(possibilities
							.get(i) - 1);
					button.setText(String.valueOf(possibilities.get(i)));
					button.setVisible(true);
				}
			}
		}
	}
	
	/* Converting the board in the UI to something our controller can understand */
	private Board getCurrentBoard() {
		Board tempBoard = new Board(9);
		for (int row = 0; row < tempBoard.getSize(); row++) {
			for (int col = 0; col < tempBoard.getSize(); col++) {
				JPanel temp = (JPanel) this.board[row][col].getComponent(1);
				JLabel tempLabel = (JLabel) temp.getComponent(0);
				tempBoard.setValueToCell(row, col,
						Integer.parseInt(tempLabel.getText()));
			}
		}

		return tempBoard;
	}

	/* Main GUI drawing */
	private void drawBoard() {
		board = new JPanel[9][9];
		JFrame baseFrame = new JFrame("Judoku");
		baseFrame.setBounds(120, 120, 650, 550);
		baseFrame.setResizable(false);

		/* CREATING GAME PANEL */
		JPanel gamePanel = new JPanel(new GridLayout(9, 9));
		baseFrame.add(gamePanel);

		/* WE'RE GOING TO ADD SOME PANELS TO BE USED AS A CARDLAYOUT */
		for (int y = 1; y < 10; y++) {
			for (int x = 1; x < 10; x++) {
				/*
				 * Each cell will have two cards; one with 9 buttons and the
				 * other one with a label
				 */
				final JPanel cell = new JPanel(new CardLayout());
				cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));

				// This will create a border around each sub grid
				createPanelBorder(cell, y, x);

				JPanel labelCard = new JPanel();
				final JLabel textLabel = new JLabel("0", JLabel.CENTER);
				textLabel.setFont(new Font("Serif", Font.PLAIN, 48));
				labelCard.add(textLabel);

				textLabel.addMouseListener(new MouseListener() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (!checkedAnswer) {
							CardLayout cl = (CardLayout) (cell.getLayout());
							cl.next(cell);
							textLabel.setText("0"); // Give it something
													// impossible
							updatePossibilities();
						}
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						textLabel.setForeground(Color.BLUE);
					}

					@Override
					public void mouseExited(MouseEvent e) {
						textLabel.setForeground(Color.BLACK);

					}

					@Override
					public void mousePressed(MouseEvent e) {

					}

					@Override
					public void mouseReleased(MouseEvent e) {

					}
				});

				JPanel buttonCard = new JPanel(new GridLayout(3, 3));
				for (int i = 0; i < 9; i++) {
					final JButton btn = new JButton(String.valueOf(i + 1));
					btn.setBorder(BorderFactory.createEmptyBorder());
					btn.setForeground(Color.GRAY);
					buttonCard.add(btn);
					btn.addMouseListener(new MouseListener() {
						@Override
						public void mouseClicked(MouseEvent arg0) {
							if (!checkedAnswer) {
								textLabel.setText(btn.getText());
								CardLayout cl = (CardLayout) (cell.getLayout());
								cl.next(cell);

								updatePossibilities();

								game.saveCurrentGameState(getCurrentBoard());
							}
						}

						@Override
						public void mouseEntered(MouseEvent arg0) {
							btn.setForeground(Color.BLUE);
						}

						@Override
						public void mouseExited(MouseEvent arg0) {
							btn.setForeground(Color.GRAY);
						}

						@Override
						public void mousePressed(MouseEvent arg0) {
						}

						@Override
						public void mouseReleased(MouseEvent arg0) {
						}
					});
				}

				cell.add(buttonCard, BUTTONCARD);
				cell.add(labelCard, LABELCARD);
				gamePanel.add(cell);

				board[y - 1][x - 1] = cell; // Add this cell to an array for
				// manipulation

			}
		}

		gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		/* CREATING MENU BAR */
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.LIGHT_GRAY);

		/* CREATING MENU */
		JMenu fileMenu = new JMenu("File");
		fileMenu.setBackground(Color.LIGHT_GRAY);
		menuBar.add(fileMenu);

		JMenu playMenu = new JMenu("Play");
		playMenu.setBackground(Color.LIGHT_GRAY);
		menuBar.add(playMenu);

		/* CREATING MENU ITEMS FOR FILE MENU */
		JMenuItem newGameItem = new JMenuItem("New");
		// This line binds a keyboard hotkey for this item.
		newGameItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
		fileMenu.add(newGameItem);
		newGameItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});

		fileMenu.addSeparator();

		JMenuItem submitSolutonItem = new JMenuItem("Submit Solution");
		fileMenu.add(submitSolutonItem);
		submitSolutonItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!checkedAnswer) {
					if (game.checkSolution(getCurrentBoard())) {
						GUIConsole.display("You have won the game");
					} else {
						GUIConsole
								.display("Your solution isn't ready yet. Please check again.");
						// Because of the game nature, we can't flag incorrect
						// cells
					}
				}
			}
		});

		JMenuItem showSolutionButton = new JMenuItem("Show Solution");
		fileMenu.add(showSolutionButton);
		showSolutionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateBoard(game.generateSolution());
				checkedAnswer = true;
			}
		});

		/* CREATING MENU ITEMS FOR PLAY MENU */
		JMenuItem undoMenuItem = new JMenuItem("Undo");
		undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Z, java.awt.Event.CTRL_MASK));
		playMenu.add(undoMenuItem);
		undoMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkedAnswer) {
					updateBoard(game.undo(getCurrentBoard()));
				}
			}
		});

		JMenuItem redoMenuItem = new JMenuItem("Redo");
		redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_R, java.awt.Event.SHIFT_MASK));
		playMenu.add(redoMenuItem);
		redoMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkedAnswer) {
					Board temp = game.redo(getCurrentBoard());
					if (temp != null) {
						updateBoard(temp);
					}
				}
			}
		});

		baseFrame.setJMenuBar(menuBar);
		baseFrame.setVisible(true);

	}

	/* THIS METHOD CREATES A BORDER AFTER 3X3 GRID */
	private void createPanelBorder(JPanel cell, int y, int x) {
		if (x % 3 == 0) {
			cell.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 4,
					Color.GRAY));
		}

		if (y % 3 == 0) {
			cell.setBorder(BorderFactory.createMatteBorder(1, 1, 4, 1,
					Color.GRAY));
		}

		if (x % 3 == 0 && y % 3 == 0) {
			cell.setBorder(BorderFactory.createMatteBorder(1, 1, 4, 4,
					Color.GRAY));

		}

		if (y == 9 || x == 9) {
			cell.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
					Color.GRAY));
			if (x % 3 == 0 && x != 9) {
				cell.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 4,
						Color.GRAY));
			}
			if (y % 3 == 0 && y != 9) {
				cell.setBorder(BorderFactory.createMatteBorder(1, 1, 4, 1,
						Color.GRAY));
			}
		}
	}

}
