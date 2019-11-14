package sample;

import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.webkit.ContextMenuItem;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Checkers extends Application {

    public static final int TILE_SIZE = 100;
    private final int WIDTH = 8;
    private final int HEIGHT = 8;

    private Piece selectedPiece = null;
//    private Tile selectedTile = null;

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();

    private SimpleIntegerProperty reds = new SimpleIntegerProperty(11);
    private SimpleIntegerProperty blues = new SimpleIntegerProperty(11);

    private Tile[][] tiles = new Tile[WIDTH][HEIGHT];


    //generates root pane and add tiles and pieces as its children
    private Parent createBoard() {

        VBox vBox = new VBox();
//        GridPane root = new GridPane();
        Pane root = new Pane();

        root.setOnMouseClicked(event -> {
            System.out.println("Mouse clicked on " + event.getX() + "," + event.getY());
        });

        MenuBar menuBar = new MenuBar();

        Menu menu = new Menu("Game");
        MenuItem menuItem = new MenuItem("Save");
        MenuItem menuItem2 = new MenuItem("Restart");
        menuItem2.setOnAction(event -> {
            for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    pieceGroup.getChildren().remove(tiles[i][j].getPiece());
                }
            }
            initializeBoard();
        });
        menu.getItems().add(menuItem2);
        menu.getItems().add(menuItem);
        menuBar.getMenus().add(menu);


        vBox.getChildren().add(menuBar);
        vBox.getChildren().add(root);

        //        Node node = new Menu();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup);


        root.setOnMouseClicked(event -> {
            int x = (int) event.getX() / TILE_SIZE;
            int y = (int) event.getY() / TILE_SIZE;

            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    tiles[i][j].setUnselected();
                }
            }
            if (tiles[x][y].hasPiece()) {
                tiles[x][y].setSelected();
            }
        });
        initializeBoard();
        pieceGroup.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                System.out.println("RED TEAM: " + reds.get());
                System.out.println("BLUE TEAM: " + blues.get());
                if (blues.get() == 0) {
                    System.out.println("Red Team Wins!");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Game Over!");
                    alert.setContentText("לבנים נצחו");
                    alert.showAndWait();
                } else if (reds.get() == 0) {
                    System.out.println("Blue Team Wins!");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Game Over!");
                    alert.setContentText("כחולים נצחו");
                    alert.showAndWait();
                }
            }
        });
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Mouse clicked at " + event.getX() + "," + event.getY());
            }
        });
        return vBox;
    }

    //paints the window like a chess boards and puts the checkers pieces for each team
    private void initializeBoard() {
        this.reds.set(11);
        this.blues.set(11);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {

                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                tiles[x][y] = tile;
                tileGroup.getChildren().add(tile);

                Piece piece = null;

                if ((x + y) % 2 != 0 && y <= 2) {
                    piece = makePiece(PieceType.RED, x, y);
                } else if ((x + y) % 2 != 0 && y >= 5) {
                    piece = makePiece(PieceType.BLUE, x, y);
                }
                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
    }

    private void performMove(Piece piece) {
//        if (piece == null) return;

        int newX = (int) Math.round(piece.getLayoutX() / TILE_SIZE);
        int newY = (int) Math.round(piece.getLayoutY() / TILE_SIZE);

        //check to see what kind of move the piece is attempting
        MoveResult movementType = tryMove(piece, newX, newY);

        //previous X and Y locations, before the piece made a move
        int prevX = toBoard(piece.getOldX().get());
        int prevY = toBoard(piece.getOldY());

        Piece otherPiece = movementType.getPiece();


        switch (movementType.getType()) {
            case NONE:
                piece.abortMove();
                break;
            case NORMAL:
                piece.move(newX, newY);
                tiles[prevX][prevY].setPiece(null);
                tiles[newX][newY].setPiece(piece);
                removeFromTeam(otherPiece);
                break;
            case KILL:
                piece.move(newX, newY);
                tiles[prevX][prevY].setPiece(null);
                tiles[newX][newY].setPiece(piece);

//                 otherPiece = movementType.getPiece();
                tiles[toBoard(otherPiece.getOldX().get())][toBoard(otherPiece.getOldY())].setPiece(null);
                pieceGroup.getChildren().remove(otherPiece);
                removeFromTeam(otherPiece);
                break;
        }
    }

    public void removeFromTeam(Piece toRemove) {
        if (toRemove == null) return;
        PieceType type = toRemove.getType();
        if (type == PieceType.BLUE) {
            blues.set(blues.get() - 1);
        } else if (type == PieceType.RED) {
            reds.set(reds.get() - 1);

        }
    }

    //given a piece and coordinates of attempted move, return type of move attempt(regular move, non, kill)
    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if (tiles[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX().get());
        int y0 = toBoard(piece.getOldY());

        if (Math.abs(newX - x0) == 1 && ((newY - y0 == piece.getType().moveDirection) || piece.isKing())) {
            return new MoveResult(MoveType.NORMAL);
        } else if (Math.abs(newX - x0) == 2 && (newY - y0 == piece.getType().moveDirection * 2) || piece.isKing()) {

            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (tiles[x1][y1].hasPiece() && tiles[x1][y1].getPiece().getType() != piece.getType()) {
                return new MoveResult(MoveType.KILL, tiles[x1][y1].getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    // return the "cell" of the board from a given point
    private int toBoard(double pixel) {
//        return (int) (pixel - TILE_SIZE / 2) / 2;
        return (int) Math.round(pixel / TILE_SIZE);
    }

    //main game logic, using mouse event location to indicate selected piece movement
    public void handlePieceMovement(MouseEvent event) {

        int mouseX = (int) (event.getX() / TILE_SIZE);
        int mouseY = (int) (event.getY() / TILE_SIZE);

        System.out.println("Mouse x: " + mouseX);
        System.out.println("Mouse y: " + mouseY);

        if (mouseY == 8) {
            mouseY = 7;
        }


        if (tiles[mouseX][mouseY].hasPiece()) {
            Checkers.this.selectedPiece = tiles[mouseX][mouseY].getPiece();
            System.out.println("Selected " + Checkers.this.selectedPiece);
        } else if (Checkers.this.selectedPiece != null) {


            MoveResult result = Checkers.this.tryMove(Checkers.this.selectedPiece, mouseX, mouseY);
            System.out.println(result);
            if (mouseX < 0 || mouseY < 0 || mouseX >= WIDTH || mouseY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = Checkers.this.tryMove(selectedPiece, mouseX, mouseY);
            }

            int oldX = (int) (selectedPiece.getOldX().get() / TILE_SIZE);
            int oldY = (int) (selectedPiece.getOldY() / TILE_SIZE);

            switch (result.getType()) {
                case NONE:
                    selectedPiece.abortMove();
                    Checkers.this.selectedPiece = null;
                    break;
                case NORMAL:
                    selectedPiece.move(mouseX, mouseY);
                    tiles[oldX][oldY].setPiece(null);
                    System.out.println(oldX + "," + oldY);
                    tiles[mouseX][mouseY].setPiece(selectedPiece);
                    Checkers.this.selectedPiece = null;
                    break;
                case KILL:
                    selectedPiece.move(mouseX, mouseY);
                    tiles[oldX][oldY].setPiece(null);
                    tiles[mouseX][mouseY].setPiece(selectedPiece);

                    Piece otherPiece = result.getPiece();
                    tiles[Checkers.this.toBoard(otherPiece.getOldX().get())][Checkers.this.toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    Checkers.this.selectedPiece = null;
                    break;
            }
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = createBoard();
        root.setOnMousePressed(this::handlePieceMovement);

        Scene scene = new Scene(root);
        primaryStage.setTitle("CheckersApp");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            performMove(piece);
        });

        return piece;
    }


    public static void main(String[] args) {
        launch(args);

    }
}
