package application;


import java.io.IOException;
import java.util.Properties;

import application.news.User;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;



public class LoginController {
//TODO Add all attribute and methods as needed 
	private LoginModel loginModel = new LoginModel();
	
	private User loggedUsr = null;
	
    @FXML
    private MFXButton Cancel;

    @FXML
    private MFXButton Login;

    @FXML
    private MFXPasswordField LoginPassword;

    @FXML
    private MFXTextField LoginUsername;

    @FXML
    private AnchorPane loginWindow;
    
    @FXML
    private Label LoginValidation;

    @FXML
    private Label PasswordValidation;

    @FXML
    private Label UsernameValidation;

    @FXML
    void onCancel(ActionEvent event) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	stage.close();
    }

    @FXML
    void onLogin(ActionEvent event) {
    	
    	UsernameValidation.setText("");
    	PasswordValidation.setText("");
    	LoginValidation.setText("");
    	
    	boolean validation = true;
    	if(LoginUsername.getText().isEmpty()) {
    		UsernameValidation.setText("Username field is mandatory");
    		validation = false;
    	}
    	
    	if(LoginPassword.getText().isEmpty()) {
    		PasswordValidation.setText("Password field is mandatory");
    		validation = false;
    	}
    	
    	if(!validation) {
    		return;
    	}
    	
    	User usr = loginModel.validateUser(LoginUsername.getText(), LoginPassword.getText());
    	if(usr == null) {
    		LoginValidation.setText("Invalid username or password");
    		return;
    	}
    	this.loggedUsr = usr;
			
	    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    currentStage.close();

    }

	public LoginController (){
	
		//Uncomment next sentence to use data from server instead dummy data
		loginModel.setDummyData(false);
	}
	
	User getLoggedUsr() {
		return loggedUsr;
		
	}
		
	void setConnectionManager (ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}
}