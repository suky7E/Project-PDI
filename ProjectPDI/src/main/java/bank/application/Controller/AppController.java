package bank.application.Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.Bank;
import utility.AlertUtil;

public class AppController implements Initializable {
	@FXML
    private Label accountNumber;

    @FXML
    private Label amountTopUp;

    @FXML
    private AnchorPane card;

    @FXML
    private Label cardBal;

    @FXML
    private AnchorPane cardInfo;

    @FXML
    private Label cvvNum;

    @FXML
    private AnchorPane cvvPane;

    @FXML
    private Label expiryDate;

    @FXML
    private Label infoCard;

    @FXML
    private Label mainBal;

    @FXML
    private VBox popUp;

    @FXML
    private TextField receiverNumber;

    @FXML
    private TextField sendAmount;

    @FXML
    private TextField topUpAmount;

    @FXML
    private AnchorPane topUpPane;

    @FXML
    private Label userName;
    
    @FXML
    private AnchorPane sendPane;
    
    @FXML
    private AnchorPane blur1;
    
    @FXML
    private AnchorPane blur2;
    
    @FXML
    private ImageView profileImage;
    
    @FXML
    private Label accountName;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getData(); // Retrieve user data when initializing
        card.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                addCSS();
            }
        });
    }

    public void getData() {
        Bank loggedInUser = Bank.getLoggedInUser(); // Get the logged-in user
        
        if (loggedInUser == null || loggedInUser.getPhonenumber() == null || loggedInUser.getPhonenumber().isEmpty()) {
            AlertUtil.showAlert("Error", "No logged-in user found.");
            return;
        }
        
        setProfileImage(loggedInUser.getIcon());
       
        String bal = loggedInUser.getBalance();
        String cvv = loggedInUser.getCvv();
        String cardNum = loggedInUser.getCardnumber();
        String name = loggedInUser.getLastname() + " " +  loggedInUser.getFirstname(); 
        String name2 = loggedInUser.getLastname() + " " +  loggedInUser.getFirstname(); 
        String date = loggedInUser.getExpirydate();
        
        
        
        String lastFourDigits = cardNum.length() > 4 ? cardNum.substring(cardNum.length() - 4) : cardNum;
        infoCard.setText("**** **** **** " + lastFourDigits);
        
        mainBal.setText(bal);
        if (cardBal != null) {
            cardBal.setText(bal);
        } else {
            System.out.println("cardBal is null");
        }
        
        accountName.setText("Hello, " + name2);
        accountNumber.setText(cardNum);
        userName.setText(name);
        expiryDate.setText(date);
        cvvNum.setText(cvv);
        
        mainBal.getStyleClass().add("fontcard");
        accountNumber.getStyleClass().add("fontcard4");
        userName.getStyleClass().add("fontcard3");
        expiryDate.getStyleClass().add("fontcard3");
        cvvNum.getStyleClass().add("fontcard3");
        

    }
    
    public void setProfileImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "/images/dog.png"; // Fallback image
        }
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        profileImage.setImage(image);
    }
    
    private void addCSS() {
        String css = getClass().getResource("/css/application.css").toExternalForm();
        card.getScene().getStylesheets().add(css);
    }
    
    public void goToCard() {
    	cardInfo.setVisible(true);
    }
    
    public void backFromCard() {
    	cardInfo.setVisible(false);
    }
    public void showCVV() {
    	cvvPane.setVisible(true);
    }
    
    public void hideCVV() {
    	cvvPane.setVisible(false);
    }

    @FXML
    void backFromTopUp() {
    	topUpPane.setVisible(false);
    }

    @FXML
    void confirmTopUp() {
        try {
            // Get the logged-in user
        	blur1.setVisible(true);
            Bank loggedInUser = Bank.getLoggedInUser();
            if (loggedInUser == null) {
                AlertUtil.showAlert("Error", "No logged-in user found.");
                return;
            }

            // Parse top-up amount
            double topUp = Double.parseDouble(topUpAmount.getText().trim());

            // Get current balance and calculate new balance
            double currentBalance = Double.parseDouble(loggedInUser.getBalance());
            double newBalance = currentBalance + topUp;

            // Update balance in the model
            loggedInUser.setBalance(String.valueOf(newBalance));

            // Update balance in the UI
            mainBal.setText(String.valueOf(newBalance));
            amountTopUp.setText(topUpAmount.getText());
            popUp.setVisible(true);

            // Update balance in the database
            updateBalanceInDatabase(loggedInUser.getPhonenumber(), newBalance);

        } catch (NumberFormatException e) {
            AlertUtil.showAlert("Error", "Invalid amount entered.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Failed to top-up balance.");
        }
    }
    
    private void updateBalanceInDatabase(String phoneNumber, double newBalance) {
        String query = "UPDATE users SET balance = ? WHERE phone_number = ?";
        
        try (Connection conn = database.ConnectBankUser.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDouble(1, newBalance);
            stmt.setString(2, phoneNumber);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Database update failed.");
        }
    }

    @FXML
    void goToTopUp() {
    	topUpPane.setVisible(true);
    }
    
    public void backFromSend() {
    	sendPane.setVisible(false);
    }
    
    public void goToSend() {
    	sendPane.setVisible(true);
    }
    
    @FXML
    public void confirmSend() {
        try {
            // Blur effect to indicate processing
            blur2.setVisible(true);

            // Get the logged-in user (sender)
            Bank loggedInUser = Bank.getLoggedInUser();
            if (loggedInUser == null) {
                AlertUtil.showAlert("Error", "No logged-in user found.");
                return;
            }

            // Get recipient card number and amount to send
            String recipientCard = receiverNumber.getText().trim();
            double sendAmountValue = Double.parseDouble(sendAmount.getText().trim());

            // Validate recipient input
            if (recipientCard.isEmpty()) {
                AlertUtil.showAlert("Error", "Please enter the recipient's card number.");
                return;
            }

            // Validate amount
            if (sendAmountValue <= 0) {
                AlertUtil.showAlert("Error", "Amount must be greater than zero.");
                return;
            }

            // Get sender's current balance
            double senderBalance = Double.parseDouble(loggedInUser.getBalance());

            // Check if sender has enough balance
            if (sendAmountValue > senderBalance) {
                AlertUtil.showAlert("Error", "Insufficient balance.");
                return;
            }

            // Get recipient balance using card number
            double recipientBalance = getRecipientBalanceByCard(recipientCard);
            if (recipientBalance == -1) {
                AlertUtil.showAlert("Error", "Recipient not found.");
                return;
            }

            // Perform transaction: Deduct from sender, add to recipient
            double newSenderBalance = senderBalance - sendAmountValue;
            double newRecipientBalance = recipientBalance + sendAmountValue;

            // Update balances in the database
            updateBalanceInDatabaseByCard(loggedInUser.getCardnumber(), newSenderBalance);
            updateBalanceInDatabaseByCard(recipientCard, newRecipientBalance);

            // Update UI with new sender balance
            loggedInUser.setBalance(String.valueOf(newSenderBalance));
            mainBal.setText(String.valueOf(newSenderBalance));

            // Show success message
            AlertUtil.showAlert("Success", "Money sent successfully!");

            // Hide the send pane
            sendPane.setVisible(false);
            blur2.setVisible(false);

        } catch (NumberFormatException e) {
            AlertUtil.showAlert("Error", "Invalid amount entered.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Transaction failed.");
        }
    }

    
    private double getRecipientBalanceByCard(String cardNumber) {
        String query = "SELECT balance FROM users WHERE card_number = ?";
        try (Connection conn = database.ConnectBankUser.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            } else {
                return -1; // Recipient not found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    private void updateBalanceInDatabaseByCard(String cardNumber, double newBalance) {
        String query = "UPDATE users SET balance = ? WHERE card_number = ?";

        try (Connection conn = database.ConnectBankUser.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, newBalance);
            stmt.setString(2, cardNumber);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Database update failed.");
        }
    }
    
}
