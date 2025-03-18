package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectBankUser {
	private static final String URL = "jdbc:mysql://localhost:3306/bankdata";
	private static final String USER = "root";
	private static final String PASSWORD = "Udom@12345$";
	
	
	public static Connection getConnection(){
		try {
			return DriverManager.getConnection(URL, USER, PASSWORD);
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}

