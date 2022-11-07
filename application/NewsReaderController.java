package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Predicate;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedArticle;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import serverConection.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NewsReaderController {
	
//	The username for your group is: DEV_TEAM_03
//	Its password: 123603@3
	
	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	
	private User usr;

	//TODO add attributes and methods as needed
	@FXML
    private MenuItem AllArticles;

    @FXML
    private MenuItem Delete;

    @FXML
    private MenuItem Economy;

    @FXML
    private MenuItem Edit;

    @FXML
    private MenuItem Exit;

    @FXML
    private MenuItem LoadArticle;

    @FXML
    private MenuItem National;

    @FXML
    private MenuItem NewArticle;

    @FXML
    private MenuItem Sports;

    @FXML
    private MenuItem Technology;

    @FXML
    private MenuItem ToLogin;
    
    @FXML
    private ListView<Article> ListofArticles;
    
    @FXML
    private MFXButton ReadMore;

    @FXML
    private ImageView NewsReaderImage;

    @FXML
    private TextArea NewsReaderText;
    
    @FXML
    private MFXComboBox<Categories> categories;
    
    private FilteredList<Article> filteredData;

    @FXML
    void onAllArticles(ActionEvent event) {

    }

    @FXML
    void onDelete(ActionEvent event) {
    	
    	System.out.print(usr);
    	
    	//Controls also for the belonging of the article to the user?
    	//Yes, needs to edit or delete only articles createdby him (articles with editId == user.id)
//    	if(usr != null) {
    		newsReaderModel.deleteArticle(ListofArticles.getSelectionModel().getSelectedItem());
//    	}
//    	else {
//    		System.out.println("Not authorized user!");
//    	}
    	

    }

    @FXML
    void onEconomy(ActionEvent event) {

    }

    @FXML
    void onEdit(ActionEvent event) {

    }

    @FXML
    void onExit(ActionEvent event) {
    	Platform.exit();
    }

    @FXML
    void onLoadArticleFromFile(ActionEvent event) {

    }

    @FXML
    void onNational(ActionEvent event) {

    }

    @FXML
    void onNewArticle(ActionEvent event) {
    	Scene parentScene = this.ReadMore.getScene();
		FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(getClass().getResource("ArticleEdit.fxml"));
			Pane root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Window parentStage = parentScene.getWindow();
			Stage stage = new Stage();
			stage.initOwner(parentStage);
			stage.setScene(scene);
			stage.setTitle("Create Article");

			ArticleEditController controller = loader.<ArticleEditController>getController();

			Properties prop = new Properties();
			prop.setProperty(ConnectionManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
			prop.setProperty(ConnectionManager.ATTR_REQUIRE_SELF_CERT, "TRUE");
			
			ConnectionManager connection = new ConnectionManager(prop);

			controller.setConnectionMannager(connection);
			//controller.setUsr(usr);
			//controller.setArticle(null);
			//Uncomment next sentence if you want clear change when user close the window
			//stage.setOnCloseRequest(ev ->controller.exitEdit(ev));
			//user response is required before continuing with the program
			stage.initModality(Modality.WINDOW_MODAL);
			stage.showAndWait();

//			if (controller.isSaved()){ 
//				this.getData();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    void onReadMore(ActionEvent event) {
    	
    	try {
    		FXMLLoader loader = new FXMLLoader (getClass().getResource(
			AppScenes.NEWS_DETAILS.getFxmlFile()));
    		Parent root1 = (Parent) loader.load();
    		Stage stage = new Stage();
    		stage.setScene(new Scene(root1));
    		ArticleDetailsController controller = loader.<ArticleDetailsController>getController();
   
    		controller.initData(ListofArticles.getSelectionModel().getSelectedItem(), getUsr());
    		
    		stage.show();
    	}
    	catch(Exception e) {
    		System.out.println(e.toString());
    	}
    	

    }

    @FXML
    void onSports(ActionEvent event) {

    }

    @FXML
    void onTechnology(ActionEvent event) {

    }

    @FXML
    void onToLogin(ActionEvent event) {
    	try {
    		FXMLLoader loader = new FXMLLoader (getClass().getResource(
					AppScenes.LOGIN.getFxmlFile()));
    		Parent root1 = (Parent) loader.load();
    		Stage stage = new Stage();
    		stage.setScene(new Scene(root1));
    		LoginController controller = loader.<LoginController>getController();
    		
			Properties prop = new Properties();
			prop.setProperty(ConnectionManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
			prop.setProperty(ConnectionManager.ATTR_REQUIRE_SELF_CERT, "TRUE");
			
			ConnectionManager connection = new ConnectionManager(prop);

			connection.setAnonymousAPIKey("ANON03_336");
			controller.setConnectionManager(connection);	
    		stage.show();
    	}
    	catch(Exception e) {
    		System.out.println(e.toString());
    	}
    }


	public NewsReaderController() {
		//TODO
		//Uncomment next sentence to use data from server instead dummy data
		newsReaderModel.setDummyData(false);
		//Get text Label
		
	}
	

	private void getData() {
		//TODO retrieve data and update UI
		//The method newsReaderModel.retrieveData() can be used to retrieve data  
		newsReaderModel.retrieveData();
		
    	filteredData = new FilteredList<Article>(newsReaderModel.getArticles(), article -> true);
    	
    	this.ListofArticles.setItems(filteredData);
	}

	/**
	 * @return the usr
	 */
	User getUsr() {
		return usr;
	}

	void setConnectionManager (ConnectionManager connection){
		this.newsReaderModel.setDummyData(false); //System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
		
		ObservableList<Categories> list = FXCollections.observableArrayList();
		list.add(Categories.ALL);
		list.add(Categories.ECONOMY);
		list.add(Categories.INTERNATIONAL);
		list.add(Categories.NATIONAL);
		list.add(Categories.SPORTS);
		list.add(Categories.TECHNOLOGY);
			
		this.categories.setItems(list);
		this.categories.selectFirst();
		
		this.categories.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Categories>() {

			@Override
			public void changed(ObservableValue<? extends Categories> observable, Categories oldValue, Categories newValue) {
				
				categories.selectItem(newValue);
				if(newValue.equals(Categories.SPORTS)) {
					filteredData.setPredicate(a -> a.getCategory().equalsIgnoreCase(Categories.SPORTS.toString()));
				}
				else if(newValue.equals(Categories.ECONOMY))
				{
					filteredData.setPredicate(a -> a.getCategory().equalsIgnoreCase(Categories.ECONOMY.toString()));
				}
				else if(newValue.equals(Categories.INTERNATIONAL))
				{
					filteredData.setPredicate(a -> a.getCategory().equalsIgnoreCase(Categories.INTERNATIONAL.toString()));
				}
				else if(newValue.equals(Categories.NATIONAL))
				{
					filteredData.setPredicate(a -> a.getCategory().equalsIgnoreCase(Categories.NATIONAL.toString()));
				}
				else if(newValue.equals(Categories.TECHNOLOGY))
				{
					filteredData.setPredicate(a -> a.getCategory().equalsIgnoreCase(Categories.TECHNOLOGY.toString()));
				}
				else
				{
					filteredData.setPredicate(a -> true);
				}
				
			}
			
		});
		
		ReadMore.setDisable(false);
		this.ListofArticles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Article>() {
			@Override
			/**
			 * When the selected element is changed this event handler is called
			 */
			public void changed(ObservableValue<? extends Article> observable, Article oldValue, Article newValue) {
				if (newValue != null){
					ReadMore.setDisable(false);
					NewsReaderText.setText(newValue.getAbstractText());
					
					if(newValue.getImageData() != null) {
						NewsReaderImage.setImage(newValue.getImageData());
					}
					else {
						NewsReaderImage.setImage(newsReaderModel.getNoImage());	
					}
					
				}
				else { //Nothing selected
					ReadMore.setDisable(true);
					NewsReaderText.setText("");
					NewsReaderImage.setImage(null);
				}
				
			}
	    
	     });
	}
	
	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		
		this.usr = usr;
		//Reload articles
		this.getData();
		//TODO Update UI
	}


}
