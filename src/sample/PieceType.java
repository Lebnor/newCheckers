package sample;

public enum PieceType {

    BLUE(-1), RED(1);

    public int moveDirection;

    PieceType(int x) {
        this.moveDirection = x;
    }
}
