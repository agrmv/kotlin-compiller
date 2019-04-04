package ru.agrmv.lexer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        try {
            Files.lines(Paths.get("src/main/resources/test.txt"), StandardCharsets.UTF_8).forEach(s -> {
                try {
                    lexer.tokenize(s);
                } catch (AnalyzerException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
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
 * аннотации
 * добавить ключевые токены котлина
 * */