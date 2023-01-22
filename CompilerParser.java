import java.util.*;
import java.text.ParseException;

class CompilerParser {
    LinkedList<Token> tokens;
    List<String> keywords = Arrays.asList();
    // List<String> keywords = Arrays.asList("class", "constructor", "function",
    // "method", "field", "static", "var",
    // "int", "char", "boolean", "void", "true", "false", "null", "this", "let",
    // "do", "if", "else", "while",
    // "return");

    /**
     * Constructor for the CompilerParser
     *
     * @param tokens A linked list of tokens to be parsed
     */
    public CompilerParser(LinkedList<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Generates a parse tree for a single program
     */
    public ParseTree compileProgram() throws ParseException {
        if (this.tokens.peek() == null)
            return null;
        else if (this.tokens.peek().getType() == "keyword" && this.tokens.peek().getValue() == "class") {
            ParseTree output = compileClass();
            return output;
        } else
            throw new ParseException("", 0);
    }

    /**
     * Generates a parse tree for a single class
     * 'class' className '{' classVarDec* subroutineDec* '}'
     */
    public ParseTree compileClass() throws ParseException {
        ParseTree output = new ParseTree("class", "");
        if (this.tokens.peek().getType() == "keyword" && this.tokens.peek().getValue() == "class") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // className: identifier
        if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A'
                        && this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))
                && !keywords.contains(this.tokens.peek().getValue())) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // {
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "{") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // check for classVar and subroutine
        while (this.tokens.peek().getType() == "keyword"
                && (this.tokens.peek().getValue() == "static"
                        || this.tokens.peek().getValue() == "field"
                        || this.tokens.peek().getValue() == "constructor"
                        || this.tokens.peek().getValue() == "method"
                        || this.tokens.peek().getValue() == "function")) {
            if (this.tokens.peek().getType() == "keyword"
                    && (this.tokens.peek().getValue() == "static" ||
                            this.tokens.peek().getValue() == "field")) {
                output.addChild(compileClassVarDec());
            } else if (this.tokens.peek().getType() == "keyword" &&
                    (this.tokens.peek().getValue() == "constructor"
                            || this.tokens.peek().getValue() == "function" ||
                            this.tokens.peek().getValue() == "method")) {
                output.addChild(compileSubroutine());
            } else
                throw new ParseException("", 0);

        }
        // }
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "}") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        return output;
    }

    /**
     * Generates a parse tree for a static variable declaration or field declaration
     * ('static'|'field') type varName (',' varName)* ';'
     *
     * type: 'int'|'char'|'boolean'|className
     * className: not starts with digit
     */
    public ParseTree compileClassVarDec() throws ParseException {
        ParseTree output = new ParseTree("classVarDec", "");
        // 'static' | 'field'
        if (this.tokens.peek().getType() == "keyword"
                && (this.tokens.peek().getValue() == "static"
                        || this.tokens.peek().getValue() == "field")) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // type
        if (this.tokens.peek().getType() == "keyword"
                && (this.tokens.peek().getValue() == "int"
                        || this.tokens.peek().getValue() == "char"
                        || this.tokens.peek().getValue() == "boolean")) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A' && this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))
                && (keywords.contains(this.tokens.peek().getValue()) == false)) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // varName
        if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A' && this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))
                && (keywords.contains(this.tokens.peek().getValue()) == false)) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // , varName
        while (this.tokens.peek().getType() == "symbol"
                && this.tokens.peek().getValue() == ",") {
            // ,
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();

            // varName
            if (this.tokens.peek().getType() == "identifier"
                    && ((this.tokens.peek().getValue().charAt(0) >= 'A'
                            && this.tokens.peek().getValue().charAt(0) <= 'Z')
                            || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                    && this.tokens.peek().getValue().charAt(0) <= 'z')
                            || (this.tokens.peek().getValue().charAt(0) == '_'))
                    && (keywords.contains(this.tokens.peek().getValue()) == false)) {
                output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
                this.tokens.pop();
            } else
                throw new ParseException("", 0);
        }

        // ;
        if (this.tokens.peek().getType() == "symbol"
                && this.tokens.peek().getValue() == ";") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        return output;
    }

    /**
     * Generates a parse tree for a method, function, or constructor
     * ('constructor'|'function'|'method') ('void'|type) subroutineName
     * '(' parameterList ')' subroutineBody
     */
    public ParseTree compileSubroutine() throws ParseException {
        ParseTree output = new ParseTree("subroutine", "");
        // 'constructor'|'function'|'method'
        if (this.tokens.peek().getType() == "keyword"
                && (this.tokens.peek().getValue() == "constructor"
                        || this.tokens.peek().getValue() == "function"
                        || this.tokens.peek().getValue() == "method")) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // 'void' | type
        // void
        if (this.tokens.peek().getType() == "keyword"
                && (this.tokens.peek().getValue() == "void"
                        || this.tokens.peek().getValue() == "int"
                        || this.tokens.peek().getValue() == "char"
                        || this.tokens.peek().getValue() == "boolean")) {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
        } else if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A' &&
                        this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))
                && (keywords.contains(this.tokens.peek().getValue()) == false)) {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // subroutineName
        if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A' &&
                        this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))) {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // (
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "(") {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // with parameter
        if ((this.tokens.peek().getType() == "keyword"
                && (this.tokens.peek().getValue() == "int"
                        || this.tokens.peek().getValue() == "char"
                        || this.tokens.peek().getValue() == "boolean"))
                || this.tokens.peek().getType() == "identifier"
                        && ((this.tokens.peek().getValue().charAt(0) >= 'A'
                                && this.tokens.peek().getValue().charAt(0) <= 'Z')
                                || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                        && this.tokens.peek().getValue().charAt(0) <= 'z')
                                || (this.tokens.peek().getValue().charAt(0) == '_'))
                        && (keywords.contains(this.tokens.peek().getValue()) == false))
            output.addChild(compileParameterList());

        // )
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == ")") {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // subroutineBody
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "{")
            output.addChild(compileSubroutineBody());
        else
            throw new ParseException("", 0);
        return output;
    }

    /**
     * Generates a parse tree for a subroutine's parameters
     * ((type varName) (',' type varName)*)?
     */
    public ParseTree compileParameterList() throws ParseException {
        ParseTree output = new ParseTree("parameterList", "");
        // type
        if (this.tokens.peek().getType() == "keyword"
                && (this.tokens.peek().getValue() == "int"
                        || this.tokens.peek().getValue() == "char"
                        || this.tokens.peek().getValue() == "boolean")) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A' && this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))
                && (keywords.contains(this.tokens.peek().getValue()) == false)) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // varName
        if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A' && this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))
                && (keywords.contains(this.tokens.peek().getValue()) == false)) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // , type varName
        while (this.tokens.peek() != null
                && this.tokens.peek().getType() == "symbol"
                && this.tokens.peek().getValue() == ",") {
            // ,
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
            // type
            if (this.tokens.peek().getType() == "keyword"
                    && (this.tokens.peek().getValue() == "int"
                            || this.tokens.peek().getValue() == "char"
                            || this.tokens.peek().getValue() == "boolean")) {
                output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
                this.tokens.pop();
            } else if (this.tokens.peek().getType() == "identifier"
                    && ((this.tokens.peek().getValue().charAt(0) >= 'A'
                            && this.tokens.peek().getValue().charAt(0) <= 'Z')
                            || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                    && this.tokens.peek().getValue().charAt(0) <= 'z')
                            || (this.tokens.peek().getValue().charAt(0) == '_'))
                    && (keywords.contains(this.tokens.peek().getValue()) == false)) {
                output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
                this.tokens.pop();
            } else
                throw new ParseException("", 0);
            // varName
            if (this.tokens.peek().getType() == "identifier"
                    && ((this.tokens.peek().getValue().charAt(0) >= 'A'
                            && this.tokens.peek().getValue().charAt(0) <= 'Z')
                            || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                    && this.tokens.peek().getValue().charAt(0) <= 'z')
                            || (this.tokens.peek().getValue().charAt(0) == '_'))
                    && (keywords.contains(this.tokens.peek().getValue()) == false)) {
                output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
                this.tokens.pop();
            } else
                throw new ParseException("", 0);
        }
        return output;
    }

    /**
     * Generates a parse tree for a subroutine's body
     * {' varDec* statements '}'
     */
    public ParseTree compileSubroutineBody() throws ParseException {
        ParseTree output = new ParseTree("subroutineBody", "");
        // {
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "{") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);
        // varDec or statements
        while (this.tokens.peek().getValue() != "}") {
            if (this.tokens.peek().getType() == "keyword"
                    && this.tokens.peek().getValue() == "var") {
                output.addChild(compileVarDec());
            } else if (this.tokens.peek().getType() == "keyword"
                    && (this.tokens.peek().getValue() == "let"
                            || this.tokens.peek().getValue() == "do"
                            || this.tokens.peek().getValue() == "if"
                            || this.tokens.peek().getValue() == "while"
                            || this.tokens.peek().getValue() == "return")) {
                output.addChild(compileStatements());
            }
        }

        // // varDec
        // while (this.tokens.peek().getType() == "keyword" &&
        // this.tokens.peek().getValue() == "var") {
        // output.addChild(compileVarDec());
        // }

        // // statements
        // while (this.tokens.peek().getType() == "keyword"
        // && (this.tokens.peek().getValue() == "let"
        // || this.tokens.peek().getValue() == "if"
        // || this.tokens.peek().getValue() == "while"
        // || this.tokens.peek().getValue() == "do"
        // || this.tokens.peek().getValue() == "return")) {
        // output.addChild(compileStatements());
        // }
        // }
        if (this.tokens.peek().getType() == "symbol"
                && this.tokens.peek().getValue() == "}") {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);
        return output;
    }

    /**
     * Generates a parse tree for a variable declaration
     * 'var' type varname (',' varName)* ';'
     * type: 'int'|'char'|'boolean'|className
     *
     */
    public ParseTree compileVarDec() throws ParseException {
        ParseTree output = new ParseTree("varDec", "");

        // var
        if (this.tokens.peek().getType() == "keyword" && this.tokens.peek().getValue() == "var") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // type
        if (this.tokens.peek().getType() == "keyword"
                && (this.tokens.peek().getValue() == "int"
                        || this.tokens.peek().getValue() == "char"
                        || this.tokens.peek().getValue() == "boolean")) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A' && this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))
                && (keywords.contains(this.tokens.peek().getValue()) == false)) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // varName
        if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A' && this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // , type varName
        while (this.tokens.peek().getType() == "symbol"
                && this.tokens.peek().getValue() == ",") {
            // ,
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
            // varName
            if (this.tokens.peek().getType() == "identifier"
                    && ((this.tokens.peek().getValue().charAt(0) >= 'A'
                            && this.tokens.peek().getValue().charAt(0) <= 'Z')
                            || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                    && this.tokens.peek().getValue().charAt(0) <= 'z')
                            || (this.tokens.peek().getValue().charAt(0) == '_'))
                    && (keywords.contains(this.tokens.peek().getValue()) == false)) {
                output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
                this.tokens.pop();
            } else
                throw new ParseException("", 0);
        }
        // ;
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == ";") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);
        return output;

    }

    /**
     * Generates a parse tree for a series of statements
     * statement*
     * letStatement | ifStatement | whileStatement | doStatement | returnStatement
     */
    public ParseTree compileStatements() throws ParseException {
        ParseTree output = new ParseTree("statements", "");
        // if (this.tokens.size() > 1 && this.tokens.peek().getType() == "keyword"
        // && this.tokens.peek().getValue() == "let")
        // output.addChild(compileLet());
        // else if (this.tokens.size() > 1 && this.tokens.peek().getType() == "keyword"
        // && this.tokens.peek().getValue() == "do")
        // output.addChild(compileDo());
        // else if (this.tokens.size() > 1 && this.tokens.peek().getType() == "keyword"
        // && this.tokens.peek().getValue() == "if")
        // output.addChild(compileIf());
        // else if (this.tokens.size() > 1 && this.tokens.peek().getType() == "keyword"
        // && this.tokens.peek().getValue() == "while")
        // output.addChild(compileWhile());
        // else if (this.tokens.size() > 1 && this.tokens.peek().getType() == "keyword"
        // && this.tokens.peek().getValue() == "return")
        // output.addChild(compileReturn());
        while (this.tokens.size() > 1
                && (this.tokens.peek().getType() == "keyword"
                        && (this.tokens.peek().getValue() == "let"
                                || this.tokens.peek().getValue() == "if"
                                || this.tokens.peek().getValue() == "while"
                                || this.tokens.peek().getValue() == "do"
                                || this.tokens.peek().getValue() == "return"))) {
            if (this.tokens.peek().getValue() == "let")
                output.addChild(compileLet());
            else if (this.tokens.peek().getValue() == "if")
                output.addChild(compileIf());
            else if (this.tokens.peek().getValue() == "while")
                output.addChild(compileWhile());
            else if (this.tokens.peek().getValue() == "do")
                output.addChild(compileDo());
            else if (this.tokens.peek().getValue() == "return")
                output.addChild(compileReturn());
            // else
            // throw new ParseException("", 0);
        }
        return output;
    }

    /**
     * Generates a parse tree for a let statement
     * 'let' varName
     * ('['expression']')?
     * '=' expression ';'
     */
    public ParseTree compileLet() throws ParseException {
        ParseTree output = new ParseTree("letStatement", "");
        // let
        if (this.tokens.peek().getType() == "keyword" && this.tokens.peek().getValue() == "let") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // varName
        if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A' && this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))
                && (keywords.contains(this.tokens.peek().getValue()) == false)) {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // -----------with [expression]----------- //
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "[") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
            // expression - skip
            // if (this.tokens.peek().getType() == "keyword"
            // && this.tokens.peek().getValue() == "skip") {
            // output.addChild(new ParseTree(this.tokens.peek().getType(),
            // this.tokens.peek().getValue()));
            // this.tokens.pop();
            // } else
            output.addChild(compileExpression());
            // } else
            // throw new ParseException("", 0);

            // ]
            if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "]") {
                output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
                this.tokens.pop();
            } else
                throw new ParseException("", 0);
        }

        // =
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "=") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // epxression
        if (this.tokens.peek().getType() == "keyword" &&
                this.tokens.peek().getValue() == "skip") {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            output.addChild(compileExpression());
        // } // integerConstant
        // else if (this.tokens.peek().getType() == "integerConstant"
        // && (Integer.valueOf(this.tokens.peek().getValue()) >= 0)
        // && (Integer.valueOf(this.tokens.peek().getValue()) <= 32767)) {
        // output.addChild(new ParseTree(this.tokens.peek().getType(),
        // this.tokens.peek().getValue()));
        // this.tokens.pop();
        // } else
        // throw new ParseException("", 0);
        // output.addChild(compileExpression());

        // ;
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == ";") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        return output;
    }

    /**
     * Generates a parse tree for an if statement
     * 'if' '(' expression ')' '{' statements '}'
     * ('else' '{' statemtents '}')?
     */
    public ParseTree compileIf() throws ParseException {
        ParseTree output = new ParseTree("ifStatement", "");
        // if
        if (this.tokens.peek().getType() == "keyword" && this.tokens.peek().getValue() == "if") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // (
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "(") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);
        // expression - skip
        if (this.tokens.peek().getType() != "symbol" || this.tokens.peek().getValue() != ")")
            output.addChild(compileExpression());
        // if (this.tokens.peek().getType() == "keyword" &&
        // this.tokens.peek().getValue() == "skip") {
        // output.addChild(new ParseTree(this.tokens.peek().getType(),
        // this.tokens.peek().getValue()));
        // this.tokens.pop();
        // } else
        // throw new ParseException("", 0);

        // )
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == ")") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // {
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "{") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // statements
        // while (this.tokens.peek().getType() != "symbol" &&
        // this.tokens.peek().getValue() != "}") {
        output.addChild(compileStatements());
        // }

        // }
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "}") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // else
        if (this.tokens.size() > 1 && this.tokens.peek().getType() == "keyword"
                && this.tokens.peek().getValue() == "else") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
            // {
            if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "{") {
                output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
                this.tokens.pop();
            } else
                throw new ParseException("", 0);
            // statements
            // if (this.tokens.peek().getType() != "symbol" && this.tokens.peek().getValue()
            // != "}")
            output.addChild(compileStatements());
            // }
            if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "}") {
                output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
                this.tokens.pop();
            } else
                throw new ParseException("", 0);
        }
        return output;
    }

    /**
     * Generates a parse tree for a while statement
     * 'while' '(' expression ')' '{' statements '}'
     */
    public ParseTree compileWhile() throws ParseException {
        ParseTree output = new ParseTree("whileStatement", "");

        // while
        if (this.tokens.peek().getType() == "keyword" && this.tokens.peek().getValue() == "while") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        }

        // (
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "(") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // expression
        // if (this.tokens.peek().getType() == "keyword" &&
        // this.tokens.peek().getValue() == "skip") {
        // output.addChild(new ParseTree(this.tokens.peek().getType(),
        // this.tokens.peek().getValue()));
        // this.tokens.pop();
        // } else
        output.addChild(compileExpression());

        // )
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == ")") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // {
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "{") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // statements
        output.addChild(compileStatements());
        // if (this.tokens.peek().getType() != "symbol" && this.tokens.peek().getValue()
        // != "}")
        // output.addChild(compileStatements());
        // }
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "}") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);
        return output;
    }

    /**
     * Generates a parse tree for a do statement
     * 'do' expression ';'
     */
    public ParseTree compileDo() throws ParseException {
        ParseTree output = new ParseTree("doStatement", "");
        // do
        if (this.tokens.peek().getType() == "keyword" && this.tokens.peek().getValue() == "do") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);

        // epxression
        // if (this.tokens.peek().getType() == "keyword" &&
        // this.tokens.peek().getValue() == "skip") {
        // output.addChild(new ParseTree(this.tokens.peek().getType(),
        // this.tokens.peek().getValue()));
        // this.tokens.pop();
        // } else
        // throw new ParseException("", 0);
        output.addChild(compileExpression());
        // ;
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == ";") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else
            throw new ParseException("", 0);
        return output;
    }

    /**
     * Generates a parse tree for a return statement
     * 'return' (expression)? ';'
     */
    public ParseTree compileReturn() throws ParseException {
        ParseTree output = new ParseTree("returnStatement", "");
        // return
        output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
        this.tokens.pop();
        // epxression
        // if (this.tokens.peek().getType() == "keyword" &&
        // this.tokens.peek().getValue() == "skip") {
        // output.addChild(new ParseTree(this.tokens.peek().getType(),
        // this.tokens.peek().getValue()));
        // this.tokens.pop();
        // } else if (this.tokens.peek().getType() == "symbol" &&
        // this.tokens.peek().getValue() == ";") {
        // output.addChild(new ParseTree(this.tokens.peek().getType(),
        // this.tokens.peek().getValue()));
        // this.tokens.pop();
        // } else
        // throw new ParseException("", 0);
        //
        // if (this.tokens.peek().getType() != "symbol" && this.tokens.peek().getValue()
        // != ";")
        // output.addChild(compileExpression());
        // ;
        if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == ";") {
            output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
            this.tokens.pop();
        } else {
            output.addChild(compileExpression());
            if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == ";") {
                output.addChild(new ParseTree(this.tokens.peek().getType(), this.tokens.peek().getValue()));
                this.tokens.pop();
            } else
                throw new ParseException("", 0);
        }

        return output;
    }

    /**
     * Generates a parse tree for an expression
     * 'skip' | term(op term)
     */
    public ParseTree compileExpression() throws ParseException {
        ParseTree output = new ParseTree("expression", "");
        // 'skip'
        if (this.tokens.peek().getType() == "keyword" &&
                this.tokens.peek().getValue() == "skip") {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
        } else {
            // term
            output.addChild(compileTerm());
            // op term
            if (this.tokens.size() > 1 && this.tokens.peek().getValue() != "]"
                    && this.tokens.peek().getValue() != ")"
                    && this.tokens.peek().getValue() != ","
                    && this.tokens.peek().getValue() != ";") {
                if (this.tokens.peek().getType() == "symbol"
                        && (this.tokens.peek().getValue() == "+"
                                || this.tokens.peek().getValue() == "-"
                                || this.tokens.peek().getValue() == "*"
                                || this.tokens.peek().getValue() == "/"
                                || this.tokens.peek().getValue() == "&"
                                || this.tokens.peek().getValue() == "|"
                                || this.tokens.peek().getValue() == "<"
                                || this.tokens.peek().getValue() == ">"
                                || this.tokens.peek().getValue() == "=")) {
                    // op
                    output.addChild(new ParseTree(this.tokens.peek().getType(),
                            this.tokens.peek().getValue()));
                    this.tokens.pop();
                    // term
                    output.addChild(compileTerm());
                }
            }
        }
        // else
        // throw new ParseException("", 0);
        return output;
    }

    /**
     * Generates a parse tree for an expression term
     * integerConstant|stringConstant|keywordConstant|varName|varName'['expression']'
     * |'('expression')'|(unaryOp term)|subroutineCall
     */
    public ParseTree compileTerm() throws ParseException {
        ParseTree output = new ParseTree("term", "");
        // integerConstant
        if (this.tokens.peek().getType() == "integerConstant"
                && (Integer.valueOf(this.tokens.peek().getValue()) >= 0)
                && (Integer.valueOf(this.tokens.peek().getValue()) <= 32767)) {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
        }
        // stringConstant, remove double quote and new line
        else if (this.tokens.peek().getType() == "stringConstant") {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue().substring(1,
                            this.tokens.peek().getValue().length() - 1).replace("\n",
                                    "")));
            this.tokens.pop();
        }
        // keywordCostant
        else if (this.tokens.peek().getType() == "keyword"
                && (this.tokens.peek().getValue() == "true"
                        || this.tokens.peek().getValue() == "false"
                        || this.tokens.peek().getValue() == "null"
                        || this.tokens.peek().getValue() == "this")) {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
        }
        // (expression)
        else if (this.tokens.peek().getType() == "symbol" &&
                this.tokens.peek().getValue() == "(") {
            // (
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
            // expression
            output.addChild(compileExpression());
            // )
            if (this.tokens.peek().getType() != "symbol" || this.tokens.peek().getValue() != ")")
                throw new ParseException("", 0);
            else {
                output.addChild(new ParseTree(this.tokens.peek().getType(),
                        this.tokens.peek().getValue()));
                this.tokens.pop();
            }
            // if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue()
            // == ")") {
            // output.addChild(new ParseTree(this.tokens.peek().getType(),
            // this.tokens.peek().getValue()));
            // this.tokens.pop();
            // } else
            // throw new ParseException("", 0);
        }
        // unaryOp term
        else if (this.tokens.peek().getType() == "symbol"
                && (this.tokens.peek().getValue() == "-" || this.tokens.peek().getValue() == "~")) {
            output.addChild(new ParseTree(this.tokens.peek().getType(),
                    this.tokens.peek().getValue()));
            this.tokens.pop();
            // term
            output.addChild(compileTerm());
        }
        // starts with identifier, including
        // varname[expression], subroutineName(expressionList),
        // className.subroutineName
        // or varName
        else if (this.tokens.peek().getType() == "identifier"
                && ((this.tokens.peek().getValue().charAt(0) >= 'A' &&
                        this.tokens.peek().getValue().charAt(0) <= 'Z')
                        || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                && this.tokens.peek().getValue().charAt(0) <= 'z')
                        || (this.tokens.peek().getValue().charAt(0) == '_'))
                && (keywords.contains(this.tokens.peek().getValue()) == false)) {
            // varName[expression]
            if (this.tokens.size() > 1 && this.tokens.get(1).getType() == "symbol"
                    && this.tokens.get(1).getValue() == "[") {
                // varName
                output.addChild(new ParseTree(this.tokens.peek().getType(),
                        this.tokens.peek().getValue()));
                this.tokens.pop();
                // [
                output.addChild(new ParseTree(this.tokens.peek().getType(),
                        this.tokens.peek().getValue()));
                this.tokens.pop();
                // expression
                output.addChild(compileExpression());
                // ]
                if (this.tokens.get(1).getType() == "symbol"
                        && this.tokens.get(1).getValue() == "]") {
                    output.addChild(new ParseTree(this.tokens.peek().getType(),
                            this.tokens.peek().getValue()));
                    this.tokens.pop();
                } else
                    throw new ParseException("", 0);
            }
            // subroutineName(expressionList)
            else if (this.tokens.size() > 1 && this.tokens.get(1).getType() == "symbol"
                    && this.tokens.get(1).getValue() == "(") {
                // subroutineName
                output.addChild(new ParseTree(this.tokens.peek().getType(),
                        this.tokens.peek().getValue()));
                this.tokens.pop();
                // (
                output.addChild(new ParseTree(this.tokens.peek().getType(),
                        this.tokens.peek().getValue()));
                this.tokens.pop();
                // expressionList
                output.addChild(compileExpressionList());
                // )
                // if (this.tokens.peek().getType() == "symbol" &&
                // this.tokens.peek().getValue()== ")") {
                // output.addChild(new ParseTree(this.tokens.peek().getType(),
                // this.tokens.peek().getValue()));
                // this.tokens.pop();
                // } else
                // throw new ParseException("", 0);
            }
            // className.subroutineName(expressionList)
            else if (this.tokens.size() > 1 && this.tokens.get(1).getType() == "symbol"
                    && this.tokens.get(1).getValue() == ".") {
                // className
                output.addChild(new ParseTree(this.tokens.peek().getType(),
                        this.tokens.peek().getValue()));
                this.tokens.pop();
                // .
                output.addChild(new ParseTree(this.tokens.peek().getType(),
                        this.tokens.peek().getValue()));
                this.tokens.pop();
                // subroutineName
                if (this.tokens.peek().getType() == "identifier"
                        && ((this.tokens.peek().getValue().charAt(0) >= 'A'
                                && this.tokens.peek().getValue().charAt(0) <= 'Z')
                                || (this.tokens.peek().getValue().charAt(0) >= 'a'
                                        && this.tokens.peek().getValue().charAt(0) <= 'z')
                                || (this.tokens.peek().getValue().charAt(0) == '_'))) {
                    output.addChild(new ParseTree(this.tokens.peek().getType(),
                            this.tokens.peek().getValue()));
                    this.tokens.pop();
                } else
                    throw new ParseException("", 0);
                // (
                if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == "(") {
                    output.addChild(new ParseTree(this.tokens.peek().getType(),
                            this.tokens.peek().getValue()));
                    this.tokens.pop();
                } else
                    throw new ParseException("", 0);
                // expressionList
                output.addChild(compileExpressionList());
                // )
                if (this.tokens.peek().getType() == "symbol" && this.tokens.peek().getValue() == ")") {
                    output.addChild(new ParseTree(this.tokens.peek().getType(),
                            this.tokens.peek().getValue()));
                    this.tokens.pop();
                } else
                    throw new ParseException("", 0);
            }
            // varName
            if (this.tokens.size() > 1) {
                output.addChild(new ParseTree(this.tokens.peek().getType(),
                        this.tokens.peek().getValue()));
                this.tokens.pop();
            }
        } else
            throw new ParseException("", 0);
        return output;
    }

    /**
     * Generates a parse tree for an expression list
     */
    public ParseTree compileExpressionList() throws ParseException {
        ParseTree output = new ParseTree("expressionList", "");

        if (this.tokens.peek().getType() != "symbol" && this.tokens.peek().getValue() != ")") {
            output.addChild(compileExpression());

            while (this.tokens.size() > 1 && this.tokens.peek().getType() == "symbol"
                    && this.tokens.peek().getValue() == ",") {
                // ,
                output.addChild(new ParseTree(this.tokens.peek().getType(),
                        this.tokens.peek().getValue()));
                this.tokens.pop();
                // expression
                output.addChild(compileExpression());
            }
        }
        return output;
    }

    public static void main(String[] args) {

        /*
         * Tokens for:
         * class MyClass {
         *
         * }
         */
        LinkedList<Token> tokens = new LinkedList<Token>();
        // compileProgram - 1
        // tokens.add(new Token("keyword", "class"));
        // tokens.add(new Token("identifier", "Main"));
        // tokens.add(new Token("symbol", "{"));
        // tokens.add(new Token("symbol", "}"));

        // compileProgram - 2
        // tokens.add(new Token("keyword", "static"));
        // tokens.add(new Token("keyword", "int"));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", ";"));

        // // compileClass
        // tokens.add(new Token("keyword", "class"));
        // tokens.add(new Token("identifier", "Main"));
        // tokens.add(new Token("symbol", "{"));
        // // /////////////////////////////////////////
        // // tokens.add(new Token("symbol", "}")); //
        // ///////////////////////////////////////////

        // // compileClassVarDec
        // tokens.add(new Token("keyword", "static"));
        // tokens.add(new Token("keyword", "int"));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", ","));
        // tokens.add(new Token("identifier", "b"));
        // tokens.add(new Token("symbol", ";"));
        // tokens.add(new Token("keyword", "field"));
        // tokens.add(new Token("keyword", "char"));
        // tokens.add(new Token("identifier", "c"));
        // tokens.add(new Token("symbol", ";"));

        // // compileSubroutine
        // tokens.add(new Token("keyword", "function"));
        // tokens.add(new Token("keyword", "void"));
        // tokens.add(new Token("identifier", "myFunc"));
        // tokens.add(new Token("symbol", "("));
        // tokens.add(new Token("keyword", "int"));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", ","));
        // tokens.add(new Token("keyword", "char"));
        // tokens.add(new Token("identifier", "b"));
        // tokens.add(new Token("symbol", ")"));
        // tokens.add(new Token("symbol", "{"));
        // tokens.add(new Token("keyword", "var"));
        // tokens.add(new Token("keyword", "int"));
        // tokens.add(new Token("identifier", "c"));
        // tokens.add(new Token("symbol", ";"));
        // tokens.add(new Token("keyword", "var"));
        // tokens.add(new Token("keyword", "char"));
        // tokens.add(new Token("identifier", "d"));
        // tokens.add(new Token("symbol", ","));
        // tokens.add(new Token("identifier", "e"));
        // tokens.add(new Token("symbol", ";"));
        // tokens.add(new Token("keyword", "let"));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", "="));
        // tokens.add(new Token("keyword", "skip"));
        // tokens.add(new Token("symbol", ";"));
        // tokens.add(new Token("keyword", "if"));
        // tokens.add(new Token("symbol", "("));
        // tokens.add(new Token("keyword", "skip"));
        // tokens.add(new Token("symbol", ")"));
        // tokens.add(new Token("symbol", "{"));
        // tokens.add(new Token("keyword", "return"));
        // tokens.add(new Token("integerConstant", "1"));
        // tokens.add(new Token("symbol", ";"));
        // tokens.add(new Token("symbol", "}"));
        // tokens.add(new Token("keyword", "else"));
        // tokens.add(new Token("symbol", "{"));
        // tokens.add(new Token("keyword", "return"));
        // tokens.add(new Token("integerConstant", "2"));
        // tokens.add(new Token("symbol", ";"));
        // tokens.add(new Token("symbol", "}"));
        // tokens.add(new Token("symbol", "}"));
        // tokens.add(new Token("symbol", "}"));

        // compileParameterList Start
        // tokens.add(new Token("keyword", "int"));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", ","));
        // tokens.add(new Token("keyword", "char"));
        // tokens.add(new Token("identifier", "b"));

        // compileSubroutineBody
        // tokens.add(new Token("symbol", "{"));
        // tokens.add(new Token("keyword", "var"));
        // tokens.add(new Token("keyword", "int"));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", ";"));
        // /////////////////////////////////////////
        // tokens.add(new Token("symbol", "}")); //
        ///////////////////////////////////////////

        // compileStatements
        // compileLet
        // tokens.add(new Token("keyword", "let"));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", "="));
        // tokens.add(new Token("keyword", "skip"));
        // tokens.add(new Token("symbol", ";"));

        // compileLet-special
        // tokens.add(new Token("keyword", "let"));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", "["));
        // tokens.add(new Token("integerConstant", "1"));
        // tokens.add(new Token("symbol", "]"));
        // tokens.add(new Token("symbol", "="));
        // tokens.add(new Token("keyword", "skip"));
        // tokens.add(new Token("symbol", ";"));

        // compileIf
        // tokens.add(new Token("keyword", "if"));
        // tokens.add(new Token("symbol", "("));
        // tokens.add(new Token("keyword", "skip"));
        // tokens.add(new Token("symbol", ")"));
        // tokens.add(new Token("symbol", "{"));
        // tokens.add(new Token("keyword", "return"));
        // tokens.add(new Token("integerConstant", "1"));
        // tokens.add(new Token("symbol", ";"));
        // tokens.add(new Token("symbol", "}"));
        // tokens.add(new Token("keyword", "else"));
        // tokens.add(new Token("symbol", "{"));
        // tokens.add(new Token("keyword", "return"));
        // tokens.add(new Token("integerConstant", "2"));
        // tokens.add(new Token("symbol", ";"));
        // tokens.add(new Token("symbol", "}"));

        // compileWhile
        // tokens.add(new Token("keyword", "while"));
        // tokens.add(new Token("symbol", "("));
        // tokens.add(new Token("keyword", "skip"));
        // tokens.add(new Token("symbol", ")"));
        // tokens.add(new Token("symbol", "{"));
        // tokens.add(new Token("keyword", "let"));
        // tokens.add(new Token("identifier", "i"));
        // tokens.add(new Token("symbol", "="));
        // tokens.add(new Token("identifier", "i"));
        // tokens.add(new Token("symbol", "+"));
        // tokens.add(new Token("integerConstant", "1"));
        // tokens.add(new Token("symbol", ";"));
        // tokens.add(new Token("symbol", "}"));

        // compileDo - skip
        // tokens.add(new Token("keyword", "do"));
        // tokens.add(new Token("keyword", "skip"));
        // tokens.add(new Token("symbol", ";"));

        // compileDo - expression
        // tokens.add(new Token("keyword", "do"));
        // tokens.add(new Token("integerConstant", "1"));
        // tokens.add(new Token("symbol", "+"));
        // tokens.add(new Token("symbol", "("));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", "-"));
        // tokens.add(new Token("identifier", "b"));
        // tokens.add(new Token("symbol", ")"));
        // tokens.add(new Token("symbol", ";"));

        // compileDo - subroutine call for expression list
        // tokens.add(new Token("keyword", "do"));
        // tokens.add(new Token("identifier", "MyFunc"));
        // tokens.add(new Token("symbol", "("));
        // tokens.add(new Token("integerConstant", "1"));
        // tokens.add(new Token("symbol", ","));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", "-"));
        // tokens.add(new Token("identifier", "b"));
        // tokens.add(new Token("symbol", ")"));
        // tokens.add(new Token("symbol", ";"));

        // compileReturn
        // tokens.add(new Token("keyword", "return"));
        // tokens.add(new Token("keyword", "skip"));
        // tokens.add(new Token("symbol", ";"));

        // subroutineBodyClose
        // tokens.add(new Token("symbol", "}"));
        // classClose
        // tokens.add(new Token("symbol", "}"));

        // compileExpression
        // tokens.add(new Token("integerConstant", "1"));
        // tokens.add(new Token("symbol", "+"));
        // tokens.add(new Token("symbol", "("));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", "-"));
        // tokens.add(new Token("identifier", "b"));
        // tokens.add(new Token("symbol", ")"));

        // compileTerm - one term only
        // tokens.add(new Token("integerConstant", "1"));
        // compileTerm - more than one term
        // tokens.add(new Token("symbol", "("));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", "-"));
        // tokens.add(new Token("identifier", "b"));
        // tokens.add(new Token("symbol", ")"));

        // compileExpressionList
        // tokens.add(new Token("integerConstant", "1"));
        // tokens.add(new Token("symbol", ","));
        // tokens.add(new Token("identifier", "a"));
        // tokens.add(new Token("symbol", "-"));
        // tokens.add(new Token("identifier", "b"));
        // tokens.add(new Token("symbol", ","));
        // tokens.add(new Token("identifier", "c"));

        // debug case
        tokens.add(new Token("identifier", "Main"));
        tokens.add(new Token("symbol", "."));
        tokens.add(new Token("identifier", "myFunc"));
        tokens.add(new Token("symbol", "("));
        tokens.add(new Token("integerConstant", "1"));
        tokens.add(new Token("symbol", ","));
        tokens.add(new Token("identifier", "Hello"));
        tokens.add(new Token("symbol", ")"));

        CompilerParser parser = new CompilerParser(tokens);
        try {
            // ParseTree result = parser.compileProgram();
            // ParseTree result = parser.compileClass();
            // ParseTree result = parser.compileClassVarDec();
            // ParseTree result = parser.compileSubroutine();
            // ParseTree result = parser.compileParameterList();
            // ParseTree result = parser.compileSubroutineBody();
            // ParseTree result = parser.compileStatements();
            // ParseTree result = parser.compileLet();
            // ParseTree result = parser.compileWhile();
            // ParseTree result = parser.compileIf();
            // ParseTree result = parser.compileDo();
            // ParseTree result = parser.compileReturn();
            ParseTree result = parser.compileExpression();
            // ParseTree result = parser.compileTerm();
            // ParseTree result = parser.compileExpressionList();

            System.out.println(result);
        } catch (ParseException e) {
            System.out.println("Error Parsing!");
        }
    }

}
