# BowlingScore
Java command line program to calculate the score for 10 pin bowling.
 
# Compile instructions 
`javac BowlingScore.java`
 
# Usage
1. Interactive user input. The user enters the pinfalls for each roll for all 10 frames of a single game.

`java ./BowlingScore`

2. Run test cases.

`java ./BowlingScore test`

# Todo
1. Error handling.
2. One file per class.
3. Use junit.
4. Use log4j or similar library.

# Motivation / Excuses / Explanations 
1. Why 1 file with multiple classes? This program was written online using https://www.jdoodle.com/online-java-compiler because I didn't have access to a proper computer. This also explains the logging and test classes.

2. Clean code motivations.
  * KISS, YAGNI e.g. no separate classes for concepts like player, frame, roll.
  * DRY e.g. Test class to reuse unit test code.
  * Interfaces to allow decoupling and testing.
  * No premature optimization of the calculator loop.
  * Unit tests.
  * Source code versioning.
