package application;

import java.text.SimpleDateFormat;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class FileRow {

	private final SimpleStringProperty fileName;
	private final SimpleLongProperty size;
	private final SimpleStringProperty lastModified;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	public FileRow(String fileName, long size, long lastModified) {
		this.fileName = new SimpleStringProperty(fileName);
		this.size = new SimpleLongProperty(size);
		this.lastModified = new SimpleStringProperty(sdf.format(lastModified));
	}

	public String getFileName() {
		return fileName.get();
	}

	public long getSize() {
		return size.get();
	}

	public String getLastModified() {
		return lastModified.get();
	}	
}
