package loginregisterbmichat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection mycon(){
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbjava", "root", "");
        }
        catch (ClassNotFoundException | SQLException ex){
            System.out.print(ex);
        }
        
        return con;
    }
}
