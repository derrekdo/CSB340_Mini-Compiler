/**
 * Main class runs Lexer and Parser end-to-end
 */
public class Main {

    public static void main(String[] args) {
        String[] commandLineArgs = new String[1];
        String[] lexerFileNames = {"99bottles.c", "count.c", "file1.c", "file2.c", "fizzbuzz.c", "loop.py", "hello.t"};
        String[] parserFileNames = {"myLexed99bottles.lex", "myLexedcount.lex",
            "myLexedfizzbuzz.lex", "myLexedloop.lex", "myLexedhello.lex", "myLexedprime.lex", "hello.lex", "count.lex",
            "loop.lex", "myLexedfile1.lex", "myLexedfile2.lex"};


        for (String fileName : lexerFileNames) {
            commandLineArgs[0] = fileName;
            Lexer.main(commandLineArgs);
        }

        for (String fileName : parserFileNames) {
            commandLineArgs[0] = fileName;
            Parser.main(commandLineArgs);
        }
    }
}
