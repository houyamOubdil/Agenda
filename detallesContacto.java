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
import javafx.stage.Stage;
import java.sql.*;

public class detallesContacto extends Application {
    private TextField nombreField, emailField, telefonoField, sitioWebField;
    private ImageView fotoView;
    private int contactoId;

    // Constructor sin parámetros (para JavaFX)
    public detallesContacto() {
        this.contactoId = 1; // Valor por defecto
    }

    // Constructor con ID para instanciar con un contacto específico
    public detallesContacto(int id) {
        this.contactoId = id;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DETALLES DEL CONTACTO");
        primaryStage.setResizable(false);
        BorderPane root = new BorderPane();

        // Configurar el fondo
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:C:/Agenda/iconos/Background.png", 500, 600, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true));
        root.setBackground(new Background(backgroundImage));

        // Título
        Label titleLabel = new Label("DETALLES DEL CONTACTO");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        BorderPane.setAlignment(titleLabel, Pos.TOP_CENTER);
        root.setTop(titleLabel);

        // Panel de información
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(20));
        infoBox.setAlignment(Pos.CENTER);
        
        VBox nombreBox = createLabeledField("Nombre:");
        VBox emailBox = createLabeledField("Email:");
        VBox telefonoBox = createLabeledField("Teléfono:");
        VBox sitioWebBox = createLabeledField("Sitio web:");

        nombreField = (TextField) nombreBox.getChildren().get(1);
        emailField = (TextField) emailBox.getChildren().get(1);
        telefonoField = (TextField) telefonoBox.getChildren().get(1);
        sitioWebField = (TextField) sitioWebBox.getChildren().get(1);

        


        // Imagen de perfil
        Label fotoLabel = new Label("Foto perfil:");
        fotoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        fotoView = new ImageView();
        fotoView.setFitWidth(150);
        fotoView.setFitHeight(150);

        VBox fotoBox = new VBox(5, fotoLabel, fotoView);
        fotoBox.setAlignment(Pos.CENTER);
        
        infoBox.getChildren().addAll(nombreBox, emailBox, telefonoBox, sitioWebBox, fotoBox);
        root.setCenter(infoBox);

        // Botón actualizar con icono
        Button updateButton = new Button("");
        ImageView iconView = new ImageView(new Image("file:C:/Agenda/iconos/actualizar.png"));
        iconView.setFitWidth(20);
        iconView.setFitHeight(20);
        updateButton.setGraphic(iconView);
        updateButton.setStyle("-fx-font-size: 16px;");
        updateButton.setOnAction(e -> actualizarContacto());

        HBox buttonBox = new HBox(updateButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10, 20, 20, 20));
        root.setBottom(buttonBox);

        cargarDetallesContacto();

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLabeledField(String labelText) {
    Label label = new Label(labelText);
    label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

    TextField textField = new TextField();
    textField.setMaxHeight(40);  // alto de la casilla
    textField.setMaxWidth(150);  // ancho de la casilla

    VBox box = new VBox(5, label, textField);
    box.setAlignment(Pos.CENTER);
    return box;
}



    private void cargarDetallesContacto() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM contactos WHERE id = ?")) {
            stmt.setInt(1, contactoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nombreField.setText(rs.getString("nombre"));
                emailField.setText(rs.getString("email"));
                telefonoField.setText(rs.getString("telefono"));
                sitioWebField.setText(rs.getString("sitio_web"));
                
                Image image = new Image("file:" + rs.getString("foto"), 150, 150, true, true);
                fotoView.setImage(image);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void actualizarContacto() {
        String nombre = nombreField.getText();
        String email = emailField.getText();
        String telefono = telefonoField.getText();
        String sitioWeb = sitioWebField.getText();

        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || sitioWeb.isEmpty()) {
            mostrarAlerta("Por favor, completa todos los campos.");
            return;
        }
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE contactos SET nombre = ?, email = ?, telefono = ?, sitio_web = ? WHERE id = ?")) {
            stmt.setString(1, nombreField.getText());
            stmt.setString(2, emailField.getText());
            stmt.setString(3, telefonoField.getText());
            stmt.setString(4, sitioWebField.getText());
            stmt.setInt(5, contactoId);
            stmt.executeUpdate();
            mostrarAlerta("Contacto actualizado correctamente.");
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
