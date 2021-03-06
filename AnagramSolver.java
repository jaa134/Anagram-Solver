import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AnagramSolver {

  private static final int NUM_REQUIRED_ARGS = 4;

  private int minWordLength;
  private Set<String> words;
  private Pattern pattern;

  public AnagramSolver (String dictionaryPath, int minWordLength, char requiredChar, Set<Character> optionalChars) {
    this.minWordLength = minWordLength;
    this.words = this.getWordsFromFile(dictionaryPath);
    this.pattern = this.constructPattern(requiredChar, optionalChars);
  }

  private Set<String> getWordsFromFile (String dictionaryPath) {
    Set<String> result = null;
    try {
      Path resolvedPath = Paths.get(dictionaryPath);
      Stream<String> lines = Files.lines(resolvedPath, StandardCharsets.ISO_8859_1);
      result = lines.collect(Collectors.toSet());
      result.removeIf(word -> word.length() < this.minWordLength);
    } catch (Exception e) {
      System.err.println(String.format("Failed to load words from file at '%s'!", dictionaryPath));
      e.printStackTrace();
    }
    return result;
  }

  private Pattern constructPattern (char requiredChar, Set<Character> optionalChars) {
    String regexPattern = optionalChars.size() == 0
      ? String.format("^[%c]*$", requiredChar)
      : String.format("^[%2$s]*%1$c[%2$s]*$", requiredChar, String.valueOf(optionalChars) + requiredChar);
    return Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
  }

  public void printSolution () {
    if (this.words == null) {
      System.err.println("Could not find solution! A valid dictionary was not parsed.");
    } else {
      Set<String> results = new HashSet<>();
      for (String word : words) {
        if (this.pattern.matcher(word).matches()) {
          results.add(word);
        }
      }
      System.out.println(String.format("Found %d words...", results.size()));
      for (String word : results) {
        System.out.println(word);
      }
    }
  }

  private static List<String> getInputErrors (String[] args) {
    List<String> results = new ArrayList<String>();
    if (args.length != NUM_REQUIRED_ARGS) {
      results.add("Incorrect number of args supplied");
    } else {
      if (args[0].length() == 0 || !args[0].endsWith(".txt")) {
        results.add("Dictionary path must be specified and point to a valid text file");
      }
      if (!args[1].matches("^\\d+$")) {
        results.add("Min length must be a positive integer");
      }
      if (args[2].length() != 1 || !Character.isLetter(args[2].charAt(0))) {
        results.add("Required character must be a single letter");
      }
      if (args[3].chars().anyMatch(x -> !Character.isLetter(x))) {
        results.add("Optional characters must all be letters");
      }
    }
    return results;
  }

  // Use like "<path> <minWordLength> <requiredChar> <optionalChars>"
  // Example: "path/to/txt/file 5 e abcd"
  public static void main (String[] args) {
    List<String> errors = AnagramSolver.getInputErrors(args);
    if (errors.size() > 0) {
      for (String error : errors) {
        System.out.println(error);
      }
    } else {
      String dictionaryPath = args[0];
      int minWordLength = Integer.parseInt(args[1]);
      char requiredChar = args[2].charAt(0);
      Set<Character> optionalChars = args[3]
        .chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.toSet());
      AnagramSolver solver = new AnagramSolver(dictionaryPath, minWordLength, requiredChar, optionalChars);
      solver.printSolution();
    }
  }
}