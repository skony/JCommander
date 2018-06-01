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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class ListViewController implements Initializable {

	private static final String START_PATH = "C:\\Users\\Asus\\Downloads";
	private static final String TABLE_VIEW_1_ID = "tableView1";
	private static final String TABLE_VIEW_2_ID = "tableView2";
	private static final String TABLE_VIEW_ID_KEY = "ID";

	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

	private StringBuilder currentPath1 = new StringBuilder(START_PATH);
	private StringBuilder currentPath2 = new StringBuilder(START_PATH);
	private boolean copyOn = false;
	private final ObjectProperty<ListCell<String>> dragSource = new SimpleObjectProperty<>();

	@FXML
	private Pane pane1;

	@FXML
	private Label pathLabel1;

	@FXML
	private Label pathLabel2;

	@FXML
	private Button upButton1;

	@FXML
	private Button upButton2;

	@FXML
	private TableView<FileRow> tableView1;

	@FXML
	private TableView<FileRow> tableView2;

	@FXML
	private TableColumn<FileRow, String> tableColumnFileName1;

	@FXML
	private TableColumn<FileRow, Long> tableColumnSize1;

	@FXML
	private TableColumn<FileRow, String> tableColumnLastModified1;

	@FXML
	private TableColumn<FileRow, String> tableColumnFileName2;

	@FXML
	private TableColumn<FileRow, Long> tableColumnSize2;

	@FXML
	private TableColumn<FileRow, String> tableColumnLastModified2;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mapTableViewColumns();
		
		tableView1.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableView2.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		setupDragAndDrop(tableView1);
		setupDragAndDrop(tableView2);		
		
		listStartDirectory();		
	}

	public void handleKeyPressedPane1(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if (keyCode == KeyCode.C) {
			copyOn = true;
		}
	}

	public void handleKeyReleasedPane1(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if (keyCode == KeyCode.C) {
			copyOn = false;
		}
	}

	public void handleKeyPressedListView1(KeyEvent event) {
		// delete(event, tableView1, currentPath1);
	}

	public void handleKeyPressedListView2(KeyEvent event) {
		// delete(event, tableView2, currentPath2);
	}

	public void handleMouseClickTableView1(MouseEvent event) {
		changeDirectory(tableView1, currentPath1);
	}

	public void handleMouseClickTableView2(MouseEvent event) {
		changeDirectory(tableView2, currentPath2);
	}

	public void handleMouseClickUpButton1(MouseEvent event) throws IOException {
		goUpDirectory(tableView1, currentPath1);
	}

	public void handleMouseClickUpButton2(MouseEvent event) throws IOException {
		goUpDirectory(tableView2, currentPath2);
	}

	private void delete(KeyEvent event, ListView<String> listView, StringBuilder currentPath) {
		KeyCode keyCode = event.getCode();
		if (keyCode == KeyCode.DELETE) {
			String selectedItem = listView.getSelectionModel().getSelectedItem();
			File selectedFile = new File(currentPath + "\\" + selectedItem);
			if (selectedFile.isFile()) {
				selectedFile.delete();
				// listSelectedDirectory(listView, currentPath);
			}
		}
	}

	private void mapTableViewColumns() {
		tableColumnFileName1.setCellValueFactory(new PropertyValueFactory<FileRow, String>("fileName"));
		tableColumnSize1.setCellValueFactory(new PropertyValueFactory<FileRow, Long>("size"));
		tableColumnLastModified1.setCellValueFactory(new PropertyValueFactory<FileRow, String>("lastModified"));

		tableColumnFileName2.setCellValueFactory(new PropertyValueFactory<FileRow, String>("fileName"));
		tableColumnSize2.setCellValueFactory(new PropertyValueFactory<FileRow, Long>("size"));
		tableColumnLastModified2.setCellValueFactory(new PropertyValueFactory<FileRow, String>("lastModified"));
	}

	private void listStartDirectory() {
		List<FileRow> startDirectoryElements = new ArrayList<>();
		File directory = new File(START_PATH);
		for (File file : directory.listFiles()) {
			startDirectoryElements.add(new FileRow(file.getName(), file.length(), file.lastModified()));
		}
		tableView1.setItems(FXCollections.observableArrayList(startDirectoryElements));
		tableView2.setItems(FXCollections.observableArrayList(startDirectoryElements));
		pathLabel1.setText(START_PATH);
		pathLabel2.setText(START_PATH);
	}

	private void changeDirectory(TableView<FileRow> tableView, StringBuilder currentPath) {
		FileRow selectedItem = tableView.getSelectionModel().getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		File selectedFile = new File(currentPath + "\\" + selectedItem.getFileName());
		if (selectedFile.isDirectory()) {
			currentPath.append("\\" + selectedItem.getFileName());
			listSelectedDirectory(tableView, currentPath);
		}

		if (TABLE_VIEW_1_ID.equals(tableView.getId())) {
			pathLabel1.setText(currentPath.toString());
		}
		if (TABLE_VIEW_2_ID.equals(tableView.getId())) {
			pathLabel2.setText(currentPath.toString());
		}
	}

	private void goUpDirectory(TableView<FileRow> tableView, StringBuilder currentPath) throws IOException {
		File currentDir = new File(currentPath.toString());
		File parentDir = currentDir.getParentFile();
		currentPath.delete(0, currentPath.length());
		currentPath.append(parentDir.getCanonicalPath());
		listSelectedDirectory(tableView, currentPath);

		if (TABLE_VIEW_1_ID.equals(tableView.getId())) {
			pathLabel1.setText(currentPath.toString());
		}
		if (TABLE_VIEW_2_ID.equals(tableView.getId())) {
			pathLabel2.setText(currentPath.toString());
		}
	}

	private void listSelectedDirectory(TableView<FileRow> listView, StringBuilder currentPath) {
		List<FileRow> startDirectoryElements = new ArrayList<>();
		// startDirectoryElements.add(RETURN_ITEM);
		File directory = new File(currentPath.toString());
		for (File file : directory.listFiles()) {
			startDirectoryElements.add(new FileRow(file.getName(), file.length(), file.lastModified()));
		}
		listView.setItems(FXCollections.observableArrayList(startDirectoryElements));
	}

	private void setupDragAndDrop(TableView<FileRow> tableView) {
		tableView.setRowFactory(tv -> {
			TableRow<FileRow> row = new TableRow<>();

			row.setOnDragDetected(event -> {
				if (!row.isEmpty()) {
					Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
					db.setDragView(row.snapshot(null, null));
					ClipboardContent cc = new ClipboardContent();
					cc.put(SERIALIZED_MIME_TYPE, new ArrayList<>(tv.getSelectionModel().getSelectedIndices()));
					cc.putString(tv.getId());
					db.setContent(cc);
					event.consume();
				}
			});

			return row;
		});

		tableView.setOnDragOver(event -> {
			Dragboard db = event.getDragboard();
			if (db.hasContent(SERIALIZED_MIME_TYPE)) {
				String tableViewId = (String) db.getContent(DataFormat.PLAIN_TEXT);
				if (!tableView.getId().equals(tableViewId)) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					event.consume();
				}
			}
		});

		tableView.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			if (db.hasContent(SERIALIZED_MIME_TYPE)) {
				List<Integer> draggedIndex = (List<Integer>) db.getContent(SERIALIZED_MIME_TYPE);
				String tableViewId = (String) db.getContent(DataFormat.PLAIN_TEXT);
				
				TableView<FileRow> currentTuble = getCurrentTable(tableViewId);
				ObservableList<FileRow> items =currentTuble.getItems();
				String oppositeTableId = getOppositeTable(tableViewId).getId();
				
				for(int idx : draggedIndex) {
					moveOnDisk(items.get(idx).getFileName(), oppositeTableId);
				}
				
				listSelectedDirectory(tableView1, currentPath1);
				listSelectedDirectory(tableView2, currentPath2);

				event.setDropCompleted(true);
				event.consume();
			}
		});
	}

	private void moveOnDisk(String fileName, String destinationListViewId) {
		if(currentPath1.toString().equals(currentPath2.toString())) {
			return;
		}
		
		if (TABLE_VIEW_1_ID.equals(destinationListViewId)) {
			Path currentFilePath = FileSystems.getDefault().getPath(currentPath2 + "\\" + fileName);
			Path destinationFilePath = FileSystems.getDefault().getPath(currentPath1 + "\\" + fileName);
			try {
				Files.move(currentFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException exeption) {
			}
		} else if (TABLE_VIEW_2_ID.equals(destinationListViewId)) {
			Path currentFilePath = FileSystems.getDefault().getPath(currentPath1 + "\\" + fileName);
			Path destinationFilePath = FileSystems.getDefault().getPath(currentPath2 + "\\" + fileName);
			try {
				Files.move(currentFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException exeption) {
			}
		}
	}

	private void copyOnDisk(String fileName, String destinationListViewId) {
		if (TABLE_VIEW_1_ID.equals(destinationListViewId)) {
			Path currentFilePath = FileSystems.getDefault().getPath(currentPath2 + "\\" + fileName);
			Path destinationFilePath = FileSystems.getDefault().getPath(currentPath1 + "\\" + fileName);
			try {
				Files.copy(currentFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException exeption) {
			}
		} else if (TABLE_VIEW_2_ID.equals(destinationListViewId)) {
			Path currentFilePath = FileSystems.getDefault().getPath(currentPath1 + "\\" + fileName);
			Path destinationFilePath = FileSystems.getDefault().getPath(currentPath2 + "\\" + fileName);
			try {
				Files.copy(currentFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException exeption) {
			}
		}
	}
	
	private TableView<FileRow> getCurrentTable(String id) {
		if(TABLE_VIEW_1_ID.equals(id)) {
			return tableView1;
		}
		
		return tableView2;
	}
	
	private TableView<FileRow> getOppositeTable(String id) {
		if(TABLE_VIEW_1_ID.equals(id)) {
			return tableView2;
		}
		
		return tableView1;
	}
}
