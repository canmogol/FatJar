package fatjar;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IO {

    String resource = System.getProperty("resource");

    static Optional<byte[]> readBinaryResource(String... paths) {
        if (resource != null) {
            List<String> resourcePaths = new LinkedList<>();
            resourcePaths.add(resource);
            resourcePaths.addAll(Arrays.asList(paths));
            resourcePaths.toArray(new String[resourcePaths.size()]);
            String path = resourcePaths.stream().collect(Collectors.joining(File.separator));
            try {
                String normalized = new URI(path).normalize().getPath();
                if (normalized.startsWith(resource)) {
                    return readBinaryFile(resourcePaths.toArray(new String[resourcePaths.size()]));
                }
            } catch (URISyntaxException e) {
                Log.error("got exception while getting uri from path: " + path + " error: " + e);
            }
        } else {
            Log.error("resource path not defined, either give resource as command line parameter as -Dresource=\"/path/to/resources\" or define it as System.setProperty(\"resource\", \"/path/to/resources\")");
        }
        return Optional.empty();
    }

    static Optional<String> readResource(String... paths) {
        if (resource != null) {
            List<String> resourcePaths = new LinkedList<>();
            resourcePaths.add(resource);
            resourcePaths.addAll(Arrays.asList(paths));
            resourcePaths.toArray(new String[resourcePaths.size()]);
            String path = resourcePaths.stream().collect(Collectors.joining(File.separator));
            try {
                String normalized = new URI(path).normalize().getPath();
                if (normalized.startsWith(resource)) {
                    return readFile(resourcePaths.toArray(new String[resourcePaths.size()]));
                }
            } catch (URISyntaxException e) {
                Log.error("got exception while getting uri from path: " + path + " error: " + e);
            }
        } else {
            Log.error("resource path not defined, either give resource as command line parameter as -Dresource=\"/path/to/resources\" or define it as System.setProperty(\"resource\", \"/path/to/resources\")");
        }
        return Optional.empty();
    }

    static Optional<byte[]> readBinaryFile(String... paths) {
        String fileName = Stream.of(paths).collect(Collectors.joining(File.separator));
        try {
            byte[] content = Files.readAllBytes(Paths.get(fileName));
            return Optional.ofNullable(content);
        } catch (Exception e) {
            Log.error("got exception while reading binary file, fileName: " + fileName + " error: " + e);
            return Optional.empty();
        }
    }

    static Optional<String> readFile(String... paths) {
        String fileName = Stream.of(paths).collect(Collectors.joining(File.separator));
        try {
            return Optional.of(Files.readAllLines(Paths.get(fileName)).stream().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            Log.error("got exception while reading file, fileName: " + fileName + " error: " + e);
            return Optional.empty();
        }
    }

    static boolean writeFile(byte[] content, String... paths) {
        String fileName = Stream.of(paths).collect(Collectors.joining(File.separator));
        try {
            Files.write(Paths.get(fileName), content, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            Log.error("got exception while writing file, fileName: " + fileName + " error: " + e);
            return false;
        }
    }

    static boolean writeFile(String content, String... paths) {
        return writeFile(content.getBytes(), paths);
    }

}
