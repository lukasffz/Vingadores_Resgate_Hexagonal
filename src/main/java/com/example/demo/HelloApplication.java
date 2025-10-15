package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        // Permite que o HelloController acesse esta instância para trocar a cena
        HelloController controller = fxmlLoader.getController();
        controller.setApp(this);

        stage.setTitle("Vingadores: Resgate Hexagonal");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Carrega a cena do jogo e a exibe no palco principal.
     * @param config As configurações do jogo para passar ao GameController.
     * @throws IOException Se o arquivo game-view.fxml não for encontrado.
     */
    public void switchToGameScene(JogoConfig config) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("game-view.fxml"));
        Scene gameScene = new Scene(fxmlLoader.load(), primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());

        // Passa as configurações para o controlador do jogo
        GameController gameController = fxmlLoader.getController();
        gameController.initData(config);

        primaryStage.setScene(gameScene);
        primaryStage.centerOnScreen();
    }

    /**
     * Altera a resolução da janela.
     * @param width A nova largura.
     * @param height A nova altura.
     */
    public static void setResolution(double width, double height) {
        if (primaryStage != null) {
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
            primaryStage.centerOnScreen();
        }
    }

    /**
     * Retorna o Stage principal da aplicação.
     * @return O Stage principal.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}