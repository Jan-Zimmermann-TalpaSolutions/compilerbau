import java.util.*;
import java.io.*;

/**
 * The type AbstractScanner.
 * @author jan
 */
abstract class AbstractScanner implements TokenList {


    /**
     * The type Input character.
     */
    class InputCharacter {

        /**
         * The Character.
         */
        char character;
        /**
         * The Line.
         */
        int line;

        /**
         * Instantiates a new Input character.
         *
         * @param c the c
         * @param l the l
         */
        InputCharacter(char c, int l) {
            this.character = c;
            this.line = l;
        }
    }


    /**
     * The type Dea.
     */
    class DEA {
        /**
         * The Transitions.
         */
        char transitions[][][];
        /**
         * The States.
         */
        byte states[];

        /**
         * Instantiates a new Dea.
         *
         * @param transitions the transitions
         * @param states      the states
         */
        DEA(char transitions[][][], byte states[]) {
            this.transitions = transitions;
            this.states = states;
        }
    }


    /**
     * The type Token.
     */
    class Token {
        /**
         * The Token.
         */
        byte token;
        /**
         * The Lexem.
         */
        String lexem;
        /**
         * The Line.
         */
        int line;

        /**
         * Instantiates a new Token.
         *
         * @param token the token
         * @param line  the line
         * @param lexem the lexem
         */
        Token(byte token, int line, String lexem) {
            this.token = token;
            this.lexem = lexem;
            this.line = line;
        }
    }

    /**
     * The Eof.
     */
    public final char EOF = (char) 255;
    private LinkedList<InputCharacter> inputStream;
    private int pointer;
    private String lexem;
    /**
     * The Token stream.
     */
    LinkedList<Token> tokenStream;
    /**
     * The Dea.
     */
    DEA dea;


    /**
     * Match boolean.
     *
     * @param matchSet the match set
     * @return the boolean
     */
    boolean match(char[] matchSet) {
        for (int i = 0; i < matchSet.length; i++) {
            if (inputStream.get(pointer).character == matchSet[i]) {
                System.out.println("match:" + inputStream.get(pointer).character);
                lexem = lexem + inputStream.get(pointer).character;
                pointer++;    //Eingabepointer auf das n�chste Zeichen setzen
                return true;
            }
        }
        return false;
    }

    /**
     * Lexical error.
     *
     * @param s the s
     */
    void lexicalError(String s) {
        char z;
        System.out.println("lexikalischer Fehler in Zeile " +
                inputStream.get(pointer).line + ". Zeichen: " +
                inputStream.get(pointer).character);
        System.out.println((byte) inputStream.get(pointer).character);
    }


    /**
     * Gets token string.
     *
     * @param token the token
     * @return the token string
     */
    abstract String getTokenString(byte token);


    /**
     * Print token stream.
     */
    void printTokenStream() {
        for (int i = 0; i < tokenStream.size(); i++) {
            System.out.println(getTokenString(tokenStream.get(i).token) + ": " +
                    tokenStream.get(i).lexem);
        }
    }


    /**
     * Print input stream.
     */
    void printInputStream() {
        for (int i = 0; i < inputStream.size(); i++) {
            System.out.print(inputStream.get(i).character);
        }
        System.out.println();

    }

    /**
     * Read input boolean.
     *
     * @param name the name
     * @return the boolean
     */
    boolean readInput(String name) {
        int c = 0;
        int l = 1;
        inputStream = new LinkedList<InputCharacter>();
        tokenStream = new LinkedList<Token>();
        try {
            FileReader f = new FileReader(name);
            while (true) {
                c = f.read();
                if (c == -1) {
                    inputStream.addLast(new InputCharacter(EOF, l));
                    break;
                } else if (((char) c) == ' ') {
                } else if (((char) c) == '\n') {

                    l++;
                } else if (c == 13) {

                } else {

                    inputStream.addLast(new InputCharacter((char) c, l));
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Dateizugriff: " + name);
            return false;
        }
        System.out.println(inputStream.size());
        return true;
    }

    /**
     * Lexical analysis boolean.
     *
     * @return the boolean
     */
    boolean lexicalAnalysis() {
        char[] eofset = {EOF};
        byte token = NO_TYPE;

        while (!match(eofset)) {
            token = getNextToken();
            System.out.println(getTokenString(token));

            if (token == NO_TYPE) {
                return false;
            } else {
                tokenStream.
                        addLast(new Token(token, inputStream.get(pointer - 1).line, lexem));
            }
        }
        tokenStream.addLast(new Token((byte) EOF, inputStream.get(pointer - 1).line, "EOF"));
        return true;
    }

    /**
     * Gets next token.
     *
     * @return the next token
     */
    byte getNextToken() {

        boolean transitionFound = false;
        int actualState = 0;
        lexem = "";

        do {

            transitionFound = false;
            for (int j = 0; j < dea.transitions[actualState].length; j++) {
                if (match(dea.transitions[actualState][j])) {
                    actualState = j;
                    System.out.println(actualState + "->" + j);
                    transitionFound = true;
                    break;
                }
            }
        } while (transitionFound);

        if ((dea.states[actualState] != NOT_FINAL) && (dea.states[actualState] != START)) {
            return dea.states[actualState];
        } else {
            lexicalError("");
            System.out.println(pointer);
            return NO_TYPE;
        }
    }

}