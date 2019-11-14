package sample;

import com.sun.scenario.effect.Effect;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderImage;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    private Piece piece;
    private Color originalColor;

    public boolean hasPiece() {
        return this.piece != null;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

//    public Tile() {
//
//    }

    public Tile(boolean light, int x, int y) {

        setWidth(Checkers.TILE_SIZE);
        setHeight(Checkers.TILE_SIZE);

        System.out.println("Creating tile with width: " + getWidth());
        System.out.println("Creating tile with height: " + getHeight());


        relocate(x * Checkers.TILE_SIZE, y * Checkers.TILE_SIZE);

        if (light) {
            Color color = Color.BLACK;
            setFill(color);
            this.originalColor = color;
        } else {
            Color color = Color.WHITESMOKE;
            setFill(color);
            this.originalColor = color;
        }

    }

    public void setSelected() {
        setStroke(Color.BLUE);
        setStrokeWidth(3);
    }
    public void setUnselected(){
        setStrokeWidth(0);
    }


}
