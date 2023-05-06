import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * These parameterized tests use the provided files so that we know
 * they are doing the right thing.
 * There are a total of 3 tests for the Lexer and 3 tests for the Parser.
 * To see the output of all the files, including the one's authored by
 * Derrek Do navigate to the Main file and run that.
 *
 * @author jared
 */
class IntegrationTests {

    boolean fileContentsEqual(String expectedFileName, String actualFileName) {
        File expectedFile = new File("src/main/resources/" + expectedFileName);
        File actualFile = new File("src/main/resources/" + actualFileName);
        try {
            Scanner expectedReader = new Scanner(expectedFile);
            Scanner actualReader = new Scanner(actualFile);
            while (expectedReader.hasNextLine()) {
                String expectedLine = expectedReader.nextLine();
                String actualLine = actualReader.nextLine();
                if (!Objects.equals(expectedLine.stripTrailing(), actualLine.stripTrailing())) {
                    return false;
                }
            }
            expectedReader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello.t", "count.c", "loop.py"})
    void testLexer(String fileName) {
        String[] args = {fileName};
        String fileNameNoFileExt = fileName.substring(0, fileName.lastIndexOf("."));
        String expectedLexFileName = "myLexed" + fileNameNoFileExt + ".lex";
        Lexer.main(args);
        assertTrue(fileContentsEqual(fileNameNoFileExt + ".lex", expectedLexFileName));
    }


    @ParameterizedTest
    @ValueSource(strings = {"hello", "count", "loop"})
    void TestParser(String fileName) {
        String[] args = {fileName + ".lex"};
        Parser.main(args);
        assertTrue(fileContentsEqual(fileName + ".par", "myParsed" + fileName + ".par"));
    }
}