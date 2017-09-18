

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by student on 9/12/17.
 */

public class Main extends Application {

    private GameMenu gameMenu;
    private Stage primaryStage;

    private Emitter emitter = new FireEmitter();
    private List<Particle> particals = new ArrayList<>();

    private GraphicsContext g;

    private double mouseX, mouseY;

    private void onUpdate() {
        g.setGlobalAlpha(1.0);
        g.setGlobalBlendMode(BlendMode.SRC_OVER);
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, 800, 720);

        particals.addAll(emitter.emit(mouseX, mouseY));
        for (Iterator<Particle> iterator = particals.iterator(); iterator.hasNext(); ) {
            Particle p = iterator.next();
            p.update();

            if (!p.isAlive()) {
                iterator.remove();
                continue;
            }
            p.render(g);
        }
    }

    private Scene createContent() throws IOException {
        Pane root = new Pane();
        root.setPrefSize(800, 720);

        InputStream is = Files.newInputStream(Paths.get("res/images/Penguins.jpg"));
        Image img = new Image(is);
        is.close();
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(800);
        imgView.setFitHeight(720);

        gameMenu = new GameMenu();
        gameMenu.setVisible(true);

        root.getChildren().addAll(imgView, gameMenu);
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (!gameMenu.isVisible()){
                    FadeTransition ft = new FadeTransition(Duration.seconds(0.5), gameMenu);
                    ft.setFromValue(0);
                    ft.setToValue(1);

                    gameMenu.setVisible(true);
                    ft.play();
                }
                else {
                    FadeTransition ft = new FadeTransition(Duration.seconds(0.5), gameMenu);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setOnFinished(evt ->gameMenu.setVisible(false));
                    ft.play();
                }
            }
        });
        return scene;
    }

    private Scene createGame() throws IOException{
        Pane root = new Pane();
        root.setPrefSize(800, 720);
        Canvas canvas = new Canvas(800, 720);
        g = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        scene.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();



        return scene;
    }

    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setScene(createContent());
        primaryStage.show();
    }


    private class GameMenu extends Parent{
        public GameMenu(){
            VBox menu0 = new VBox(10);
            VBox menu1 = new VBox(10);

            menu0.setTranslateX(100);
            menu0.setTranslateY(200);

            menu1.setTranslateX(100);
            menu1.setTranslateY(200);

            final int offset = 400;
            menu1.setTranslateX(offset);

            Title title = new Title("Trivia");



            MenuButton btnPlay = new MenuButton("Play");
            btnPlay.setOnMouseClicked(event -> {
                try {
                    FadeTransition ft = new FadeTransition(Duration.seconds(0.5), this);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setOnFinished(evt -> this.setVisible(false));
                    ft.play();
                    primaryStage.setScene(createGame());

                }catch (IOException e){
                    e.printStackTrace();
                }

            });

            MenuButton btnOptions = new MenuButton("Options");
            btnOptions.setOnMouseClicked(event -> {
                getChildren().add(menu1);
                TranslateTransition tt0 = new TranslateTransition(Duration.seconds(0.25), menu0);
                tt0.setToX(menu0.getTranslateX() - offset);

                TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5), menu1);
                tt1.setToX(menu0.getTranslateX());

                tt0.play();
                tt1.play();

                tt0.setOnFinished(evt -> {
                    getChildren().remove(menu0);
                });
            });

            MenuButton btnQuit = new MenuButton("Quit");
            btnQuit.setOnMouseClicked(event -> {
                System.exit(0);
            });

            MenuButton btnBack = new MenuButton("Back");
            btnBack.setOnMouseClicked(event ->{
                getChildren().add(menu0);
                TranslateTransition tt0 = new TranslateTransition(Duration.seconds(0.25), menu1);
                tt0.setToX(menu1.getTranslateX() + offset);

                TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5), menu0);
                tt1.setToX(menu1.getTranslateX());

                tt0.play();
                tt1.play();

                tt0.setOnFinished(evt -> {
                    getChildren().remove(menu1);
                });
            });
            MenuButton btnSound = new MenuButton("Sound");
            MenuButton btnVideo = new MenuButton("Video");

            menu0.getChildren().addAll(title, btnPlay, btnOptions, btnQuit);
            menu1.getChildren().addAll(btnBack, btnSound, btnVideo);

            Rectangle bg = new Rectangle(800, 720);
            bg.setFill(Color.GRAY);
            bg.setOpacity(0.4);
            getChildren().addAll(bg, menu0);
        }
    }

    private static class Title extends StackPane {
        private Text text;

        public Title(String name) {
            text = new Text(name);
            text.setFont(text.getFont().font(35));

            Rectangle bg = new Rectangle(250, 60);
            bg.setOpacity(0);
            bg.setFill(Color.BLACK);
            bg.setEffect(new GaussianBlur(3.5));

            setAlignment(Pos.CENTER);
//            setRotate(-0.5);
            getChildren().addAll(bg, text);

            DropShadow drop = new DropShadow(80, Color.WHITE);
            drop.setInput(new Glow(8));
            drop.setSpread(1.5);

            setOnMousePressed(event -> setEffect(drop));
            setOnMouseReleased(event -> setEffect(null));
        }
    }

    private static class MenuButton extends StackPane {
        private Text text;

        public MenuButton(String name){
            text = new Text(name);
            text.setFont(text.getFont().font(20));

            Rectangle bg = new Rectangle(250, 30);
            bg.setOpacity(0.6);
            bg.setFill(Color.BLACK);
            bg.setEffect(new GaussianBlur(3.5));

            setAlignment(Pos.CENTER_LEFT);
            setRotate(-0.5);
            getChildren().addAll(bg, text);

            setOnMouseEntered(event -> {
                bg.setTranslateX(10);
                text.setTranslateX(10);
                bg.setFill(Color.WHITE);
                text.setFill(Color.BLACK);
            });
            setOnMouseExited(event -> {
                bg.setTranslateX(0);
                text.setTranslateX(0);
                bg.setFill(Color.BLACK);
                text.setFill(Color.WHITE);
            });

            DropShadow drop = new DropShadow(50, Color.WHITE);
            drop.setInput(new Glow());

            setOnMousePressed(event -> setEffect(drop));
            setOnMouseReleased(event -> setEffect(null));
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
