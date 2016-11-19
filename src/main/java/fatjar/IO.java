package fatjar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IO {

    static Optional<byte[]> readBinaryFile(String fileName) {
        try {
            byte[] content = null;
            URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);
            if (resource != null) {
                content = Files.readAllBytes(Paths.get(resource.toURI()));
            }
            return Optional.ofNullable(content);
        } catch (Exception e) {
            Log.error("got exception while reading binary file, fileName: " + fileName + " error: " + e);
            return Optional.empty();
        }
    }

    static Optional<byte[]> readBinaryFile(String folder, String fileName) {
        try {
            return Optional.of(Files.readAllBytes(Paths.get(folder + File.separator + fileName)));
        } catch (IOException e) {
            Log.error("got exception while reading binary file, folder: " + folder + " fileName: " + fileName + " error: " + e);
            return Optional.empty();
        }
    }

    static Optional<String> readFile(String... paths) {
        return readFile(Stream.of(paths)
                .collect(Collectors.joining(File.separator)));
    }

    static Optional<String> readFile(String fileName) {
        try {
            String content = null;
            URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);
            if (resource != null) {
                content = Files.readAllLines(Paths.get(resource.toURI())).stream().collect(Collectors.joining("\n"));
            }
            return Optional.ofNullable(content);
        } catch (Exception e) {
            Log.error("got exception while reading binary file, fileName: " + fileName + " error: " + e);
            return Optional.empty();
        }
    }

    static Optional<String> readFile(String folder, String fileName) {
        try {
            return Optional.of(Files.readAllLines(Paths.get(folder + File.separator + fileName)).stream().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    static boolean writeFile(String path, String filename, byte[] content) {
        try {
            Files.write(Paths.get(path + File.separator + filename), content, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    static boolean writeFile(String path, String filename, String content) {
        return writeFile(path, filename, content.getBytes());
    }

}
