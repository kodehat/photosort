package de.codehat.photosort.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import de.codehat.photosort.args.validator.PathValidator;

import java.nio.file.Path;

public class Args {

    @Parameter(names = { "-h", "--help" }, description = "Shows help dialog.", help = true)
    private boolean help;

    @Parameter(names = { "-s", "--source" }, description = "The source directory containing the unsorted images.",
            required = true, validateWith = PathValidator.class, converter = PathConverter.class)
    private Path sourceDir;

    @Parameter(names = { "-t", "--target" }, description = "The target directory where the sorted images get saved to.",
            required = true, validateWith = PathValidator.class, converter = PathConverter.class)
    private Path targetDir;

    public boolean isHelp() {
        return help;
    }

    public Path getSourceDir() {
        return sourceDir;
    }

    public Path getTargetDir() {
        return targetDir;
    }
}
