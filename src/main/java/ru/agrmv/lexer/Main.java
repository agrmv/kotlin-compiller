package ru.agrmv.lexer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        AtomicInteger lineIndex = new AtomicInteger(0);
        try {
            Files.lines(Paths.get("src/main/resources/test.kt"), StandardCharsets.UTF_8).forEach(s -> {
                try {
                    if(!s.isEmpty()) {
                        lexer.tokenize(s, lineIndex.incrementAndGet());
                    } else {
                        lineIndex.incrementAndGet();
                    }
                } catch (AnalyzerException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            int i = 0;
            for (Token token : lexer.getFilteredTokens()) {
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