package de.codehat.photosort;

import com.beust.jcommander.JCommander;
import de.codehat.photosort.args.Args;
import de.codehat.photosort.model.Image;
import de.codehat.photosort.model.ImageMeta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PhotoSort {

  private static final Logger LOG = Logger.getLogger(PhotoSort.class.getName());

  private static final int ARGUMENTS_SIZE = 2;
  private static final int ARGUMENT_SOURCE_PATH = 0;
  private static final int ARGUMENT_TARGET_PATH = 1;

  public static void main(String[] args) throws IOException {
    Args argsObj = new Args();
    JCommander jCommander = JCommander.newBuilder()
            .addObject(argsObj)
            .build();
    jCommander.parse(args);

    if (args.length != ARGUMENTS_SIZE) {
      throw new IllegalArgumentException("Exactly two arguments are required!");
    }

    final String sourcePathStr = args[ARGUMENT_SOURCE_PATH];
    final String targetPathStr = args[ARGUMENT_TARGET_PATH];

    LOG.log(Level.INFO, "Source: {0}.", sourcePathStr);
    LOG.log(Level.INFO, "Target: {0}.", targetPathStr);

    final Path sourcePath = Paths.get(sourcePathStr);
    final Path targetPath = Paths.get(targetPathStr);

    if (!sourcePath.toFile().exists()) {
      throw new IllegalArgumentException("Source folder does not exist!");
    } else if (!sourcePath.toFile().isDirectory()) {
      throw new IllegalArgumentException("Source has to be a folder, not a file!");
    }

    if (!targetPath.toFile().exists()) {
      if (!targetPath.toFile().mkdirs()) {
        throw new IOException("Unable to create target folder! Manually create it and restart.");
      }
      LOG.info("Created target folder as it was missing.");
    }

    LOG.info("Analyzing source folder...");
    List<Path> allImages = Files.walk(sourcePath)
        .filter(p -> p.toString().matches("(?i).*\\.(jpg|jpeg|png|gif)$"))
        .collect(Collectors.toList());
    LOG.log(Level.INFO, "Found {0} images.", allImages.size());

    final Map<Path, List<ImageMeta>> allGroupedMetas =
        allImages.stream()
            .map(
                p -> {
                  try {
                    return new ImageMeta(new Image(p));
                  } catch (IOException e) {
                    LOG.log(
                        Level.WARNING, "Unable to process image with path '{0}'!", p.toString());
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(i -> i.getFinalPath(targetPath)));

    allGroupedMetas.forEach((p, l) -> {
      LOG.log(Level.INFO, "Copying {0} images to folder {1}.", new Object[]{l.size(), p.toString()});
      l.forEach(i -> {
        LOG.log(Level.FINE, "Moving image {0}.", i.toString());
        p.toFile().mkdirs();
        try {
          Files.copy(i.getImage().getImagePath(), p.resolve(i.getImage().getImagePath().getFileName().toString()), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
          LOG.log(Level.WARNING, "Unable to copy file {0} cause of {1}.", new Object[]{i.getImage().getImagePath().toString(), e.getMessage()});
        }
      });
    });
  }
}
