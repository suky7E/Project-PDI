package bank.application.Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import database.ConnectBankUser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.Bank;
import utility.AlertUtil;

public class AppController implements Initializable {
    @FXML
    private AnchorPane card;

    @FXML
    private Label mainBal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getData(); // Retrieve user data when initializing
    }

    public void getData() {
        Bank loggedInUser = Bank.getLoggedInUser(); // Get the logged-in user

        if (loggedInUser == null || loggedInUser.getPhonenumber() == null || loggedInUser.getPhonenumber().isEmpty()) {
            AlertUtil.showAlert("Error", "No logged-in user found.");
            return;
        }

        String phoneNumber = loggedInUser.getPhonenumber();

        try (Connection conn = ConnectBankUser.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM users WHERE phone_number = ?")) {
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                mainBal.setText("" + balance);
            } else {
                AlertUtil.showAlert("Error", "User data not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Failed to retrieve user data.");
        }
    }
}
