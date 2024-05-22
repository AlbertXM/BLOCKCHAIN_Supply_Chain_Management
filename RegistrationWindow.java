package acsse.csc03a3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * This class represents a registration window application.
 * 
 * @author Manamela Machuene Albert
 * @version Mini_Project_2024 Blockchain_Based Clothing Retail Supply Chain Management.
 */
public class RegistrationWindow extends Application 
{

    /**
     * Displays the registration window.
     */
    public void show() 
    {
        Stage primaryStage = new Stage();

        // Create registration form components
        TextField nameField = new TextField();
        TextField surnameField = new TextField();
        TextField contactField = new TextField();
        ComboBox<String> companyComboBox = new ComboBox<>();
        companyComboBox.getItems().addAll("Fibre_Producer", "Yarn_Manufacturing", "Fabric_Manufacturing","Apparel_Manufacturing", "Retail");
        companyComboBox.setPromptText("Select Company Type");
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        Button registerButton = new Button("Register");

        // Handle register button click
        registerButton.setOnAction(event -> 
        {
            // Get user input
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String contact = contactField.getText().trim();
            String company = companyComboBox.getValue();
            String email = usernameField.getText().trim();
            String passwordA = encryptPassword(passwordField.getText()); // Encrypt password
            String password = passwordField.getText();

            // Perform validation checks
            StringBuilder errorMessage = new StringBuilder();

            if (name.isEmpty()) 
            {
                errorMessage.append("-Name is required.\n");
            }

            if (surname.isEmpty()) 
            {
                errorMessage.append("-Surname is required.\n");
            }

            if (contact.isEmpty() || !isValidContactNumber(contact)) 
            {
                errorMessage.append("-Contact number is invalid. It should be 10 digits starting with '0'.\n");
            }

            if (company == null || company.isEmpty()) 
            {
                errorMessage.append("-Company name is required.\n");
            }

            if (!isValidEmail(email)) 
            {
                errorMessage.append("-Email is invalid or already in use.\n");
            }

            if (!isValidPassword(password)) 
            {
                errorMessage.append("-Password must contain at least one letter, one special character, and one number.\n");
            }

            if (errorMessage.length() > 0) 
            {
                showErrorAlert("Validation Error", errorMessage.toString());
                return; // Exit registration process
            }

            // Write user information to users.txt
            try (FileWriter fw = new FileWriter("data/server/users.txt", true);
                 PrintWriter writer = new PrintWriter(fw)) 
            {
                writer.print(email + " " + password + " " + passwordA); // Write email and password to file
                writer.println(" [For Name: " + name + ", Surname: " + surname + ", Contact Number: " + contact + ", Company Name: " + company + " ]");
                // Optionally, you can also write other user information here
                writer.flush();
            } catch (IOException e) 
            {
                e.printStackTrace();
            }

            // Clear input fields after registration
            nameField.clear();
            surnameField.clear();
            contactField.clear();
            companyComboBox.setValue(null);
            usernameField.clear();
            passwordField.clear();

            // Display success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("You have been successfully registered!");
            alert.showAndWait();
        });

        // Create registration form layout
        VBox formLayout = new VBox(10);
        formLayout.getChildren().addAll(
            createBoldRedLabel("Important: (*) - means required!!"),
            new Label("Name*:"), nameField,
            new Label("Surname*:"), surnameField,
            new Label("Contact Number*{10 numbers starting by 0}:"), contactField,
            new Label("Company Type*:"), companyComboBox,
            new Label("Email*:"), usernameField,
            new Label("Password*{at least 1-number, 1-special character, 1-letter}:"), passwordField,
            registerButton
        );

        // Set the scene
        Scene scene = new Scene(formLayout, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Registration");
        primaryStage.show();
    }

    /**
     * Creates a bold red label with the given text.
     * @param text The text of the label.
     * @return The created label.
     */
    private Label createBoldRedLabel(String text) 
    {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        return label;
    }

    /**
     * Encrypts the given password using SHA-256 algorithm.
     * @param password The password to be encrypted.
     * @return The encrypted password.
     */
    private String encryptPassword(String password) 
    {
        try 
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes)
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) 
        {
            showErrorAlert("Encryption Error", "Failed to encrypt password.");
            return null;
        }
    }

    /**
     * Validates the format of a contact number.
     * @param contact The contact number to validate.
     * @return True if the contact number is valid, false otherwise.
     */
    private boolean isValidContactNumber(String contact) 
    {
        // Check if contact number has exactly 10 digits and starts with '0'
        return contact.matches("^0\\d{9}$");
    }

    /**
     * Validates the format of an email and checks if it's already in use.
     * @param email The email to validate.
     * @return True if the email is valid and not in use, false otherwise.
     */
    private boolean isValidEmail(String email) 
    {
        // Check if email contains at least one '@' and one '.'
        if (!(email.contains("@") && email.contains("."))) 
        {
            return false;
        }

        // Check if email is already in use
        try (BufferedReader br = new BufferedReader(new FileReader("data/server/users.txt"))) 
        {
            String line;
            while ((line = br.readLine()) != null) 
            {
                if (line.startsWith(email + " ")) 
                {
                    return false; // Email is already in use
                }
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Validates the format of a password.
     * @param password The password to validate.
     * @return True if the password is valid, false otherwise.
     */
    private boolean isValidPassword(String password) 
    {
        // Check if password contains at least one letter, one special character, and one number
        return password.matches("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=!*])(?=\\S+$).{8,}$");
    }

    /**
     * Shows an error alert dialog with the given title and message.
     * @param title The title of the error alert dialog.
    * @param message The message of the error.
    */
    private void showErrorAlert(String title, String message) 
    {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
    }
    /**
     * Overrides the start method of Application class to initiate the registration window.
     * @param primaryStage The primary stage of the application.
     */
    @Override
    public void start(Stage primaryStage) 
    {
        show();
    }

    /**
     * Launches the application.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) 
    {
        launch(args);
    }
}