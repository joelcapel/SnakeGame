package com.example.snakegame;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static final int ANCHO = 800;
    private static final int ALTURA = ANCHO;
    private static final int FILAS = 20;
    private static final int COLUMAS = FILAS;
    private static final int TAMAÑO_CUADRADO = ANCHO/FILAS;
    private static final String [] FRUTAS_IMAGENES = new String[]{"ic_naranja.png", "ic_manzana.png", "ic_cereza.png", "ic_fresa.png",
            "ic_coco.png", "ic_melocoton.png", "ic_sandia.png", "ic_naranja.png", "ic_pomelo.png"};

    private static final int DERECHA = 0;
    private static final int IZQUIERDA = 1;
    private static final int ARRIBA = 2;
    private static final int ABAJO = 3;

    private GraphicsContext graphicsContext;
    private List<Point> cuerpoSerpiente = new ArrayList();
    private Point cabezaSerpiente;
    private Image frutasImage;
    private int frutaX;
    private int frutaY;
    private boolean finJuego;
    private int direccionActual;
    private int puntuacion = 0;
    private int velocidad = 1;

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Snake");
        Group root = new Group();
        Canvas canvas = new Canvas(ANCHO, ALTURA);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        graphicsContext = canvas.getGraphicsContext2D();

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT || code == KeyCode.D){
                    if (direccionActual != IZQUIERDA){
                        direccionActual = DERECHA;
                    }
                }else if (code == KeyCode.LEFT || code == KeyCode.A){
                    if (direccionActual != DERECHA){
                        direccionActual = IZQUIERDA;
                    }
                }else if (code == KeyCode.UP || code == KeyCode.W){
                    if (direccionActual != ARRIBA){
                        direccionActual = ABAJO;
                    }
                }else if (code == KeyCode.DOWN || code == KeyCode.S){
                    if (direccionActual != ABAJO){
                        direccionActual = ARRIBA;
                    }
                }
            }
        });

        for (int i = 0; i < 3; i++) {
            cuerpoSerpiente.add(new Point(5, FILAS / 2));
        }
        cabezaSerpiente = cuerpoSerpiente.get(0);
        generarFruta();
        puntuacion();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(130),e -> run(graphicsContext)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void run(GraphicsContext graphicsContext){
        if (finJuego){
            graphicsContext.setFill(Color.RED);
            graphicsContext.setFont(new Font("Digital-7", 70));
            graphicsContext.fillText("FIN DEL JUEGO", ANCHO / 4, ALTURA / 2);
            return;
        }

        dibujarFondo(graphicsContext);
        dibujarFruta(graphicsContext);
        dibujarSerpiente(graphicsContext);
        puntuacion();

        for (int i = cuerpoSerpiente.size() -1; i >= 1 ; i--) {
            cuerpoSerpiente.get(i).x = cuerpoSerpiente.get(i -1).x;
            cuerpoSerpiente.get(i).y = cuerpoSerpiente.get(i -1).y;
        }

        switch (direccionActual){
            case DERECHA:
                moverDerecha();
                break;
            case IZQUIERDA:
                moverIzquierda();
                break;
            case ARRIBA:
                moverArriba();
                break;
            case ABAJO:
                moverAbajo();
                break;
        }

        finJuego();
        comerFruta();
    }

    private void dibujarFondo(GraphicsContext graphicsContext){
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMAS; j++) {
                if ((i + j) %2 == 0){
                    graphicsContext.setFill(Color.web("7D7E7A"));
                }else {
                    graphicsContext.setFill(Color.web("395602"));
                }
                graphicsContext.fillRect(i * TAMAÑO_CUADRADO, j * TAMAÑO_CUADRADO, TAMAÑO_CUADRADO, TAMAÑO_CUADRADO);
            }
        }
    }

    private void generarFruta(){
        start:
        while (true) {
            frutaX = (int) (Math.random() * FILAS);
            frutaY = (int) (Math.random() * COLUMAS);

            for (Point serpiente: cuerpoSerpiente){
                if (serpiente.getX() == frutaX && serpiente.getY() == frutaY){
                    continue start;
                }
            }

            frutasImage = new Image(FRUTAS_IMAGENES[(int) (Math.random() * FRUTAS_IMAGENES.length)]);
            break;
        }
    }


    private void dibujarFruta(GraphicsContext graphicsContext){
        graphicsContext.drawImage(frutasImage, frutaX * TAMAÑO_CUADRADO, frutaY * TAMAÑO_CUADRADO, TAMAÑO_CUADRADO, TAMAÑO_CUADRADO);
    }

    private void dibujarSerpiente(GraphicsContext graphicsContext){
        graphicsContext.setFill(Color.web("B4F538"));
        graphicsContext.fillRoundRect(cabezaSerpiente.getX()*TAMAÑO_CUADRADO, cabezaSerpiente.getY() * TAMAÑO_CUADRADO, TAMAÑO_CUADRADO-1, TAMAÑO_CUADRADO-1, 35, 35);
        if (puntuacion >=5 && puntuacion <=10) {
            graphicsContext.setFill(Color.web("0638F7"));
        }else if (puntuacion >=11 && puntuacion <=20) {
            graphicsContext.setFill(Color.web("F71106"));
        }else if (puntuacion >=21 && puntuacion <=30) {
            graphicsContext.setFill(Color.web("000000"));
        }else if (puntuacion >=31) {
            graphicsContext.setFill(Color.web("ffffff"));
        }
        for (int i = 1; i < cuerpoSerpiente.size(); i++) {
            graphicsContext.fillRoundRect(cuerpoSerpiente.get(i).getX() * TAMAÑO_CUADRADO, cuerpoSerpiente.get(i).getY()* TAMAÑO_CUADRADO, TAMAÑO_CUADRADO-1, TAMAÑO_CUADRADO-1, 20, 20);
        }

    }

    //He comentado las siguientes lineas porque no consigo lo previsto, el proposito es aumentar la velocidad cuando
    //La puntuacion supere ciertos valores y lo hace pero el cuerpo no acompaña, la serpiente no rellena esos campos

    private void moverDerecha(){
        cabezaSerpiente.x++;
        /*if (puntuacion >=5 && puntuacion <=10) {
            cabezaSerpiente.x+=2;
        }else if (puntuacion >10 && puntuacion <=20){
            cabezaSerpiente.x+=3;
        }else if (puntuacion >20 && puntuacion <=30) {
            cabezaSerpiente.x += 4;
        }else if (puntuacion >31) {
            cabezaSerpiente.x += 5;
        }*/
    }

    private void moverIzquierda(){
        cabezaSerpiente.x--;
        /*if (puntuacion >=5 && puntuacion <=10) {
            cabezaSerpiente.x-=2;
        }else if (puntuacion >10 && puntuacion <=20){
            cabezaSerpiente.x-=3;
        }else if (puntuacion >20 && puntuacion <=30) {
            cabezaSerpiente.x-= 4;
        }else if (puntuacion >31) {
            cabezaSerpiente.x-= 5;
        }*/
    }

    private void moverArriba(){
        cabezaSerpiente.y++;
        /*if (puntuacion >=5 && puntuacion <=10) {
            cabezaSerpiente.y+=2;
        }else if (puntuacion >10 && puntuacion <=20){
            cabezaSerpiente.y+=3;
        }else if (puntuacion >20 && puntuacion <=30) {
            cabezaSerpiente.y+= 4;
        }else if (puntuacion >31) {
            cabezaSerpiente.y+= 5;
        }*/
    }

    private void moverAbajo(){
        cabezaSerpiente.y--;
        /*if (puntuacion >=5 && puntuacion <=10) {
            cabezaSerpiente.y-=2;
        }else if (puntuacion >10 && puntuacion <=20){
            cabezaSerpiente.y-=3;
        }else if (puntuacion >20 && puntuacion <=30) {
            cabezaSerpiente.y-= 4;
        }else if (puntuacion >31) {
            cabezaSerpiente.y-= 5;
        }*/
    }

    public void finJuego(){
        if (cabezaSerpiente.x <0 || cabezaSerpiente.y < 0 || cabezaSerpiente.x * TAMAÑO_CUADRADO >= ANCHO || cabezaSerpiente.y * TAMAÑO_CUADRADO >= ALTURA){
            finJuego = true;
        }


        for (int i = 1; i < cuerpoSerpiente.size(); i++) {
            if (cabezaSerpiente.x == cuerpoSerpiente.get(i).getX() && cabezaSerpiente.getY() == cuerpoSerpiente.get(i).getY()){
                finJuego = true;
                break;
            }
        }
    }

    private void comerFruta(){
        if (cabezaSerpiente.getX() == frutaX && cabezaSerpiente.getY() == frutaY){
            cuerpoSerpiente.add(new Point(1, -1));
            generarFruta();
            puntuacion++;
        }
    }

    private void puntuacion(){
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(new Font("Digital-7", 35));
        graphicsContext.fillText("Puntuacion: " + puntuacion, 10, 35);
    }

    public static void main(String[] args) {
        launch();
    }
}