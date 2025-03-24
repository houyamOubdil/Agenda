/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agenda;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.regex.Pattern;

public class login extends Application {
    private Scene loginScene, registerScene;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Login / Registro");
        primaryStage.setResizable(false); // No permitir maximizar

        StackPane loginPane = new StackPane();
        loginPane.getChildren().add(createLoginPane());
        loginScene = new Scene(loginPane, 500, 600);

        StackPane registerPane = new StackPane();
        registerPane.getChildren().add(createRegisterPane());
        registerScene = new Scene(registerPane, 500, 600);

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private VBox createLoginPane() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:C:/Agenda/iconos/Background.png", 500, 600, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        layout.setBackground(new Background(backgroundImage));

        Label userLabel = new Label("Username:");
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        TextField userField = new TextField();
        userField.setMaxWidth(150);

        Label passLabel = new Label("Password:");
        passLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        PasswordField passField = new PasswordField();
        passField.setMaxWidth(150);

        Button loginButton = new Button("Iniciar Sesión");
        Button registerButton = new Button("Crear Cuenta");

        loginButton.setOnAction(e -> loginUser(userField.getText(), passField.getText()));
        registerButton.setOnAction(e -> primaryStage.setScene(registerScene));

        layout.getChildren().addAll(userLabel, userField, passLabel, passField, loginButton, registerButton);
        return layout;
    }

    private VBox createRegisterPane() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:C:/Agenda/iconos/Background.png", 500, 600, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        layout.setBackground(new Background(backgroundImage));

        Label nameLabel = new Label("Nombre:");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        TextField nameField = new TextField();
        nameField.setMaxWidth(150);

        Label lastNameLabel = new Label("Apellido:");
        lastNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        TextField lastNameField = new TextField();
        lastNameField.setMaxWidth(150);

        Label userLabel = new Label("Username:");
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        TextField userField = new TextField();
        userField.setMaxWidth(150);

        Label passLabel = new Label("Password:");
        passLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        PasswordField passField = new PasswordField();
        passField.setMaxWidth(150);

        // Advertencia sobre los requisitos de la contraseña
        Label passwordHint = new Label("* Tu password debe contener al menos 5 caracteres \n con al menos una mayúscula y un número!");
        passwordHint.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        Label confirmPassLabel = new Label("Confirmar Password:");
        confirmPassLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setMaxWidth(150);

        Button registerButton = new Button("Registrar");
        Button backButton = new Button("Volver");

        registerButton.setOnAction(e -> registerUser(nameField.getText(), lastNameField.getText(), userField.getText(), passField.getText(), confirmPassField.getText()));
        backButton.setOnAction(e -> primaryStage.setScene(loginScene));

        layout.getChildren().addAll(nameLabel, nameField, lastNameLabel, lastNameField, userLabel, userField, passLabel, passField, 
                                    passwordHint, confirmPassLabel, confirmPassField, registerButton, backButton);
        return layout;
    }

    private void loginUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM usuarios WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Inicio de sesión exitoso");
                new Agenda().start(new Stage());
                primaryStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Usuario o contraseña incorrectos");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void registerUser(String nombre, String apellido, String username, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Las contraseñas no coinciden");
            return;
        }
        if (!Pattern.matches("(?=.*[A-Z])(?=.*\\d).{5,}", password)) {
            showAlert(Alert.AlertType.ERROR, "La contraseña debe tener al menos 5 caracteres, una mayúscula y un número");
            return;
        }
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO usuarios (nombre, apellido, username, password) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, username);
            stmt.setString(4, password);
            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Usuario registrado exitosamente");
            new Agenda().start(new Stage());
            primaryStage.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
