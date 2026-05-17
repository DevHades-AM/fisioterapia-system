package Conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    private static final String URL = "jdbc:mysql://localhost:3306/registro_pacientes";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static Connection getConexion(){
        Connection con = null;
        try{
            con = DriverManager.getConnection(URL,USER,PASSWORD);
        }catch(SQLException e){
            System.out.println("Error de Conexión: " + e.getMessage());
        }
        return con;
    }
}