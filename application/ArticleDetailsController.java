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
	//TODO add attributes and methods as needed
	/**
	 * Registered user
	 */
    private User usr;
    
    /**
     * Article to be shown
     */
    private Article article;
    
    
    
//    private NewsReaderModel newsReaderModel = new NewsReaderModel();
    
    
    
    @FXML // fx:id="DetailsTitle"
    private Label DetailsTitle; // Value injected by FXMLLoader
    @FXML // fx:id="DetailsSubtitle"
    private Label DetailsSubtitle; // Value injected by FXMLLoader
    @FXML // fx:id="DetailsCategory"
    private Label DetailsCategory; // Value injected by FXMLLoader
    @FXML // fx:id="DetailsImage"
    private ImageView DetailsImage; // Value injected by FXMLLoader
    
    @FXML // fx:id="BodyAbstractLable"
    private Label BodyAbstractLable; // Value injected by FXMLLoader 
    @FXML // fx:id="DetailsText"
    private TextArea DetailsText; // Value injected by FXMLLoader 
    @FXML // fx:id="BodyToAbstract"
    private MFXToggleButton BodyToAbstract; // Value injected by FXMLLoader

    void initialize() {
    	assert DetailsTitle != null : "fx:id=\"DetailsTitle\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
        assert DetailsSubtitle != null : "fx:id=\"DetailsSubtitle\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
        assert DetailsCategory != null : "fx:id=\"DetailsCategory\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
    }
    
    void initData(Article article, User user) {
    	
		setUsr(user);
    	setArticle(article);
    	setVariables();
    	
    	//Debug
    	System.out.print(this.article.getAbstractText());
    	System.out.print("\n");
    	System.out.print(this.article.getCategory());
    	System.out.print("\n");
    	System.out.print(this.article.getBodyText());
    	System.out.print("\n");
    	System.out.print(this.article.getIdArticle());
    	System.out.print("\n");
    	System.out.print(this.article.getImageData());
    	System.out.print("\n");
    	System.out.print(this.usr);
    	System.out.print("\n");
    	
//    	System.out.print(newsReaderModel.getFullArticle(this.article.getIdArticle()));
    	
    }

	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		if (usr == null) {
			return; //Not logged user
		}
		//TODO Update UI information
	}

	/**
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.article = article;
		//TODO complete this method
	}
	
	/**
	 * Set the variables shown in the page
	 */
	void setVariables() {
		
		DetailsCategory.setText(article.getCategory());
		DetailsSubtitle.setText(article.getSubtitle());
		DetailsTitle.setText(article.getTitle());

		if(article.getImageData() != null) {
			DetailsImage.setImage(article.getImageData());
		}
		else {
			Image image = new Image("./NewsArticleProj/images/noImage.jpg");
			DetailsImage.setImage(image);
		}
		
		BodyAbstractLable.setText("Abstract");
		DetailsText.setText(article.getAbstractText());
		
	}
	
	@FXML
	void onBackBtn(ActionEvent event) {		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	stage.close();
	}

	@FXML
	void onChangeBodyAbstract() {	
		if(BodyToAbstract.isSelected()) {
			BodyAbstractLable.setText("Body");
			DetailsText.setText(article.getBodyText());
		}
		else {
			BodyAbstractLable.setText("Abstract");
			DetailsText.setText(article.getAbstractText());
		}
		
	}
	
}
