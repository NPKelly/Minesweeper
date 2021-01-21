package Main;

import javafx.scene.control.Button;

public class MinesweeperCell {
    private Button button;
    private int x;
    private int y;
    private char type;
    private boolean flag;
    private boolean disabled;

    public MinesweeperCell(int x, int y) {
        setX(x);
        setY(y);
        setFlag(false);
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
