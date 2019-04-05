package ru.agrmv.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Класс {@code Lexer} представляет лексический анализатор
 * для языка Kotlin.
 * @author Aleksey Gromov
 * */
class Lexer {
    /**Сопоставление типа токена с его регулярным выражением*/
    private Map<TokenType, String> regEx;

    /**Список входных токенов*/
    private List<Token> result;

    /**Индекс строки входного токена*/
    private int lineIndex = 0;

    Lexer() {
        regEx = new TreeMap<>();
        launchRegEx();
        result = new ArrayList<>();
    }

    /**
     * Считывает токены из входного файла и добавляет их в список {@code result}
     * @param source входной файл
     * @throws AnalyzerException если встречается лексическая ошибка
     * */
    void tokenize(String source) throws AnalyzerException {
        int position = 0;
        lineIndex++;
        Token token;
        do {
            token = separateToken(source, position);
            if (token != null) {
                position = token.getEnd();
                result.add(token);
            }
        } while (token != null && position != source.length());
        if (position != source.length()) {
            throw new AnalyzerException("Lexical error at position # ["+ lineIndex +  ";" + position + "]", position, lineIndex);
        }
    }

    /**
     * Возвращает последоватеьность токенов
     *
     * @return список токенов
     * */
    List<Token> getTokens() {
        return result;
    }

    /**
     * Возвращает последовательность токенов без вспомогательных типов
     * {@code BlockComment}, {@code LineComment}, {@code NewLine}, {@code Tab}, {@code WhiteSpace}
     *
     * @return список токенов
     * */
    public List<Token> getFilteredTokens() {
        List<Token> filteredResult = new ArrayList<Token>();
        for (Token t : this.result) {
            if (!t.getTokenType().isAuxiliary()) {
                filteredResult.add(t);
            }
        }
        return filteredResult;
    }

    /**
     * Сканирует файла по индексу и возвращает найденный токен
     *
     * @param source входной файл
     * @param  fromIndex индекс с которого начинается сканирование
     *
     * @return первый найденный токен, или {@code null}, если токен не найден
     * */
    private Token separateToken(String source, int fromIndex) {
        if (fromIndex < 0 || fromIndex >= source.length()) {
            throw new IllegalArgumentException("Illegal index in the input stream!");
        }
        for (TokenType tokenType : TokenType.values()) {
            Pattern p = Pattern.compile(".{" + fromIndex + "}" + regEx.get(tokenType), Pattern.DOTALL);
            Matcher m = p.matcher(source);
            if (m.matches()) {
                String lexema = m.group(1);
                return new Token(lineIndex, fromIndex, fromIndex + lexema.length(), lexema, tokenType);
            }
        }
        return null;
    }

    /**
     * Создает {@code Map<TokenType, String>} из типов токенов и регулярных выражений
     * */
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