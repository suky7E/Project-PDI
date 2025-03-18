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
import java.util.ResourceBundle;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateView();
        checkBox();
        profileChooser.getItems().addAll(iconSelect);
        profileChooser.setValue("Cat");

        dots = Arrays.asList(dot1, dot2, dot3, dot4);
        if (dots.contains(null)) {
            throw new IllegalStateException("One or more Circle elements are not defined in the FXML file.");
        }
        passcodeHandler = new PasscodeHandler(dots, 4);
        
        passcodeHandler.setOnSuccess(() -> {
            if (!isConfirmingPasscode) {
                // First passcode entry
                initialPasscode = passcodeHandler.getCorrectPasscode();
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
                    switchToMain();
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
        String icon = profileChooser.getValue();
        LocalDate intEventDate = dateofbirthField.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateofbirth = intEventDate.format(formatter);
        String phonenumber = phonenumberField.getText();
        String nationalid = nationalidField.getText();
        String countrycode = countrycodeField.getText();
        
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || dateofbirth.isEmpty() || icon.isEmpty() || phonenumber.isEmpty() || nationalid.isEmpty() || countrycode.isEmpty()) {
            AlertUtil.showAlert("Error", "You must input your information before signing up.");
            return;
        }

        if (!password.equals(confirmpassword)) {
            AlertUtil.showAlert("Error", "Passwords do not match. Please try again.");
            return;
        }

        if (password.contains(" ") || firstname.contains(" ") || lastname.contains(" ")) {
            AlertUtil.showAlert("Error", "Input fields should not contain spaces.");
            return;
        }

        if (initialPasscode.isEmpty()) {
            AlertUtil.showAlert("Error", "Please set up and confirm your passcode before continuing.");
            return;
        }

        try (Connection conn = ConnectBankUser.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (firstname, lastname, email, password, profile_icon, date_of_birth, phone_number, national_id, country_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, firstname);
            stmt.setString(2, lastname);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setString(5, icon);
            stmt.setString(6, dateofbirth);
            stmt.setString(7, phonenumber);
            stmt.setString(8, nationalid);
            stmt.setString(9, countrycode);
            
            stmt.executeUpdate();
            
            AlertUtil.showAlert("Success", "You have successfully registered your account.");
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

    public void switchToSignIn() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignIn.fxml"));
            stage = (Stage) continueButton.getScene().getWindow(); 
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Failed to load the main screen.");
        }
    }
    public void switchToMain() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScreen.fxml"));
            stage = (Stage) continueButton.getScene().getWindow(); 
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Error", "Failed to load the main screen.");
        }
    }

}
