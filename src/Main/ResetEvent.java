package Main;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

class ResetEvent implements EventHandler<KeyEvent> {

    private Minesweeper parent;

    public ResetEvent(Minesweeper parent) {
        this.parent = parent;
    }

    @Override
    public void handle(KeyEvent event) {
        switch (event.getCharacter()) {
            case "b": {
                parent.setAttributes(9,9,10);
                resetBoard();
                break;
            }
            case "m": {
                parent.setAttributes(16,16,40);
                resetBoard();
                break;
            }
            case "h": {
                parent.setAttributes(30,16,99);
                resetBoard();
                break;
            }
            case "n": {
                resetBoard();
                break;
            }
        }
    }

    private void resetBoard() {
        parent.stopColorShift();
        parent.setScene(parent.createScene());
        parent.getScene().setOnKeyTyped(this);
        parent.getPrimaryStage().setScene(parent.getScene());
        parent.getPrimaryStage().sizeToScene();
        parent.setGameStarted(false);
    }
}