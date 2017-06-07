package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class ListViewController implements Initializable {

	private static final String START_PATH = "C:\\Users\\Asus\\Downloads";
	private static final String RETURN_ITEM = "..\\";
	private static final String LIST_VIEW_1_ID = "listView1";
	private static final String LIST_VIEW_2_ID = "listView2";
	private String currentPath1 = START_PATH;
	private String currentPath2 = START_PATH;
	private boolean copyOn = false;
	private final ObjectProperty<ListCell<String>> dragSource = new SimpleObjectProperty<>();
	@FXML
	private Pane pane1;
	@FXML
	private ListView<String> listView1;
	@FXML
	private ListView<String> listView2;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		listStartDirectory();
		setupListView(listView1, LIST_VIEW_1_ID);
		setupListView(listView2, LIST_VIEW_2_ID);
	}
	
	public void handleKeyPressedPane1(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if(keyCode == KeyCode.C) {
			copyOn = true;
		}
	}
	
	public void handleKeyReleasedPane1(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if(keyCode == KeyCode.C) {
			copyOn = false;
		}
	}

	public void handleKeyPressedListView1(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if (keyCode == KeyCode.DELETE) {
			String selectedItem = listView1.getSelectionModel().getSelectedItem();
			File selectedFile = new File(currentPath1 + "\\" + selectedItem);
			if (selectedFile.isFile()) {
				selectedFile.delete();
				listSelectedDirectory(listView1);
			}
		}
	}

	public void handleKeyPressedListView2(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if (keyCode == KeyCode.DELETE) {
			String selectedItem = listView2.getSelectionModel().getSelectedItem();
			File selectedFile = new File(currentPath2 + "\\" + selectedItem);
			if (selectedFile.isFile()) {
				selectedFile.delete();
				listSelectedDirectory(listView2);
			}
		}
	}

	public void handleMouseClickListView1(MouseEvent event) {
		String selectedItem = listView1.getSelectionModel().getSelectedItem();
		File selectedFile = new File(currentPath1 + "\\" + selectedItem);
		if (selectedFile.isDirectory()) {
			currentPath1 += "\\" + selectedItem;
			listSelectedDirectory(listView1);
		}
	}

	public void handleMouseClickListView2(MouseEvent event) {
		String selectedItem = listView2.getSelectionModel().getSelectedItem();
		File selectedFile = new File(currentPath2 + "\\" + selectedItem);
		if (selectedFile.isDirectory()) {
			currentPath2 += "\\" + selectedItem;
			listSelectedDirectory(listView2);
		}
	}

	private void listSelectedDirectory(ListView<String> listView) {
		List<String> startDirectoryElements = new ArrayList<>();
		startDirectoryElements.add(RETURN_ITEM);
		File directory = new File(currentPath1);
		for (String file : directory.list()) {
			startDirectoryElements.add(file);
		}
		listView.setItems(FXCollections.observableArrayList(startDirectoryElements));
	}

	private void listStartDirectory() {
		List<String> startDirectoryElements = new ArrayList<>();
		startDirectoryElements.add(RETURN_ITEM);
		File directory = new File(START_PATH);
		for (String file : directory.list()) {
			ListCell<String> cell = new ListCell<>();
			cell.setText(file);
			startDirectoryElements.add(file);
		}
		listView1.setItems(FXCollections.observableArrayList(startDirectoryElements));
		listView2.setItems(FXCollections.observableArrayList(startDirectoryElements));
	}

	private void setupListView(ListView<String> listView, String id) {
		listView.setId(id);
		listView.setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(item);
				};
			};
			cell.setOnDragDetected(event -> {
				if (!cell.isEmpty()) {
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
			cell.setOnDragDone(event -> {
				if(copyOn == false) {
					listView.getItems().remove(cell.getItem());
				}
			});
			cell.setOnDragDropped(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasString() && dragSource.get() != null) {
					ListCell<String> dragSourceCell = dragSource.get();
					if (copyOn == true) {
						copyOnDisk(dragSourceCell.getItem(), listView.getId());
					} else {
						moveOnDisk(dragSourceCell.getItem(), listView.getId());
					}
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

	private void moveOnDisk(String fileName, String destinationListViewId) {
		if (LIST_VIEW_1_ID.equals(destinationListViewId)) {
			Path currentFilePath = FileSystems.getDefault().getPath(currentPath2 + "\\" + fileName);
			Path destinationFilePath = FileSystems.getDefault().getPath(currentPath1 + "\\" + fileName);
			try {
				Files.move(currentFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException exeption) {
			}
		} else if (LIST_VIEW_2_ID.equals(destinationListViewId)) {
			Path currentFilePath = FileSystems.getDefault().getPath(currentPath1 + "\\" + fileName);
			Path destinationFilePath = FileSystems.getDefault().getPath(currentPath2 + "\\" + fileName);
			try {
				Files.move(currentFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException exeption) {
			}
		}
	}

	private void copyOnDisk(String fileName, String destinationListViewId) {
		if (LIST_VIEW_1_ID.equals(destinationListViewId)) {
			Path currentFilePath = FileSystems.getDefault().getPath(currentPath2 + "\\" + fileName);
			Path destinationFilePath = FileSystems.getDefault().getPath(currentPath1 + "\\" + fileName);
			try {
				Files.copy(currentFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException exeption) {
			}
		} else if (LIST_VIEW_2_ID.equals(destinationListViewId)) {
			Path currentFilePath = FileSystems.getDefault().getPath(currentPath1 + "\\" + fileName);
			Path destinationFilePath = FileSystems.getDefault().getPath(currentPath2 + "\\" + fileName);
			try {
				Files.copy(currentFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException exeption) {
			}
		}
	}
}
