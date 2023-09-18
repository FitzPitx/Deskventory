package com.example.proyectoescritorio.Modelo;


import com.example.proyectoescritorio.Controlador.LoginController;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.sql.*;

public class Producto{
    private IntegerProperty codigoProducto; // id_producto
    private StringProperty nombreProducto; // nombre_producto
    private StringProperty descripcionProducto; // descripcion_producto
    private DoubleProperty precioProducto; // precio_producto
    private StringProperty marcaProducto; // marca_producto
    private IntegerProperty cantidadProducto; // cantidad_producto
    private Categoria categoria;
    private int codigoUsuario;


    public Producto(Integer codigoProducto, String nombreProducto, String descripcionProducto, Double precioProducto, String marcaProducto, Integer cantidadProducto,  Categoria categoria, int codigoUsuario) {
        this.codigoProducto = new SimpleIntegerProperty(codigoProducto);
        this.nombreProducto = new SimpleStringProperty(nombreProducto);
        this.descripcionProducto = new SimpleStringProperty(descripcionProducto);
        this.precioProducto = new SimpleDoubleProperty(precioProducto);
        this.marcaProducto = new SimpleStringProperty(marcaProducto);
        this.cantidadProducto = new SimpleIntegerProperty(cantidadProducto);
        this.categoria = categoria;
        this.codigoUsuario = codigoUsuario;
    }

    public Producto(Integer codigoProducto){
        this.codigoProducto = new SimpleIntegerProperty(codigoProducto);
    }

    public Integer getCodigoProducto() {
        return codigoProducto.get();
    }

    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = new SimpleIntegerProperty(codigoProducto);
    }

    public IntegerProperty codigoProductoProperty() {
        return codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto.get();
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = new SimpleStringProperty(nombreProducto);
    }

    public StringProperty nombreProductoProperty() {
        return nombreProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto.get();
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = new SimpleStringProperty(descripcionProducto);
    }

    public StringProperty descripcionProductoProperty() {
        return descripcionProducto;
    }

    public Double getPrecioProducto() {
        return precioProducto.get();
    }

    public void setPrecioProducto(Double precioProducto) {
        this.precioProducto = new SimpleDoubleProperty(precioProducto);
    }

    public DoubleProperty precioProductoProperty() {
        return precioProducto;
    }

    public String getMarcaProducto() {
        return marcaProducto.get();
    }

    public void setMarcaProducto(String marcaProducto) {
        this.marcaProducto = new SimpleStringProperty(marcaProducto);
    }

    public StringProperty marcaProductoProperty() {
        return marcaProducto;
    }

    public Integer getCantidadProducto() {
        return cantidadProducto.get();
    }

    public void setCantidadProducto(Integer cantidadProducto) {
        this.cantidadProducto = new SimpleIntegerProperty(cantidadProducto);
    }

    public IntegerProperty cantidadProductoProperty() {
        return cantidadProducto;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public int getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(int codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }



    public int guardarRegistro(Connection connection){
        try {
            //Evitar injeccion SQL
            PreparedStatement instruccion =
                    connection.prepareStatement(
                            "INSERT INTO tbl_productos (nombre_producto, " +
                                    "descripcion_producto, " +
                                    "precio_producto, " +
                                    "marca_producto, " +
                                    "cant_producto, " +
                                    "codigo_categoria, " +
                                    "id_usuario) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)");
            instruccion.setString(1, nombreProducto.get());
            instruccion.setString(2, descripcionProducto.get());
            instruccion.setDouble(3, precioProducto.get());
            instruccion.setString(4, marcaProducto.get());
            instruccion.setInt(5, cantidadProducto.get());
            instruccion.setInt(6, categoria.getCodigoCategoria());
            instruccion.setInt(7, LoginController.codigoUsuario);
            return instruccion.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int consultarCodigoProductoActual(Connection connection){
        try {
            //Consultar el valor actual a la hora de realizar la actualizacion de nuestro producto
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT MAX(id_producto) FROM tbl_productos");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                System.out.println(resultSet.getInt(1));
                return resultSet.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public int actualizarRegistro(Connection connection){
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE tbl_productos " +
                            "SET nombre_producto = ?, " +
                            "descripcion_producto = ?, " +
                            "precio_producto = ?, " +
                            "marca_producto = ?, " +
                            "cant_producto = ?, " +
                            "codigo_categoria = ? " +
                            "WHERE id_usuario = ? AND id_producto = ?");
            preparedStatement.setString(1, nombreProducto.get());
            preparedStatement.setString(2, descripcionProducto.get());
            preparedStatement.setDouble(3, precioProducto.get());
            preparedStatement.setString(4, marcaProducto.get());
            preparedStatement.setInt(5, cantidadProducto.get());
            preparedStatement.setInt(6, categoria.getCodigoCategoria());
            preparedStatement.setInt(7, LoginController.codigoUsuario);
            preparedStatement.setInt(8, codigoProducto.get());
            return preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    public int eliminarRegistro(Connection connection){ //Actualizar el estado
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE tbl_productos " +
                            "SET estado_producto = 0 " +
                            "WHERE id_usuario = ? AND id_producto = ?");
            preparedStatement.setInt(1, LoginController.codigoUsuario);
            preparedStatement.setInt(2, codigoProducto.get());
           return preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public static void llenarInformacionProducto(Connection connection, ObservableList<Producto> lista){
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(
                    "SELECT A.id_producto, " +
                            "A.nombre_producto, " +
                            "A.descripcion_producto, " +
                            "A.precio_producto, " +
                            "A.marca_producto, " +
                            "A.cant_producto, " +
                            "A.estado_producto, " +
                            "A.codigo_categoria, " +
                            "A.id_usuario, " +
                            "B.nombre_categoria, " +
                            "C.username, " +
                            "C.nombre_usuario, " +
                            "C.apellido_usuario, " +
                            "C.password, " +
                            "C.estado_usuario " +
                    "FROM tbl_productos A " +
                    "INNER JOIN tbl_categoria_productos B ON (A.codigo_categoria = B.codigo_categoria) " +
                    "INNER JOIN tbl_usuario C ON (A.id_usuario = C.id_usuario) " +
                            "WHERE A.estado_producto = 1 " +
                            "AND A.id_usuario = " + LoginController.codigoUsuario + ";");//Cargar variable
            while (rs.next()){
                lista.add(new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getString("descripcion_producto"),
                        rs.getDouble("precio_producto"),
                        rs.getString("marca_producto"),
                        rs.getInt("cant_producto"),
                        new Categoria(rs.getInt("codigo_categoria"),
                                rs.getString("nombre_categoria")
                        ),
                        LoginController.codigoUsuario
                ));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

    }
}
