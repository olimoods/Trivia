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
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * Created by Andrew, Oliver, Casey, Tyler!!
 */

@SuppressWarnings("serial")
public class Main extends Application {

    private MainMenu mainMenu;
    private GameMenu gameMenu;
    private Stage primaryStage;
    private int points;

    private Emitter emitter = new FireEmitter();
    private List<Particle> particals = new ArrayList<>();

    private GraphicsContext g;

    private double mouseX, mouseY;

    private void onUpdate() throws IOException {
        g.setGlobalAlpha(2.0);
        g.setGlobalBlendMode(BlendMode.SRC_OVER);
//        g.setFill(Color.BLACK);
//        g.fillRect(0, 0, 800, 720);
        Pane root = new Pane();
        root.setPrefSize(800, 720);
        Image image1 = new Image(new FileInputStream("/Users/student/IdeaProjects/Trivia-by-olivia/res/images/Penguins.jpg"));
        g.drawImage(image1, 0, 0, 800, 720);


//

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

        InputStream is = Files.newInputStream(Paths.get("res/sickgif.gif"));
        Image img = new Image(is);
        is.close();
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(800);
        imgView.setFitHeight(720);

        mainMenu = new MainMenu();
        mainMenu.setVisible(true);

        root.getChildren().addAll(imgView, mainMenu);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (!mainMenu.isVisible()) {
                    FadeTransition ft = new FadeTransition(Duration.seconds(0.5), mainMenu);
                    ft.setFromValue(0);
                    ft.setToValue(1);

                    mainMenu.setVisible(true);
                    ft.play();
                } else {
                    FadeTransition ft = new FadeTransition(Duration.seconds(0.5), mainMenu);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setOnFinished(evt -> mainMenu.setVisible(false));
                    ft.play();
                }
            }
        });
        return scene;
    }

    private Scene createGame(ArrayList<questionAnswer> classes) throws IOException {
        Pane root = new Pane();
        root.setPrefSize(800, 720);
        Canvas canvas = new Canvas(800, 720);
        g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, 800, 720);

        gameMenu = new GameMenu(classes);
        gameMenu.setVisible(true);


        //Images for avatar
        InputStream py1 = Files.newInputStream(Paths.get("res/images/player1.png"));
        InputStream py2 = Files.newInputStream(Paths.get("res/images/player2.png"));

        Image img1 = new Image(py1);
        py1.close();
        Image img2 = new Image(py2);
        py2.close();

        ImageView img = new ImageView(img1);
        img.setFitWidth(50);
        img.setFitHeight(50);
        img.setX(0);
        img.setY(0);
        img.setOpacity(0.6);

        root.getChildren().addAll(canvas, img, gameMenu);

        Scene scene = new Scene(root);
        scene.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    onUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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

    private class GameMenu extends Parent {
        private ArrayList<questionAnswer> classes;
        private AudioClip clip;
        private AudioClip clip2;

        public GameMenu(ArrayList<questionAnswer> classes) {
            VBox menu = new VBox(10);
            menu.setTranslateX(100);
            menu.setTranslateY(200);
            this.classes = classes;
            refreshScreen(classes, menu);
        }

        private boolean determineAnswer(questionAnswer quest, int index) {
            return quest.getCorrectAnswer() == index;
        }

        private void reset(ArrayList<questionAnswer> classes, VBox menu, Rectangle bg, Text t) {
            getChildren().removeAll(menu, gameMenu, bg, t);
            GameMenu gameMenu = new GameMenu(classes);
            getChildren().add(gameMenu);

        }

        private void refreshScreen(ArrayList<questionAnswer> classes, VBox menu) {
            if (classes.size() > 0) {

                int rand = (int) (Math.random() * classes.size());
                questionAnswer quest = classes.get(rand);
//            System.out.println(quest);
                classes.remove(rand);
                clip = null;
                clip2 = null;

                if (quest.getQuestion().equals("What song is this?")) {
                    String str = "res/songs/OliverAndTyler.m4a";
                    String file = new File(str).toURI().toString();
                    clip = new AudioClip(file);
                    clip.play();

                }
                if (quest.getQuestion().equals("Which song is this?")) {
                    try {
                        String str = "res/songs/Panda.mp3";
                        String file = new File(str).toURI().toString();
                        clip2 = new AudioClip(file);
                        clip2.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Rectangle bg = new Rectangle(800, 720);
                Text t = new Text("Score: " + getPoints());
                t.setFill(Color.WHITE);

                Title question = new Title(quest.getQuestion());
                question.text.setFont(question.text.getFont().font(20));
                question.setAlignment(Pos.CENTER_LEFT);

                QuestionButton quesbtnA = new QuestionButton(quest.getAnswers(0));
                quesbtnA.setOnMouseClicked(event -> {

                    if (determineAnswer(quest, 0)) {
                        points += 100;
                        //add points
                    }

                    if (clip != null) {
                        clip.stop();
                    }
                    if (clip2 != null) {
                        clip2.stop();
                    }
                    reset(classes, menu, bg, t);
                });

                QuestionButton quesbtnB = new QuestionButton(quest.getAnswers(1));
                quesbtnB.setOnMouseClicked(event -> {
                    if (determineAnswer(quest, 1)) {
                        //add points
                        points += 100;
                    }
                    if (clip != null) {
                        clip.stop();
                    }
                    if (clip2 != null) {
                        clip2.stop();
                    }
                    reset(classes, menu, bg, t);
                });

                QuestionButton quesbtnC = new QuestionButton(quest.getAnswers(2));
                quesbtnC.setOnMouseClicked(event -> {

                    if (determineAnswer(quest, 2)) {
                        //add points
                        points += 100;
                    }
                    if (clip != null) {
                        clip.stop();
                    }
                    if (clip2 != null) {
                        clip2.stop();
                    }
                    reset(classes, menu, bg, t);
                });

                QuestionButton quesbtnD = new QuestionButton(quest.getAnswers(3));
                quesbtnD.setOnMouseClicked(event -> {

                    if (determineAnswer(quest, 3)) {
                        //add points
                        points += 100;
                    }
                    if (clip != null) {
                        clip.stop();
                    }
                    if (clip2 != null) {
                        clip2.stop();
                    }
                    reset(classes, menu, bg, t);
                });

                bg.setFill(Color.GRAY);
                bg.setOpacity(0.4);

                menu.getChildren().addAll(question, quesbtnA, quesbtnB, quesbtnC, quesbtnD, t);
                getChildren().addAll(bg, menu);
                System.out.println(points);
            } else {
                Title title = new Title("Good Game! Score: " + getPoints());
                title.text.setFont(title.text.getFont().font(60));
                title.setPrefSize(800, 720);
                title.text.setFill(Color.CHOCOLATE);
                getChildren().add(title);
                System.out.println("sup");

            }
        }

        public int getPoints() {
            return points;
        }
    }

    private class MainMenu extends Parent {
        public MainMenu() {
            VBox menu0 = new VBox(10);
            VBox menu1 = new VBox(10);

            menu0.setTranslateX(100);
            menu0.setTranslateY(200);

            menu1.setTranslateX(100);
            menu1.setTranslateY(200);
            ArrayList<questionAnswer> classes = new ArrayList<questionAnswer>();

            try {
                File inputFile = new File("/Users/student/IdeaProjects/Trivia-by-olivia/src/Questions.xml");
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputFile);
                doc.getDocumentElement().normalize();
//                System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList nList = doc.getElementsByTagName("questions");

                String question;
                String a1;
                String a2;
                String a3;
                String a4;
                String correct;


                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode2 = nList.item(temp);
                    NodeList nl = nNode2.getChildNodes();
//                    System.out.println("\nCurrent Element :" + nNode2.getNodeName());
                    for (int temp1 = 0; temp1 < nl.getLength(); temp1++) {
                        Node nNode1 = nl.item(temp1);
                        NodeList nl2 = nNode1.getChildNodes();
                        for (int temp2 = 0; temp2 < nl2.getLength(); temp2++) {
                            Node nNode = nl2.item(temp2);
                            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElement = (Element) nNode;
                                question = eElement
                                        .getElementsByTagName("q")
                                        .item(0)
                                        .getTextContent();
                                a1 = eElement
                                        .getElementsByTagName("a1")
                                        .item(0)
                                        .getTextContent();
                                a2 = eElement
                                        .getElementsByTagName("a2")
                                        .item(0)
                                        .getTextContent();
                                a3 = eElement
                                        .getElementsByTagName("a3")
                                        .item(0)
                                        .getTextContent();
                                a4 = eElement
                                        .getElementsByTagName("a4")
                                        .item(0)
                                        .getTextContent();
                                correct = eElement
                                        .getElementsByTagName("correct")
                                        .item(0)
                                        .getTextContent();

//                                System.out.println(question);
                                classes.add(new questionAnswer(question, a1, a2, a3, a4, Integer.parseInt(correct)));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


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
                    primaryStage.setScene(createGame(classes));
                    getChildren().remove(mainMenu);

                } catch (IOException e) {
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
                primaryStage.close();
                System.exit(0);
            });

            MenuButton btnBack = new MenuButton("Back");
            btnBack.setOnMouseClicked(event -> {
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
            text.setFill(Color.WHITE);
            text.setFont(text.getFont().font(30));

            Rectangle bg = new Rectangle(250, 60);
            bg.setOpacity(0);
            bg.setFill(Color.BLACK);
            bg.setEffect(new GaussianBlur(3.5));

            setAlignment(Pos.CENTER);
//            setRotate(-0.5);
            getChildren().addAll(bg, text);
        }
    }

    private static class QuestionButton extends StackPane {
        private Text text;

        public QuestionButton(String name) {
            text = new Text(name);
            text.setFont(text.getFont().font(20));

            Rectangle bg = new Rectangle(600, 80);
            bg.setOpacity(0.8);
            bg.setFill(Color.DARKGRAY);
            bg.setEffect(new GaussianBlur(4.5));

            setAlignment(Pos.CENTER_LEFT);
            setRotate(-0.5);
            getChildren().addAll(bg, text);

            setOnMouseEntered(event -> {
                bg.setTranslateX(20);
                text.setTranslateX(20);
                bg.setFill(Color.DARKGRAY);
                text.setFill(Color.WHITE);
            });
            setOnMouseExited(event -> {
                bg.setTranslateX(0);
                text.setTranslateX(0);
                bg.setFill(Color.DARKGRAY);
                text.setFill(Color.BLACK);
            });

            DropShadow drop = new DropShadow(100, Color.WHITE);
            drop.setInput(new Glow());

            setOnMousePressed(event -> setEffect(drop));
            setOnMouseReleased(event -> setEffect(null));
        }
    }

    private static class MenuButton extends StackPane {
        private Text text;

        public MenuButton(String name) {
            text = new Text(name);
            text.setFill(Color.WHITE);
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