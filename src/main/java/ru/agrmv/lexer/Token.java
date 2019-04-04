package ru.agrmv.lexer;

public class Token {
    private int beginIndex;
    private int endIndex;
    private int line;
    private TokenType tokenType;
    private String tokenString;

    Token(int line, int beginIndex, int endIndex, String tokenString, TokenType tokenType) {
        this.line = line;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.tokenType = tokenType;
        this.tokenString = tokenString;
    }


    public int getBegin() {
        return beginIndex;
    }

    int getEnd() {
        return endIndex;
    }

    public String getTokenString() {
        return tokenString;
    }

    TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        if (!this.getTokenType().isAuxiliary())
            return tokenType + "  '" + tokenString + "' ["+ line + ";" + beginIndex + "] ";
        else
            return tokenType + "   [" + line + ";" + beginIndex + "] ";
    }
}