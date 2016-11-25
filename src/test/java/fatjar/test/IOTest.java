package fatjar.test;

import java.io.File;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;

import fatjar.IO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IOTest {

	private static final String resourcesFolder = "src" + File.separator + "main" + File.separator + "resources";

	private static final String folder = File.separator + "target";
	private static final String file = "IOTest.txt";
	private static final String fileByteArray = "IOTest.byte.txt";
	private static final String content = "IOTest.writeTest.content";
	private static final byte[] contentByteArray = "IOTest.writeTest.content".getBytes();
	private static final String favicon = "favicon.ico";
	private static final String cssFolder = "css";
	private static final String cssFile = "reset.css";

	private String getCurrentDir() {
		String currentDir = Paths.get(".").toAbsolutePath().toString();
		if (currentDir.endsWith(".")) {
			currentDir = currentDir.substring(0, currentDir.length() - 1);
		}
		return currentDir;
	}

	@Before
	public void setResource() throws Exception {
		String currentDir = getCurrentDir();
		System.setProperty("resource", currentDir + resourcesFolder + File.separator + "web");
	}

	@Test
	public void readWriteFileBinary() {
		writeFileBinary();
		readBinaryFile();
	}

	private void writeFileBinary() {
		boolean result = IO.writeFile(contentByteArray, getCurrentDir(), folder, fileByteArray);
		Assert.assertTrue("test failed, could not write to binary file: " + fileByteArray, result);
	}

	private void readBinaryFile() {
		Optional<byte[]> bytesOptional = IO.readBinaryFile(getCurrentDir(), folder, fileByteArray);
		Assert.assertNotNull("test failed, could not read binary file: " + fileByteArray, bytesOptional.get());
	}

	@Test
	public void writeFileBinaryFail() {
		boolean result = IO.writeFile(contentByteArray, "dev", "test", folder, fileByteArray);
		Assert.assertFalse("test failed, should not be able to write to binary file: " + fileByteArray, result);
	}

	@Test(expected = NoSuchElementException.class)
	public void readBinaryFileFail() {
		Optional<byte[]> bytesOptional = IO.readBinaryFile("dev", "test", folder, fileByteArray);
		Assert.assertNull("test failed, content should be null, binary file: " + fileByteArray, bytesOptional.get());
	}

	@Test
	public void readWriteFile() {
		writeFile();
		readFile();
	}

	private void writeFile() {
		boolean result = IO.writeFile(content, getCurrentDir(), folder, file);
		Assert.assertTrue("test failed, could not write to text file: " + file, result);
	}

	private void readFile() {
		Optional<String> contentOptional = IO.readFile(getCurrentDir(), folder, file);
		Assert.assertTrue(contentOptional.isPresent());
		Assert.assertEquals(contentOptional.get(), content);
	}

	@Test
	public void writeFileFail() {
		boolean result = IO.writeFile(content, "dev", "test", folder, file);
		Assert.assertFalse("test failed, should not be able to write to text file: " + file, result);
	}

	@Test(expected = NoSuchElementException.class)
	public void readFileFail() {
		Optional<String> contentOptional = IO.readFile("dev", "test", folder, file);
		Assert.assertFalse("test failed, should not be able to read file: " + file, contentOptional.isPresent());
		Assert.assertNull("test failed, content should be null", contentOptional.get());
	}

	@Test
	public void readBinaryResource() {
		Optional<byte[]> optional = IO.readBinaryResource(favicon);
		Assert.assertNotNull("test failed, could not read binary resource file: " + favicon, optional.get());
	}

	@Test(expected = NoSuchElementException.class)
	public void readBinaryResourceFail() {
		Optional<byte[]> optional = IO.readBinaryResource("dev", "test", favicon);
		Assert.assertNull("test failed, resource content should be null file: " + favicon, optional.get());
	}

	@Test
	public void readResource() {
		Optional<String> optional = IO.readResource(cssFolder, cssFile);
		Assert.assertNotNull("test failed, could not read resource file: " + cssFile + " under folder: " + cssFolder, optional.get());
	}

	@Test(expected = NoSuchElementException.class)
	public void readResourceFail() {
		Optional<String> optional = IO.readResource("dev", "test", cssFolder, cssFile);
		Assert.assertNull("test failed, resource content should be null file: " + cssFile, optional.get());
	}

}
