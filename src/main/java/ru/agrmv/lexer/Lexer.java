package ru.agrmv.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Lexer {
    private Map<TokenType, String> regEx;
    private List<Token> result;
    private int line = 0;

    Lexer() {
        regEx = new TreeMap<>();
        launchRegEx();
        result = new ArrayList<>();
    }

    void tokenize(String source) throws AnalyzerException {
        int position = 0;
        line++;
        Token token;
        do {
            token = separateToken(source, position);
            if (token != null) {
                position = token.getEnd();
                if (token.getTokenType() == TokenType.WhiteSpace) {
                    continue;
                }
                result.add(token);
            }
        } while (token != null && position != source.length());
        if (position != source.length()) {
            throw new AnalyzerException("Lexical error at position # ["+ line +  ";" + position + "]", position, line);
        }
    }

    List<Token> getTokens() {
        return result;
    }

    private Token separateToken(String source, int fromIndex) {
        if (fromIndex < 0 || fromIndex >= source.length()) {
            throw new IllegalArgumentException("Illegal index in the input stream!");
        }
        for (TokenType tokenType : TokenType.values()) {
            Pattern p = Pattern.compile(".{" + fromIndex + "}" + regEx.get(tokenType), Pattern.DOTALL);
            Matcher m = p.matcher(source);
            if (m.matches()) {
                String lexema = m.group(1);
                return new Token(line, fromIndex, fromIndex + lexema.length(), lexema, tokenType);
            }
        }
        return null;
    }

    private void launchRegEx() {
        regEx.put(TokenType.BlockComment, "(/\\*.*?\\*/).*");
        regEx.put(TokenType.WhiteSpace, "( ).*");
        regEx.put(TokenType.LeftPare, "(\\().*");
        regEx.put(TokenType.RightParen, "(\\)).*");
        regEx.put(TokenType.Semicolon, "(;).*");
        regEx.put(TokenType.Comma, "(,).*");
        regEx.put(TokenType.LeftBrace, "(\\{).*");
        regEx.put(TokenType.RightBrace, "(\\}).*");
        regEx.put(TokenType.DoubleConstant, "\\b(\\d{1,9}\\.\\d{1,32})\\b.*");
        regEx.put(TokenType.IntConstant, "\\b(\\d{1,9})\\b.*");
        regEx.put(TokenType.Void, "\\b(void)\\b.*");
        regEx.put(TokenType.Int, "\\b(int)\\b.*");
        regEx.put(TokenType.Double, "\\b(int|double)\\b.*");
        regEx.put(TokenType.Tab, "(\\t).*");
        regEx.put(TokenType.NewLine, "(\\n).*");
        regEx.put(TokenType.False, "\\b(false)\\b.*");
        regEx.put(TokenType.True, "\\b(true)\\b.*");
        regEx.put(TokenType.Null, "\\b(null)\\b.*");
        regEx.put(TokenType.Return, "\\b(return)\\b.*");
        regEx.put(TokenType.Function, "\\b(fun)\\b.*");
        regEx.put(TokenType.Class, "\\b(class)\\b.*");
        regEx.put(TokenType.If, "\\b(if)\\b.*");
        regEx.put(TokenType.Else, "\\b(else)\\b.*");
        regEx.put(TokenType.While, "\\b(while)\\b.*");
        regEx.put(TokenType.Point, "(\\.).*");
        regEx.put(TokenType.Plus, "(\\+{1}).*");
        regEx.put(TokenType.Minus, "(\\-{1}).*");
        regEx.put(TokenType.Multiply, "(\\*).*");
        regEx.put(TokenType.Divide, "(/).*");
        regEx.put(TokenType.EqualEqual, "(==).*");
        regEx.put(TokenType.Equal, "(=).*");
        regEx.put(TokenType.ExclameEqual, "(\\!=).*");
        regEx.put(TokenType.Greater, "(>).*");
        regEx.put(TokenType.Less, "(<).*");
        regEx.put(TokenType.Identifier, "\\b([a-zA-Z]{1}[0-9a-zA-Z_]{0,31})\\b.*");
    }
}