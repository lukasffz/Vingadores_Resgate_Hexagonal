package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.util.HashSet;
import java.util.Set;

public class GameController {

    @FXML private AnchorPane gamePane;
    @FXML private Label infoLabel;

    private JogoConfig config;
    private ImageView player;
    private Set<Node> platforms = new HashSet<>();

    // --- Constantes de Física e Movimento ---
    private static final double GRAVITY = 0.6;
    private static final double JUMP_FORCE = -15.0;
    private static final double MOVE_SPEED = 5.0;

    private double velocityY = 0.0;
    private boolean canJump = false;

    private final Set<KeyCode> activeKeys = new HashSet<>();
    private AnimationTimer gameLoop;

    /**
     * Inicializa o controlador com os dados do jogo.
     * @param config As configurações passadas do menu principal.
     */
    public void initData(JogoConfig config) {
        this.config = config;
        infoLabel.setText("Personagem: " + config.getPersonagemEscolhido() + " | Use as setas/WASD para mover e pular.");

        initializeGame();

        // CORREÇÃO: Força o foco no AnchorPane e registra os listeners APÓS a cena estar pronta
        Platform.runLater(() -> {
            gamePane.requestFocus(); // Garante que o painel receba o foco para eventos de teclado
            if (gamePane.getScene() != null) {
                gamePane.getScene().setOnKeyPressed(e -> activeKeys.add(e.getCode()));
                gamePane.getScene().setOnKeyReleased(e -> activeKeys.remove(e.getCode()));
            }
        });
    }

    private void initializeGame() {
        // 1. Cria o personagem como ImageView (Herói escolhido)
        String heroName = config.getPersonagemEscolhido();
        String imagePath = getImagePath(heroName);

        try {
            Image heroImage = new Image(getClass().getResourceAsStream(imagePath));
            player = new ImageView(heroImage);
            player.setFitWidth(70);
            player.setFitHeight(70);
            gamePane.getChildren().add(player);

            // Posicionamento inicial
            player.setLayoutX(100);
            player.setLayoutY(400);

        } catch (Exception e) {
            System.err.println("ERRO: Falha ao carregar a imagem do herói: " + imagePath + ". Usando Placeholder.");

            // Cria um placeholder (Retângulo Roxo) caso a imagem falhe
            Rectangle placeholder = new Rectangle(50, 50, javafx.scene.paint.Color.PURPLE);
            placeholder.setLayoutX(100);
            placeholder.setLayoutY(400);
            gamePane.getChildren().add(placeholder);

            // Define o ImageView para que a lógica de colisão ainda funcione
            player = new ImageView();
            player.setLayoutX(placeholder.getLayoutX());
            player.setLayoutY(placeholder.getLayoutY());
            player.setFitWidth(placeholder.getWidth());
            player.setFitHeight(placeholder.getHeight());
        }

        // 2. Adiciona as plataformas do FXML ao conjunto de plataformas
        for (Node node : gamePane.getChildren()) {
            // Filtrar apenas os retângulos de plataforma para colisão
            if (node instanceof Rectangle) {
                platforms.add(node);
            }
        }

        // 3. Inicia o loop do jogo
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
    }

    private String getImagePath(String heroName) {
        // Assume que as imagens estão na pasta base do resources
        if ("Homem de Ferro".equals(heroName)) {
            return "/homem_ferro.png";
        } else if ("Capitão América".equals(heroName)) {
            return "/capitao_america.png";
        }
        return "/placeholder.png";
    }

    private void update() {
        // 1. Processa movimento lateral
        double dx = 0;

        if (activeKeys.contains(KeyCode.LEFT) || activeKeys.contains(KeyCode.A)) {
            dx -= MOVE_SPEED;
            player.setScaleX(-1); // Vira o personagem para a esquerda
        }
        if (activeKeys.contains(KeyCode.RIGHT) || activeKeys.contains(KeyCode.D)) {
            dx += MOVE_SPEED;
            player.setScaleX(1); // Vira o personagem para a direita
        }

        player.setLayoutX(player.getLayoutX() + dx);

        // Limita o personagem à tela
        if (player.getLayoutX() < 0) player.setLayoutX(0);
        if (player.getLayoutX() > gamePane.getWidth() - player.getFitWidth()) {
            player.setLayoutX(gamePane.getWidth() - player.getFitWidth());
        }

        // 2. Processa pulo
        if ((activeKeys.contains(KeyCode.UP) || activeKeys.contains(KeyCode.W) || activeKeys.contains(KeyCode.SPACE)) && canJump) {
            velocityY = JUMP_FORCE;
            canJump = false;
        }

        // 3. Aplica gravidade e move verticalmente
        velocityY += GRAVITY;
        player.setLayoutY(player.getLayoutY() + velocityY);

        // 4. Checa colisão
        checkCollisions();
    }

    private void checkCollisions() {
        boolean onGroundThisFrame = false;

        for (Node platformNode : platforms) {
            // Colisão com o Node
            if (player.getBoundsInParent().intersects(platformNode.getBoundsInParent())) {
                Rectangle platform = (Rectangle) platformNode;

                // Colisão por cima (Aterrissagem/Pisar)
                // Checa se estava acima no frame anterior (player.getLayoutY() + player.getFitHeight() - velocityY)
                if (velocityY >= 0 && (player.getLayoutY() + player.getFitHeight() - velocityY) <= platform.getLayoutY()) {

                    // Reposiciona exatamente no topo da plataforma
                    player.setLayoutY(platform.getLayoutY() - player.getFitHeight());

                    velocityY = 0;   // Zera a velocidade vertical
                    canJump = true;  // Permite pular
                    onGroundThisFrame = true;
                    break; // Sai do loop após aterrissar com sucesso
                }

                // Colisão por baixo (cabeçada):
                else if (velocityY < 0 && player.getLayoutY() <= platform.getLayoutY() + platform.getHeight()) {
                    velocityY = 0; // Para o movimento ascendente
                    player.setLayoutY(platform.getLayoutY() + platform.getHeight()); // Empurra o jogador para baixo
                }
            }
        }

        // 5. Reinicia se cair para fora da tela
        if (player.getLayoutY() > gamePane.getHeight()) {
            player.setLayoutX(100);
            player.setLayoutY(400);
            velocityY = 0;
            canJump = true;
        }
    }
}