import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Lexer Class.
 *
 * @author (updated by) Derrek Do
 */
public class Lexer {
    private int line;
    private int pos;
    private int position;
    private char chr;
    private String s;

    Map<String, TokenType> keywords = new HashMap<>();

    static class Token {
        public TokenType tokentype;
        public String value;
        public int line;
        public int pos;

        Token(TokenType token, String value, int line, int pos) {
            this.tokentype = token;
            this.value = value;
            this.line = line;
            this.pos = pos;
        }

        @Override
        public String toString() {
            String result = String.format("%5d  %5d %-15s", this.line, this.pos, this.tokentype);
            switch (this.tokentype) {
                case Integer:
                    result += String.format("  %4s", value);
                    break;
                case Identifier:
                    result += String.format(" %s", value);
                    break;
                case String:
                    result += String.format(" \"%s\"", value);
                    break;
            }
            return result;
        }
    }

    static enum TokenType {
        End_of_input, Op_multiply, Op_divide, Op_mod, Op_add, Op_subtract,
        Op_negate, Op_not, Op_less, Op_lessequal, Op_greater, Op_greaterequal,
        Op_equal, Op_notequal, Op_assign, Op_and, Op_or, Keyword_if,
        Keyword_else, Keyword_while, Keyword_print, Keyword_putc, LeftParen, RightParen,
        LeftBrace, RightBrace, Semicolon, Comma, Identifier, Integer, String
    }

    static void error(int line, int pos, String msg) {
        if (line > 0 && pos > 0) {
            System.out.printf("%s in line %d, pos %d\n", msg, line, pos);
        } else {
            System.out.println(msg);
        }
        System.exit(1);
    }

    Lexer(String source) {
        this.line = 1;
        this.pos = 0;
        this.position = 0;
        this.s = source;
        this.chr = this.s.charAt(0);
        this.keywords.put("if", TokenType.Keyword_if);
        this.keywords.put("else", TokenType.Keyword_else);
        this.keywords.put("print", TokenType.Keyword_print);
        this.keywords.put("putc", TokenType.Keyword_putc);
        this.keywords.put("while", TokenType.Keyword_while);

    }

    /**
     * determines the operator type of the current token based on the second operator character following it
     *
     * @param expect the second operator in the token
     * @param ifyes  the token type if the second character is the expected operator
     * @param ifno   the token type if not
     * @param line   the current line in the file
     * @param pos    the current position in the line
     * @return a new token object,  with token type of either of the inputs
     */
    Token follow(char expect, TokenType ifyes, TokenType ifno, int line, int pos) {
        if (getNextChar() == expect) {
            getNextChar();
            prevChar();
            return new Token(ifyes, "", line, pos);
        }
        if (ifno == TokenType.End_of_input) {
            error(line, pos, String.format("follow: unrecognized character: (%d) '%c'", (int) this.chr, this.chr));
        }
        prevChar();
        return new Token(ifno, "", line, pos);
    }

    /**
     * finds the ascii value of the character
     *
     * @param line the current line in the file
     * @param pos  the current position in the line
     * @return new token object of type integer, and the ascii value
     */
    Token char_lit(int line, int pos) { // handle character literals
        char c = getNextChar(); // skip opening quote
        int n = (int) c;
        getNextChar();

        return new Token(TokenType.Integer, "" + n, line, pos);
    }

    /**
     * builds the entire string within the quotation marks
     *
     * @param line the current line the file
     * @param pos  the current position in the line
     * @return new token object with token type string
     */
    Token string_lit(int line, int pos) { // handle string literals
        String result = "";
        while (getNextChar() != '\"') {
            result += chr;
        }
        return new Token(TokenType.String, result, line, pos);
    }

    /**
     * determines if the current character is being used as a comment or division operator
     * Ignores the entire commment
     *
     * @param line current line in  file
     * @param pos  current position in line
     * @return new token object with token type divide
     */
    Token div_or_comment(int line, int pos) { // handle division or comments
        chr = getNextChar();
        if (Character.isWhitespace(chr) || isNumber(chr) || isLetter(chr)) {
            prevChar();
            return new Token(TokenType.Op_divide, "", line, pos);
        } else if (chr == '/') {
            while (getNextChar() != '\n') {
                getNextChar();
            }
        } else {
            while (chr != '/') {
                getNextChar();
            }
        }
        getNextChar();
        return getToken();
    }

    /**
     * determines if the character is being used as a negate or subtract operator
     *
     * @param line the current line in file
     * @param pos  the current postion in the line
     * @return new token object of eithe token type negate or subtract
     */
    Token negate_or_subtract(int line, int pos) {
        if (Character.isWhitespace(getNextChar())) {
            return new Token(TokenType.Op_subtract, "", line, pos);
        }
        prevChar();
        return new Token(TokenType.Op_negate, "", line, pos);
    }

    /**
     * checks if the current char is part of an identifier, keyword, or an integer
     *
     * @param line the current line in the file
     * @param pos  the current position on the line
     * @return new token object with a token type of keyword or identifier or integer
     */
    Token identifier_or_integer(int line, int pos) { // handle identifiers and integers
        String text = "";
        if (isLetter(chr)) {
            while (!Character.isWhitespace(chr)) {
                text += chr;
                getNextChar();
                if (!isLetter(chr) && !isNumber(chr)) {
                    prevChar();
                    break;
                }
            }
            if (keywords.containsKey(text)) {
                return new Token(keywords.get(text), text, line, pos);
            } else {
                return new Token(TokenType.Identifier, text, line, pos);
            }
        } else if (isNumber(chr)) {
            while (!Character.isWhitespace(chr)) {
                text += chr;
                getNextChar();
                if (!isNumber(chr)) {
                    prevChar();
                    break;
                }
            }
        }
        return new Token(TokenType.Integer, text, line, pos);
    }

