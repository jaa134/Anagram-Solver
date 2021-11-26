import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MaxAnagrams {

  private static final int NUM_REQUIRED_ARGS = 3;

  private Set<String> words;
  private Set<Set<Character>> charSets;

  public MaxAnagrams (String dictionaryPath, int minWordLength, int optionalCharSetSize) {
    this.words = this.getWordsFromFile(dictionaryPath, minWordLength, optionalCharSetSize);
    this.charSets = this.getCharSetsFromWords(this.words);
  }

  private Set<String> getWordsFromFile (String dictionaryPath, int minWordLength, int optionalCharSetSize) {
    Set<String> result = null;
    try {
      Path resolvedPath = Paths.get(dictionaryPath);
      Stream<String> lines = Files.lines(resolvedPath, StandardCharsets.ISO_8859_1);
      result = lines.collect(Collectors.toSet());
      result.removeIf(word -> word.length() < minWordLength);
      // remove words where the number of unique characters exceeds the optional char set size plus 1 for the required char
      result.removeIf(word -> word.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()).size() > (optionalCharSetSize + 1));
    } catch (Exception e) {
      System.err.println(String.format("Failed to load words from file at '%s'!", dictionaryPath));
      e.printStackTrace();
    }
    return result;
  }

  private Set<Set<Character>> getCharSetsFromWords (Set<String> words) {
    Set<Set<Character>> result = new HashSet<>();
    for (String word : words) {
      result.add(word.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()));
    }
    return result;
  }

  private boolean isWordMatch (String word, char requiredChar, Set<Character> allowedChars) {
    boolean hasRequired = false;
    for (int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      if (!allowedChars.contains(c)) {
        return false;
      } else if (c == requiredChar) {
        hasRequired = true;
      }
    }
    return hasRequired;
  }

  public void printSolution () {
    if (this.words == null) {
      System.err.println("Could not find solution! A valid dictionary was not parsed.");
    } else {
      char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
      for (char requiredChar : alphabet) {
        Set<String> wordsWithRequiredChar = new HashSet<>(this.words);
        wordsWithRequiredChar.removeIf(word -> word.indexOf(requiredChar) < 0);
        // update all of the counts and find the max
        Set<Character> maxSolution = null;
        int maxNumMatches = 0;
        for (Set<Character> charSet : this.charSets) {
          int numMatches = 0;
          for (String word : wordsWithRequiredChar) {
            if (this.isWordMatch(word, requiredChar, charSet)) {
              numMatches++;
            }
          }
          Set<Character> optionalCharSet = new HashSet<>(charSet);
          optionalCharSet.remove(requiredChar);
          if (numMatches > maxNumMatches) {
            maxSolution = optionalCharSet;
            maxNumMatches = numMatches;
          }
        }
        System.out.println(String.format("%c   %s   %d", requiredChar, String.valueOf(maxSolution), maxNumMatches));
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
      if (!args[1].matches("^\\d+$")) {
        results.add("Optional char set size must be a positive integer");
      }
    }
    return results;
  }

  // Use like "<path> <minWordLength> <optionalCharSetSize>"
  // Example: "path/to/txt/file 5"
  public static void main (String[] args) {
    List<String> errors = MaxAnagrams.getInputErrors(args);
    if (errors.size() > 0) {
      for (String error : errors) {
        System.out.println(error);
      }
    } else {
      String dictionaryPath = args[0];
      int minWordLength = Integer.parseInt(args[1]);
      int optionalCharSetSize = Integer.parseInt(args[2]);
      MaxAnagrams solver = new MaxAnagrams(dictionaryPath, minWordLength, optionalCharSetSize);
      solver.printSolution();
    }
  }
}