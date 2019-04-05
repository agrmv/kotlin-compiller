package ru.agrmv.lexer;

/**
 * Класс {@code Token} представляет токен(лексему)
 * Токен это строка символов классифицированная по правилам.
 * @author Aleksey Gromov
 *
 * */
public class Token {
    /**Начальный индекс входного токена*/
    private int beginIndex;

    /**Конечный индекс входного токена*/
    private int endIndex;

    /**Индекс строки входного токена*/
    private int lineIndex;

    /**Тип токена*/
    private TokenType tokenType;

    /**Символьное представление токена*/
    private String tokenString;

    /**
     * Создает новый объект класса {@code Token} с указанными параметрами
     * @param lineIndex индекс строки входного токена
     * @param beginIndex начальный индекс входного токена
     * @param endIndex конечный индекс входного токена
     * @param tokenString строка символов
     * @param tokenType тип токена
     * */
    Token(int lineIndex, int beginIndex, int endIndex, String tokenString, TokenType tokenType) {
        this.lineIndex = lineIndex;
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
            return tokenType + "  '" + tokenString + "' [" + lineIndex + ";" + beginIndex + "] ";
        else
            return tokenType + "   [" + lineIndex + ";" + beginIndex + "] ";
    }
}