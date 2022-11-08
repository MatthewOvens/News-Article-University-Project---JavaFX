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
    private MenuItem Delete;

    @FXML
    private MenuItem Edit;

    @FXML
    private MenuItem Exit;

    @FXML
    private MenuItem LoadArticle;

    @FXML
    private MenuItem NewArticle;

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
    void onEdit(ActionEvent event) {
//    	Scene parentScene = this.ReadMore.getScene();
//		FXMLLoader loader = null;
//		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
//			Pane root = loader.load();
//			Scene scene = new Scene(root);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			Window parentStage = parentScene.getWindow();
//			Stage stage = new Stage();
//			stage.initOwner(parentStage);
//			stage.setScene(scene);
//			stage.setTitle("Edit Article");
			
			Stage stage = openScene(loader, this.ReadMore.getScene(), AppScenes.EDITOR.getFxmlFile());
			stage.setTitle("Edit Article");
			
			ArticleEditController controller = loader.<ArticleEditController>getController();

			controller.setConnectionMannager(this.newsReaderModel.getConnectionManager());
			controller.setUsr(usr);
			
			Article selected = this.ListofArticles.getSelectionModel().selectedItemProperty().get();
			
			if(selected != null)
			{
				Article articleFromServer = this.newsReaderModel.getFullArticle(selected.getIdArticle());
				
				controller.setArticle(articleFromServer);
				//Uncomment next sentence if you want clear change when user close the window
				//stage.setOnCloseRequest(ev ->controller.exitEdit(ev));
				//user response is required before continuing with the program
				stage.initModality(Modality.WINDOW_MODAL);
				stage.showAndWait();

				if (controller.getIsSaved()){ 
					this.getData();
				}
			}
			else
			{
				System.out.println("Nothing is selected");
				//TODO alert the user that he hasn't select any article
			}

//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
    	 Window parentStage = this.ReadMore.getScene().getWindow();
    	 File selectedFile = fileChooser.showOpenDialog(parentStage);
    	 //Getting the URI for the local file
    	 if (selectedFile != null) {
    		 Path path = FileSystems.getDefault().getPath(selectedFile.getAbsolutePath());

//    		Scene parentScene = this.ReadMore.getScene();
//			FXMLLoader loader = null;
//			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
//				Pane root = loader.load();
//				Scene scene = new Scene(root);
//				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//				Window parentStageNews = parentScene.getWindow();
//				Stage stage = new Stage();
//				stage.initOwner(parentStageNews);
//				stage.setScene(scene);
//				stage.setTitle("Edit Article");
				
				Stage stage = openScene(loader, this.ReadMore.getScene(), AppScenes.EDITOR.getFxmlFile());
				stage.setTitle("Edit Article from file");
				
				ArticleEditController controller = loader.<ArticleEditController>getController();

				controller.setConnectionMannager(this.newsReaderModel.getConnectionManager());
				
				//TODO Can the user save it to database? Maybe check if he's logged?
				controller.setUsr(usr);
				
				try {
					Article articleFromFile = JsonArticle.jsonToArticle(JsonArticle.readFile(path.toString()));
					
					if(articleFromFile != null)
					{
						controller.setArticle(articleFromFile);
						//Uncomment next sentence if you want clear change when user close the window
						//stage.setOnCloseRequest(ev ->controller.exitEdit(ev));
						//user response is required before continuing with the program
						stage.initModality(Modality.WINDOW_MODAL);
						stage.showAndWait();
	
						if (controller.getIsSaved()){ 
							this.getData();
						}
					}
					else
					{
						System.out.println("Nothing is selected");
						//TODO alert the user that he hasn't select any article
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
    		 
    	 }
    }


    @FXML
    void onNewArticle(ActionEvent event) {
//    	Scene parentScene = this.ReadMore.getScene();
//		FXMLLoader loader = null;
//		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
//			Pane root = loader.load();
//			Scene scene = new Scene(root);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			Window parentStage = parentScene.getWindow();
//			Stage stage = new Stage();
//			stage.initOwner(parentStage);
//			stage.setScene(scene);
//			stage.setTitle("Create Article");
			
			Stage stage = openScene(loader, this.ReadMore.getScene(), AppScenes.EDITOR.getFxmlFile());
			stage.setTitle("Create a new article");
			
			ArticleEditController controller = loader.<ArticleEditController>getController();

			controller.setConnectionMannager(this.newsReaderModel.getConnectionManager());
			controller.setUsr(null); //Error
			controller.setArticle(null);
			//Uncomment next sentence if you want clear change when user close the window
			//stage.setOnCloseRequest(ev ->controller.exitEdit(ev));
			//user response is required before continuing with the program
			stage.initModality(Modality.WINDOW_MODAL);
			stage.showAndWait();

			if (controller.getIsSaved()){ 
				this.getData();
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }

    @FXML
    void onReadMore(ActionEvent event) {
    	
//    	try {
    		FXMLLoader loader = new FXMLLoader (getClass().getResource(AppScenes.NEWS_DETAILS.getFxmlFile()));
//			AppScenes.NEWS_DETAILS.getFxmlFile()));
//    		Parent root1 = (Parent) loader.load();
//    		Stage stage = new Stage();
//    		stage.setScene(new Scene(root1));
    				
    		Stage stage = openScene(loader, this.ReadMore.getScene(), AppScenes.NEWS_DETAILS.getFxmlFile());
    		stage.setTitle("Article details");
    		
    		ArticleDetailsController controller = loader.<ArticleDetailsController>getController();
    		controller.initData(ListofArticles.getSelectionModel().getSelectedItem(), getUsr());
    		
    		stage.show();
//    	}
//    	catch(Exception e) {
//    		System.out.println(e.toString());
//    	}
    	

    }

    @FXML
    void onToLogin(ActionEvent event) {    	
//    	Scene parentScene = this.ReadMore.getScene();
//		FXMLLoader loader = null;
			
		FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.LOGIN.getFxmlFile()));
		
		///////
		Stage stage = openScene(loader, this.ReadMore.getScene(), AppScenes.LOGIN.getFxmlFile());
		stage.setTitle("Login");
		
		LoginController controller = loader.<LoginController>getController();
		controller.setConnectionManager(this.newsReaderModel.getConnectionManager());
		//Uncomment next sentence if you want clear change when user close the window
		//stage.setOnCloseRequest(ev ->controller.exitEdit(ev));
		//user response is required before continuing with the program
		stage.initModality(Modality.WINDOW_MODAL);
		stage.showAndWait();

		if (controller.getLoggedUsr() != null) { 
			System.out.println("Nice user");
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
