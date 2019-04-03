package ru.agrmv.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Lexer {
    private Map<TokenType, String> regex;

    private List<Token> result;

    Lexer() {
        regex = new TreeMap<>();
        launchRegEx();
        result = new ArrayList<>();
    }

    void tokenize(String source) throws AnalyzerException {
        int position = 0;
        Token token;
        do {
            token = separateToken(source, position);
            if (token != null) {
                position = token.getEnd();
                result.add(token);
            }
        } while (token != null && position != source.length());
        if (position != source.length()) {
            throw new AnalyzerException("Lexical error at position # "+ position, position);

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
            Pattern p = Pattern.compile(".{" + fromIndex + "}" + regex.get(tokenType),
                    Pattern.DOTALL);
            Matcher m = p.matcher(source);
            if (m.matches()) {
                String lexema = m.group(1);
                return new Token(fromIndex, fromIndex + lexema.length(), lexema, tokenType);
            }
        }

        return null;
    }

    private void launchRegEx() {
        regex.put(TokenType.BlockComment, "(/\\*.*?\\*/).*");
        regex.put(TokenType.LineComment, "(//(.*?)[\r$]?\n).*");
        regex.put(TokenType.WhiteSpace, "( ).*");
        regex.put(TokenType.OpenBrace, "(\\().*");
        regex.put(TokenType.CloseBrace, "(\\)).*");
        regex.put(TokenType.Semicolon, "(;).*");
        regex.put(TokenType.Comma, "(,).*");
        regex.put(TokenType.OpeningCurlyBrace, "(\\{).*");
        regex.put(TokenType.ClosingCurlyBrace, "(\\}).*");
        regex.put(TokenType.DoubleConstant, "\\b(\\d{1,9}\\.\\d{1,32})\\b.*");
        regex.put(TokenType.IntConstant, "\\b(\\d{1,9})\\b.*");
        regex.put(TokenType.Void, "\\b(void)\\b.*");
        regex.put(TokenType.Int, "\\b(int)\\b.*");
        regex.put(TokenType.Double, "\\b(int|double)\\b.*");
        regex.put(TokenType.Tab, "(\\t).*");
        regex.put(TokenType.NewLine, "(\\n).*");
        regex.put(TokenType.Return, "\\b(return)\\b.*");
        regex.put(TokenType.If, "\\b(if)\\b.*");
        regex.put(TokenType.Else, "\\b(else)\\b.*");
        regex.put(TokenType.While, "\\b(while)\\b.*");
        regex.put(TokenType.Plus, "(\\+{1}).*");
        regex.put(TokenType.Minus, "(\\-{1}).*");
        regex.put(TokenType.Multiply, "(\\*).*");
        regex.put(TokenType.Divide, "(/).*");
        regex.put(TokenType.EqualEqual, "(==).*");
        regex.put(TokenType.Equal, "(=).*");
        regex.put(TokenType.ExclameEqual, "(\\!=).*");
        regex.put(TokenType.Greater, "(>).*");
        regex.put(TokenType.Less, "(<).*");
        regex.put(TokenType.Identifier, "\\b([a-zA-Z]{1}[0-9a-zA-Z_]{0,31})\\b.*");
    }
}