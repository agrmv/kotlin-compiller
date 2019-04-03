package ru.agrmv.lexer;

@SuppressWarnings("serial")
public class AnalyzerException extends Exception {

    private int errorPosition;

    private String message;

    public AnalyzerException(int errorPosition) {
        this.errorPosition = errorPosition;
    }

    AnalyzerException(String message, int errorPosition) {
        this.errorPosition = errorPosition;
        this.message = message;
    }

    public int getErrorPosition() {
        return errorPosition;
    }

    @Override
    public String getMessage() {
        return message;
    }
}