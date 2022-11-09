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
    private MFXButton cancel;

    @FXML
    private MFXButton login;

    @FXML
    private MFXPasswordField loginPassword;

    @FXML
    private MFXTextField loginUsername;

    @FXML
    private AnchorPane loginWindow;
    
    @FXML
    private Label loginValidation;

    @FXML
    private Label passwordValidation;

    @FXML
    private Label usernameValidation;

    @FXML
    void onCancel(ActionEvent event) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	stage.close();
    }

    @FXML
    void onLogin(ActionEvent event) {
    	
    	usernameValidation.setText("");
    	passwordValidation.setText("");
    	loginValidation.setText("");
    	
    	boolean validation = true;
    	if(loginUsername.getText().isEmpty()) {
    		usernameValidation.setText("Username field is mandatory");
    		validation = false;
    	}
    	
    	if(loginPassword.getText().isEmpty()) {
    		passwordValidation.setText("Password field is mandatory");
    		validation = false;
    	}
    	
    	if(!validation) {
    		return;
    	}
    	
    	User usr = loginModel.validateUser(loginUsername.getText(), loginPassword.getText());
    	if(usr == null) {
    		loginValidation.setText("Invalid username or password");
    		return;
    	}
    	this.loggedUsr = usr;
			
	    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    currentStage.close();

    }

	public LoginController (){
		loginModel.setDummyData(false);
	}
	
	User getLoggedUsr() {
		return loggedUsr;
		
	}
		
	void setConnectionManager (ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}
}