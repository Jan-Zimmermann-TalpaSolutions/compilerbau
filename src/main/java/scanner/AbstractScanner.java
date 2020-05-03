package scanner;

import java.util.*;
import java.io.*;

abstract class AbstractScanner implements TokenList {

    public final char EOF = (char) 255;
    private LinkedList<InputCharacter> inputStream;
    private int pointer;
    private String lexem;
    LinkedList<Token> tokenStream;
    DEA dea;

    boolean match(char[] matchSet) {
        for (char c : matchSet)
            if (inputStream.get(pointer).character == c) {
                char currentChar = inputStream.get(pointer).character;
                if (!(currentChar == '"' || currentChar == ' '))
                    lexem = lexem + inputStream.get(pointer).character;
                pointer++;
                return true;
            }
        return false;
    }

    void lexicalError(String s) {
        char z;
        System.out.println("lexikalischer 💣 in Zeile " +
                inputStream.get(pointer).line + ".\nZeichen: " +
                inputStream.get(pointer).character);
    }

    abstract String getTokenString(byte token);

    void printTokenStream() {
        for (Token token : tokenStream)
            System.out.println(getTokenString(token.token) + ": " +
                    token.lexem);
    }

    void printInputStream() {
        for (InputCharacter inputCharacter : inputStream) System.out.print(inputCharacter.character);
        System.out.println();
    }

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
                    //}else if(((char)c)==' '){
                } else if (((char) c) == '\n') {
                    inputStream.addLast(new InputCharacter('\n', l));// carriage return ueberlesen und Zeilennummer hochzaehlen
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
        return true;
    }

    boolean lexicalAnalysis() {
        char[] EOFSet = {EOF};
        byte token = NO_TYPE;
        while (!match(EOFSet)) {
            token = getNextToken();
            if (token == NO_TYPE) {
                return false;
            } else if (token == EndState) {

            } else if (token == SYMBOL) {
                matchLexem();
            } else {
                tokenStream.addLast(new Token(token, inputStream.get(pointer - 1).line, lexem));
            }
        }
        tokenStream.addLast(new Token((byte) EOF, inputStream.get(pointer - 1).line, "EOF"));
        return true;
    }

    private void matchLexem() {
        switch (lexem.trim()) {
            case "if":
                tokenStream.addLast(new Token(IF, inputStream.get(pointer - 1).line, lexem));
                break;
            case "do":
                tokenStream.addLast(new Token(DO, inputStream.get(pointer - 1).line, lexem));
                break;
            case "end":
                tokenStream.addLast(new Token(END, inputStream.get(pointer - 1).line, lexem));
                break;
            case "while":
                tokenStream.addLast(new Token(WHILE, inputStream.get(pointer - 1).line, lexem));
                break;
            case "define":
                tokenStream.addLast(new Token(DEFINE, inputStream.get(pointer - 1).line, lexem));
                break;
            case "function":
                tokenStream.add(new Token(FUNCTION, inputStream.get(pointer - 1).line, lexem));
                break;
            case "call":
                tokenStream.add(new Token(CALL, inputStream.get(pointer - 1).line, lexem));
                break;
            case "assign":
                tokenStream.add(new Token(ASSIGN, inputStream.get(pointer - 1).line, lexem));
                break;
            case "return":
                tokenStream.add(new Token(RETURN, inputStream.get(pointer - 1).line, lexem));
                break;
            default:
                tokenStream.addLast(new Token(SYMBOL, inputStream.get(pointer - 1).line, lexem));
                break;
        }
    }

    byte getNextToken() {
        boolean transitionFound = false;
        int actualState = 0;
        int bufferState = 0;

        lexem = "";
        do {
            transitionFound = false;

            for (int j = 0; j < dea.transitions[actualState].length; j++) {
                if (match(dea.transitions[actualState][j])) {
                    if ((dea.states[j] == EndState) && bufferState != 0) {
                        actualState = bufferState;
                        transitionFound = false;
                        break;
                    } else {

                        actualState = j;
                        bufferState = actualState;
                        transitionFound = true;
                        break;
                    }

                }
            }
        } while (transitionFound);

        if ((dea.states[actualState] != NOT_FINAL) && (dea.states[actualState] != START)) {
            return dea.states[actualState];
        } else {
            lexicalError("");
            return NO_TYPE;
        }
    }

    static class InputCharacter {
        char character;
        int line;

        InputCharacter(char c, int l) {
            this.character = c;
            this.line = l;
        }
    }

    static class DEA {
        char[][][] transitions;
        byte[] states;

        DEA(char[][][] transitions, byte[] states) {
            this.transitions = transitions;
            this.states = states;
        }
    }

    public static class Token {
        public byte token;
        public String lexem;
        int line;

        Token(byte token, int line, String lexem) {
            this.token = token;
            this.lexem = lexem;
            this.line = line;
        }
    }
}