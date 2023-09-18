package com.example.proyectoescritorio.Controlador;

import com.example.proyectoescritorio.Application;
import com.example.proyectoescritorio.Conexion.DatabaseConnection;
import com.example.proyectoescritorio.Modelo.Categoria;
import com.example.proyectoescritorio.Modelo.Producto;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import org.apache.poi.ss.usermodel.Workbook;

public class ProductsController implements Initializable { //interface with initialize method
    ///Gui components
    @FXML private ComboBox<Categoria> categoriaComboBox;
    @FXML private TableView<Producto> productosTableView;
    @FXML private TextField nombreProductoTextField;
    @FXML private TextField descripProductoTextField;
    @FXML private TextField precioProductoTextField;
    @FXML private TextField marcaProductoTextField;
    @FXML private TextField cantProductoTextField;
    @FXML private TextField keywordTextField;
    @FXML private Button guardarProductoButton;
    @FXML private Button actualizarProductoButton;
    @FXML private Button eliminarProductoButton;
    @FXML private Button salirSesionButton;
    @FXML private Button importarDatosButton;
    //Columns of the table
    @FXML
    private TableColumn<Producto, Integer> codigoProductoColumn;
    @FXML
    private TableColumn<Producto, String> nombreProductoColumn;
    @FXML
    private TableColumn<Producto, Double> precioProductoColumn;
    @FXML
    private TableColumn<Producto, String> marcaProductoColumn;
    @FXML
    private TableColumn<Producto, Integer> cantProductoColumn;
    @FXML
    private TableColumn<Categoria, Producto> catProductoColumn;
    @FXML
    private TableColumn<Producto, String> descripProductoColumn;
    //Collections
    @FXML
    private ObservableList<Categoria> listaCategorias;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private ObservableList<Producto> listaProductos; //Para nuestros filtros
    // ObservableList<Producto> productSearch = FXCollections.observableArrayList();

    //conecction to database
    private DatabaseConnection conexion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File brandingFile = new File("img/LOGO.png");
        Image brandingImage = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(brandingImage);

