package Main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Minesweeper extends Application {

	private int windowWidth = 600;
	private int windowHeight = 400;
	private int playWidth = 30;
	private int playHeight = 16;
	private int numBombs = 99;
	private MinesweeperCell[][] cells;
	private boolean gameStarted = false;
	private boolean gameOver = false;
	private int cellsRevealed;
	private long randomSeed;
	private Timer timer;
	private int r;
	private int g;
	private int b;
	private int rDelta = -1;
	private int gDelta = 1;
	private int bDelta = 1;
	private Scene scene;
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		scene = createScene();
		scene.setOnKeyTyped(new ResetEvent(this));
		primaryStage.setScene(scene);
		primaryStage.setTitle("Minesweeper");
		primaryStage.sizeToScene();
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
		primaryStage.show();
	}

	public Scene createScene() {
		Scene scene = new Scene(setupBoard());
		return scene;
	}

	public GridPane setupBoard() {
		gameOver = false;
		cellsRevealed = 0;
		GridPane gridPane = new GridPane();
		gridPane.setGridLinesVisible(true);
		cells = new MinesweeperCell[playWidth][playHeight];
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new MinesweeperCell(i, j);
				cells[i][j].setButton(new Button());
				int J = j;
				int I = i;
				cells[i][j].getButton().setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						switch (event.getButton()) {
							case PRIMARY: {
								revealButton(I, J);
								break;
							}
							case SECONDARY: {
								flagButton(I, J);
								break;
							}
							case MIDDLE: {
								massReveal(I, J);
								break;
							}
						}
					}
				});
				double fontPx = cells[i][j].getButton().getFont().getSize() * (4 / 3);
				cells[i][j].getButton().setMinSize(windowWidth / playWidth + fontPx, windowHeight / playHeight + fontPx);
				cells[i][j].getButton().setStyle("-fx-background-color: #3B3B3B; -fx-text-fill: #9E9E9E;");
				gridPane.add(cells[i][j].getButton(), i, j);
			}
		}

		return gridPane;
	}

	public void setupMinesweeper(int x, int y) {
		Random random = new Random();
		r = random.nextInt(256);
		g = random.nextInt(256);
		b = random.nextInt(256);
		rDelta = Math.random() > 0.5 ? 1 : -1;
		gDelta = Math.random() > 0.5 ? 1 : -1;
		bDelta = Math.random() > 0.5 ? 1 : -1;
		// Randomly place bombs around the game board
		for (int i = 0; i < numBombs; i++) {
			int bombX;
			int bombY;
			do {
				bombX = random.nextInt(playWidth);
				bombY = random.nextInt(playHeight);
				if (cells[bombX][bombY].getType() == 'B') {
					bombX = x;
					bombY = y;
				}
			} while (((bombX >= x - 1) && (bombX <= x + 1)) && ((bombY >= y - 1) && (bombY <= y + 1)));
			cells[bombX][bombY].setType('B');
		}
		// Count the number of bombs that are around a given cell
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				int bombCount = 0;
				if (cells[i][j].getType() != 'B') {
					if (i - 1 >= 0) {
						if (j - 1 >= 0) {
							if (cells[i - 1][j - 1].getType() == 'B') {
								bombCount++;
							}
						}
						if (cells[i - 1][j].getType() == 'B') {
							bombCount++;
						}
						if (j + 1 < playHeight) {
							if (cells[i - 1][j + 1].getType() == 'B') {
								bombCount++;
							}
						}
					}
					if (j - 1 >= 0) {
						if (cells[i][j - 1].getType() == 'B') {
							bombCount++;
						}
					}
					if (j + 1 < playHeight) {
						if (cells[i][j + 1].getType() == 'B') {
							bombCount++;
						}
					}
					if (i + 1 < playWidth) {
						if (j - 1 >= 0) {
							if (cells[i + 1][j - 1].getType() == 'B') {
								bombCount++;
							}
						}
						if (cells[i + 1][j].getType() == 'B') {
							bombCount++;
						}
						if (j + 1 < playHeight) {
							if (cells[i + 1][j + 1].getType() == 'B') {
								bombCount++;
							}
						}
					}
					cells[i][j].setType(Character.forDigit(bombCount, 10));
				}
			}
		}
		gameStarted = true;
	}

	public void revealButton(int x, int y) {
		if (!gameStarted) {
			setupMinesweeper(x, y);
		}
		if (x < 0 || x >= playWidth || y < 0 || y >= playHeight) {
			return;
		}
		if (cells[x][y].isDisabled() || cells[x][y].isFlag() || gameOver) {
			return;
		}
		if (cells[x][y].getType() == 'B') {
			loseGame(x, y);
		}
		else {
			cells[x][y].setDisabled(true);
			cells[x][y].getButton().setStyle("-fx-background-color: darkgrey;");
			if (cells[x][y].getType() == '0') {
				revealButton(x - 1, y - 1);
				revealButton(x - 1, y);
				revealButton(x - 1, y + 1);
				revealButton(x, y - 1);
				revealButton(x, y + 1);
				revealButton(x + 1, y - 1);
				revealButton(x + 1, y);
				revealButton(x + 1, y + 1);
			}
			if (cells[x][y].getType() != '0') {
				cells[x][y].getButton().setText(Character.toString(cells[x][y].getType()));
			}
			cellsRevealed++;
			if (cellsRevealed == (playHeight * playWidth - numBombs)) {
				winGame();
			}
		}
	}

	public void flagButton(int x, int y) {
		if (gameOver) {
			return;
		}
		if (cells[x][y].isDisabled()) {
			return;
		}
		if (cells[x][y].isFlag()) {
			cells[x][y].getButton().setText(Character.toString(' '));
			cells[x][y].setFlag(false);
		}
		else {
			cells[x][y].getButton().setText(Character.toString('F'));
			cells[x][y].setFlag(true);
		}
	}

	public void massReveal(int x, int y) {
		if (!cells[x][y].isDisabled()) {
			return;
		}
		int numFlags = 0;
		if (x - 1 >= 0) {
			if (y - 1 >= 0) {
				if (cells[x - 1][y - 1].isFlag()) {
					numFlags++;
				}
			}
			if (cells[x - 1][y].isFlag()) {
				numFlags++;
			}
			if (y + 1 < playHeight) {
				if (cells[x - 1][y + 1].isFlag()) {
					numFlags++;
				}
			}

		}
		if (y - 1 >= 0) {
			if (cells[x][y - 1].isFlag()) {
				numFlags++;
			}
		}
		if (y + 1 < playHeight) {
			if (cells[x][y + 1].isFlag()) {
				numFlags++;
			}
		}
		if (x + 1 < playWidth) {
			if (y - 1 >= 0) {
				if (cells[x + 1][y - 1].isFlag()) {
					numFlags++;
				}
			}
			if (cells[x + 1][y].isFlag()) {
				numFlags++;
			}
			if (y + 1 < playHeight) {
				if (cells[x + 1][y + 1].isFlag()) {
					numFlags++;
				}
			}
		}
		if (numFlags == Character.getNumericValue(cells[x][y].getType())) {
			revealButton(x - 1, y - 1);
			revealButton(x - 1, y);
			revealButton(x - 1, y + 1);
			revealButton(x, y - 1);
			revealButton(x, y + 1);
			revealButton(x + 1, y - 1);
			revealButton(x + 1, y);
			revealButton(x + 1, y + 1);
		}
	}

	public void colorCycle() {
		if (r < 0 || r > 255) {
			rDelta *= -1;
		}
		if (g < 0 || g > 255) {
			gDelta *= -1;
		}
		if (b < 0 || b > 255) {
			bDelta *= -1;
		}
		r += rDelta;
		g += gDelta;
		b += bDelta;
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (cells[i][j].getType() != 'B' || !cells[i][j].isFlag()) {
					cells[i][j].getButton().setStyle("-fx-background-color: rgb(" + r + "," + g + "," + b + ");");
				}
			}
		}
	}

	public void startColorShift() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				colorCycle();
			}
		}, 0, 35);
	}

	public void stopColorShift() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	public void winGame() {
		gameOver = true;
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (cells[i][j].isFlag()) {
					cells[i][j].getButton().setStyle("-fx-background-color: gold;");
				}
				else if (cells[i][j].getType() == 'B') {
					cells[i][j].setFlag(true);
					cells[i][j].getButton().setText("F");
					cells[i][j].getButton().setStyle("-fx-background-color: gold;");
				}
			}
		}
		startColorShift();
	}

	public void loseGame(int x, int y) {
		cells[x][y].getButton().setStyle("-fx-background-color: red;");
		gameOver = true;
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (cells[i][j].getType() == 'B') {
					cells[i][j].getButton().setText(Character.toString(cells[i][j].getType()));
				}
				else if (cells[i][j].isFlag()) {
					cells[i][j].getButton().setStyle("-fx-background-color: orange;");
				}

			}
		}
	}

	public boolean isGameStarted() {
		return gameStarted;
	}

	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void setAttributes(int playWidth, int playHeight, int numBombs) {
		this.playWidth = playWidth;
		this.playHeight = playHeight;
		this.numBombs = numBombs;
	}

	public static void main(String[] args) {
		launch(args);
	}
}



