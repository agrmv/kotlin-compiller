package ru.agrmv.lexer;

/**
 * Класс {@code AnalyzerException} представляет исключения
 * кторые могут быть вызваны лексическим анализом
 *
 * @author Aleksey Gromov
 * */
@SuppressWarnings("serial")
public class AnalyzerException extends Exception {

    private int errorPosition;
    private int errorLine;

    private String message;

    public AnalyzerException(int errorPosition) {
        this.errorPosition = errorPosition;
    }

    AnalyzerException(String message, int errorPosition, int errorLine) {
        this.errorPosition = errorPosition;
        this.errorLine = errorLine;
        this.message = message;
    }

    public int getErrorPosition() {
        return errorPosition;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getErrorLine() {
        return errorLine;
    }
}