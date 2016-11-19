package fatjar.test;

import fatjar.IO;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Optional;

public class IOTest {

    private static final String folder = File.separator + "tmp";
    private static final String file = "IOTest.txt";
    private static final String content = "IOTest.writeTest.content";

    @Test
    public void writeTest() {
        boolean result = IO.writeFile(content, folder, file);
        Assert.assertTrue("test failed, could not write to file", result);
    }

    @Test
    public void readTest() {
        Optional<String> contentOptional = IO.readFile(folder, file);
        Assert.assertTrue(contentOptional.isPresent());
        Assert.assertEquals(contentOptional.get(), content);
    }

}
