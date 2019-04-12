/*

7
3
10
0
9
0
10
0
10
0
8
2
7
0
10
0
10
0
6
4
10


*/
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BowlingScore {
    
    public static void main(String args[]) {
    
        logger = LogManager.getLogger();
        logger.info("Bowling Score Calculator Version "+VERSION);
        
        if (checkRunTests(args)) {
            runTests();
        } else {
            calculateFromConsole();
        }
    
    }

    private static  boolean checkRunTests(String args[]) {
        return args.length==1 && args[0].equals("test");
    }
    
    private static void runTests() {
        TestRunner testRunner = new TestRunner();
        testRunner.run();
    }
    
    private static void calculateFromConsole() {
        Logger logger = LogManager.getLogger();
    
        //get input
        ArrayList<Integer> pinFalls = readPinFalls();
        //calculate score
        String msg = "";
        for (int i=0; i<pinFalls.size(); i++) {
            msg += pinFalls.get(i) + " ";
        }
        logger.info("Calculating score for "+msg);
        BowlingScoreCalculator calculator = new MyCalculator();
        int score = calculator.calculateScore(pinFalls);
        logger.debug("Score= "+Integer.toString(score));  
    }
    
    private static ArrayList<Integer> readPinFalls() {
        ArrayList<Integer> pinFalls = new ArrayList<Integer>();
        
        Scanner keyboard = new Scanner(System.in);
        logger.info("Please enter the pin falls for each roll (enter zero if the roll was not played / not applicable).");
        for (int frame = 1; frame <= 10; frame++) {
            logger.info("Frame "+Integer.toString(frame));
            pinFalls.add(readSinglePinFall(keyboard, frame, 1));
            pinFalls.add(readSinglePinFall(keyboard, frame, 2));
            if (frame==10) {
                //extra roll possible in last frame (10)
                pinFalls.add(readSinglePinFall(keyboard, frame, 3));
            }
        }
        
        return pinFalls;
    }
    
    private static int readSinglePinFall(Scanner keyboard, int frame, int roll) {
        logger.info("Pinfall for frame "+Integer.toString(frame) + " and roll "+roll+": ");
        return keyboard.nextInt();
    }
    
    private static Logger logger;
    private static String VERSION = "1.0.0.0";

}

// -----------------------------------------------------------------
// Bowling score calculator
interface BowlingScoreCalculator {
    int calculateScore(List<Integer> pinFalls);
} 

class MyCalculator implements BowlingScoreCalculator {
    
    enum FrameResult {
        STRIKE, SPARE, OPEN_FRAME
    } 
    
    public int calculateScore(List<Integer> pinFalls) {
        
        Logger logger = LogManager.getLogger();
        
        int runningTotal = 0;
        
        for(int frame=1; frame<=10; frame++) {
            int frameScore = calculateFrameScore(pinFalls, frame);
            runningTotal += frameScore;
            
            logger.debug("Frame: " + Integer.toString(frame) + ", Frame Score: " + Integer.toString(frameScore) + ", Running Total: " + Integer.toString(runningTotal));
        }
        return runningTotal;
    }
    
    private int calculateFrameScore(List<Integer> pinFalls, int frame) {
        int currentRoll = firstRollFromFrame(frame);
        int frameScore = 0;
    
        FrameResult frameResult = determineFrameResult(pinFalls, frame);
        switch (frameResult) {
            case STRIKE:
                if (frame==10) {
                    //last frame: add score for last 3 rolls
                    frameScore = pinFalls.get(currentRoll) + pinFalls.get(currentRoll+1) + pinFalls.get(currentRoll+2);
                } else {
                    //this frame is a strike, so add pinfall of the first roll of next frame
                    frameScore = pinFalls.get(currentRoll) + pinFalls.get(currentRoll+2);
                    
                    int nextFrame = frame + 1;
                    FrameResult nextFrameResult = determineFrameResult(pinFalls, nextFrame);
                    if (nextFrameResult == FrameResult.STRIKE) {
                        //next frame is a strike
                        if (nextFrame == 10) {
                            //next frame is also the last frame, so add the pinfall of the first extra roll
                            frameScore += pinFalls.get(currentRoll+3);
                        } else {
                            //next frame is not the last frame, so add the pinfall of the first roll of the next-next frame
                            frameScore += pinFalls.get(currentRoll+4);
                        }
                    } else {
                        //next frame is not a strike, so add pinfall of the second roll of the next frame
                        frameScore += pinFalls.get(currentRoll+3);
                    }
                }
                break;
            case SPARE: 
                frameScore = pinFalls.get(currentRoll) + pinFalls.get(currentRoll+1) + pinFalls.get(currentRoll+2);
                break;
            default: 
                frameScore = pinFalls.get(currentRoll) + pinFalls.get(currentRoll+1);
                break;
        }
        return frameScore;
    }
    
    private FrameResult determineFrameResult(List<Integer> pinFalls, int frame) {
        int currentRoll = firstRollFromFrame(frame);
        if (pinFalls.get(currentRoll) == 10) {
            return FrameResult.STRIKE;
        } else if (pinFalls.get(currentRoll) + pinFalls.get(currentRoll+1) == 10) {
            return FrameResult.SPARE;
        }
        return FrameResult.OPEN_FRAME; 
    }
    
