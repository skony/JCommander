package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("FXML TableView Example");
	        Pane myPane = (Pane) FXMLLoader.load(getClass().getClassLoader().getResource("MainWindow.fxml"));
	        new ListViewController(myPane);
	        Scene myScene = new Scene(myPane);
	        primaryStage.setScene(myScene);
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
