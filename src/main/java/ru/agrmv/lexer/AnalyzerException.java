package ru.agrmv.lexer;

/**
 * Класс {@code AnalyzerException} представляет исключения
 * кторые могут быть вызваны лексическим анализом
 *
 * @author Aleksey Gromov
 * */
@SuppressWarnings("serial")
public class AnalyzerException extends Exception {

    private final int errorPositionInLine;
    private final int errorLine;

    private String message;

    public AnalyzerException(String message, int errorPositionInLine, int errorLine) {
        this.errorPositionInLine = errorPositionInLine;
        this.errorLine = errorLine;
        this.message = message;
    }

    public int getErrorPositionInLine() {
        return errorPositionInLine;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getErrorLine() {
        return errorLine;
    }
}