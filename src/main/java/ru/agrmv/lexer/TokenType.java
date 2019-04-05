package ru.agrmv.lexer;

/**
 * Класс {@code TokenType} представляет перечисление типов токенов
 * в языке Kotlin.
 * @author Aleksey Gromov
 * */
public enum TokenType {
    BlockComment,

    LineComment,

    WhiteSpace,

    Tab,

    NewLine,

    RightParen,

    LeftPare,

    LeftBrace,

    RightBrace,

    Val,

    Var,

    Array,

    StringConstant,

    DoubleConstant,

    IntConstant,

    Plus,

    Minus,

    Multiply,

    Divide,

    Point,

    EqualEqual,

    Equal,

    ExclameEqual,

    Greater,

    Less,

    Int,

    Double,

    String,

    False,

    True,

    Null,

    Return,

    Function,

    Class,

    If,

    While,

    Else,

    Colon,

    Semicolon,

    Comma,

    Identifier;

    /**
     * Определяет является ли токен вспомогательным
     * @return {@code true} если токен вспомогательный, иначе {@code false}
     * */
    public boolean isAuxiliary() {
        return this == BlockComment || this == LineComment || this == NewLine || this == Tab
                || this == WhiteSpace;
    }
}