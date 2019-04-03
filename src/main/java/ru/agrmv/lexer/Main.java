package ru.agrmv.lexer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        try {
            String input = readFile("src/main/resources/test.txt", Charset.defaultCharset());
            lexer.tokenize(input);
        } catch (IOException | AnalyzerException e) {
            e.printStackTrace();
        } finally {
            int i = 0;
            for (Token token : lexer.getTokens()) {
                if (token.getTokenType().isAuxiliary())
                    System.out.println("   " + token.toString() + "\n");
                else {
                    i++;
                    System.out.println(i + "   " + token.toString() + "\n");
                }
            }
        }
    }
}

/**
 * TODO LIST
 * перенести считывание из файла в отдельный файл
 * аннотации
 * добавить ключевые токены котлина
 * добавить определение номера строки строки
 * */