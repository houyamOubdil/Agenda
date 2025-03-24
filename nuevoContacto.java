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
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.*;

public class nuevoContacto extends Application {
    private TextField nombreField, emailField, telefonoField, sitioWebField;
    private ImageView fotoView;
    private String fotoRuta;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NUEVO CONTACTO");
        primaryStage.setResizable(false);

        // Crear fondo con imagen
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:C:/Agenda/iconos/Background.png"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100,  true, true, false, true)
        );

        Background background = new Background(backgroundImage);

        VBox root = new VBox(15); // Espaciado de 15px entre elementos
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setBackground(background);

        Label titleLabel = new Label("NUEVO CONTACTO");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        // Crear un VBox para cada campo con su etiqueta
        VBox nombreBox = crearCampo("Nombre:", nombreField = new TextField());
        VBox emailBox = crearCampo("Email:", emailField = new TextField());
        VBox telefonoBox = crearCampo("Teléfono:", telefonoField = new TextField());
        VBox sitioWebBox = crearCampo("Sitio Web:", sitioWebField = new TextField());

        // Foto de perfil
        Label fotoLabel = new Label("Foto perfil:");
        fotoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        
        fotoView = new ImageView();
        fotoView.setFitWidth(50);
        fotoView.setFitHeight(50);

        Button fotoButton = new Button("+");
        fotoButton.setOnAction(e -> seleccionarFoto());

        VBox fotoBox = new VBox(5, fotoLabel, fotoButton, fotoView);
        fotoBox.setAlignment(Pos.CENTER);

        // Botón de guardar 
        ImageView saveIcon = new ImageView(new Image("file:C:/Agenda/iconos/guardar.png"));
        saveIcon.setFitWidth(20);
        saveIcon.setFitHeight(20);
        Button saveButton = new Button("", saveIcon);
        saveButton.setOnAction(e -> guardarContacto());   

        
        HBox bottomPanel = new HBox();
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setAlignment(Pos.BOTTOM_RIGHT); 
        bottomPanel.getChildren().add(saveButton);

        // Agregar todos los elementos en orden al root
        root.getChildren().addAll(titleLabel, nombreBox, emailBox, telefonoBox, sitioWebBox, fotoBox, bottomPanel);


        Scene scene = new Scene(root, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para crear un VBox con etiqueta y campo de texto
    private VBox crearCampo(String labelText, TextField textField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        
        textField.setMaxHeight(40);  // alto de la casilla
        textField.setMaxWidth(150);  // ancho de la casilla

        VBox box = new VBox(5, label, textField);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void seleccionarFoto() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            fotoRuta = file.getAbsolutePath();
            fotoView.setImage(new Image("file:" + fotoRuta));
        }
    }

    private void guardarContacto() {
        String nombre = nombreField.getText();
        String email = emailField.getText();
        String telefono = telefonoField.getText();
        String sitioWeb = sitioWebField.getText();

        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || sitioWeb.isEmpty()) {
            mostrarAlerta("Por favor, completa todos los campos.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda", "root", "");
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO contactos (nombre, email, telefono, sitio_web, foto) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, telefono);
            stmt.setString(4, sitioWeb);
            stmt.setString(5, fotoRuta);
            stmt.executeUpdate();
            mostrarAlerta("Contacto guardado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(mensaje);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
