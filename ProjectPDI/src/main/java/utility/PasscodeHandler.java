package utility;

import java.util.List;
import java.util.logging.Logger;
import javafx.scene.shape.Circle;

public class PasscodeHandler {
    private static final Logger logger = Logger.getLogger(PasscodeHandler.class.getName());

    private final StringBuilder enteredPasscode = new StringBuilder();
    private String tempPasscode = null;  // Stores the first passcode entry
    private String correctPasscode = null; // Stores the confirmed passcode
    private final List<Circle> dots;
    private final int passcodeLength;
    private boolean isConfirmingPasscode = false; // Flag to check if we're in confirmation mode
    private Runnable onSuccess;

    public PasscodeHandler(List<Circle> dots, int passcodeLength) {
        if (dots == null || dots.size() != passcodeLength) {
            throw new IllegalArgumentException("Dots list must contain exactly " + passcodeLength + " elements.");
        }
        this.dots = dots;
        this.passcodeLength = passcodeLength;
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    public void handleButtonPress(int number) {
        if (enteredPasscode.length() < passcodeLength) {
            enteredPasscode.append(number);
            updateDotsUI();

            if (enteredPasscode.length() == passcodeLength) {
                processPasscodeEntry();
            }
        }
    }

    private void processPasscodeEntry() {
        if (tempPasscode == null) {
            // First entry: Save passcode and ask for confirmation
            tempPasscode = enteredPasscode.toString();
            logger.info("✅ Passcode Set. Please enter it again to confirm.");
            isConfirmingPasscode = true;
            resetPasscode();
        } else if (isConfirmingPasscode) {
            // Second entry: Verify confirmation
            if (enteredPasscode.toString().equals(tempPasscode)) {
                correctPasscode = tempPasscode; // Finalize passcode
                logger.info("✅ Passcode Confirmed Successfully!");
                isConfirmingPasscode = false;
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else {
                // Passcodes don't match: reset everything
                logger.warning("❌ Passcodes do not match! Restarting setup.");
                correctPasscode = null;
                tempPasscode = null;
                isConfirmingPasscode = false;
                showErrorFeedback();
            }
            resetPasscode();
        } else {
            // If the passcode is already set, verify user input
            verifyPasscode();
        }
    }

    private void updateDotsUI() {
        for (int i = 0; i < dots.size(); i++) {
            Circle dot = dots.get(i);
            dot.getStyleClass().removeAll("filled-dot", "empty-dot", "error-dot");
            if (i < enteredPasscode.length()) {
                dot.getStyleClass().add("filled-dot");
            } else {
                dot.getStyleClass().add("empty-dot");
            }
        }
    }

    private void verifyPasscode() {
        if (enteredPasscode.toString().equals(correctPasscode)) {
            logger.info("✅ Correct Passcode Entered!");
            if (onSuccess != null) {
                onSuccess.run();
            }
        } else {
            logger.warning("❌ Incorrect Passcode! Try Again.");
            showErrorFeedback();
        }
        resetPasscode();
    }

    private void showErrorFeedback() {
        for (Circle dot : dots) {
            dot.getStyleClass().removeAll("filled-dot", "empty-dot", "error-dot");
            dot.getStyleClass().add("error-dot");
        }
    }

    public void resetPasscode() {
        enteredPasscode.setLength(0);
        updateDotsUI();
    }

    public String getCorrectPasscode() {
        return correctPasscode;
    }
}
