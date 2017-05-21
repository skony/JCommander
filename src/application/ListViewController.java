package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class ListViewController implements Initializable {
	
	private static final String START_PATH = "C:\\Users\\Asus\\Downloads";
	private static final String RETURN_ITEM = "..\\";
	private String currentPath = START_PATH;
    private final ObjectProperty<ListCell<String>> dragSource = new SimpleObjectProperty<>();
	@FXML
	private ListView<String> listView1;
	@FXML
	private ListView<String> listView2;
			
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		listStartDirectory();
		setupListView(listView1);
		setupListView(listView2);
	}
	
	public void handleDragDetectedListView2(MouseEvent event) {
		System.out.println("BANG!");
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
	
	public void handleDragOverListView1(DragEvent event) {
		System.out.println(listView2.getSelectionModel().getSelectedItem());
		System.out.println("xxxx");
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
			ListCell<String> cell = new ListCell<>();
			cell.setText(file);
			startDirectoryElements.add(file);
		}
		listView1.setItems(FXCollections.observableArrayList(startDirectoryElements));
		listView2.setItems(FXCollections.observableArrayList(startDirectoryElements));
	}
	
	private void setupListView(ListView<String> listView) {
		listView.setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<String>(){
	            @Override
	            public void updateItem(String item , boolean empty) {
	                super.updateItem(item, empty);
	                setText(item);
	            };
			};
	        cell.setOnDragDetected(event -> {
	        	if (! cell.isEmpty()) {
			       Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
			       ClipboardContent cc = new ClipboardContent();
			       cc.putString(cell.getItem());
			       db.setContent(cc);
			       dragSource.set(cell);
	            }
	        });
	        cell.setOnDragOver(event -> {
	            Dragboard db = event.getDragboard();
                if (db.hasString()) {
                   event.acceptTransferModes(TransferMode.MOVE);
                }
	        });
	        cell.setOnDragDone(event -> listView.getItems().remove(cell.getItem()));
	        cell.setOnDragDropped(event -> {
	           Dragboard db = event.getDragboard();
               if (db.hasString() && dragSource.get() != null) {
                   ListCell<String> dragSourceCell = dragSource.get();
                   listView.getItems().add(dragSourceCell.getItem());
                   event.setDropCompleted(true);
                   dragSource.set(null);
               } else {
                   event.setDropCompleted(false);
               }
	        });
	        return cell;
		});
   }
}
