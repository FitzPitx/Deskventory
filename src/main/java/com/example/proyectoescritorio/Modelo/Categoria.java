package com.example.proyectoescritorio.Modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.sql.*;

public class Categoria {
    private IntegerProperty codigoCategoria; // id_categoria
    private StringProperty nombreCategoria; // nombre_categoria

    public Categoria(Integer codigoCategoria, String nombreCategoria) {
        this.codigoCategoria = new SimpleIntegerProperty(codigoCategoria);
        this.nombreCategoria = new SimpleStringProperty(nombreCategoria);
    }

    public Integer getCodigoCategoria() {
        return codigoCategoria.get();
    }

    public void setCodigoCategoria(int codigoCategoria) {
        this.codigoCategoria = new SimpleIntegerProperty(codigoCategoria);
    }

    public IntegerProperty codigoProductoProperty() {
        return codigoCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria.get();
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = new SimpleStringProperty(nombreCategoria);
    }

    public StringProperty nombreCategoriaProperty() {
        return nombreCategoria;
    }

    public static void llenarInformacion(Connection connection, ObservableList<Categoria> lista){
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(
                    "SELECT codigo_categoria, " +
                    "nombre_categoria " +
                    "FROM tbl_categoria_productos ");
            while (rs.next()){
                lista.add(new Categoria(
                        rs.getInt("codigo_categoria"),
                        rs.getString("nombre_categoria"))
                );
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public String toString(){
        return  " ("+codigoCategoria.get()+") " + nombreCategoria.get();
    }

}
