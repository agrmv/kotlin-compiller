package ru.agrmv.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Класс {@code Lexer} представляет лексический анализатор для языка Kotlin.
 *
 * @author Aleksey Gromov
 * */
class Lexer {
    /**Сопоставление типа токена с его регулярным выражением*/
    private final Map<TokenType, String> regEx;

    /**Список входных токенов*/
    private final List<Token> result;

    Lexer() {
        regEx = new TreeMap<>();
        launchRegEx();
        result = new ArrayList<>();
    }

    /**
     * Считывает токены из входного файла и добавляет их в список {@code result}
     * @param source входной файл
     * @param lineIndex индекс входной строки
     * @throws AnalyzerException если встречается лексическая ошибка
     * */
    void tokenize(String source, int lineIndex) throws AnalyzerException {
        int positionInLine = 0;
        Token token;
        do {
            token = separateToken(source, positionInLine, lineIndex);
            if (token != null) {
                positionInLine = token.getEndTokenIndex();
                result.add(token);
            }
        } while (token != null && positionInLine != source.length());
        if (positionInLine != source.length()) {
            throw new AnalyzerException("Lexical error at position # ["+
                    lineIndex +  ";" + positionInLine + "]", positionInLine, lineIndex);
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
    List<Token> getFilteredTokens() {
        List<Token> filteredResult = new ArrayList<>();
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
     * @param lineIndex индекс строки токена
     * @return первый найденный токен, или {@code null}, если токен не найден
     * */
    private Token separateToken(String source, int fromIndex, int lineIndex) {
        if (fromIndex < 0 || fromIndex >= source.length()) {
            throw new IllegalArgumentException("Illegal index in the input stream!");
        }
        for (TokenType tokenType : TokenType.values()) {

            /**Компилирует регулярное выражение в объект класс {@code Pattern}*/
            Pattern p = Pattern.compile(".{" + fromIndex + "}" + regEx.get(tokenType), Pattern.DOTALL);

            /**Объект {@code Matcher} анализирует строку и ищет соответствие шаблону.*/
            Matcher m = p.matcher(source);
            if (m.matches()) {
                String lexema = m.group(1);
                return new Token(lineIndex, fromIndex, lexema, tokenType);
            }
        }
        return null;
    }

    /**
     * Создает {@code Map<TokenType, String>} из типов токенов и регулярных выражений
     * */
    private void launchRegEx() {
        regEx.put(TokenType.BlockComment, "(/\\*.*?\\*/).*");
        regEx.put(TokenType.LineComment, "(//.*).*");
        regEx.put(TokenType.WhiteSpace, "( ).*");
        regEx.put(TokenType.LeftPare, "(\\().*");
        regEx.put(TokenType.RightParen, "(\\)).*");
        regEx.put(TokenType.Val, "\\b(val)\\b.*");
        regEx.put(TokenType.Var, "\\b(var)\\b.*");
        regEx.put(TokenType.Semicolon, "(;).*");
        regEx.put(TokenType.Colon, "(:).*");
        regEx.put(TokenType.Comma, "(,).*");
        regEx.put(TokenType.LeftBrace, "(\\{).*");
        regEx.put(TokenType.RightBrace, "(\\}).*");
        regEx.put(TokenType.StringConstant, "(\".*?\").*");
        regEx.put(TokenType.DoubleConstant, "\\b(\\d{1,9}\\.\\d{1,32})\\b.*");
        regEx.put(TokenType.IntConstant, "\\b(\\d{1,9})\\b.*");
        regEx.put(TokenType.Array, "\\b(Array<Int|Double|String>)\\b.*");
        regEx.put(TokenType.String, "\\b(String)\\b.*");
        regEx.put(TokenType.Int, "\\b(Int)\\b.*");
        regEx.put(TokenType.Double, "\\b(Int|Double)\\b.*");
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

/**
 *   \b граница
 *   ^     - начало проверяемой строки
 *   $     - конец проверяемой строки
 *   .     - представляет собой сокращенную форму записи для символьного класса, совпадающего с любым символом
 *   |     -  означает «или». Подвыражения, объединенные этим способом, называются альтернативами (alternatives)
 *   ?     - означает, что предшествующий ему символ является необязательным
 *   +     -  обозначает «один или несколько экземпляров непосредственно предшествующего элемента
 *   *     -  любое количество экземпляров элемента (в том числе и нулевое)
 *   {n}   - Ровно n раз
 *   dotAll - включает символ новой строки
 * */