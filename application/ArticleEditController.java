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
	
	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	
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

    @FXML
    private MFXButton back;

    @FXML
    private MFXToggleButton bodytoAbstract;

    @FXML
    private ImageView editImage;

    @FXML
    private TextField editSubtitle;

    @FXML
    private HTMLEditor editTextAbstract;

    @FXML
    private HTMLEditor editTextBody;
    
    @FXML
    private TextArea htmlAbstract;
    
    @FXML
    private TextArea htmlBody;

    @FXML
    private TextField editTitle;

    @FXML
    private MFXButton saveToFile;

    @FXML
    private MFXButton sendAndBack;

    @FXML
    private MFXToggleButton textToHtml;
    
    @FXML
    private MFXComboBox<Categories> categories;
    
    boolean isSaved;
    boolean editMode;
    String abstractText;
    String body;

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
    	if(this.textToHtml.isSelected()) {
    		if(this.bodytoAbstract.isSelected()) {
    			
    			this.body = this.editTextBody.getHtmlText();
    			
    			this.htmlBody.setVisible(true);
    			this.editTextBody.setVisible(false);
    			this.htmlBody.setText(this.body);
    		}
    		else
    		{
    			this.abstractText = this.editTextAbstract.getHtmlText();
    			
    			this.editTextAbstract.setVisible(false);
    			this.htmlAbstract.setVisible(true);
    			this.htmlAbstract.setText(this.abstractText);
    		}
    		this.textToHtml.setText("HTML");
    	}
    	else
    	{
    		if(this.bodytoAbstract.isSelected()) {
    			
    			this.body = this.htmlBody.getText();
    			
    			this.editTextBody.setHtmlText(this.body);
    			this.htmlBody.setVisible(false);
    			this.editTextBody.setVisible(true);
    		}
    		else
    		{
    			this.abstractText = this.htmlAbstract.getText();
    			
    			this.editTextAbstract.setHtmlText(this.abstractText);
    			this.htmlAbstract.setVisible(false);
    			this.editTextAbstract.setVisible(true);
    		}
    		
    		this.textToHtml.setText("Text");
    	}
    }
    
    @FXML
    void onBodyAbstract(MouseEvent event) {
    	if(this.bodytoAbstract.isSelected()) {
    		if(this.textToHtml.isSelected()) {
    			
    			this.abstractText = this.htmlAbstract.getText();
    			
    			this.htmlBody.setVisible(true);
    			this.editTextAbstract.setVisible(false);
        		this.editTextBody.setVisible(false);
        		this.htmlAbstract.setVisible(false);
        		this.htmlBody.setText(this.body);
    		}
    		else
    		{
    			this.abstractText = this.editTextAbstract.getHtmlText();
    			
    			this.editTextBody.setVisible(true);
    			this.editTextAbstract.setVisible(false);
    			this.htmlBody.setVisible(false);
    			this.htmlAbstract.setVisible(false);
        		this.editTextBody.setHtmlText(this.body);
    		}

    		this.bodytoAbstract.setText("Body");
    	}
    	else
    	{
    		if(this.textToHtml.isSelected() ) {
    			
    			this.body = this.htmlBody.getText();
    					
    			this.htmlAbstract.setVisible(true);
    			this.editTextAbstract.setVisible(false);
        		this.editTextBody.setVisible(false);
        		this.htmlBody.setVisible(false);
        		this.htmlAbstract.setText(this.abstractText);
    		}
    		else
    		{
    			this.body = this.editTextBody.getHtmlText();
    			
    			this.editTextAbstract.setVisible(true);
    			this.htmlBody.setVisible(false);
        		this.editTextBody.setVisible(false);
        		this.htmlAbstract.setVisible(false);
        		this.editTextAbstract.setHtmlText(this.abstractText);
    		}
    		this.bodytoAbstract.setText("Abstract");
    	}
    }
    
    
    @FXML
    void onHtmlAbstractKeyReleased(KeyEvent event) {

    	String character = event.getText();
    	if(Character.isLetter(character.charAt(0)) || Character.isDigit(character.charAt(0))) {
    		this.abstractText = this.htmlAbstract.getText();
    	}

    }

    @FXML
    void onHtmlBodyKeyReleased(KeyEvent event) {
    	String character = event.getText();
    	if(Character.isLetter(character.charAt(0)) || Character.isDigit(character.charAt(0))) {
    		this.body = this.htmlBody.getText();
    	}
    }
    
    @FXML
    void onTextBodyKeyReleased(KeyEvent event) {
    	String character = event.getText();
    	if(Character.isLetter(character.charAt(0)) || Character.isDigit(character.charAt(0))) {
    		this.body = this.editTextBody.getHtmlText();
    	}
    }

    @FXML
    void onAbstractTextKeyReleased(KeyEvent event) {
    	String character = event.getText();
    	if(Character.isLetter(character.charAt(0)) || Character.isDigit(character.charAt(0))) {
    		this.abstractText = this.editTextAbstract.getHtmlText();
    	}
    }

    

	@FXML
	void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 1) {
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
					this.editImage.setImage(image);
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
		String titleText = this.editTitle.getText();
		Categories category = this.categories.getSelectedItem();
		if (titleText == null || category == null || 
				titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article!! Title and categoy are mandatory", ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		try {
			if(this.editMode) {
				modifyArticle();
				this.editingArticle.commit();
				this.connection.saveArticle(this.editingArticle.getArticleOriginal());
			}
			else
			{
				Article article = new Article(titleText, this.usr.getIdUser(), category.toString(), this.editTextAbstract.getHtmlText());
				article.setSubtitle(this.editSubtitle.getText());
				article.setImageData(this.editImage.getImage());
				article.setBodyText(this.editTextBody.getHtmlText());
				this.connection.saveArticle(article);				
			}
			this.isSaved = true;
		} catch (ServerCommunicationError e) {
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	/*
	 * Function to avoid duplicate code. It actually modify the article
	 */
	void modifyArticle() {
		this.editingArticle.titleProperty().set(this.editTitle.getText());
		this.editingArticle.subtitleProperty().set(this.editSubtitle.getText());
		this.editingArticle.abstractTextProperty().set(this.abstractText);
		this.editingArticle.setCategory(this.categories.getSelectedItem());
		this.editingArticle.bodyTextProperty().set(this.body);
		this.editingArticle.setImage(this.editImage.getImage());		
	}
	
	/**
	 * This method is used to set the connection manager which is
	 * needed to save a news 
	 * @param connection connection manager
	 */
	void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
		ObservableList<Categories> list = FXCollections.observableArrayList();
		list.add(Categories.ALL);
		list.add(Categories.ECONOMY);
		list.add(Categories.NATIONAL);
		list.add(Categories.SPORTS);
		list.add(Categories.TECHNOLOGY);
			
		this.categories.setItems(list);
		this.categories.selectFirst();
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
			this.sendAndBack.setDisable(true);
		}
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
		this.editingArticle = (article != null) ? new ArticleEditModel(article) : new ArticleEditModel(usr);
		//TODO update UI
		if(article != null) {
			this.editTitle.setText(article.getTitle());
			this.editSubtitle.setText(article.getSubtitle());
			this.editTextAbstract.setHtmlText(article.getAbstractText());
			this.editTextBody.setHtmlText(article.getBodyText());
			this.categories.selectItem(Categories.valueOf(article.getCategory().toUpperCase()));
			
			if(article.getImageData() != null) {
				this.editImage.setImage(article.getImageData());
			}
			else {
				this.editImage.setImage(newsReaderModel.getNoImage());	
			}
			
			this.editMode = true;
		}
		else {
			this.editImage.setImage(newsReaderModel.getNoImage());	
		}
	}
	
	/**
	 * Save an article to a file in a json format
	 * Article must have a title
	 */
	private void write() {
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
