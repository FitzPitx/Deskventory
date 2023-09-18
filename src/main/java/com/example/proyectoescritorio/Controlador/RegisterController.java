package com.example.proyectoescritorio.Controlador;

import com.example.proyectoescritorio.Conexion.DatabaseConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    private Label registerMessageLabel;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private TextField userNameTextField;
    @FXML
    private TextField setPasswordField;
    @FXML
    private TextField confirmPasswordField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private Button closeButton;
    @FXML private Button registerButton;

    @FXML
    private ImageView shieldImageView;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File shieldFile= new File("img/proteger.png");
        Image shieldImage = new Image(shieldFile.toURI().toString());
        shieldImageView.setImage(shieldImage);
    }

    public void closeButtonOnAction(ActionEvent event){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void registerButtonOnAction(ActionEvent event){
        if(userNameTextField.getText().isBlank() == false && setPasswordField.getText().isBlank() == false && confirmPasswordField.getText().isBlank() == false && firstNameTextField.getText().isBlank() == false && lastNameTextField.getText().isBlank() == false){
            validatePassword();
        } else {
            registerMessageLabel.setText("Por favor ingrese todos los campos");
        }
    }

    public void validatePassword(){
        if(setPasswordField.getText().equals(confirmPasswordField.getText())){
            confirmPasswordLabel.setText("");
            registerUser();
        } else {
            confirmPasswordLabel.setText("Las contraseñas no coinciden");
            confirmPasswordLabel.setStyle("-fx-text-fill: #ff0000");
        }

    }

    public void registerUser(){
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();
        String nombre_usuario = firstNameTextField.getText();
        String apellido_usuario = lastNameTextField.getText();
        String username = userNameTextField.getText();
        String password = setPasswordField.getText();
        String insertFields = "INSERT INTO tbl_usuario(nombre_usuario, apellido_usuario, username, password) VALUES ('";
        String insertValues = nombre_usuario + "','" + apellido_usuario + "','" + username + "','" + password + "')";
        String insertToRegister = insertFields + insertValues;
        try{
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToRegister);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro exitoso");
            alert.setHeaderText("El usuario ha sido registrado exitosamente!");
            alert.setContentText("Bienvenido " + nombre_usuario + " " + apellido_usuario + "!");
            alert.showAndWait();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.close();
        } catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }

    }
}