    /**
     * Determines if the current char is a letter, works for both upper and lower case
     *
     * @param chr the current char
     * @return true if it is a letter, otherwise return false
     */
    boolean isLetter(char chr) {
        return (64 < (int) chr && (int) chr < 91) || (96 < (int) chr && (int) chr < 123);
    }

    /**
     * Determines if the current char is a letter, works for both upper and lower case
     *
     * @param chr the current char
     * @return true if it is a number, otherwise return false
     */
    boolean isNumber(char chr) {
        return 47 < (int) chr && (int) chr < 58;
    }

    /**
     * checks the current char and determines which token type it is
     *
     * @return a new Token object for current token
     */
    Token getToken() {
        int line, pos;
        while (Character.isWhitespace(this.chr)) {
            getNextChar();
        }
        line = this.line;
        pos = this.pos;

        switch (this.chr) {
            case '\u0000':
                return new Token(TokenType.End_of_input, "", this.line, this.pos);
            // remaining case statements -,",',.

            case '*':
                return new Token(TokenType.Op_multiply, "", line, pos);

            case '/':
                return div_or_comment(line, pos);

            case '%':
                return new Token(TokenType.Op_mod, "", line, pos);

            case '+':
                return new Token(TokenType.Op_add, "", line, pos);

            case '-':
                return negate_or_subtract(line, pos);

            case '<':
                return follow('=', TokenType.Op_lessequal, TokenType.Op_less, line, pos);

            case '>':
                return follow('=', TokenType.Op_greaterequal, TokenType.Op_greater, line, pos);

            case '=':
                return follow('=', TokenType.Op_equal, TokenType.Op_assign, line, pos);

            case '!':
                return follow('=', TokenType.Op_notequal, TokenType.Op_not, line, pos);

            case '&':
                return follow('&', TokenType.Op_and, TokenType.String, line, pos);

            case '|':
                return follow('|', TokenType.Op_or, TokenType.String, line, pos);

            case '(':
                return new Token(TokenType.LeftParen, "", line, pos);

            case ')':
                return new Token(TokenType.RightParen, "", line, pos);

            case '{':
                return new Token(TokenType.LeftBrace, "", line, pos);

            case '}':
                return new Token(TokenType.RightBrace, "", line, pos);

            case ';':
                return new Token(TokenType.Semicolon, "", line, pos);

            case ',':
                return new Token(TokenType.Comma, "", line, pos);

            case '\"':
                return string_lit(line, pos);

            case '\'':
                return char_lit(line, pos);

            default:
                return identifier_or_integer(line, pos);
        }
    }

    /**
     * an additional getNextChar() is called when getToken() is called, to prevent infinite loop on operator tokens
     * prevChar() is called to prevent skipping over a char in the file
     */
    void prevChar() {
        this.position--;
        this.pos--;
        this.chr = s.charAt(this.position);
    }

    /**
     * checks the next character in the file
     * and increments the position in the file and position in each line
     *
     * @return the next character
     */
    char getNextChar() {
        // get next character
        this.pos++;
        this.position++;
        if (this.position >= this.s.length()) {
            this.chr = '\u0000';
            return this.chr;
        }
        this.chr = this.s.charAt(this.position);
        if (this.chr == '\n') {
            this.line++;
            this.pos = 0;
        }
        return this.chr;
    }

    String printTokens() {
        Token t;
        StringBuilder sb = new StringBuilder();
        while ((t = getToken()).tokentype != TokenType.End_of_input) {
            getNextChar();
            sb.append(t);
            sb.append("\n");
            System.out.println(t);
        }
        sb.append(t);
        System.out.println(t);
        return sb.toString().stripTrailing();
    }

    /**
     * writes the token, token type, line, and line position of all tokens in a file to a .lex file
     *
     * @param result   the .lex file
     * @param fileName the current input file
     */
    static void outputToFile(String result, String fileName) {
        try {
            FileWriter myWriter = new FileWriter("src/main/resources/myLexed" +
                    fileName.substring(0, fileName.lastIndexOf(".")) + ".lex");
            myWriter.write(result);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        //Array list of each file to be used as input
        ArrayList<String> files = new ArrayList<>();
        if (args.length > 0) {
            files.add(args[0]);
        } else {
            files.add("fizzbuzz.c");
            files.add("prime.c");
            files.add("99bottles.c");
            files.add("file1.c");
            files.add("file2.c");
            files.add("count.c");
            files.add("hello.t");
        }

        for (int i = 0; i < files.size(); i++) {
            String fileName = files.get(i);
            try {
                File f = new File("src/main/resources/" + fileName);
                Scanner s = new Scanner(f);
                String source = " ";
                String result = " ";
                while (s.hasNext()) {
                    source += s.nextLine() + "\n";
                }
                Lexer l = new Lexer(source);
                result = l.printTokens();

                outputToFile(result, fileName);

            } catch (FileNotFoundException e) {
                error(-1, -1, "Exception: " + e.getMessage());
            }
        }
    }
}