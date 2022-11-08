/**
 * 
 */
package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.json.JsonObject;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.scene.control.TextField;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;


public class ArticleEditController {
	/**
	 * Connection used to send article to server after editing process
	 */
    private ConnectionManager connection;
    
    /**
     * Instance that represent an article when it is editing 
     */
	private ArticleEditModel editingArticle;
	/**
	 * User whose is editing the article 
	 */
	private User usr;
	//TODO add attributes and methods as needed

    @FXML
    private MFXButton Back;

    @FXML
    private MFXToggleButton BodytoAbstract;

    @FXML
    private ImageView EditImage;

    @FXML
    private TextField EditSubtitle;

    @FXML
    private HTMLEditor EditTextAbstract;

    @FXML
    private HTMLEditor EditTextBody;
    
    @FXML
    private TextArea HtmlText;

    @FXML
    private TextField EditTitle;

    @FXML
    private MFXButton SaveToFile;

    @FXML
    private MFXButton SendAndBack;

    @FXML
    private MFXToggleButton TextToHtml;
    
    @FXML
    private MFXComboBox<Categories> categories;
    
    boolean isSaved;
    boolean editMode;

    @FXML
    void onBack(ActionEvent event) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	stage.close();
    }

    @FXML
    void onSaveToFile(ActionEvent event) {
		modifyArticle();
		this.write();
		Alert alert = new Alert(AlertType.CONFIRMATION, "Data successfully saved to file", ButtonType.OK);
		alert.showAndWait();
    }

    @FXML
    void onSendAndBack(ActionEvent event) {
    	if(this.send()) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Added article successful", ButtonType.OK);
			alert.showAndWait();
	    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    	stage.close();
    	}
    }
    
    @FXML
    void onTextHtml(MouseEvent event) {
    	if(this.TextToHtml.isSelected()) {
    		this.HtmlText.setVisible(true);
    		this.HtmlText.setText(this.BodytoAbstract.isSelected() 
    										? this.EditTextBody.getHtmlText() : this.EditTextAbstract.getHtmlText());
    	}
    	else
    	{
    		this.HtmlText.setVisible(false);
    		if(this.BodytoAbstract.isSelected()) {
    			this.EditTextBody.setHtmlText(this.HtmlText.getText());
    		}
    		else
    		{
    			this.EditTextAbstract.setHtmlText(this.HtmlText.getText());
    		}
    	}
    	//TODO, set text
    }
    
    @FXML
    void onBodyAbstract(MouseEvent event) {
    	if(this.BodytoAbstract.isSelected()) {
    		this.EditTextAbstract.setVisible(false);
    		this.EditTextBody.setVisible(true);
    		this.BodytoAbstract.setText("Body");
    	}
    	else
    	{
    		this.EditTextAbstract.setVisible(true);
    		this.EditTextBody.setVisible(false);
    		this.BodytoAbstract.setText("Abstract");
    	}
    }


	@FXML
	void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;
			try {
				loader = new FXMLLoader(getClass().getResource(AppScenes.IMAGE_PICKER.getFxmlFile()));
				Pane root = loader.load();
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Window parentStage = parentScene.getWindow();
				Stage stage = new Stage();
				stage.initOwner(parentStage);
				stage.setScene(scene);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.showAndWait();
				ImagePickerController controller = loader.<ImagePickerController>getController();
				Image image = controller.getImage();
				if (image != null) {
					//editingArticle.setImage(image);
					//TODO Update image on UI
					this.EditImage.setImage(image);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	/**
	 * Send and article to server,
	 * Title and category must be defined and category must be different to ALL
	 * @return true if only if article was been correctly send
	 */
	private boolean send() {
		String titleText = this.EditTitle.getText(); // TODO Get article title
		Categories category = this.categories.getSelectedItem(); //TODO Get article category
		if (titleText == null || category == null || 
				titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article!! Title and categoy are mandatory", ButtonType.OK);
			alert.showAndWait();
			return false;
		}
//TODO prepare and send using connection.saveArticle( ...)
		
//		System.out.println(
//				JsonArticle.imagetToString(this.EditImage.getImage()));
		try {
//			System.out.println(this.EditImage.getImage().getUrl());
			
			//TODO check if it is edit. Save to file :)
			if(this.editMode) {
				modifyArticle();
				this.editingArticle.commit();
				this.connection.saveArticle(this.editingArticle.getArticleOriginal());
			}
			else
			{
				this.connection.saveArticle(new Article(titleText, this.usr.getIdUser(), category.toString(), this.EditTextAbstract.getHtmlText()));				
			}
			this.isSaved = true;
		} catch (ServerCommunicationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	/*
	 * Function to avoid duplicate code. It actually modify the article
	 */
	void modifyArticle() {
		this.editingArticle.titleProperty().set(this.EditTitle.getText());
		this.editingArticle.subtitleProperty().set(this.EditSubtitle.getText());
		this.editingArticle.abstractTextProperty().set(this.EditTextAbstract.getHtmlText());
		this.editingArticle.setCategory(this.categories.getSelectedItem());
		this.editingArticle.bodyTextProperty().set(this.EditTextBody.getHtmlText());
		this.editingArticle.setImage(this.EditImage.getImage());		
	}
	
	/**
	 * This method is used to set the connection manager which is
	 * needed to save a news 
	 * @param connection connection manager
	 */
	void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
		//TODO enable send and back button
		ObservableList<Categories> list = FXCollections.observableArrayList();
		list.add(Categories.ALL);
		list.add(Categories.ECONOMY);
		list.add(Categories.INTERNATIONAL);
		list.add(Categories.NATIONAL);
		list.add(Categories.SPORTS);
		list.add(Categories.TECHNOLOGY);
			
		this.categories.setItems(list);
		this.categories.selectFirst();
		
//		this.EditText.setOnKeyReleased(new EventHandler<KeyEvent>() {
//
//			@Override
//			public void handle(KeyEvent event) {
//				// TODO Auto-generated method stub
//				System.out.println(EditText.getHtmlText());
//		    	if(BodytoAbstract.isSelected()) {
//		    		abstractText = EditText.getHtmlText();
//		    	}
//		    	else
//		    	{
//		    		body = EditText.getHtmlText();
//		    	}
//			}
//			
//		});
	}

	/**
	 * 
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		if(usr != null)
		{
			this.usr = usr;
		}
		else
		{
			this.SendAndBack.setDisable(true);
		}

		//TODO Update UI and controls 
		
	}

	/**
	 * Get the article without changes since last commit 
	 * @return article without changes since last commit
	 */
	Article getArticle() {
		Article result = null;
		if (this.editingArticle != null) {
			result = this.editingArticle.getArticleOriginal();
		}
		return result;
	}

	/**
	 * PRE: User must be set
	 * 
	 * @param article
	 *            the article to set
	 */
	void setArticle(Article article) {
		
		//Check of the User??
		
		this.editingArticle = (article != null) ? new ArticleEditModel(article) : new ArticleEditModel(usr);
		//TODO update UI
		if(article != null) {
			this.EditTitle.setText(article.getTitle());
			this.EditSubtitle.setText(article.getSubtitle());
			this.EditTextAbstract.setHtmlText(article.getAbstractText());
			this.EditTextBody.setHtmlText(article.getBodyText());
			this.categories.selectItem(Categories.valueOf(article.getCategory().toUpperCase()));
			this.EditImage.setImage(article.getImageData());
			
			this.editMode = true;
		}
	}
	
	/**
	 * Save an article to a file in a json format
	 * Article must have a title
	 */
	private void write() {
		//TODO Consolidate all changes	
		this.editingArticle.commit();
		//Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?","");
		String fileName ="saveNews//"+name+".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());
		  try (FileWriter file = new FileWriter(fileName)) {
	            file.write(data.toString());
	            file.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
	public boolean getIsSaved(){
		return this.isSaved;
	}
}
