import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Parser {
    private final List<Token> source;
    private Token token;
    private int position;

    static class Node {
        public NodeType nt;
        public Node left, right;
        public String value;

        Node() {
            this.nt = null;
            this.left = null;
            this.right = null;
            this.value = null;
        }
        Node(NodeType node_type, Node left, Node right, String value) {
            this.nt = node_type;
            this.left = left;
            this.right = right;
            this.value = value;
        }
        public static Node make_node(NodeType nodetype, Node left, Node right) {
            return new Node(nodetype, left, right, "");
        }
        public static Node make_node(NodeType nodetype, Node left) {
            return new Node(nodetype, left, null, "");
        }
        public static Node make_leaf(NodeType nodetype, String value) {
            return new Node(nodetype, null, null, value);
        }
    }

    static class Token {
        public TokenType tokentype;
        public String value;
        public int line;
        public int pos;

        Token(TokenType token, String value, int line, int pos) {
            this.tokentype = token; this.value = value; this.line = line; this.pos = pos;
        }
        @Override
        public String toString() {
            return String.format("%5d  %5d %-15s %s", this.line, this.pos, this.tokentype, this.value);
        }
    }

    static enum TokenType {
        End_of_input(false, false, false, -1, NodeType.nd_None),
        Op_multiply(false, true, false, 13, NodeType.nd_Mul),
        Op_divide(false, true, false, 13, NodeType.nd_Div),
        Op_mod(false, true, false, 13, NodeType.nd_Mod),
        Op_add(false, true, false, 12, NodeType.nd_Add),
        Op_subtract(false, true, false, 12, NodeType.nd_Sub),
        Op_negate(false, false, true, 14, NodeType.nd_Negate),
        Op_not(false, false, true, 14, NodeType.nd_Not),
        Op_less(false, true, false, 10, NodeType.nd_Lss),
        Op_lessequal(false, true, false, 10, NodeType.nd_Leq),
        Op_greater(false, true, false, 10, NodeType.nd_Gtr),
        Op_greaterequal(false, true, false, 10, NodeType.nd_Geq),
        Op_equal(false, true, true, 9, NodeType.nd_Eql),
        Op_notequal(false, true, false, 9, NodeType.nd_Neq),
        Op_assign(false, false, false, -1, NodeType.nd_Assign),
        Op_and(false, true, false, 5, NodeType.nd_And),
        Op_or(false, true, false, 4, NodeType.nd_Or),
        Keyword_if(false, false, false, -1, NodeType.nd_If),
        Keyword_else(false, false, false, -1, NodeType.nd_None),
        Keyword_while(false, false, false, -1, NodeType.nd_While),
        Keyword_print(false, false, false, -1, NodeType.nd_None),
        Keyword_putc(false, false, false, -1, NodeType.nd_None),
        LeftParen(false, false, false, -1, NodeType.nd_None),
        RightParen(false, false, false, -1, NodeType.nd_None),
        LeftBrace(false, false, false, -1, NodeType.nd_None),
        RightBrace(false, false, false, -1, NodeType.nd_None),
        Semicolon(false, false, false, -1, NodeType.nd_None),
        Comma(false, false, false, -1, NodeType.nd_None),
        Identifier(false, false, false, -1, NodeType.nd_Ident),
        Integer(false, false, false, -1, NodeType.nd_Integer),
        String(false, false, false, -1, NodeType.nd_String);

        private final int precedence;
        private final boolean right_assoc;
        private final boolean is_binary;
        private final boolean is_unary;
        private final NodeType node_type;

        TokenType(boolean right_assoc, boolean is_binary, boolean is_unary, int precedence, NodeType node) {
            this.right_assoc = right_assoc;
            this.is_binary = is_binary;
            this.is_unary = is_unary;
            this.precedence = precedence;
            this.node_type = node;
        }
        boolean isRightAssoc() { return this.right_assoc; }
        boolean isBinary() { return this.is_binary; }
        boolean isUnary() { return this.is_unary; }
        int getPrecedence() { return this.precedence; }
        NodeType getNodeType() { return this.node_type; }
    }
    static enum NodeType {
        nd_None(""), nd_Ident("Identifier"), nd_String("String"), nd_Integer("Integer"), nd_Sequence("Sequence"), nd_If("If"),
        nd_Prtc("Prtc"), nd_Prts("Prts"), nd_Prti("Prti"), nd_While("While"),
        nd_Assign("Assign"), nd_Negate("Negate"), nd_Not("Not"), nd_Mul("Multiply"), nd_Div("Divide"), nd_Mod("Mod"), nd_Add("Add"),
        nd_Sub("Subtract"), nd_Lss("Less"), nd_Leq("LessEqual"),
        nd_Gtr("Greater"), nd_Geq("GreaterEqual"), nd_Eql("Equal"), nd_Neq("NotEqual"), nd_And("And"), nd_Or("Or");

        private final String name;

        NodeType(String name) {
            this.name = name;
        }

        @Override
        public String toString() { return this.name; }
    }

    /**
     * Print error message.
     * @param line - line.
     * @param pos - position.
     * @param msg - message.
     */
    static void error(int line, int pos, String msg) {
        if (line > 0 && pos > 0) {
            System.out.printf("%s in line %d, pos %d\n", msg, line, pos);
        } else {
            System.out.println(msg);
        }
        System.exit(1);
    }

    /**
     * Constructor for Parser class.
     * @param source - list of tokens.
     */
    Parser(List<Token> source) {
        this.source = source;
        this.token = null;
        this.position = 0;
    }

    /**
     * Get the next token in the list.
     * @return - Token
     */
    Token getNextToken() {
        this.token = this.source.get(this.position++);
        return this.token;
    }

    /**
     * create nodes for token types such as LeftParen, Op_add, Op_subtract, etc.
     * @param precedence - int
     * @return - Node
     */
    Node expr(int precedence) {
        // create nodes for token types such as LeftParen, Op_add, Op_subtract, etc.
        // TODO: be very careful here and be aware of the precedence rules for the AST tree
        Node result = null;
        Node node = null;
        TokenType op = null;
        int opPrecedence = 0;

        if (this.token.tokentype == TokenType.LeftParen) {
            result = parenExpr();
        } else if (this.token.tokentype == TokenType.Op_subtract || this.token.tokentype == TokenType.Op_add) {
            op = this.token.tokentype == TokenType.Op_subtract ? TokenType.Op_negate : TokenType.Op_add;
            getNextToken();
            node = expr(TokenType.Op_negate.getPrecedence());
            if (op == TokenType.Op_negate) {
                result = Node.make_node(NodeType.nd_Negate, node);
            } else {
                result = node;
            }
        } else if (this.token.tokentype == TokenType.Op_not) {
            getNextToken();
            result = Node.make_node(NodeType.nd_Not, expr(TokenType.Op_not.getPrecedence()));
        } else if (this.token.tokentype == TokenType.Identifier) {
            result = Node.make_leaf(NodeType.nd_Ident, this.token.value);
            getNextToken();
        } else if (this.token.tokentype == TokenType.Integer) {
            result = Node.make_leaf(NodeType.nd_Integer, this.token.value);
            getNextToken();
        } else {
            error(this.token.line, this.token.pos, this.token.value);
        }

        while (this.token.tokentype.isBinary() && this.token.tokentype.getPrecedence() >= precedence) {
            op = this.token.tokentype;
            getNextToken();
            opPrecedence = this.token.tokentype.getPrecedence();

            if (!op.isRightAssoc()) {
                opPrecedence++;
            }

            node = expr(opPrecedence);
            result = Node.make_node(op.node_type, result, node);
        }
        return result;
    }

    /**
     * Handles left and right parenthesis and braces.
     * @return - Node
     */
    Node parenExpr() {
        Node node = null;
        expect("paren_expr", TokenType.LeftParen);
        node = expr(0);
        expect("paren_expr", TokenType.RightParen);
        return node;
    }

    /**
     * Error handler for checking tokens.
     * Check if token is the right type and if not raise an error.
     * @param msg - string message passed to error handler.
     * @param s - TokenType
     */
    void expect(String msg, TokenType s) {
        if (this.token.tokentype == s) {
            getNextToken();
            return;
        }
        error(this.token.line, this.token.pos, msg + ": Expecting '" + s + "', found: '" + this.token.tokentype + "'");
    }

    /**
     * Handles TokenTypes such as Keyword_if, Keyword_else, nd_If, Keyword_print, etc.
     * @return - Node
     */
    Node stmt() {
        Node s, s2, t = null, expression, value;

        if (this.token.tokentype == TokenType.Keyword_if) {
            getNextToken();
            expression = parenExpr();
            s = stmt();
            s2 = null;
            if (this.token.tokentype == TokenType.Keyword_else) {
                getNextToken();
                s2 = stmt();
            }
            t = Node.make_node(NodeType.nd_If, expression, Node.make_node(NodeType.nd_If, s, s2));
        } else if (this.token.tokentype == TokenType.Keyword_putc) {
            getNextToken();
            expression = parenExpr();
            t = Node.make_node(NodeType.nd_Prtc, expression);
            expect(TokenType.Keyword_putc.name(), TokenType.Semicolon);
        } else if (this.token.tokentype == TokenType.Keyword_print) {
            getNextToken();
            expect(TokenType.Keyword_print.name(), TokenType.LeftParen);
            while (true) {
                if (this.token.tokentype == TokenType.String) {
                    expression = Node.make_node(NodeType.nd_Prts, Node.make_leaf(NodeType.nd_String, this.token.value));
                    getNextToken();
                } else {
                    expression = Node.make_node(NodeType.nd_Prti, expr(0));
                }

                t = Node.make_node(NodeType.nd_Sequence, t, expression);

                if (this.token.tokentype != TokenType.Comma) {
                    break;
                }
                getNextToken();
            }
            expect(TokenType.Keyword_print.name(), TokenType.RightParen);
            expect(TokenType.Keyword_print.name(), TokenType.Semicolon);
        } else if (this.token.tokentype == TokenType.Semicolon) {
            getNextToken();
        } else if (this.token.tokentype == TokenType.Identifier) {
            value = Node.make_leaf(NodeType.nd_Ident, this.token.value);
            getNextToken();
            expect(TokenType.Op_assign.name(), TokenType.Op_assign);
            expression = expr(0);
            t = Node.make_node(NodeType.nd_Assign, value, expression);
            expect(TokenType.Op_assign.name(), TokenType.Semicolon);
        } else if (this.token.tokentype == TokenType.Keyword_while) {
            getNextToken();
            expression = parenExpr();
            s = stmt();
            t = Node.make_node(NodeType.nd_While, expression, s);
        } else if (this.token.tokentype == TokenType.LeftBrace) {
            getNextToken();
            while (this.token.tokentype != TokenType.RightBrace && this.token.tokentype != TokenType.End_of_input) {
                t = Node.make_node(NodeType.nd_Sequence, t, stmt());
            }
            expect(TokenType.LeftBrace.name(), TokenType.RightBrace);
        } else if (this.token.tokentype == TokenType.End_of_input) {
            assert true;
        } else {
            error(this.token.line, this.token.pos, "Expected start of statement, instead found: " + this.token);
        }

        return t;
    }

    /**
     * Parses token and returns a Node.
     * @return - Node
     */
    Node parse() {
        Node t = null;
        getNextToken();
        while (this.token.tokentype != TokenType.End_of_input) {
            t = Node.make_node(NodeType.nd_Sequence, t, stmt());
        }
        return t;
    }

    /**
     * Print AST.
     * @param t - Node.
     * @param sb - StringBuilder.
     * @return - String representation of AST.
     */
    String printAST(Node t, StringBuilder sb) {
//        System.out.println("In printAST");
        int i = 0;
        if (t == null) { // TODO: anytime a null node is encountered it prints this semi colon so I have nulls when I shouldn't
            sb.append(";");
            sb.append("\n");
            System.out.println(";");
        } else {
            sb.append(t.nt);
            System.out.printf("%-14s", t.nt);
            if (t.nt == NodeType.nd_Ident || t.nt == NodeType.nd_Integer || t.nt == NodeType.nd_String) {
                sb.append(" " + t.value);
                sb.append("\n");
                System.out.println(" " + t.value);
            } else {
                sb.append("\n");
                System.out.println();
                printAST(t.left, sb);
                printAST(t.right, sb);
            }

        }
        return sb.toString();
    }

    static void outputToFile(String result) { // TODO: Add string filename as param
        try {
            FileWriter myWriter = new FileWriter("src/main/resources/test_print.par");
            myWriter.write(result);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static HashMap<String, TokenType> createStringToTokensMap() {
        HashMap<String, TokenType> map = new HashMap<>();
        map.put("Op_multiply", TokenType.Op_multiply);
        map.put("Op_divide", TokenType.Op_divide);
        map.put("Op_mod", TokenType.Op_mod);
        map.put("Op_add", TokenType.Op_add);
        map.put("Op_subtract", TokenType.Op_subtract);
        map.put("Op_negate", TokenType.Op_negate);
        map.put("Op_not", TokenType.Op_not);
        map.put("Op_less", TokenType.Op_less);
        map.put("Op_lessequal", TokenType.Op_lessequal);
        map.put("Op_greater", TokenType.Op_greater);
        map.put("Op_greaterequal", TokenType.Op_greaterequal);
        map.put("Op_equal", TokenType.Op_equal);
        map.put("Op_notequal", TokenType.Op_notequal);
        map.put("Op_assign", TokenType.Op_assign);
        map.put("Op_and", TokenType.Op_and);
        map.put("Op_or", TokenType.Op_or);
        map.put("Keyword_if", TokenType.Keyword_if);
        map.put("Keyword_else", TokenType.Keyword_else);
        map.put("Keyword_while", TokenType.Keyword_while);
        map.put("Keyword_print", TokenType.Keyword_print);
        map.put("Keyword_putc", TokenType.Keyword_putc);
        map.put("LeftParen", TokenType.LeftParen);
        map.put("RightParen", TokenType.RightParen);
        map.put("LeftBrace", TokenType.LeftBrace);
        map.put("RightBrace", TokenType.RightBrace);
        map.put("Semicolon", TokenType.Semicolon);
        map.put("Comma", TokenType.Comma);
        map.put("Identifier", TokenType.Identifier);
        map.put("Integer", TokenType.Integer);
        map.put("String", TokenType.String);
        map.put("End_of_input", TokenType.End_of_input);
        return map;
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            String filename = args[0];
            try {
                StringBuilder value;
                String token;
                String result = "";
                StringBuilder sb = new StringBuilder();
                int line, pos;
                Token t;
                boolean found;
                List<Token> list = new ArrayList<>();
                Map<String, TokenType> str_to_tokens = createStringToTokensMap();

                Scanner s = new Scanner(new File("src/main/resources/" + filename));
                String source = " ";
                while (s.hasNext()) {
                    String str = s.nextLine();
                    StringTokenizer st = new StringTokenizer(str);
                    line = Integer.parseInt(st.nextToken());
                    pos = Integer.parseInt(st.nextToken());
                    token = st.nextToken();
                    value = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        value.append(st.nextToken()).append(" ");
                    }
                    found = false;
                    if (str_to_tokens.containsKey(token)) {
                        found = true;
                        t = new Token(str_to_tokens.get(token), value.toString(), line, pos);
                        list.add(t);
                    }
                    if (!found) {
                        throw new Exception("Token not found: '" + token + "'");
                    }
                }

                Parser parser = new Parser(list);
                result = parser.printAST(parser.parse(), sb);
                outputToFile(result);
            } catch (FileNotFoundException e) {
                error(-1, -1, "Exception: " + e.getMessage());
            } catch (Exception e) {
                error(-1, -1, "Exception: " + e.getMessage());
            }
        } else {
            error(-1, -1, "No args");
        }
    }
}