package utility;

import javafx.scene.control.Alert;

public class AlertUtil {
	public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);  // We don't need a header text
        alert.setContentText(message);
        alert.showAndWait();  // This waits for the user to close the alert
    }
}
