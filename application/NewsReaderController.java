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
	
	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	
	private User usr;

    @FXML
    private MenuItem delete;

    @FXML
    private MenuItem edit;

    @FXML
    private MenuItem exit;

    @FXML
    private MenuItem loadArticle;

    @FXML
    private MenuItem newArticle;

    @FXML
    private MenuItem toLogin;
    
    @FXML
    private ListView<Article> listOfArticles;
    
    @FXML
    private MFXButton readMore;

    @FXML
    private ImageView newsReaderImage;

    @FXML
    private TextArea newsReaderText;
    
    @FXML
    private MFXComboBox<Categories> categories;
    
    private FilteredList<Article> filteredData;

    /*
     * Function to delete a selected article. Could be done only if the selected article belong to the logged user
     */
    @FXML
    void onDelete(ActionEvent event) {
    	if(getUsr() != null && listOfArticles.getSelectionModel().getSelectedItem().getIdUser() == getUsr().getIdUser()) {
    		newsReaderModel.deleteArticle(listOfArticles.getSelectionModel().getSelectedItem());
    	}
    	else {
    		Alert alert = new Alert(AlertType.ERROR, "Not authorized user! Can't be deleted", ButtonType.OK);
			alert.showAndWait();
    	}
    }

    @FXML
    void onEdit(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
		
		Stage stage = openScene(loader, this.readMore.getScene(), AppScenes.EDITOR.getFxmlFile());
		stage.setTitle("edit Article");
		
		ArticleEditController controller = loader.<ArticleEditController>getController();

		controller.setConnectionMannager(this.newsReaderModel.getConnectionManager());
		controller.setUsr(usr);
		
		Article selected = this.listOfArticles.getSelectionModel().selectedItemProperty().get();
		
		if(selected != null) {
			Article articleFromServer = this.newsReaderModel.getFullArticle(selected.getIdArticle());
			
			controller.setArticle(articleFromServer);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.showAndWait();

			if (controller.getIsSaved()){ 
				this.getData();
			}
		}
		else
		{
			Alert alert = new Alert(AlertType.CONFIRMATION, "No article selected" + usr.getIdUser(), ButtonType.OK);
			alert.showAndWait();
		}
			
    }

    @FXML
    void onExit(ActionEvent event) {
    	Platform.exit();
    }

    @FXML
    void onLoadArticleFromFile(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	 fileChooser.setTitle("Open Resource File");
    	 fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("News Files", "*.news"),
    	         new ExtensionFilter("All Files", "*.*"));
    	 Window parentStage = this.readMore.getScene().getWindow();
    	 File selectedFile = fileChooser.showOpenDialog(parentStage);
    	 //Getting the URI for the local file
    	 if (selectedFile != null) {
    		 Path path = FileSystems.getDefault().getPath(selectedFile.getAbsolutePath());

			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
			
			Stage stage = openScene(loader, this.readMore.getScene(), AppScenes.EDITOR.getFxmlFile());
			stage.setTitle("edit Article from file");
			
			ArticleEditController controller = loader.<ArticleEditController>getController();

			controller.setConnectionMannager(this.newsReaderModel.getConnectionManager());
			
			controller.setUsr(usr);
			
			try {
				Article articleFromFile = JsonArticle.jsonToArticle(JsonArticle.readFile(path.toString()));
				
				if(articleFromFile != null)
				{
					controller.setArticle(articleFromFile);
					stage.initModality(Modality.WINDOW_MODAL);
					stage.showAndWait();

					if (controller.getIsSaved()){ 
						this.getData();
					}
				}
				else
				{
					Alert alert = new Alert(AlertType.CONFIRMATION, "No article selected", ButtonType.OK);
					alert.showAndWait();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		 
    	 }
    }


    @FXML
    void onNewArticle(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
		
		Stage stage = openScene(loader, this.readMore.getScene(), AppScenes.EDITOR.getFxmlFile());
		stage.setTitle("Create a new article");
		
		ArticleEditController controller = loader.<ArticleEditController>getController();

		controller.setConnectionMannager(this.newsReaderModel.getConnectionManager());
		controller.setUsr(usr);
		controller.setArticle(null);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.showAndWait();

		if (controller.getIsSaved()){ 
			this.getData();
		}
    }

    @FXML
    void onReadMore(ActionEvent event) {

		FXMLLoader loader = new FXMLLoader (getClass().getResource(AppScenes.NEWS_DETAILS.getFxmlFile()));
				
		Stage stage = openScene(loader, this.readMore.getScene(), AppScenes.NEWS_DETAILS.getFxmlFile());
		stage.setTitle("Article details");
		
		ArticleDetailsController controller = loader.<ArticleDetailsController>getController();
		Article articleFromServer = this.newsReaderModel.getFullArticle(listOfArticles.getSelectionModel().getSelectedItem().getIdArticle());
		controller.initData(articleFromServer, getUsr());
		
		stage.show();    	

    }

    @FXML
    void onToLogin(ActionEvent event) {
			
		FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.LOGIN.getFxmlFile()));
		
		Stage stage = openScene(loader, this.readMore.getScene(), AppScenes.LOGIN.getFxmlFile());
		stage.setTitle("Login");
		
		LoginController controller = loader.<LoginController>getController();
		controller.setConnectionManager(this.newsReaderModel.getConnectionManager());
		stage.initModality(Modality.WINDOW_MODAL);
		stage.showAndWait();

		if (controller.getLoggedUsr() != null) { 
			this.setUsr(controller.getLoggedUsr());
			Alert alert = new Alert(AlertType.CONFIRMATION, "Login successful", ButtonType.OK);
			alert.showAndWait();
		}
    }
    
    /*
     * Function to avoid duplicate code. Used to create a stage
     */
    Stage openScene(FXMLLoader loader, Scene parentScene, String name) {
    	
    	Stage stage = null;
    	try {
			Pane root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Window parentStage = parentScene.getWindow();
			stage = new Stage();
			stage.initOwner(parentStage);
			stage.setScene(scene);
			if(name.split(".").length > 0) {
				System.out.println(name.split(".")[0]);
			}
			
	    } catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return stage;
    	
    }
    

	public NewsReaderController() {
		newsReaderModel.setDummyData(false);		
	}
	

	private void getData() {
		newsReaderModel.retrieveData();
		
    	filteredData = new FilteredList<Article>(newsReaderModel.getArticles(), article -> true);
    	
    	this.listOfArticles.setItems(filteredData);
	}

	/**
	 * @return the usr
	 */
	User getUsr() {
		return usr;
	}
	
	/*
	 * Function to unlock buttons and element for a registered user who logged in
	 */
	void areElementsDisabledForLoggedUser(Boolean areUnlocked) {
		edit.setDisable(areUnlocked);
		delete.setDisable(areUnlocked);
	}

	void setConnectionManager (ConnectionManager connection){
		this.newsReaderModel.setDummyData(false); //System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
		
		ObservableList<Categories> list = FXCollections.observableArrayList();
		list.add(Categories.ALL);
		list.add(Categories.ECONOMY);
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
		
		readMore.setDisable(true);
		this.areElementsDisabledForLoggedUser(true);
		
		this.listOfArticles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Article>() {
			@Override
			/**
			 * When the selected element is changed this event handler is called
			 */
			public void changed(ObservableValue<? extends Article> observable, Article oldValue, Article newArticle) {
				if (newArticle != null){
					
					if(getUsr() != null && newArticle.getIdUser() == getUsr().getIdUser()) {
						areElementsDisabledForLoggedUser(false);
					}
					else {
						areElementsDisabledForLoggedUser(true);
					}
					
					readMore.setDisable(false);
					
					newsReaderText.setText(newArticle.getAbstractText());
					
					if(newArticle.getImageData() != null) {
						newsReaderImage.setImage(newArticle.getImageData());
					}
					else {
						newsReaderImage.setImage(newsReaderModel.getNoImage());	
					}
					
				}
				else { //Nothing selected
					readMore.setDisable(true);
					newsReaderText.setText("");
					newsReaderImage.setImage(null);
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
	}


}
