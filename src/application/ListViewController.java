package application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;

public class ListViewController {
	
	private static final String START_PATH = "C:\\Testowy";
	private ListView listView;
	private ObservableList<String> elements;

	public ListViewController(Pane pane) {
		SplitPane splitPane = null;
		for(Node node : Tools.getAllNodes(pane)) {
			if(node instanceof SplitPane) {
				splitPane = (SplitPane) node;
			}
		}
		ScrollPane scrollPane = null;
		for(Node node : splitPane.getItems()) {
			if(node instanceof ScrollPane && "scrollPane1".equals(node.getId())) {
				scrollPane = (ScrollPane) node;
			}
		}
		listView = (ListView) scrollPane.getContent();
		//elements  = FXCollections.emptyObservableList();
		listStartDirectory();
		//listView.setItems(elements);
	}
	
	private void listStartDirectory() {
		List<String> startDirectoryElements = new ArrayList<>();
		try(Stream<Path> paths = Files.walk(Paths.get(START_PATH))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            System.out.println(filePath);
		            startDirectoryElements.add(filePath.toString());
		        }
		    });
		} catch(IOException exception) {
			
		}
		listView.setItems(FXCollections.observableArrayList(startDirectoryElements));
	}
}
