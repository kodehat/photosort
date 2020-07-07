package de.codehat.photosort.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import org.apache.commons.io.FileUtils;

public class Image {

  private static final String FILE_EXTENSION_JSON = ".json";
  private static final Gson GSON = new Gson();

  private final Path imagePath;
  private final Path jsonPath;

  public Image(Path imagePath) {
    this.imagePath = imagePath;

    final String imageName = imagePath.getFileName().toString();
    final Path jsonPath = Paths.get(imagePath.getParent().toString(),
        imageName + FILE_EXTENSION_JSON);
    if (jsonPath.toFile().exists()) {
      this.jsonPath = jsonPath;
    } else {
      this.jsonPath = null;
    }
  }

  private LocalDateTime fromImageFile() {
    return new Timestamp(imagePath.toFile().lastModified()).toLocalDateTime();
  }

  private LocalDateTime fromJsonFile() throws IOException {
    final String jsonStr = FileUtils.readFileToString(jsonPath.toFile(), StandardCharsets.UTF_8);
    final JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
    final JsonObject creationTime = jsonObject.getAsJsonObject("photoTakenTime");
    final long timestamp = creationTime.get("timestamp").getAsLong();
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp),
        TimeZone.getTimeZone("UTC").toZoneId());
  }

  public LocalDateTime getCreationDate() throws IOException {
    if (jsonPath != null) {
      return fromJsonFile();
    }
    return fromImageFile();
  }

  public Path getImagePath() {
    return imagePath;
  }
}
