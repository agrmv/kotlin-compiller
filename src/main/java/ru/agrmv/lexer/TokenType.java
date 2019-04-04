package ru.agrmv.lexer;

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

    Void,

    False,

    True,

    Null,

    Return,

    Function,

    Class,

    If,

    While,

    Else,

    Semicolon,

    Comma,

    Identifier;

    public boolean isAuxiliary() {
        return this == BlockComment || this == LineComment || this == NewLine || this == Tab
                || this == WhiteSpace;
    }
}