package fatjar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.stream.Collectors;

public interface IO {

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
