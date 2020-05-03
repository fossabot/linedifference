package ch.uhlme.commands;

import ch.uhlme.utils.Quadruple;
import com.google.common.flogger.FluentLogger;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ReplaceCommand {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public void run(String[] args) throws Exception {
        logger.atInfo().log("started with %s", args);

        Quadruple<Path, Path, String, String> arguments = verifyParameters(args);

        replace(arguments.first, arguments.second, arguments.third, arguments.fourth);

        logger.atInfo().log("Process finished successfully");
    }

    private Quadruple<Path, Path, String, String> verifyParameters(String[] args) throws FileNotFoundException, FileAlreadyExistsException {
        if (args == null || args.length != 4) {
            throw new IllegalArgumentException("usage: replace <input> <output> <searchPattern> <replacePattern>");
        }

        Path input = Paths.get(args[0]);
        if (!input.toFile().exists()) {
            throw new FileNotFoundException(String.format("the input file %s can't be found", input));
        }

        Path output = Paths.get(args[1]);
        if (output.toFile().exists()) {
            throw new FileAlreadyExistsException(String.format("the output file %s mustn't exist", output));
        }

        return new Quadruple<>(input, output, args[2], args[3]);
    }

    private void replace(Path input, Path output, String searchPattern, String replacePattern) throws IOException {
        Objects.requireNonNull(searchPattern);
        Objects.requireNonNull(replacePattern);

        try (BufferedReader reader = new BufferedReader(new FileReader(input.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(output.toFile()))) {

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String replaced = currentLine.replaceAll(searchPattern, replacePattern);
                writer.write(replaced);
                writer.newLine();
            }
        }
    }
}