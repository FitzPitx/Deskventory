package com.example.proyectoescritorio.Controlador;

import com.example.proyectoescritorio.Application;
import com.example.proyectoescritorio.Conexion.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    @FXML private Button cancelButton;
    @FXML private Label loginMessageLabel;
    @FXML private ImageView  brandingImageView;
    @FXML private ImageView lockImageView;
    @FXML private TextField userNameTextField;
    @FXML private PasswordField enterPasswordField;
    @FXML private Button signUpButton;
    @FXML private Button loginButton;
    public static int codigoUsuario;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File brandingFile = new File("img/loginScreen.png");
        Image brandingImage = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(brandingImage);

        File lockFile = new File("img/candado.png");
        Image lockImage = new Image(lockFile.toURI().toString());
        lockImageView.setImage(lockImage);

    }

    @FXML private void signInButtonAction(ActionEvent event){
        if(!userNameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Por favor ingrese su usuario y contraseña");
        }
    }

    @FXML private void signUpButtonAction(ActionEvent event){
        Stage stage = (Stage) signUpButton.getScene().getWindow();
        createAccountForm();
        //stage.close();
    }

    @FXML public void cancelButtonAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin(){
        PreparedStatement st = null;
        ResultSet rs = null;
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try{
            st = connectDB.prepareStatement("SELECT * FROM tbl_usuario WHERE username =?  AND password =? ");
            st.setString(1, userNameTextField.getText());
            st.setString(2, enterPasswordField.getText());
            rs = st.executeQuery();
            if (rs.next()) {
                int userStatus = rs.getInt("estado_usuario");
                codigoUsuario = rs.getInt("id_usuario");
                if (userStatus == 1) {
                            dashboardProduct();
                            Stage stage = (Stage) loginButton.getScene().getWindow();
                            stage.close();
                } else {
                    loginMessageLabel.setText("Su cuenta está inactiva. Por favor, contacte al administrador.");
                }
            } else {
                loginMessageLabel.setText("Usuario o contraseña invalido, intente de nuevo.");
            }
        } catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }

    public void createAccountForm(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("register.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 570);
            registerStage.setScene(scene);
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.show();

        } catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }

    public void dashboardProduct(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("Product.fxml"));
            Stage productsStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            productsStage.setScene(scene);
            productsStage.initStyle(StageStyle.UNDECORATED);
            productsStage.setTitle("Productos");
            productsStage.show();
        } catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }

    public void initializeLogin(){
        userNameTextField.setText(null);
        enterPasswordField.setText(null);
        loginMessageLabel.setText(null);
    }



}