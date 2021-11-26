# AnagramSolver
A tool to solve anagram based games like the NY Times Spelling Bee given a minimum word length, a required character, and a set of optional characters.

### Usage
java AnagramSolver \<path\> \<minWordLength\> \<requiredChar\> \<optionalChars\>

### Example 
java AnagramSolver path/to/txt/file 5 e pnmos


# MaxAnagrams
A tool to determine the maximum number of words that can be created for the NY Times Spelling Bee given a minimum word length and maximum set size for optional characters. This will help determine which puzzle configuration will be the most challenging to complete.

### Usage
java MaxAnagrams \<path\> \<minWordLength\> \<optionalCharSetSize\>

### Example 
java MaxAnagrams path/to/txt/file 4 6
