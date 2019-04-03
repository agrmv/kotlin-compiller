package ru.agrmv.lexer;

public enum TokenType {
    BlockComment,

    LineComment,

    WhiteSpace,

    Tab,

    NewLine,

    CloseBrace,

    OpenBrace,

    OpeningCurlyBrace,

    ClosingCurlyBrace,

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

    Public,

    Private,

    Int,

    Double,

    Void,

    False,

    True,

    Null,

    Return,

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