package com.example.demo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.Optional;

public class HelloController {

    @FXML private VBox rootVBox;
    @FXML private ComboBox<String> resolucaoComboBox;
    @FXML private Slider volumeSlider;
    @FXML private ToggleGroup dificuldadeToggleGroup;
    @FXML private Label statusLabel;
    @FXML private Button homemFerroButton;
    @FXML private Button capitaoAmericaButton;

    private JogoConfig config = new JogoConfig();
    private HelloApplication app; // Referência à classe principal

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    @FXML
    public void initialize() {
        // --- 1. Configura a Imagem de Fundo ---
        try {
            // O caminho é relativo à raiz de resources
            String imagePath = "/img.png";
            Image backgroundImage = new Image(getClass().getResourceAsStream(imagePath));
            BackgroundImage background = new BackgroundImage(
                    backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true)
            );
            rootVBox.setBackground(new Background(background));
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem de fundo: /img.png.");
            e.printStackTrace();
        }

        // --- 2. Configuração do ComboBox de Resolução ---
        ObservableList<String> resolucoes = FXCollections.observableArrayList("1920x1080", "1280x720", "1024x576");
        resolucaoComboBox.setItems(resolucoes);
        resolucaoComboBox.setValue(config.getResolucao());
        resolucaoComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String[] parts = newVal.split("x");
                double width = Double.parseDouble(parts[0]);
                double height = Double.parseDouble(parts[1]);
                HelloApplication.setResolution(width, height);
                config.setResolucao(newVal);
            }
        });

        // --- 3. Configuração do Slider de Volume ---
        volumeSlider.setValue(config.getVolume() * 100);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            config.setVolume(newVal.doubleValue() / 100.0);
            statusLabel.setText("Volume ajustado para " + (int)newVal.doubleValue() + "%");
        });

        // --- 4. Configuração da Dificuldade ---
        setDificuldadeToggle(config.getDificuldade());
        dificuldadeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                String dificuldade = ((RadioButton) newToggle).getText();
                config.setDificuldade(dificuldade);
                statusLabel.setText("Dificuldade definida para: " + dificuldade);
            }
        });
    }

    private void setDificuldadeToggle(String dificuldade) {
        for (Toggle toggle : dificuldadeToggleGroup.getToggles()) {
            RadioButton rb = (RadioButton) toggle;
            if (rb.getText().equals(dificuldade)) {
                rb.setSelected(true);
                break;
            }
        }
    }

    @FXML
    protected void onIniciarJogoClick() {
        if (config.getPersonagemEscolhido() == null) {
            statusLabel.setText("ERRO: Por favor, selecione um personagem para Iniciar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Início do Jogo");
        alert.setHeaderText("Preparado para o Resgate Hexagonal?");
        alert.setContentText(String.format("Iniciar com %s na dificuldade %s?", config.getPersonagemEscolhido(), config.getDificuldade()));
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Chama o método da classe principal para trocar a cena
                app.switchToGameScene(config);
            } catch (IOException e) {
                statusLabel.setText("ERRO ao carregar o cenário do jogo.");
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Início do jogo cancelado.");
        }
    }

    @FXML
    protected void onSalvarProgressoClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Progresso");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo de Progresso (.sav)", "*.sav"));
        File file = fileChooser.showSaveDialog(HelloApplication.getPrimaryStage());
        if (file != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(config);
                statusLabel.setText("Progresso salvo com sucesso em: " + file.getName());
            } catch (IOException e) {
                statusLabel.setText("ERRO ao Salvar Progresso: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Salvamento cancelado.");
        }
    }

    @FXML
    protected void onCarregarProgressoClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Carregar Progresso");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo de Progresso (.sav)", "*.sav"));
        File file = fileChooser.showOpenDialog(HelloApplication.getPrimaryStage());
        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                this.config = (JogoConfig) ois.readObject();
                // Atualiza a UI com os dados carregados
                resolucaoComboBox.setValue(config.getResolucao());
                volumeSlider.setValue(config.getVolume() * 100);
                setDificuldadeToggle(config.getDificuldade());
                String[] parts = config.getResolucao().split("x");
                HelloApplication.setResolution(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                statusLabel.setText("Progresso carregado de: " + file.getName());
            } catch (IOException | ClassNotFoundException e) {
                statusLabel.setText("ERRO ao Carregar Progresso: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Carregamento cancelado.");
        }
    }

    @FXML protected void onSairClick() { Platform.exit(); }
    @FXML protected void onEscolherHomemFerro() {
        config.setPersonagemEscolhido("Homem de Ferro");
        statusLabel.setText("Personagem Escolhido: Homem de Ferro.");
        homemFerroButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: black;");
        capitaoAmericaButton.setStyle(null); // Reseta o estilo
    }
    @FXML protected void onEscolherCapitaoAmerica() {
        config.setPersonagemEscolhido("Capitão América");
        statusLabel.setText("Personagem Escolhido: Capitão América.");
        capitaoAmericaButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        homemFerroButton.setStyle(null); // Reseta o estilo
    }
}