    private int firstRollFromFrame(int frame) {
        return (frame - 1) * 2;
    }
}
// -----------------------------------------------------------------

// -----------------------------------------------------------------
// Logging code
interface Logger {
    void debug(String message);
    void info(String message);
    void error(String message);
}

class LogManager {
    public static Logger getLogger() {
        if (LOGGER==null) {
            LOGGER = new ConsoleLogger();
        }
        return LOGGER;
    }
    private static Logger LOGGER =null;
}

class ConsoleLogger implements Logger {
    public void debug(String message) {
    	LocalDateTime now = LocalDateTime.now();
        System.out.println("Debug " + formatter.format(now) + " " + message);
    }
    public void info(String message) {
    	LocalDateTime now = LocalDateTime.now();
        System.out.println("Info " + formatter.format(now) + " " + message);
    }
    public void error(String message) {
    	LocalDateTime now = LocalDateTime.now();
        System.err.println("Error " + formatter.format(now) + " " + message);
    }
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

}
// -----------------------------------------------------------------

// -----------------------------------------------------------------
// Test code
class TestRunner {
    
    public TestRunner() {
        logger = LogManager.getLogger();
    }
    
     public void run() {
        logger.debug("running tests");
        addTests();
        runTests();
        showResults();
    }
    
    private void addTests(){
       tests = new Test[] {
            new TestBowlingScore("1", new ArrayList<Integer>(Arrays.asList(1,4, 4,5, 6,4, 5,5, 10,0, 0,1, 7,3, 6,4, 10,0, 2,6)), 123),//Sematall example
            new TestBowlingScore("2", new ArrayList<Integer>(Arrays.asList(10,0, 7,3, 9,0, 10,0, 0,8, 8,2, 0,6, 10,0, 10,0, 10,8,1)), 167),//http://slocums.homestead.com/gamescore.html
            new TestBowlingScore("3", new ArrayList<Integer>(Arrays.asList(7,3, 10,0, 9,0, 10,0, 10,0, 8,2, 7,0, 10,0, 10,0, 6,4,10)), 186),//http://ten-pin-bowling.com/how-to-bowl/how-to-score-bowling.php
            new TestBowlingScore("4", new ArrayList<Integer>(Arrays.asList(10,0, 10,0, 10,0, 10,0, 10,0, 10,0, 10,0, 10,0, 10,0, 10,10,10)), 300),//perfect game
            new TestBowlingScore("5", new ArrayList<Integer>(Arrays.asList(0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0)), 0),//zero game
        };
    }

	private void runTests() {
	    for (int each = 0; each < tests.length; each++){
	        runOneTest(each);
	    }
	}

	private void runOneTest(int anIndex) {
	    tests[anIndex].runAndCaptureAborts();
	}

    private void showResults() {
        for (int each = 0; each < tests.length; each++) {
            logger.info(tests[each].result);}
        showScore();
    }

    private void showScore() {
        int passed = numberPassed();
        float total = (float) tests.length;
        int score = (int)(passed / total * 100);
        logger.info(score + "%");
    }

    private int numberPassed () {
        int passed = 0;
        for (int each = 0; each < tests.length; each++) {
            if (tests[each].success) {
                passed++;
            }
        }
        return passed;
    }
    
    private Test[] tests;
    
    private void runSingleTest(List<Integer> pinFalls, int expectedScore) {
        BowlingScoreCalculator target = new MyCalculator();
        int actualScore = target.calculateScore(pinFalls);
        if (actualScore == expectedScore) {
            logger.error(Integer.toString(actualScore));
        }
        assert actualScore == expectedScore;
        
    }
    
    private Logger logger;
}

//Source: http://wiki.c2.com/?SimpleJavaUnitTestFramework
class Test {
    public boolean success = false;
    public String result = "not run";


    public void run()throws RuntimeException {}


    public void should (boolean aTestPassed, String aMessage){
        if (!aTestPassed) {
            throw new TestFailedException(aMessage);};}


    public void runAndCaptureAborts() {
        try {
            runAndCaptureFailures();}
        catch (RuntimeException exception) {
            success = false;
            exception.fillInStackTrace();
            result = message("Aborted : " + exception.getMessage());}}


    private void runAndCaptureFailures()throws RuntimeException {
        try {
            runAndAllowExceptions();}
		catch (TestFailedException exception) {
			success = false;
			result = message("Failed : " + exception.getMessage());}}


    private void runAndAllowExceptions()throws TestFailedException, RuntimeException {
        run();
        success = true;
        result = message("Passed");}


    private String message(String aString){
        return getClass().getName() + " : " + aString;}
 }


class TestFailedException extends java.lang.RuntimeException {

    TestFailedException(String aMessage){
        super(aMessage);}
}

class TestBowlingScore extends Test {
    
    public TestBowlingScore(String testName, List<Integer> pinFalls, int expectedScore) {
        this.testName = testName;
        this.pinFalls = pinFalls;
        this.expectedScore = expectedScore;
    }
    
    public void run(){
        BowlingScoreCalculator target = new MyCalculator();
        int actualScore = target.calculateScore(pinFalls);
        should (actualScore == expectedScore, testName + "FAILED!");
    }
    
    private String testName;
    private List<Integer> pinFalls;
    private int expectedScore;
}
// -----------------------------------------------------------------
