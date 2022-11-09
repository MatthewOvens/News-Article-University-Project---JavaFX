package application;


//import com.jfoenix.controls.JFXTextField;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class ImagePickerController {

    @FXML // fx:id="idImageURL"
    private TextField idImageURL; // Value injected by FXMLLoader

 
    @FXML // fx:id="imgPreview"
    private ImageView imgPreview; // Value injected by FXMLLoader
    private Image image;

    @FXML
    void onAccept(ActionEvent event) {
    	
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    		
		if(imageCheck(idImageURL.getText())) {
			
			image = new Image(idImageURL.getText(), false);
			
			if(!image.isError()) {
				stage.close();
			}
			else {
				Alert alert = new Alert(AlertType.ERROR, "Error loading the insert URL", ButtonType.OK);
				alert.showAndWait();
			}

		}
    		
    }
    
    @FXML
    void onCancel(ActionEvent event) {
    	idImageURL.setText("");
    	image = null;
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
    }
    
    @FXML
    void onFileDialog(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	 fileChooser.setTitle("Open Resource File");
    	 fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
    	         new ExtensionFilter("All Files", "*.*"));
    	 Window parentStage = ((Node) event.getSource()).getScene().getWindow();
    	 File selectedFile = fileChooser.showOpenDialog(parentStage);
    	 //Getting the URI for the local file
    	 if (selectedFile != null) {
    		 Path path = FileSystems.getDefault().getPath(
    				 selectedFile.getAbsolutePath());
    		 idImageURL.setText(path.toUri().toString());
    	 }
    }

    @FXML
    void onImagePreview(ActionEvent event) {
    	
		if(imageCheck(idImageURL.getText())) {
			image = new Image(idImageURL.getText(), false);
			
			if(!image.isError()) {
				imgPreview.setImage(image);
			}
			else {
				Alert alert = new Alert(AlertType.ERROR, "Error loading the insert URL", ButtonType.OK);
				alert.showAndWait();
			}
			
		}
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert idImageURL != null : "fx:id=\"idImageURL\" was not injected: check your FXML file 'ImagePicker.fxml'.";
        assert imgPreview != null : "fx:id=\"imgPreview\" was not injected: check your FXML file 'ImagePicker.fxml'.";
        Image image = new Image("file:./images/noImage.jpg", true);
        imgPreview.setImage(image);

    }
    
    /*
     * 
     */
    Boolean imageCheck(String imgString) {
    	URL imageUrl;
    	
    	if(imgString!=null && !imgString.equals("")) {
    		try {
    			imageUrl = new URL(imgString);
    			return true;
    		} 
    		catch (MalformedURLException e) {
    			Alert alert = new Alert(AlertType.ERROR, "Malformed URL Exception. " + e.getMessage(), ButtonType.OK);
    			alert.showAndWait();
    			return false;
    		}
    		catch (IllegalArgumentException e) {
    			Alert alert = new Alert(AlertType.ERROR, "Illegal Argument Exception. " + e.getMessage(), ButtonType.OK);
    			alert.showAndWait();
    			return false;
    		} 
    		
    	}
    	else {
    		Alert alert = new Alert(AlertType.ERROR, "No image added. Insert an image", ButtonType.OK);
			alert.showAndWait();
			return false;
    	}
		
		
    }
    
    /**
     * 
     * @return image path
     */
    String getFileImagePath(){
    	return idImageURL.getText();
    }
    
    /**
     * 
     * @return image selected by user
     */
    Image getImage(){
    	return image;
    }
}