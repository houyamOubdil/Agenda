/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package agenda;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Agenda extends Application {
    private TextField searchField;
    private TableView<Contacto> contactsTable;
    private ObservableList<Contacto> contactos;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TU AGENDA TELEFÓNICA");
        primaryStage.setWidth(500);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-image: url('file:/C:/Agenda/iconos/Background.png'); " +
                      "-fx-background-size: cover;");

        // Panel superior con búsqueda
        HBox searchPanel = new HBox(10);
        searchPanel.setPadding(new Insets(10));
        searchPanel.setAlignment(Pos.CENTER);
        searchPanel.setStyle("-fx-background-color: transparent;");
        
        searchField = new TextField();
        searchField.setPromptText("Buscar...");
        Button searchButton = new Button("Buscar");
        searchButton.setOnAction(e -> buscarContactos());
        
        searchPanel.getChildren().addAll(searchField, searchButton);
        root.setTop(searchPanel);

        // Panel central con tabla
        contactsTable = new TableView<>();
        TableColumn<Contacto, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Contacto, String> nombreCol = new TableColumn<>("Nombre");
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        TableColumn<Contacto, String> telefonoCol = new TableColumn<>("Teléfono");
        telefonoCol.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        
        contactsTable.getColumns().addAll(idCol, nombreCol, telefonoCol);
        contactos = FXCollections.observableArrayList();
        contactsTable.setItems(contactos);
        
        //Las columnas compartirán el espacio de la tabla: 
        idCol.prefWidthProperty().bind(contactsTable.widthProperty().multiply(0.2));
        nombreCol.prefWidthProperty().bind(contactsTable.widthProperty().multiply(0.4));
        telefonoCol.prefWidthProperty().bind(contactsTable.widthProperty().multiply(0.4));
   
        VBox centerPanel = new VBox(10, contactsTable);
        centerPanel.setPadding(new Insets(10));
        root.setCenter(centerPanel);
        
        // Context Menu
         ContextMenu contextMenu = new ContextMenu();
         MenuItem detallesItem = new MenuItem("Ver Detalles");
         MenuItem eliminarItem = new MenuItem("Eliminar Contacto");

        // Acción para ver detalles
        detallesItem.setOnAction(e -> {
            Contacto selected = contactsTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    new detallesContacto(selected.getId()).start(new Stage());
                }
            });

        // Acción para eliminar contacto
         eliminarItem.setOnAction(e -> {
             Contacto selected = contactsTable.getSelectionModel().getSelectedItem();
             if (selected != null) {
                 Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                 confirmAlert.setTitle("Confirmar Eliminación");
                 confirmAlert.setHeaderText("¿Seguro que quieres eliminar este contacto?");
                 confirmAlert.setContentText("Esta acción no se puede deshacer.");

                 confirmAlert.showAndWait().ifPresent(response -> {
                     if (response == ButtonType.OK) {
                         eliminarContacto(selected);
                     }
                 });
             }
         });

         contextMenu.getItems().addAll(detallesItem, eliminarItem);

         contactsTable.setRowFactory(tv -> {
             TableRow<Contacto> row = new TableRow<>();
             row.setOnMouseClicked(event -> {
                 if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                     contextMenu.show(row, event.getScreenX(), event.getScreenY());
                 }
             });
             return row;
         });


        // Panel inferior con título, foto y botones
        HBox bottomPanel = new HBox(10);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setAlignment(Pos.CENTER_LEFT);
        bottomPanel.setStyle("-fx-background-color: transparent;");

        // Foto de perfil
        Image profilePicImage = new Image("file:/C:/Agenda/iconos/agenda.jpg");
        ImageView profilePic = new ImageView(profilePicImage);
        profilePic.setFitWidth(40);
        profilePic.setFitHeight(40);

        // Título
        Label titleLabel = new Label("TU AGENDA TELEFÓNICA");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);

        // Contenedor izquierda (Foto + Título)
        HBox leftContainer = new HBox(10, profilePic, titleLabel);
        leftContainer.setAlignment(Pos.CENTER_LEFT);

        // Botón "Nuevo Contacto" con solo icono
        Image addIconImage = new Image("file:C:/Agenda/iconos/nuevoContacto.jpg");  
        ImageView addIcon = new ImageView(addIconImage);
        addIcon.setFitWidth(20);
        addIcon.setFitHeight(20);
        Button nuevoContactoButton = new Button("", addIcon);
        nuevoContactoButton.setOnAction(e -> new nuevoContacto().start(new Stage()));

        // Botón "Salir"
        Button salirButton = new Button("Salir");
        salirButton.setOnAction(e -> primaryStage.close());

        // Contenedor derecha (Botones)
        HBox rightContainer = new HBox(10, nuevoContactoButton, salirButton);
        rightContainer.setAlignment(Pos.CENTER_RIGHT);

        // Espaciador para separar izquierda y derecha
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Agregar todo al panel inferior
        bottomPanel.getChildren().addAll(leftContainer, spacer, rightContainer);
        root.setBottom(bottomPanel);

        // Configurar y mostrar la escena
        Scene scene = new Scene(root, 700, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void buscarContactos() {
        String nombre = searchField.getText();
        contactos.clear();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda", "root", "");
                 PreparedStatement stmt = conn.prepareStatement("SELECT id, nombre, telefono FROM contactos WHERE nombre LIKE ?")) {
                stmt.setString(1, "%" + nombre + "%");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    contactos.add(new Contacto(rs.getInt("id"), rs.getString("nombre"), rs.getString("telefono")));
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void eliminarContacto(Contacto contacto) {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda", "root", "");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM contactos WHERE id = ?")) {
            stmt.setInt(1, contacto.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                contactos.remove(contacto);
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Contacto Eliminado");
                successAlert.setHeaderText(null);
                successAlert.setContentText("El contacto ha sido eliminado correctamente.");
                successAlert.show();
            }
        }
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
    }
}


    public static void main(String[] args) {
        launch(args);
    }
}
