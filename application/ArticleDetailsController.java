package application;

import application.news.Article;
import application.news.User;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class ArticleDetailsController {
	/**
	 * Registered user
	 */
    private User usr;
    
    /**
     * Article to be shown
     */
    private Article article;
    
    @FXML // fx:id="detailsTitle"
    private Label detailsTitle; // Value injected by FXMLLoader
    @FXML // fx:id="detailsSubtitle"
    private Label detailsSubtitle; // Value injected by FXMLLoader
    @FXML // fx:id="detailsCategory"
    private Label detailsCategory; // Value injected by FXMLLoader
    @FXML // fx:id="detailsImage"
    private ImageView detailsImage; // Value injected by FXMLLoader
    
    @FXML // fx:id="bodyAbstractLable"
    private Label bodyAbstractLable; // Value injected by FXMLLoader 
    @FXML // fx:id="detailsText"
    private TextArea detailsText; // Value injected by FXMLLoader 
    @FXML // fx:id="bodyToAbstract"
    private MFXToggleButton bodyToAbstract; // Value injected by FXMLLoader

    void initialize() {
    	assert detailsTitle != null : "fx:id=\"detailsTitle\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
        assert detailsSubtitle != null : "fx:id=\"detailsSubtitle\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
        assert detailsCategory != null : "fx:id=\"detailsCategory\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
    }
    
    void initData(Article article, User user) {
		setUsr(user);
    	setArticle(article);
    	setVariables();
    }

	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		if (usr == null) {
			return; //Not logged user
		}
	}

	/**
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.article = article;
	}
	
	/**
	 * Set the variables shown in the page
	 */
	void setVariables() {
		
		detailsCategory.setText(article.getCategory());
		detailsSubtitle.setText(article.getSubtitle());
		detailsTitle.setText(article.getTitle());

		if(article.getImageData() != null) {
			detailsImage.setImage(article.getImageData());
		}
		else {
			Image image = new Image("./NewsArticleProj/images/noImage.jpg");
			detailsImage.setImage(image);
		}
		
		bodyAbstractLable.setText("Abstract");
		detailsText.setText(article.getAbstractText());
		
	}
	
	@FXML
	void onBackBtn(ActionEvent event) {		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	stage.close();
	}

	@FXML
	void onChangeBodyAbstract() {	
		if(bodyToAbstract.isSelected()) {
			bodyAbstractLable.setText("Body");
			detailsText.setText(article.getBodyText());
		}
		else {
			bodyAbstractLable.setText("Abstract");
			detailsText.setText(article.getAbstractText());
		}
		
	}
	
}
