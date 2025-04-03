package bank.application.Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import database.ConnectBankUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import utility.AlertUtil;
import utility.PasscodeHandler;
import model.BankCard;
import model.Bank;

public class SignUpController implements Initializable {
    
    private Scene scene;
    private Stage stage;
    
    @FXML private AnchorPane step1Pane;
    @FXML private AnchorPane step2Pane;
    @FXML private AnchorPane step3Pane;
    @FXML private JFXButton continueButton;
    @FXML private JFXCheckBox checkBox;

    @FXML private TextField confirmField, emailField, firstnameField, lastnameField, passwordField, nationalidField, phonenumberField, countrycodeField;
    @FXML private DatePicker dateofbirthField;
    @FXML private JFXComboBox<String> profileChooser;
    
    private final String[] iconSelect = {"Cat","Dog","Panda","Tiger"};
    
    @FXML private JFXButton numberButton1, numberButton2, numberButton3, numberButton4, numberButton5, numberButton6, numberButton7, numberButton8, numberButton9;
    private List<JFXButton> numberButtons; 
    @FXML private Circle dot1, dot2, dot3, dot4;
    private List<Circle> dots; 

    private PasscodeHandler passcodeHandler;
    private boolean isConfirmingPasscode = false; 
    private String initialPasscode = "";

    private int currentStep = 1;
    
    private final Map<String, String> iconPaths = Map.of(
    	    "Cat", "/images/cat.jpg",
    	    "Dog", "/images/dog.png",
    	    "Panda", "/images/panda.png",
    	    "Tiger", "/images/tiger.png"
    	);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	Bank bank = new Bank();
        updateView();
        checkBox();
        profileChooser.getItems().addAll(iconPaths.keySet());
        profileChooser.setValue("Dog");

        dots = Arrays.asList(dot1, dot2, dot3, dot4);
        if (dots.contains(null)) {
            throw new IllegalStateException("One or more Circle elements are not defined in the FXML file.");
        }
        passcodeHandler = new PasscodeHandler(dots, 4);
        
        passcodeHandler.setOnSuccess(() -> {
            if (!isConfirmingPasscode) {
                // First passcode entry
                initialPasscode = passcodeHandler.getCorrectPasscode();
                bank.setPasscode(initialPasscode);
                isConfirmingPasscode = true;
                AlertUtil.showAlert("Confirm Passcode", "Enter your passcode again to Sign Up.");
                passcodeHandler.resetPasscode(); 
            } else {
                // Confirm the passcode
                if (passcodeHandler.getCorrectPasscode().equals(initialPasscode)) {
                    AlertUtil.showAlert("Success", "Passcode confirmed successfully!");
                    try {
						registerUser();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    try {
						switchToMain();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                } else {
                    AlertUtil.showAlert("Error", "Passcodes do not match! Try again.");
                    passcodeHandler.resetPasscode();
                }
            }
        });

        numberButtons = Arrays.asList(numberButton1, numberButton2, numberButton3, numberButton4,
                                      numberButton5, numberButton6, numberButton7, numberButton8, numberButton9);
        if (numberButtons.contains(null)) {
            throw new IllegalStateException("One or more JFXButton elements are not defined in the FXML file.");
        }

        for (JFXButton button : numberButtons) {
            button.setOnAction(e -> {
                try {
                    int number = Integer.parseInt(button.getText()); 
                    passcodeHandler.handleButtonPress(number);
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid button text: " + button.getText());
                }
            });
        }
    }

    public void registerUser() throws IOException {
        String firstname = firstnameField.getText();
        String lastname = lastnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmpassword = confirmField.getText();
        String selectedIcon = profileChooser.getValue();
        String iconPath = iconPaths.getOrDefault(selectedIcon, "/images/dog.png");
        LocalDate intEventDate = dateofbirthField.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateofbirth = intEventDate.format(formatter);
        String phonenumber = phonenumberField.getText();
        String nationalid = nationalidField.getText();
        String countrycode = countrycodeField.getText();
        
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || dateofbirth.isEmpty() || iconPath.isEmpty() || phonenumber.isEmpty() || nationalid.isEmpty() || countrycode.isEmpty()) {
            AlertUtil.showAlert("Error", "You must input your information before signing up.");
            return;
        }
        if (!password.equals(confirmpassword)) {
            AlertUtil.showAlert("Error", "Passwords do not match. Please try again.");
            return;
        }
        if (initialPasscode.isEmpty()) {
            AlertUtil.showAlert("Error", "Please set up and confirm your passcode before continuing.");
            return;
        }
        
        String passcode = initialPasscode;
        
        String userId = UUID.randomUUID().toString();
        BankCard card = new BankCard();
        String cardNumber = card.generateCardNumber("4567");
        String cvv = card.generateCVV();
        String expiryDate = card.generateExpiryDate();
        double balance = 0.0;

        try (Connection conn = ConnectBankUser.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (user_id, firstname, lastname, email, password, profile_icon, date_of_birth, phone_number, national_id, country_code, passcode, card_number, cvv, expiry_date, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
            stmt.setString(1, userId);
            stmt.setString(2, firstname);
            stmt.setString(3, lastname);
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.setString(6, iconPath);
            stmt.setString(7, dateofbirth);
            stmt.setString(8, phonenumber);
            stmt.setString(9, nationalid);
            stmt.setString(10, countrycode);
            stmt.setString(11, passcode);
            stmt.setString(12, cardNumber);
            stmt.setString(13, cvv);
            stmt.setString(14, expiryDate);
            stmt.setDouble(15, balance);
            
            stmt.executeUpdate();
            AlertUtil.showAlert("Success", "You have successfully registered your account.");
            Bank bank = new Bank();
			bank.setFirstname(firstnameField.getText());
            bank.setLastname(lastnameField.getText());
            bank.setEmail(emailField.getText());
            bank.setPassword(passwordField.getText());
            bank.setConfirmpassword(confirmField.getText());
            bank.setIcon(profileChooser.getValue());
            bank.setDateofbirth(dateofbirthField.getValue().toString());
            bank.setPhonenumber(phonenumberField.getText());
            bank.setNationalid(nationalidField.getText());
            bank.setCountrycode(countrycodeField.getText());
            bank.setCardnumber(cardNumber);
            bank.setCvv(cvv);
            bank.setExpirydate(expiryDate);

            switchToSignIn();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Failed to register user. Please try again!");
        }
    }

    @FXML
    private void handleNextButtonAction() {
        if (currentStep < 3) {
            currentStep++;
            updateView();
        }
    }

    @FXML
    private void handlePreviousButtonAction() {
        if (currentStep > 1) {
            currentStep--;
            updateView();
        }
    }

    private void updateView() {
        step1Pane.setVisible(currentStep == 1);
        step2Pane.setVisible(currentStep == 2);
        step3Pane.setVisible(currentStep == 3);
    }

    @FXML
    private void checkBox() {
        continueButton.setDisable(true); 
        checkBox.setOnAction(e -> continueButton.setDisable(!checkBox.isSelected()));
    }

    public void switchToSignIn() throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignIn.fxml"));
            Stage stage = (Stage) continueButton.getScene().getWindow(); // Get stage from a known UI element
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Failed to switch to the main screen.");
        }
    }


    public void switchToMain() throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScreen.fxml"));
            Stage stage = (Stage) continueButton.getScene().getWindow(); // Get stage from a known UI element
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Failed to switch to the main screen.");
        }
    }


}
