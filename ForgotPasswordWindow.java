/**
 * A JavaFX application for handling password reset functionality.
 * 
 * @author Manamela Machuene Albert
 * @version Mini_Project_2024 Blockchain_Based Clothing Retail Supply Chain Management.
 */
package acsse.csc03a3;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ForgotPasswordWindow extends Application 
{

    /**
     * Path to the file containing user information.
     */
    private static final String USERS_FILE_PATH = "data/server/users.txt";

    private Label emailLabel = new Label("Email:");
    private TextField emailField = new TextField();
    private Button submitButton = new Button("Submit");

    /**
     * Main method to launch the application.
     */
    public static void main(String[] args) 
    {
        launch(args);
    } 

    /**
     * Overrides the start method of Application class.
     * @param primaryStage The primary stage of the application.
     */
    @Override
    public void start(Stage primaryStage) 
    {
        show();
    }

    /**
     * Displays the forgot password window.
     */
    public void show() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Forgot Password");

        // Set action for submit button
        submitButton.setOnAction(e -> 
        {
            // Verify if the email exists in the system
            if (isEmailRegistered(emailField.getText())) 
            {
                // Close the current window
                window.close();
                // Open the window for resetting password
                showResetPasswordWindow(emailField.getText());
            } else 
            {
                // If email does not exist, show an error message
                showAlert("Error", "The provided email does not exist in our system.");
            }
        });

        // Create layout
        GridPane layout = new GridPane();
        layout.add(emailLabel, 0, 0);
        layout.add(emailField, 1, 0);
        layout.add(submitButton, 1, 1);

        // Set spacing and padding
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(10));

        // Set scene
        Scene scene = new Scene(layout, 300, 150);
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * Displays the reset password window.
     * @param email The email for which password is to be reset.
     */
    private void showResetPasswordWindow(String email) 
    {
        Stage resetWindow = new Stage();
        resetWindow.initModality(Modality.APPLICATION_MODAL);
        resetWindow.setTitle("Reset Password");

        Label newPasswordLabel = new Label("New Password:");
        TextField newPasswordField = new TextField();
        Label confirmPasswordLabel = new Label("Confirm Password:");
        TextField confirmPasswordField = new TextField();
        Button resetButton = new Button("Reset");

        // Set action for reset button
        resetButton.setOnAction(e -> 
        {
            // Validate passwords
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            if (newPassword.equals(confirmPassword)) 
            {
                // Update password in the system
                updatePassword(email, newPassword);
                showAlert("Success", "Password reset successfully!");
                resetWindow.close();
            } else {
                showAlert("Error", "Passwords do not match. Please try again.");
            }
        });

        // Create layout for reset window
        GridPane resetLayout = new GridPane();
        resetLayout.add(newPasswordLabel, 0, 0);
        resetLayout.add(newPasswordField, 1, 0);
        resetLayout.add(confirmPasswordLabel, 0, 1);
        resetLayout.add(confirmPasswordField, 1, 1);
        resetLayout.add(resetButton, 1, 2);
        resetLayout.setHgap(10);
        resetLayout.setVgap(10);
        resetLayout.setPadding(new Insets(10));

        // Set scene for reset window
        Scene resetScene = new Scene(resetLayout, 300, 200);
        resetWindow.setScene(resetScene);
        resetWindow.show(); // Changed to show() instead of showAndWait()
    }

    /**
     * Displays an alert dialog.
     * @param title The title of the alert.
     * @param content The content of the alert.
     */
    private void showAlert(String title, String content) 
    {
        // Create an Alert dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Display the alert dialog
        alert.showAndWait();
    }

    /**
     * Checks if the email is registered in the system.
     * @param email The email to be checked.
     * @return True if the email is registered, false otherwise.
     */
    private boolean isEmailRegistered(String email) 
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                // Split the line by space to get the email
                String[] parts = line.split(" ");
                if (parts.length > 0 && parts[0].equals(email)) 
                {
                    return true; // Email found in the file
                }
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        return false; // Email not found in the file
    }

    /**
     * Updates the password for the given email.
     * @param email The email for which password is to be updated.
     * @param newPassword The new password.
     */
    private void updatePassword(String email, String newPassword) 
    {
        try 
        {
            BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_PATH));
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) 
            {
                String[] parts = line.split(" ");
                if (parts.length > 0 && parts[0].equals(email)) 
                {
                    // Replace the password with the new one
                    line = parts[0] + " " + newPassword  + line.substring(line.indexOf(" ", parts[0].length() + 1));
                }
                fileContent.append(line).append("\n");
            }
            reader.close();

            // Write the updated content back to the file
            FileWriter writer = new FileWriter(USERS_FILE_PATH);
            writer.write(fileContent.toString());
            writer.close();
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
