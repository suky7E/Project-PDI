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
import model.Bank;
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
	    // Get the values entered by the user
	    String phoneNumber = loginPhoneNumber.getText();
	    String password = loginPassword.getText();

	    // Validate input
	    if (phoneNumber.isEmpty() || password.isEmpty()) {
	        AlertUtil.showAlert("Error", "All fields are required!");
	        return;
	    }

	    // Database connection and user validation
	    try (Connection conn = ConnectBankUser.getConnection();
	         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE phone_number = ? AND password = ?")) {

	        stmt.setString(1, phoneNumber);
	        stmt.setString(2, password);
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            // Create Bank instance and set user data
	            Bank loggedInUser = new Bank();
	            loggedInUser.setFirstname(rs.getString("firstname"));
	            loggedInUser.setLastname(rs.getString("lastname"));
	            loggedInUser.setEmail(rs.getString("email"));
	            loggedInUser.setPhonenumber(rs.getString("phone_number"));
	            loggedInUser.setNationalid(rs.getString("national_id"));
	            loggedInUser.setCountrycode(rs.getString("country_code"));
	            loggedInUser.setPasscode(rs.getString("passcode"));
	            loggedInUser.setCvv(rs.getString("cvv"));
	            loggedInUser.setCardnumber(rs.getString("card_number"));
	            loggedInUser.setExpirydate(rs.getString("expiry_date"));
	            loggedInUser.setBalance(rs.getString("balance"));
	            loggedInUser.setIcon(rs.getString("profile_icon"));
	            // Store logged-in user globally
	            Bank.setLoggedInUser(loggedInUser);

	            // Success alert and switch to main screen
	            AlertUtil.showAlert("Success", "Login Successful");
	            switchToMain(event);
	        } else {
	            // Invalid credentials
	            AlertUtil.showAlert("Failed to login", "Invalid phone number or password");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        AlertUtil.showAlert("Error", "Login Failed");
	    }
	}

	
	 public void back(ActionEvent event) throws IOException {
		    try {
		        // Load the SignIn FXML
		        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Start.fxml"));

		        // Get the current stage (window) from the event
		        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		        // Create a new scene and set it on the stage
		        scene = new Scene(root);
		        stage.setScene(scene);
		        stage.show();
		    } catch (IOException e) {
		        e.printStackTrace();
		        AlertUtil.showAlert("Error", "Failed to switch to Login page.");
		    }
		}

	
	    public void switchToSignUp(ActionEvent event) throws IOException {
	        try {
	            // Load the SignIn FXML
	            Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignUp.fxml"));

	            // Get the current stage (window) from the event
	            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

	            // Create a new scene and set it on the stage
	            scene = new Scene(root);
	            stage.setScene(scene);
	            stage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	            AlertUtil.showAlert("Error", "Failed to switch to Login page.");
	        }
	    }

	
    public void switchToSignIn(ActionEvent event) throws IOException {
        try {
            // Load the SignIn FXML
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignIn.fxml"));

            // Get the current stage (window) from the event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Create a new scene and set it on the stage
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Failed to switch to Login page.");
        }
    }

	
    public void switchToMain(ActionEvent event) throws IOException {
        try {
            // Load the SignIn FXML
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScreen.fxml"));

            // Get the current stage (window) from the event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Create a new scene and set it on the stage
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Failed to switch to Login page.");
        }
    }


}
