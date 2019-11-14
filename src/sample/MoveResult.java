package sample;

public class MoveResult {

    private Piece piece;
    private MoveType type;


    public Piece getPiece() {
        return piece;
    }

    public MoveType getType() {
        return type;
    }

    public MoveResult(MoveType type, Piece piece) {
        this.piece = piece;
        this.type = type;
    }

    public MoveResult(MoveType type) {
        this(type,null);
    }

    @Override
    public String toString() {
        return piece + " " + type;

    }
}
