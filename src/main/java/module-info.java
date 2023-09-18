module com.example.proyectoescritorio {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.ooxml.schemas;
    requires org.apache.commons.collections4;

    opens com.example.proyectoescritorio to javafx.fxml;
    exports com.example.proyectoescritorio;
    exports com.example.proyectoescritorio.Modelo;
    opens com.example.proyectoescritorio.Modelo to javafx.fxml;
    exports com.example.proyectoescritorio.Controlador;
    opens com.example.proyectoescritorio.Controlador to javafx.fxml;
    exports com.example.proyectoescritorio.Conexion;
    opens com.example.proyectoescritorio.Conexion to javafx.fxml;
}