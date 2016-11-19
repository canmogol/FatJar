package fatjar.test;

import fatjar.IO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Optional;

public class IOTest {

    private static final String targetTestFolder = "test-classes";
    private static final String targetClassesFolder = "classes";

    private static final String folder = File.separator + "tmp";
    private static final String file = "IOTest.txt";
    private static final String fileByteArray = "IOTest.byte.txt";
    private static final String content = "IOTest.writeTest.content";
    private static final byte[] contentByteArray = "IOTest.writeTest.content".getBytes();
    private static final String favicon = "favicon.ico";
    private static final String cssFolder = "css";
    private static final String cssFile = "reset.css";

    @Before
    public void setResource() throws Exception {
        URL url = getClass().getClassLoader().getResource(".");
        if (url != null) {
            String path = url.getPath();
            path = path.replace(targetTestFolder, targetClassesFolder);
            System.setProperty("resource", path + "web");
        } else {
            throw new Exception("resource not set");
        }
    }

    @Test
    public void writeFileBinary() {
        boolean result = IO.writeFile(contentByteArray, folder, fileByteArray);
        Assert.assertTrue("test failed, could not write to binary file: " + fileByteArray, result);
    }

    @Test
    public void writeFileBinaryFail() {
        boolean result = IO.writeFile(contentByteArray, "dev", "test", folder, fileByteArray);
        Assert.assertFalse("test failed, should not be able to write to binary file: " + fileByteArray, result);
    }

    @Test
    public void readBinaryFile() {
        Optional<byte[]> bytesOptional = IO.readBinaryFile(folder, fileByteArray);
        Assert.assertNotNull("test failed, could not read binary file: " + fileByteArray, bytesOptional.get());
    }

    @Test(expected = NoSuchElementException.class)
    public void readBinaryFileFail() {
        Optional<byte[]> bytesOptional = IO.readBinaryFile("dev", "test", folder, fileByteArray);
        Assert.assertNull("test failed, content should be null, binary file: " + fileByteArray, bytesOptional.get());
    }

    @Test
    public void writeFile() {
        boolean result = IO.writeFile(content, folder, file);
        Assert.assertTrue("test failed, could not write to text file: " + file, result);
    }

    @Test
    public void writeFileFail() {
        boolean result = IO.writeFile("dev", "test", content, folder, file);
        Assert.assertFalse("test failed, should not be able to write to text file: " + file, result);
    }

    @Test
    public void readFile() {
        Optional<String> contentOptional = IO.readFile(folder, file);
        Assert.assertTrue(contentOptional.isPresent());
        Assert.assertEquals(contentOptional.get(), content);
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
