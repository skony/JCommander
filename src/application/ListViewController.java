package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class ListViewController implements Initializable {
	
	private static final String START_PATH = "C:\\Users\\Asus\\Downloads";
	private static final String RETURN_ITEM = "..\\";
	private String currentPath = START_PATH;
	@FXML
	private ListView<String> listView1;
	@FXML
	private ListView<String> listView2;
			
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		listStartDirectory();
	}
	
	public void handleMouseClickListView1(MouseEvent event) {
		handleMouseClick(listView1);
	}
	
	public void handleMouseClickListView2(MouseEvent event) {
		handleMouseClick(listView2);
	}
	
	public void handleKeyPressedListView1(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if(keyCode == KeyCode.DELETE) {
			String selectedItem = listView1.getSelectionModel().getSelectedItem();
			File selectedFile = new File(currentPath + "\\" + selectedItem);
			if(selectedFile.isFile()) {
				selectedFile.delete();
				listSelectedDirectory(listView1);
			}
		}
	}
	
	private void handleMouseClick(ListView<String> listView) {
		String selectedItem = listView.getSelectionModel().getSelectedItem();
		File selectedFile = new File(currentPath + "\\" + selectedItem);
		if(selectedFile.isDirectory()) {
			currentPath += "\\" + selectedItem;
			listSelectedDirectory(listView);
		}
	}
	
	private void listSelectedDirectory(ListView<String> listView) {
		List<String> startDirectoryElements = new ArrayList<>();
		startDirectoryElements.add(RETURN_ITEM);
		File directory = new File(currentPath);
		for(String file : directory.list()) {
			startDirectoryElements.add(file);
		}		
		listView.setItems(FXCollections.observableArrayList(startDirectoryElements));
	}
	
	private void listStartDirectory() {
		List<String> startDirectoryElements = new ArrayList<>();
		startDirectoryElements.add(RETURN_ITEM);
		File directory = new File(START_PATH);
		for(String file : directory.list()) {
			startDirectoryElements.add(file);
		}
		listView1.setItems(FXCollections.observableArrayList(startDirectoryElements));
		listView2.setItems(FXCollections.observableArrayList(startDirectoryElements));
	}
}
