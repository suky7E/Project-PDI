package bank.application.Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import database.ConnectBankUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import utility.AlertUtil;
import utility.PasscodeHandler;

public class LoginController implements Initializable{
	
	private Scene scene;
	
	@FXML
	private PasswordField loginPassword;

	@FXML
	private TextField loginPhoneNumber;
	
	  
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		 
	}
	
	@FXML
	public void loginUser(ActionEvent event) throws IOException {
		String phonenum = loginPhoneNumber.getText();
		String pass = loginPassword.getText();
		
		if (phonenum.isEmpty() || pass.isEmpty()) {
			AlertUtil.showAlert("Error", "All field are required!");
			return;
		}
		
		try (Connection conn = ConnectBankUser.getConnection();
			PreparedStatement stmt = conn.prepareStatement("Select * From users where phone_number = ? And password = ?" )){
			stmt.setString(1, phonenum);
			stmt.setString(2, pass);
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				AlertUtil.showAlert("Success", "Login Successful");
				switchToMain(event);
			}
			else {
				AlertUtil.showAlert("Failed to login", "Invalid password or username");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			AlertUtil.showAlert("Error", "Login Failed");
		}
	}
	
	public void back(ActionEvent event) throws IOException {
    	try {
    		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
    		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    		scene = new Scene(root);
    		stage.setScene(scene);
    		stage.show();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
	
	public void switchToSignUp(ActionEvent event) throws IOException {
    	try {
    		Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignUp.fxml"));
    		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    		scene = new Scene(root);
    		stage.setScene(scene);
    		stage.show();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
	
	public void switchToSignIn(ActionEvent event) throws IOException {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignIn.fxml"));
			Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void switchToMain(ActionEvent event) throws IOException {
    	try {
    		Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScreen.fxml"));
    		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    		scene = new Scene(root);
    		stage.setScene(scene);
    		stage.show();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

}