        try {
            conexion = new DatabaseConnection();
            conexion.getConnection();

            //Initialize the list
            listaCategorias = FXCollections.observableArrayList();
            listaProductos = FXCollections.observableArrayList(); //Para nuestros filtros

            //Fill the list with data
            Categoria.llenarInformacion(conexion.getConnection(), listaCategorias);
            Producto.llenarInformacionProducto(conexion.getConnection(), listaProductos);

            //Link the list with the combobox
            categoriaComboBox.setItems(listaCategorias);


            //Link the columns with the attributes of the class
            codigoProductoColumn.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
            nombreProductoColumn.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
            precioProductoColumn.setCellValueFactory(new PropertyValueFactory<>("precioProducto"));
            marcaProductoColumn.setCellValueFactory(new PropertyValueFactory<>("marcaProducto"));
            cantProductoColumn.setCellValueFactory(new PropertyValueFactory<>("cantidadProducto"));
            catProductoColumn.setCellValueFactory(new PropertyValueFactory<Categoria, Producto>("categoria"));
            descripProductoColumn.setCellValueFactory(new PropertyValueFactory<>("descripcionProducto"));

            productosTableView.setItems(listaProductos);

            FilteredList<Producto> filteredData = new FilteredList<>(listaProductos, b -> true);
            keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(producto -> {
                    // si no hay ninguna coincidencia entonces muestra todos los registros
                    if (newValue.isEmpty() || newValue.isBlank() || newValue == null){
                        return true;
                    }
                    String searchKeyword = newValue.toLowerCase();

                    if (producto.getNombreProducto().toLowerCase().indexOf(searchKeyword) > -1){
                        return true; // Encontramos una coincidencia con el nombre
                    } else if (producto.getDescripcionProducto().toLowerCase().indexOf(searchKeyword) > -1){
                        return true; // Encontramos coincidencias con la descripcion del producto
                    } else if (producto.getPrecioProducto().toString().indexOf(searchKeyword) > -1){
                        return true; // Encontramos coincidencias con el precio del producto
                    } else  if (producto.getMarcaProducto().toLowerCase().indexOf(searchKeyword) > -1){
                        return true; // Encontramos coincidencias con la marca del producto
                    } else if (producto.getCantidadProducto().toString().indexOf(searchKeyword) > -1){
                        return true; // Encontramos coincidencia con la cantidad del producto
                    } else if (producto.getCategoria().toString().indexOf(searchKeyword) > -1) {
                        return true; // Encontramos coincidencias con la cat del producto
                    } else {
                        return false; // no encontramos match
                    }
                });
            });

            SortedList<Producto> sortedData = new SortedList<>(filteredData);
            //Vincular el resultado ordenado con la vista de la tabla
            sortedData.comparatorProperty().bind(productosTableView.comparatorProperty());
            // Aplicar el filtro y la data ordenada a la tabla
            productosTableView.setItems(sortedData);

            gestionarEventos();
            conexion.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        importarDatosButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Importar datos");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx")
                );
                File selectedFile = fileChooser.showOpenDialog(importarDatosButton.getScene().getWindow());

                if (selectedFile != null){
                    String path = selectedFile.getAbsolutePath();
                    System.out.println("Archivo seleccionado "+ selectedFile.getName());
                    File file = new File(path);
                    try {
                        try (FileInputStream fis = new FileInputStream(file)){
                            Workbook workbook = new XSSFWorkbook(fis);
                            Sheet sheet = workbook.getSheetAt(0);
                            int numeroFilas = sheet.getLastRowNum();
                            for (int i = 0; i <= numeroFilas; i++){
                                Row row = sheet.getRow(i);
                                Connection connection = conexion.getConnection();
                                PreparedStatement preparedStatement = connection.prepareStatement(
                                        "INSERT INTO tbl_productos (nombre_producto, descripcion_producto, precio_producto, marca_producto, cant_producto, codigo_categoria, id_usuario) " +
                                                "VALUES (?, ?, ?, ?, ?, ?, ?)");

                                int idUsuario = LoginController.codigoUsuario;

                                if (row.getCell(i) != null) {
                                    preparedStatement.setString(1, row.getCell(0).getStringCellValue());
                                    preparedStatement.setString(2, row.getCell(1).getStringCellValue());
                                    preparedStatement.setDouble(3, row.getCell(2).getNumericCellValue());
                                    preparedStatement.setString(4, row.getCell(3).getStringCellValue());
                                    preparedStatement.setInt(5, (int) row.getCell(4).getNumericCellValue());
                                    preparedStatement.setInt(6, (int) row.getCell(5).getNumericCellValue());
                                    preparedStatement.setInt(7, idUsuario);
                                    preparedStatement.executeUpdate();
                                }
                            }
                            Alert mensaje = new Alert(Alert.AlertType.INFORMATION);
                            mensaje.setTitle("Datos importados");
                            mensaje.setContentText("Los datos han sido importados exitosamente");
                            mensaje.setHeaderText("Resultado:");
                            mensaje.showAndWait();
                            //Refresh the table
                            listaProductos.clear();
                            Producto.llenarInformacionProducto(conexion.getConnection(), listaProductos);
                            productosTableView.setItems(listaProductos);
                            conexion.closeConnection();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    } catch (SQLException e){
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("No se ha seleccionado ningún archivo.");
                }
            }
        });
    }

    public void gestionarEventos() {
        productosTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Producto>() {
            @Override
            public void changed(ObservableValue<? extends Producto> observableValue, Producto valorAnterior, Producto valorSeleccionado) {
                if (valorSeleccionado != null) {
                    nombreProductoTextField.setText(valorSeleccionado.getNombreProducto());
                    descripProductoTextField.setText(valorSeleccionado.getDescripcionProducto());
                    precioProductoTextField.setText(valorSeleccionado.getPrecioProducto().toString());
                    marcaProductoTextField.setText(valorSeleccionado.getMarcaProducto());
                    cantProductoTextField.setText(valorSeleccionado.getCantidadProducto().toString());
                    categoriaComboBox.setValue(valorSeleccionado.getCategoria());

                    guardarProductoButton.setDisable(true);
                    actualizarProductoButton.setDisable(false);
                    eliminarProductoButton.setDisable(false);
                }
            }
        });
    }

    @FXML
    public void guardarRegistro(ActionEvent event) throws SQLException {
        //Crear una nueva instancia de producto
        if (nombreProductoTextField.getText().isEmpty() || descripProductoTextField.getText().isEmpty() ||
                precioProductoTextField.getText().isEmpty() || marcaProductoTextField.getText().isEmpty() ||
                cantProductoTextField.getText().isEmpty() || categoriaComboBox.getSelectionModel().getSelectedItem() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Por favor, llene todos los campos");
            alert.setHeaderText("Campos vacíos");
            alert.show();
        } else {
            Producto producto = new Producto(0, nombreProductoTextField.getText(),
                    descripProductoTextField.getText(),
                    Double.valueOf(precioProductoTextField.getText()),
                    marcaProductoTextField.getText(),
                    Integer.valueOf(cantProductoTextField.getText()),
                    categoriaComboBox.getSelectionModel().getSelectedItem(),
                    LoginController.codigoUsuario);
            //LLamar al metodo guardarRegistro
            int resultado = producto.guardarRegistro(conexion.getConnection());
            conexion.closeConnection();
            if (resultado == 1) {
                listaProductos.add(producto);
                Alert mensaje = new Alert(Alert.AlertType.INFORMATION);
                mensaje.setTitle("Registro agregado");
                mensaje.setContentText("El registro ha sido agregado exitosamente");
                mensaje.setHeaderText("Resultado:");
                mensaje.show();
            }
            int codigoActual = producto.consultarCodigoProductoActual(conexion.getConnection());
            conexion.closeConnection();
            producto.setCodigoProducto(codigoActual);
        }
    }

    @FXML
    public void actualizarRegistro(ActionEvent event) throws SQLException {
        Producto producto = productosTableView.getSelectionModel().getSelectedItem();
        if (producto == null){
            // No se ha seleccionado ningún registro, muestra un mensaje de advertencia.
            Alert mensaje = new Alert(Alert.AlertType.WARNING);
            mensaje.setTitle("Sin selección");
            mensaje.setContentText("Por favor, seleccione un registro para actualizar.");
            mensaje.setHeaderText("Advertencia");
            mensaje.show();
            return; // No hacemos nada más si no hay una selección.
        }
        producto = new Producto(producto.getCodigoProducto(), nombreProductoTextField.getText(),
                descripProductoTextField.getText(),
                Double.valueOf(precioProductoTextField.getText()),
                marcaProductoTextField.getText(),
                Integer.valueOf(cantProductoTextField.getText()),
                categoriaComboBox.getSelectionModel().getSelectedItem(),
                LoginController.codigoUsuario);
        int resultado = producto.actualizarRegistro(conexion.getConnection());
        //conexion.closeConnection();
        if (resultado == 1){
            listaProductos.set(productosTableView.getSelectionModel().getSelectedIndex(), producto);
            Alert mensaje = new Alert(Alert.AlertType.INFORMATION);
            mensaje.setTitle("Registro actualizado");
            mensaje.setContentText("El registro ha sido actualizado exitosamente");
            mensaje.setHeaderText("Resultado:");
            mensaje.show();
        }
    }

    @FXML
    public void eliminarRegistro(ActionEvent event) throws SQLException {
        Producto seleccionado = productosTableView.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            // No se ha seleccionado ningún registro, muestra un mensaje de advertencia.
            Alert mensaje = new Alert(Alert.AlertType.WARNING);
            mensaje.setTitle("Sin selección");
            mensaje.setContentText("Por favor, seleccione un registro para eliminar.");
            mensaje.setHeaderText("Advertencia");
            mensaje.show();
            return; // No hacemos nada más si no hay una selección.
        }

        Alert confirmarEliminar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmarEliminar.setTitle("Confirmar eliminación");
        confirmarEliminar.setHeaderText("¿Está seguro de que desea eliminar este registro?");
        confirmarEliminar.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmarEliminar.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK){
            int resultadoEliminacion = seleccionado.eliminarRegistro(conexion.getConnection());
            //conexion.closeConnection();

            if (resultadoEliminacion == 1){
                listaProductos.remove(seleccionado);
                Alert mensaje = new Alert(Alert.AlertType.INFORMATION);
                mensaje.setTitle("Registro eliminado");
                mensaje.setContentText("El registro ha sido eliminado exitosamente");
                mensaje.setHeaderText("Resultado:");
                mensaje.show();
            }
        }
    }
    @FXML
    public void limpiarComponentes(ActionEvent event){
        nombreProductoTextField.setText(null);
        descripProductoTextField.setText(null);
        precioProductoTextField.setText(null);
        marcaProductoTextField.setText(null);
        cantProductoTextField.setText(null);
        categoriaComboBox.setValue(null);

        guardarProductoButton.setDisable(false);
        actualizarProductoButton.setDisable(true);
        eliminarProductoButton.setDisable(true);
    }

    @FXML public void salirSesion(ActionEvent event){
        Stage stage = (Stage) salirSesionButton.getScene().getWindow();
        LoginController loginController = new LoginController();
        if (loginController.equals(null)){
            loginController.initializeLogin();
        } else {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("login.fxml"));
                Stage registerStage = new Stage();
                Scene scene = new Scene(fxmlLoader.load(), 520, 400);
                registerStage.setScene(scene);
                registerStage.initStyle(StageStyle.UNDECORATED);
                registerStage.show();
            } catch (Exception e){
                e.printStackTrace();
                e.getCause();
            }
            stage.close();
        }
    }
}
