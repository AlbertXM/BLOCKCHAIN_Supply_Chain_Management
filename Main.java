package acsse.csc03a3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private boolean loggedIn = false;

    private TextField tfNonce ,tfBlockNo, tfPreviousHash,tfUsername, tfSizeToSend;
    private ComboBox<String> cbReceiverID, cbSenderID;
    private PasswordField tfPassword;
    private Label lblCurrentTimestamp;
    private Label lblTransactionID;
    private Label lblNonce;
    private TextArea taPublicInfo = new TextArea();

    private TextField tfSearchTransaction;
    private Button btnRegister = new Button("Register");
    private Button btnForgotPassword = new Button("Forgot Password");

    private Stage loggedInStage = new Stage();

    private Image backgroundImage1 = new Image("file:data/server/mini_pic.jpg");
    private BackgroundImage background1 = new BackgroundImage(backgroundImage1, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
    private Image backgroundImage2 = new Image("file:data/server/mini_pic2.jpg");
    private BackgroundImage background2 = new BackgroundImage(backgroundImage2, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

	

    /**
     * Initializes and displays the login window.
     * @param primaryStage The primary stage of the application.
     */
    public void start(Stage primaryStage) 
    {
        GridPane pane = new GridPane();

        pane.setBackground(new Background(background1));

        Label lblUsername = new Label("Email:");
        lblUsername.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        tfUsername = new TextField();

        Label lblPassword = new Label("Password:");
        lblPassword.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        tfPassword = new PasswordField();

        Button btnLogIn = new Button("LOG IN");

        pane.add(lblUsername, 0, 0);
        pane.add(tfUsername, 1, 0);
        pane.add(lblPassword, 0, 1);
        pane.add(tfPassword, 1, 1);
        pane.add(btnLogIn, 1, 2);
        pane.add(btnRegister, 2, 2);
        pane.add(btnForgotPassword, 2, 3);
        

        pane.setAlignment(Pos.CENTER);
        btnLogIn.setOnAction(e -> btnLogInImplementation(primaryStage));
        btnRegister.setOnAction(e -> openRegistrationWindow());
        btnForgotPassword.setOnAction(e -> openForgotPasswordWindow());

        Scene scene = new Scene(pane, 700, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Blockchain Supplychain Application");
        primaryStage.show();
    }
    
    /**
     * Displays a graph representing transaction analysis.
     */
    @SuppressWarnings("unchecked")
	private void displayGraph() {
        // Parse transaction number, size to send, and timestamp from transactions file
        List<Integer> transactionNumbers = new ArrayList<>();
        List<Integer> sizesToSend = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("data/server/transactions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Transaction ID:")) {
                    // Extract size to send from the line
                    int sizeIndex = line.indexOf("Size To Send: ") + 14;
                    int endIndex = line.indexOf("\t", sizeIndex);
                    int size = Integer.parseInt(line.substring(sizeIndex, endIndex));
                    sizesToSend.add(size);

                    // Extract timestamp from the line
                    int timestampIndex = line.indexOf("Timestamp: ") + 11;
                    String timestampString = line.substring(timestampIndex).trim();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    Date date = dateFormat.parse(timestampString);
                    long timestamp = date.getTime();
                    timestamps.add(timestamp);

                    // Increment transaction number
                    transactionNumbers.add(transactionNumbers.size() + 1);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // Create a line chart
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Transaction Number");
        yAxis.setLabel("Value");
        yAxis.setTickUnit(10); // Decrease the scale of y-axis values
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Transaction Analysis Graph");

        // Prepare data series for size to send
        XYChart.Series<Number, Number> sizeSeries = new XYChart.Series<>();
        sizeSeries.setName("Size To Send");

        // Prepare data series for transaction minutes
        XYChart.Series<Number, Number> minutesSeries = new XYChart.Series<>();
        minutesSeries.setName("Transaction Minutes");

        // Populate data series for both size to send and transaction minutes
        long previousTimestamp = timestamps.get(0);
        for (int i = 1; i < timestamps.size(); i++) {
            long currentTimestamp = timestamps.get(i);
            long minutesElapsed = (currentTimestamp - previousTimestamp) / (60 * 1000); // Convert milliseconds to minutes
            sizeSeries.getData().add(new XYChart.Data<>(transactionNumbers.get(i), sizesToSend.get(i)));
            minutesSeries.getData().add(new XYChart.Data<>(transactionNumbers.get(i), minutesElapsed));
            previousTimestamp = currentTimestamp;
        }

        lineChart.getData().addAll(sizeSeries, minutesSeries);

        // Display chart
        VBox vbox = new VBox(lineChart);
        Scene scene = new Scene(vbox, 900, 500);

        Stage graphStage = new Stage();
        graphStage.setTitle("Transaction Analysis Graph");
        graphStage.setScene(scene);
        graphStage.show();
    }


    /**
     * Displays a graph representing transaction analysis for different company types.
     */
    private void displayGraph2() {
        // Initialize transaction count for each company type
        Map<String, Integer> transactionCounts = new HashMap<>();
        transactionCounts.put("Fibre_Producer", 0);
        transactionCounts.put("Yarn_Manufacturing", 0);
        transactionCounts.put("Fabric_Manufacturing", 0);
        transactionCounts.put("Apparel_Manufacturing", 0);
        transactionCounts.put("Retail", 0);

        try (BufferedReader reader = new BufferedReader(new FileReader("data/server/transactions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Sender ID:")) {
                    // Extract company type
                    int senderIndex = line.indexOf("Sender ID: ") + 11;
                    int endIndex = line.indexOf("\t", senderIndex);
                    String sender = line.substring(senderIndex, endIndex);

                    // Increment transaction count for the corresponding company type
                    if (transactionCounts.containsKey(sender)) {
                        int count = transactionCounts.get(sender);
                        transactionCounts.put(sender, count + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a bar chart
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Company Type");
        yAxis.setLabel("Transaction Number");
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        barChart.setTitle("Transaction Analysis Graph");

        // Prepare data series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Transaction Count");

        // Populate data series
        for (Map.Entry<String, Integer> entry : transactionCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);

        // Display chart
        VBox vbox = new VBox(barChart);
        Scene scene = new Scene(vbox, 800, 450);

        Stage graphStage = new Stage();
        graphStage.setTitle("Transaction Analysis Graph");
        graphStage.setScene(scene);
        graphStage.show();
    }
    
    /**
     * Displays a graph representing blockchain analysis for nonce behaviour  company types.
     */
    private void displayNonceGraph() {
        List<Long> nonces = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("data/client/blockchain_output.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Block Number:") && !line.contains("Block Number: 0\tNonce: 0\tPrevious Hash: 0000000000000000000000000000000000000000000000000000000000000000")) {
                    int nonceIndex = line.indexOf("Nonce: ") + 7;
                    int endIndex = line.indexOf("\t", nonceIndex);
                    long nonce = Long.parseLong(line.substring(nonceIndex, endIndex));
                    nonces.add(nonce);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a line chart
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Block Number");
        yAxis.setLabel("Nonce");
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Nonce Behavior Graph");

        // Prepare data series for nonce values
        XYChart.Series<Number, Number> nonceSeries = new XYChart.Series<>();
        nonceSeries.setName("Nonce");

        // Populate data series for nonce values
        for (int i = 0; i < nonces.size(); i++) {
            nonceSeries.getData().add(new XYChart.Data<>(i + 1, nonces.get(i)));
        }

        lineChart.getData().add(nonceSeries);

        // Display chart
        VBox vbox = new VBox(lineChart);
        Scene scene = new Scene(vbox, 700, 400);

        Stage graphStage = new Stage();
        graphStage.setTitle("Nonce Behavior Graph");
        graphStage.setScene(scene);
        graphStage.show();
    }


    /**
     * Checks if a given string is numeric.
     *
     * @param str The string to be checked.
     * @return True if the string is numeric, false otherwise.
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }




   



    /**
     * Opens the registration window.
     */
    private void openRegistrationWindow() {
        RegistrationWindow r = new RegistrationWindow();
        r.show();
    }

    /**
     * Opens the forgot password window.
     */
    private void openForgotPasswordWindow() {
        ForgotPasswordWindow f = new ForgotPasswordWindow();
        f.show();
    }

    /**
     * Creates the layout for displaying public messages.
     *
     * @return GridPane containing the public messages layout.
     */
    private GridPane publicMessagePane() {
        Label lblPublicInfo = new Label("Attatch Message:");

        GridPane publicMessagesGridPane = new GridPane();
        publicMessagesGridPane.setVgap(10);
        publicMessagesGridPane.setHgap(10);
        publicMessagesGridPane.add(lblPublicInfo, 0, 0);
        publicMessagesGridPane.add(taPublicInfo, 0, 1);

        return publicMessagesGridPane;
    }

    /**
     * Creates the layout for adding transactions.
     *
     * @return GridPane containing the transaction input fields.
     */
    private GridPane createTransactionsContent() {
        Label lblSenderID = new Label("SenderID: ");
        
        cbSenderID = new ComboBox<>();
        cbSenderID.getItems().addAll("Fibre_Producer", "Yarn_Manufacturing", "Fabric_Manufacturing","Apparel_Manufacturing", "Retail"); // Add your receiver IDs here
        cbSenderID.setPromptText("Confirm_Your_Company_Type");
        
        Label lblReceiverID = new Label("Receiver_Type:");
        cbReceiverID = new ComboBox<>();
        cbReceiverID.getItems().addAll("Fibre_Producer", "Yarn_Manufacturing", "Fabric_Manufacturing","Apparel_Manufacturing", "Retail"); // Add your receiver IDs here
        cbReceiverID.setPromptText("select_company_type");
        Label lblSizeToSend = new Label("Size To Send:");
        tfSizeToSend = new TextField();

        GridPane transactionGridPane = new GridPane();
        transactionGridPane.setVgap(10);
        transactionGridPane.setHgap(10);
        transactionGridPane.add(lblSenderID, 0, 0);
        transactionGridPane.add(cbSenderID, 1, 0);
        transactionGridPane.add(lblReceiverID, 0, 1);
        transactionGridPane.add(cbReceiverID, 1, 1);
        transactionGridPane.add(lblSizeToSend, 0, 2);
        transactionGridPane.add(tfSizeToSend, 1, 2);

        return transactionGridPane;
    }


    /**
     * Creates the layout for the logged-in window.
     *
     * @return GridPane containing the components for logged-in users.
     */
    private GridPane afterLogInContent() 
	{
        // Create buttons for interaction
        lblCurrentTimestamp = new Label("...");
        Button btnViewChain = new Button("ViewBlockchain + Download");
        Button btnAddTransaction = new Button("Add Transaction");
        Button btnLogOut = new Button("Log Out");
        Button btnSearchTransaction = new Button("Search Transaction");
        Button btnViewGraph = new Button("View Graphs");
        
        Label lblBlock = new Label("Block:");
        lblBlock.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        tfBlockNo = new TextField("_no__edit_needed_");
        tfBlockNo.setEditable(false);

        lblNonce = new Label("Nonce:");
        lblNonce.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        tfNonce = new TextField("_no__edit_needed_");
        tfNonce.setEditable(false);

        Label lblPreviousHash = new Label("Previous Hash:");
        lblPreviousHash.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        tfPreviousHash = new TextField("_no__edit_needed_");
        tfPreviousHash.setEditable(false);

        
        
        
        // Create new search field and button for transactions
        Label lblSearchTransaction = new Label("Search :");
        lblSearchTransaction.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        tfSearchTransaction = new TextField();
        btnSearchTransaction = new Button("SearchT");
        
        lblTransactionID = new Label("TransactionID:");

        // Create layout and add buttons
        GridPane gridPane = new GridPane();
        
        gridPane.setBackground(new Background(background2));
        gridPane.setAlignment(Pos.CENTER); // Center the contents of the GridPane

        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        gridPane.add(lblBlock, 0, 0);
        gridPane.add(tfBlockNo, 1, 0);

        gridPane.add(lblNonce, 0, 1);
        gridPane.add(tfNonce, 1, 1);

        gridPane.add(lblPreviousHash, 0, 2);
        gridPane.add(tfPreviousHash, 1, 2);

      
        gridPane.add(lblSearchTransaction, 0, 12);
        gridPane.add(tfSearchTransaction, 1, 12);
        gridPane.add(btnSearchTransaction, 2, 12);

        
        gridPane.add(btnViewGraph, 0, 13);
        btnViewGraph.setOnAction(event -> {displayGraph(); displayGraph2(); displayNonceGraph();});
        
       
        
        
        // Create sections using TitledPane
        TitledPane messagesPane = new TitledPane("Public Messages", publicMessagePane());
        messagesPane.setCollapsible(true); // control collapsibility
        gridPane.add(messagesPane, 0, 3, 2, 1); // Span two columns

        TitledPane TransactionsPane = new TitledPane("Transactions", createTransactionsContent());
        TransactionsPane.setCollapsible(true);
        gridPane.add(TransactionsPane, 0, 8, 2, 1);

        gridPane.add(btnAddTransaction, 0, 10);

        gridPane.add(lblCurrentTimestamp, 0, 9);
        gridPane.add(lblTransactionID, 1, 9);
        gridPane.add(btnViewChain, 1, 10);
        gridPane.add(btnLogOut, 0, 11);
        
        
        // Set actions for the buttons
        btnViewChain.setOnAction(e -> {saveConsoleTextToFile(); btnViewBlockchainImplementation();});
        btnAddTransaction.setOnAction(e -> btnAddTransactionImplementation(taPublicInfo.getText()));
        btnSearchTransaction.setOnAction(e -> handleSearchTransaction());
        

     // Set action for the Log Out button
        btnLogOut.setOnAction(e -> 
        {
            // Close the current window
            Stage stage = (Stage) btnLogOut.getScene().getWindow();
            stage.close();
            // Open the login window
            start(new Stage());
        });
        
        return gridPane;
    }
    
       
    /**
     * Handles the addition of a transaction.
     *
     * @param messageSent The message sent along with the transaction.
     */
    private void btnAddTransactionImplementation(String messageSent) {
        // Check if the user has logged in successfully
        if (loggedIn) {
            // Check if the receiver type is answered
            String receiverType = cbReceiverID.getValue();
            if (receiverType == null || receiverType.isEmpty()) {
                showErrorAlert("Error", "Please select a receiver type.");
                return; // Exit the method
            }

            // Check if the size to send is a number
            String sizeToSendText = tfSizeToSend.getText();
            if (!isNumeric(sizeToSendText) || sizeToSendText.isEmpty()) {
                showErrorAlert("Error", "Size to send must be a number.");
                return; // Exit the method
            }
            
            
            if (!isCompanyExists(tfUsername.getText(), cbSenderID.getValue())) {
                // Company name not found, show error message
                showErrorAlert("Error", "Sender company name not found in users.txt.");
                return; // Exit the method
            }


            String transactionID = myTransaction.getTransactionID(cbSenderID.getValue(), receiverType, Integer.parseInt(sizeToSendText));
            String receiverID = receiverType;
            int sizeToSend = Integer.parseInt(sizeToSendText);

            // Get the current timestamp
            long currentTimestamp = System.currentTimeMillis();

            // Display the timestamp
            lblCurrentTimestamp.setText("Timestamp: " + currentTimestamp);
            lblTransactionID.setText("TransactionID: " + transactionID);

            // Create a new transaction
            myTransaction<String> transaction = new myTransaction<>(transactionID, cbSenderID.getValue(), receiverID, sizeToSend, currentTimestamp);

            // Write the transaction data to a text file
            saveTransactionToFile(transaction, messageSent);

            // Add the transaction to the blockchain
            myBlockchain.addTransaction(transaction);

            // Show a confirmation message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Transaction Added");
            alert.setHeaderText(null);
            alert.setContentText("Transaction added successfully.");
            alert.showAndWait();
        } else {
            // User has not logged in successfully, show error message
            showErrorAlert("Error", "Please log in to add a transaction.");
        }
    }
    
   
    /**
     * Checks if a company with a given email and company name exists.
     *
     * @param email       The email of the company.
     * @param companyName The name of the company.
     * @return True if the company exists, false otherwise.
     */
    private boolean isCompanyExists(String email, String companyName) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/server/users.txt"))) {
            
            String line;
            while ((line = br.readLine()) != null) {
            	String[] parts = line.split(" ");
                // Check if the line contains the email
                if (parts.length > 0 && parts[0].equals(email)) {
                    // Check if the line contains "Company Name:"
                    if (line.contains("Company Name:")) {
                        // Extract the company name from the line
                        int startIndex = line.indexOf("Company Name:") + 14;
                        int endIndex = line.indexOf("]", startIndex);
                        String lineCompanyName = line.substring(startIndex, endIndex).trim();
                        
                        if (lineCompanyName.equals(companyName)) {
                            return true; // Company name found
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Company name not found
    }

    


    /**
     * Displays an error alert dialog with the specified title and message.
     *
     * @param title   The title of the error alert.
     * @param message The message to be displayed in the error alert.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


	/**
	 * Saves transaction data to a text file.
	 *
	 * @param transaction The transaction to be saved.
	 * @param messageSent The message sent along with the transaction.
	 */
	private void saveTransactionToFile(myTransaction<String> transaction, String messageSent) {
		 try {
		     // Create a file in the specified directory
		     File directory = new File("data/server/");
		     if (!directory.exists()) {
		         directory.mkdirs(); // Create the directory if it doesn't exist
		     }
		     File file = new File(directory, "transactions.txt");
		
		     // Open the file in append mode
		     FileWriter fileWriter = new FileWriter(file, true);
		     PrintWriter writer = new PrintWriter(fileWriter);
		
		     // Write the transaction data to the file
		     writer.print("Transaction ID: " + myTransaction.getTransactionID(cbSenderID.getValue(), cbReceiverID.getValue(), Integer.parseInt(tfSizeToSend.getText())));
		     writer.print("Sender ID: " + transaction.getSender()+"\t");
		     writer.print("Receiver ID: " + transaction.getReceiver() +"\t");
		     writer.print("Size To Send: " + transaction.getSizeToSend() +"\t");
		     writer.print("Message Sent: " + messageSent +"\t"); // Include the public message
		     writer.print("Timestamp: " + new Date(transaction.getTimestamp()).toString() +"\t");
		     writer.println(); // Add an empty line for separation
		
		     writer.close(); // Close the writer to flush data to the file
		 } catch (IOException e) 
		 {
		     e.printStackTrace();
		 }
	}





	/**
	 * Displays the blockchain information in the console.
	 */
    private void btnViewBlockchainImplementation() 
    {
    	
        
        // Get the blockchain instance
        myBlockchain<String> blockchain = getBlockchainInstance();

        // Print each block in the blockchain to the console
        for (myBlock<String> block : blockchain.getChain()) 
        {
            System.out.println("Block Number: " + block.getBlockNumber());
            System.out.println("Nonce: " + block.getNonce());
            System.out.println("Previous Hash: " + block.getPreviousHash());

            // Print each transaction in the block
            for (myTransaction<String> transaction : block.getTransactions()) 
            {
                System.out.println("\tTransaction ID: " + myTransaction.getTransactionID(cbSenderID.getValue(), cbReceiverID.getValue(), Integer.parseInt(tfSizeToSend.getText())));
                System.out.println("\tSender ID: " + transaction.getSender());
                System.out.println("\tReceiver ID: " + transaction.getReceiver());
                System.out.println("\tSize To Send: " + transaction.getSizeToSend());
                System.out.println("\tData: " + transaction.getData());
                System.out.println("\tTimestamp: " + transaction.getTimestamp());
            }

            System.out.println("======================================================================================");
            System.out.println("                                        []                                            ");
            System.out.println("                                        []                                            ");
            System.out.println("======================================================================================");
            
        }
        
        
        
    }

    /**
     * Handles the login process.
     *
     * @param primaryStage The primary stage of the application.
     */
    private void btnLogInImplementation(Stage primaryStage)
    {
    	 // Check if the user has logged in successfully
        String username = tfUsername.getText();
        String password = tfPassword.getText();

        // Read user data from file and check if the username and password match
        try (BufferedReader reader = new BufferedReader(new FileReader("data/server/users.txt"))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                String[] parts = line.split(" ");
                if ( parts[0].equals(username) && parts[1].equals(password)) 
                {
                    loggedIn = true;
                    break;
                }
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
        }

        if (loggedIn) 
        {
            // Launch the logged-in window
            launchLoggedInWindow(primaryStage);
            loggedInStage.setOnCloseRequest(event1 -> 
            {
                // Reset the login status when the logged-in window is closed
                loggedIn = false;
            });

            primaryStage.close(); // Close the login window
        } else 
        {
            // Show an alert indicating invalid login
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password. Please try again.");
            alert.showAndWait();

            // Clear the login form
            clearLoginForm();
        }
    }
    
   
    
    /**
     * Retrieves the blockchain instance.
     *
     * @return The blockchain instance.
     */
    private myBlockchain<String> getBlockchainInstance() 
    {
        return myBlockchain.getInstance();
    }
    
    
    
    /**
     * Launches the logged-in window.
     *
     * @param primaryStage The primary stage of the application.
     */
    private void launchLoggedInWindow(Stage primaryStage) 
    {
    	Stage loggedInStage = new Stage();
        loggedInStage.setOnCloseRequest(event -> loggedIn = false);
        GridPane content = afterLogInContent();
        Scene loggedInScene = new Scene(content, 700, 700);
        loggedInStage.setScene(loggedInScene);
        loggedInStage.setTitle("Blockchain Supply Chain Application - Logged In");
        loggedInStage.show();
    }
    
 

    
    /**
     * Saves the console text to a file.
     */
    private void saveConsoleTextToFile() 
    {
        try 
        {
            // Create a file in the data/client directory
            File file = new File("data/client/blockchain_output.txt");
            myBlockchain<String> blockchain = getBlockchainInstance();
            FileWriter fileWriter;
            
            // Open the file in append mode
            if(loggedIn)
            {
            	 fileWriter = new FileWriter(file,true);
            }else
            {
            	fileWriter = new FileWriter(file);  
            }
            PrintWriter writer = new PrintWriter(fileWriter);
           // long currentBlockNumber =  (blockchain.getLastBlock().getBlockNumber() + 1); // Get the latest block number and increment

            // Write blockchain console text to the file
            // Iterate through each block in the blockchain (starting from the second block)
            for (int i = 0; i < blockchain.getChain().size(); i++) 
            {
                myBlock<String> block = blockchain.getChain().get(i);
                writer.print("Block Number: " + block.getBlockNumber());
                writer.print("\tNonce: " + block.getNonce());
                if(block.getPreviousHash() =="0000000000000000000000000000000000000000000000000000000000000000")
                {writer.print("\tPrevious Hash: " + block.getPreviousHash()+"\n");}
                else
                	{writer.print("\tPrevious Hash: " + block.getPreviousHash());}

                // Print each transaction in the block
                for (myTransaction<String> transaction : block.getTransactions()) 
                {
                    writer.print("\tTransaction ID: " + myTransaction.getTransactionID(cbSenderID.getValue(), cbReceiverID.getValue(), Integer.parseInt(tfSizeToSend.getText())));
                    writer.print("\tSender ID: " + transaction.getSender());
                    writer.print("\tReceiver ID: " + transaction.getReceiver());
                    writer.print("\tSize To Send: " + transaction.getSizeToSend());
                    writer.print("\tData: " + transaction.getData());
                    writer.println("\tTimestamp: " + transaction.getTimestamp());
                }

                
            }

            
            writer.close(); // Important to close the writer to flush data to the file

            System.out.println("Blockchain console output appended to: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Searches for transactions containing a given term.
     *
     * @param searchTerm The term to search for.
     * @return A list of matching transactions.
     */
    private List<String> searchTransactions(String searchTerm) {
        List<String> matchingTransactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("data/server/transactions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(searchTerm)) {
                    matchingTransactions.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        

        return matchingTransactions;
    }
    
    /**
     * Searches for blockchain data containing a given term.
     *
     * @param searchTerm The term to search for.
     * @return A list of matching blockchain data.
     */
    private List<String> searchBlockchain(String searchTerm) 
    {
        List<String> matchingTransactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("data/client/blockchain_output.txt"))) {
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (line.contains(searchTerm)) {
                    matchingTransactions.add(line);
                }
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        

        return matchingTransactions;
    }
    
    /**
     * Handles the search transaction button click.
     */
    private void handleSearchTransaction() 
    {
        String searchTerm = tfSearchTransaction.getText();
        
         
        if (searchTerm.isEmpty())
        {
            // Show error message if search term is empty
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Search Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a term to search.");
            alert.showAndWait();
            return;
        }

        // Perform the search
        List<String> matchingTransactions = searchTransactions(searchTerm);
        List<String> matchingBlockchain = searchBlockchain(searchTerm);

        if (!matchingTransactions.isEmpty() || matchingBlockchain.isEmpty()) 
        {
            // Display matching transactions in a list or some other suitable format
            displayMatchingTransactions(matchingTransactions);
            displayMatchingBlockchains(matchingBlockchain);
        } else 
        {
            // If no match is found, show an alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search Result");
            alert.setHeaderText(null);
            alert.setContentText("No matching transaction found.");
            alert.showAndWait();
        }
        
    }
    
    /**
     * Displays matching transactions in a separate window.
     *
     * @param matchingTransactions The list of matching transactions.
     */
    private void displayMatchingBlockchains(List<String> matchingBlockchain) 
    {
        // Create a ListView to display the matching transactions
        ListView<String> listView = new ListView<>();

        // Add each matching transaction to the ListView
        listView.getItems().addAll(matchingBlockchain);

        listView.setPrefWidth(550); // Set the desired width
        Stage matchingTransactionsStage = new Stage();
        matchingTransactionsStage.setTitle("Matching Blockchain Transactions");
        matchingTransactionsStage.setScene(new Scene(new Group(listView), 600, 400));
        matchingTransactionsStage.show();
    }



    /**
     * Displays matching blockchain data in a separate window.
     *
     * @param matchingBlockchain The list of matching blockchain data.
     */
    private void displayMatchingTransactions(List<String> matchingTransactions) 
    {
        // Create a ListView to display the matching transactions
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(matchingTransactions);
        listView.setPrefWidth(550); // Set the desired width

        Stage matchingTransactionsStage = new Stage();
        matchingTransactionsStage.setTitle("Matching Transactions");
        matchingTransactionsStage.setScene(new Scene(new Group(listView), 600, 400));
        matchingTransactionsStage.show();
    }


    /**
     * Clears the login form fields.
     */
    private void clearLoginForm() 
    {
        tfUsername.clear();
        tfPassword.clear();
    }
   
    public static void main(String[] args) 
    {      
        launch(args);
    }
}
