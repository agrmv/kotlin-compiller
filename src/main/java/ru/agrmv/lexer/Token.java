package ru.agrmv.lexer;

/**
 * Класс {@code Token} представляет токен(лексему)
 * Токен это строка символов классифицированная по правилам.
 * @author Aleksey Gromov
 *
 * */
public class Token {
    /**Начальный индекс входного токена*/
    private int indexInLine;

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
     * @param tokenString строка символов
     * @param tokenType тип токена
     * */
    Token(int lineIndex, int beginIndex, String tokenString, TokenType tokenType) {
        this.lineIndex = lineIndex;
        this.indexInLine = beginIndex;
        this.tokenType = tokenType;
        this.tokenString = tokenString;
    }

    public void setLineTokenIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public int getBeginTokenIndex() {
        return indexInLine;
    }

    /**Возвращает конечный индекс входного токена*/
    int getEndTokenIndex() {
        return indexInLine + tokenString.length();
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
            return tokenType + "  '" + tokenString + "' [" + lineIndex + ";" + indexInLine + "] ";
        else
            return tokenType + "   [" + lineIndex + ";" + indexInLine + "] ";
    }
}