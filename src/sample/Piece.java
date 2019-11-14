package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;


import static sample.Checkers.TILE_SIZE;

public class Piece extends StackPane {

    private boolean king = false;

    private PieceType type;

    private double mouseX;
    private double mouseY;

    private SimpleDoubleProperty oldX = new SimpleDoubleProperty();
    private SimpleDoubleProperty oldY = new SimpleDoubleProperty();
//    private double oldY;

    public Piece(PieceType type, int x, int y) {

        this.type = type;

        Color bgColor = (Color.DARKSLATEGREY);

        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        ellipse.setFill(bgColor);

        ellipse.setStroke(bgColor);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);

        move(x, y);

        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2 + TILE_SIZE * 0.07);

        Ellipse ellipse2 = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);


        ellipse2.setFill(type == PieceType.RED ? Color.ANTIQUEWHITE : Color.BLUE);

        ellipse2.setStroke(Color.BLACK);
        ellipse2.setStrokeWidth(TILE_SIZE * 0.03);

        ellipse2.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse2.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);


        getChildren().addAll(ellipse, ellipse2);


        setOnMousePressed(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX.get(), e.getSceneY() - mouseY + oldY.get());
            System.out.println("Moving piece to " + (e.getSceneX() - mouseX + oldX.get()) + "," + (e.getSceneY() - mouseY + oldY.get()));
        });

        setOnMouseEntered(event -> {
            System.out.println("Hovering at " + (int) oldX.get() / TILE_SIZE + "," + (int) oldY.get() / TILE_SIZE + this);
        });

        //tracks y value to check if a piece becomes king
        oldY.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println("TESTING BIND X\n" + "newValue:" + newValue + "\noldValue: " + oldValue);
                if (newValue.intValue() == 0 && getType() == PieceType.BLUE) {
                    setKing(true);
                    setEffect(new Bloom());
                    setEffect(new DropShadow(23, Color.YELLOW));
                    System.out.println(this + " is now king");
                } else if ((newValue.intValue() == (TILE_SIZE * 7) && getType() == PieceType.RED)) {
                    setKing(true);
                    setEffect(new Bloom());
                    setEffect(new DropShadow(23, Color.YELLOW));
                    System.out.println(this + " is now king");
                }

            }
        });


    }

    public void setKing(boolean isKing) {
        this.king = true;
    }

    public boolean isKing() {
        return this.king;
    }

    public PieceType getType() {
        return type;
    }

    public SimpleDoubleProperty getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY.get();
    }

    @Override
    public String toString() {
        //capitalize first letter
        String theType = this.type.toString().substring(0, 1).toUpperCase() + this.type.toString().substring(1).toLowerCase();

        String isKing = "";
        if (king) {
            isKing = "(king) ";
        }

        return
                isKing + theType + " "
                        +
                        +(int) oldX.get() / TILE_SIZE + ","
                        + (int) oldY.get() / TILE_SIZE;

    }


    public void move(int x, int y) {
        oldX.set(x * TILE_SIZE);
        oldY.set(y * TILE_SIZE);
        relocate(oldX.get(), oldY.get());
    }

    public void abortMove() {
        relocate(oldX.get(), oldY.get());
    }

}
