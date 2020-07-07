package de.codehat.photosort.model;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class ImageMeta {

  private final String year;
  private final String month;
  private final Image image;

  public ImageMeta(Image image) throws IOException {
    this.image = image;

    final LocalDateTime creationDate = image.getCreationDate();
    this.year = String.valueOf(creationDate.getYear());
    this.month = String.format("%02d", creationDate.getMonth().getValue());
  }

  public Path getFinalPath(final Path target) {
    return target.resolve(getYear()).resolve(getMonth());
  }

  public String getYear() {
    return year;
  }

  public String getMonth() {
    return month;
  }

  public Image getImage() {
    return image;
  }
}
