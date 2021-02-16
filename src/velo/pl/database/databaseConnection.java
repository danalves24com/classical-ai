package velo.pl.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public class databaseConnection {
	/** The connection params. */
	private String url = "jdbc:mysql://10.0.1.23:3306/neuralNetworks", username = "root", password = "Saniroot";

	/** The con. */
	private static Connection con = null;

	/**
	 * Instantiates a new connection.
	 */
	public databaseConnection() {

	}

	/**
	 * Instantiates a new connection.
	 *
	 * @param URL   the url
	 * @param uName the u name
	 * @param psswd the psswd
	 */
	public databaseConnection(String URL, String uName, String psswd) {
		this.url = URL;
		this.username = uName;
		this.password = psswd;
	}

	private byte[] fromObjectToByteArray(Object obj) throws IOException {
		if (Objects.nonNull(obj)) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try (ObjectOutputStream os = new ObjectOutputStream(bos)) {
				os.writeObject(obj);
			}
			return bos.toByteArray();
		}
		return null;
	}

	/**
	 * Convert entry to blob.
	 *
	 * @param entry the entry
	 * @return the blob
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Blob convertEntryToBlob(MultiLayerNetwork entry) throws IOException {
		byte[] bArray = fromObjectToByteArray(entry);

		Blob blob = null;
		try {
			blob = new SerialBlob(bArray);
		} catch (SerialException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return blob;
	}

	/**
	 * From byte array to object.
	 *
	 * @param byteArr the byte arr
	 * @return the object
	 * @throws IOException            Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	private Object fromByteArrayToObject(byte[] byteArr) throws IOException, ClassNotFoundException {
		if (Objects.nonNull(byteArr)) {
			ByteArrayInputStream bis = new ByteArrayInputStream(byteArr);
			ObjectInput in = new ObjectInputStream(bis);
			return in.readObject();
		}
		return null;
	}

	/**
	 * Convert blob to entry.
	 *
	 * @param blob the blob
	 * @return the entry
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException            Signals that an I/O exception has occurred.
	 * @throws SQLException           the SQL exception
	 */
	private MultiLayerNetwork convertBlobToEntry(Blob blob) throws ClassNotFoundException, IOException, SQLException {
		int blobLength = (int) blob.length();
		byte[] blobAsBytes = blob.getBytes(1, blobLength);
		return (MultiLayerNetwork) fromByteArrayToObject(blobAsBytes);

	}

	/**
	 * Adds the entry.
	 *
	 * @param entry the entry
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void addModel(MultiLayerNetwork entry, String name) throws IOException {

		String sql = "INSERT INTO musicModels (Name, model) VALUES (?, ?)";
		PreparedStatement statement;
		try {
			statement = con.prepareStatement(sql);
			statement.setString(1, name);
			statement.setBlob(2, convertEntryToBlob(entry));			
			int rowsInserted;
			rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				// inserted - success
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the entry.
	 *
	 * @param name the name
	 * @return the entry
	 * @throws SQLException the SQL exception
	 */
	public MultiLayerNetwork getModel(String name) throws SQLException {
		String sql = "SELECT model FROM musicModels where Name='" + name + "'";

		Statement statement = con.createStatement();
		ResultSet result = statement.executeQuery(sql);

		int count = 0;
		Blob blob = null;
		while (result.next()) {

			blob = result.getBlob(1);
		}

		MultiLayerNetwork out = null;
		try {
			out = convertBlobToEntry(blob);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("*no such entry");
		}

		return out;

	}

	/**
	 * Open connection.
	 */
	public void openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(this.url, this.username, this.password);
			System.out.println("connected");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
