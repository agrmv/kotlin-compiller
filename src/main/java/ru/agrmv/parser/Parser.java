package ru.agrmv.parser;

import ru.agrmv.lexer.AnalyzerException;
import ru.agrmv.lexer.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Класс {@code Parser} представляет прогнозирующий парсер. Поддерживает только LL(1) грамматику.
 * Если грамматика не LL(1) скорее всего вы получите {@code StackOverflowError}
 * Выражение в грамматике имеют следующий формат:
 * <blockquote>
 *
 *<pre>
 *   Goal -> A
 *   A -> ( A ) | Two
 *   Two -> a
 *</pre>
 *
 * </blockquote>
 *
 * @author Aleksey Gromov
 * */

public class Parser {

	/**Terminal symbol of grammar which represents empty string*/
	public static Terminal epsilon = new Terminal(0, "EPSILON");

	/**Терминальный символ, обозначающий конец программы*/
	public static Terminal endOfProgram = new Terminal(-1, "ENDOFPROGRAM");

	/**Начальный символ грамматики*/
	private NonTerminal startSymbol;

	/**Список правил в грамматике без чередований*/
	private List<Rule> rules;

	/**Грамматический алфавит. Содержит терминальные и нетерминальные символы*/
	private Set<Symbol> alphabet;

	/**Отображение строкового представления символа объектно*/
	private Map<String, Symbol> nameToSymbol;

	private Map<Symbol, Set<Terminal>> firstSet;
	private Map<Symbol, Set<Terminal>> followSet;

	/**Представление таблицы разбора для парсера LL (1)*/
	private Map<SimpleEntry<NonTerminal, Terminal>, Symbol[]> parsingTable;

	/**Стек терминалов, которые были построены из входных токенов*/
	private Stack<Terminal> input;

	/**Последовательность применяемых правил при деривациях*/
	private List<Rule> sequenceOfAppliedRules;

	public Parser() {
		rules = new ArrayList<>();
		alphabet = new HashSet<>();
		nameToSymbol = new HashMap<>();
		alphabet.add(epsilon);
		firstSet = new HashMap<>();
		followSet = new HashMap<>();
		parsingTable = new HashMap<>();
		sequenceOfAppliedRules = new ArrayList<>();
	}

	/**
	 * Анализирует входной файл, представленный списком токенов, используя указанные LL(1) правила грамматики
	 *
	 * @param grammarFile файл с правилами грамматики
	 * @param list список входящих токенов
	 * @throws FileNotFoundException если файл не существует
	 * @throws AnalyzerException если фход содержит синтаксическую ошибку
	 * */

	public void parse(File grammarFile, List<Token> list) throws FileNotFoundException, AnalyzerException {
		parseRules(grammarFile);
		calculateFirst();
		calculateFollow();
		buildParsingTable();
		input = convertTokensToStack(list);
		performParsingAlgorithm();
	}

	/**
	 * Возвращает последовательность правил грамматики, которые применялись во время  разбора
	 *
	 * @return список применяемых правил
	 * */
	public List<Rule> getSequenceOfAppliedRules() {
		return sequenceOfAppliedRules;
	}

	/**
	 * Реализует алгоритм прогнозирующего синтаксического анализа LL (1)
	 *
	 * @throws AnalyzerException если найдена синтаксическая ошибка
	 * */
	private void performParsingAlgorithm() throws AnalyzerException {
		Stack<Symbol> stack = new Stack<>();
		stack.push(endOfProgram);
		stack.push(startSymbol);
		int parsedTokensCount = 0;
		do {

			Symbol stackTop = stack.peek();
			Terminal inputTop = input.peek();
			if (stackTop.isTerminal()) {
				if (stackTop.equals(inputTop)) {
					stack.pop();
					input.pop();
					parsedTokensCount++;
				} else {
					/*throw new AnalyzerException("Syntax error after token #" + parsedTokensCount,
							parsedTokensCount);*/
				}
			} else {
				SimpleEntry<NonTerminal, Terminal> tableKey = new SimpleEntry<NonTerminal, Terminal>(
						(NonTerminal) stackTop, inputTop);
				if (parsingTable.containsKey(tableKey)) {
					stack.pop();
					Symbol[] tableEntry = parsingTable.get(tableKey);
					for (int j = tableEntry.length - 1; j > -1; j--) {
						if (!tableEntry[j].equals(epsilon))
							stack.push(tableEntry[j]);
					}
					sequenceOfAppliedRules.add(getRule((NonTerminal) stackTop, tableEntry));
				} else {
					/*throw new AnalyzerException("Syntax error after token #" + parsedTokensCount,
							parsedTokensCount);*/
				}
			}
		} while (!stack.isEmpty() && !input.isEmpty());

		if (!input.isEmpty()) {
			/*throw new AnalyzerException("Syntax error after token #" + parsedTokensCount,
					parsedTokensCount);*/
		}
	}

	/**
	 * Преобразует список токенов из лексера в стек терминалов для парсера
	 * Первый токен на входе будет на вершине стека
	 *
	 * @param inputTokens список входных токенов
	 * @return стек терминальных символов
	 * */
	private Stack<Terminal> convertTokensToStack(List<Token> inputTokens) {
		Stack<Terminal> input = new Stack<Terminal>();
		Collections.reverse(inputTokens);
		input.push(endOfProgram);
		for (Token token : inputTokens) {
			Terminal s = (Terminal) nameToSymbol.get(token.getTokenString());
			if (s == null) {
				switch (token.getTokenType()) {
				case Identifier:
					s = (Terminal) nameToSymbol.get("id");
					break;
				case IntConstant:
					s = (Terminal) nameToSymbol.get("intConst");
					break;
				case DoubleConstant:
					s = (Terminal) nameToSymbol.get("doubleConst");
					break;
				default:
					throw new RuntimeException("Somethig is wrong!");
				}
			}
			input.push(s);
		}
		return input;
	}

	/**Автоматически создает таблицу разбора LL(1)*/
	private void buildParsingTable() {
		for (Rule r : rules) {
			Symbol[] rightSide = r.getRightSide();
			NonTerminal leftSide = r.getLeftSide();
			Set<Terminal> firstSetForRightSide = first(rightSide);
			Set<Terminal> followSetForLeftSide = followSet.get(leftSide);

			for (Terminal s : firstSetForRightSide) {
				parsingTable.put(new SimpleEntry<>(leftSide, s), rightSide);
			}

			if (firstSetForRightSide.contains(epsilon)) {
				for (Terminal s : followSetForLeftSide) {
					parsingTable
							.put(new SimpleEntry<>(leftSide, s), rightSide);
				}
			}
		}
	}

	private void calculateFirst() {
		for (Symbol s : alphabet) {
			firstSet.put(s, new HashSet<>());
		}
		for (Symbol s : alphabet) {
			first(s);
		}
	}

	/**
	 * Вычисляет first set для указанного символа. Используя следующие правила:
	 * <blockquote>
	 *
	 * <pre>
	 * 1. If X is terminal, then FIRST(X) is {X}.
	 * 2. If X -> EPSILON is production, then add EPSILON to FIRST(X).
	 * 3. If X is nonterminal and X -> Y1 Y2 ... Yk is a production,
	 * then place <i>a</i> (terminal) in FIRST(X) if for some i <i>a</i> is in FIRST(Yi), and Y1, ... ,Yi-1 -> EPSILON.
	 * If EPSILON is in FIRST(Yj) for all j = 1, 2, ... , k, then add EPSILON to FIRST(X).
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * @param symbol терминальный или нетерминальный символ грамматики
	 *
	 * */
	private void first(Symbol symbol) {
		Set<Terminal> first = firstSet.get(symbol);
		Set<Terminal> auxiliarySet;
		if (symbol.isTerminal()) {
			first.add((Terminal) symbol);
			return;
		}

		for (Rule r : getRulesWithLeftSide((NonTerminal) symbol)) {
			Symbol[] rightSide = r.getRightSide();
			first(rightSide[0]);
			auxiliarySet = new HashSet<>(firstSet.get(rightSide[0]));
			auxiliarySet.remove(epsilon);
			first.addAll(auxiliarySet);

			for (int i = 1; i < rightSide.length
					&& firstSet.get(rightSide[i - 1]).contains(epsilon); i++) {
				first(rightSide[i]);
				auxiliarySet = new HashSet<>(firstSet.get(rightSide[i]));
				auxiliarySet.remove(epsilon);
				first.addAll(auxiliarySet);
			}

			boolean allContainEpsilon = true;
			for (Symbol rightS : rightSide) {
				if (!firstSet.get(rightS).contains(epsilon)) {
					allContainEpsilon = false;
					break;
				}
			}
			if (allContainEpsilon)
				first.add(epsilon);
		}
	}

	/**
	 * Вычисляет first set для цепочки символов
	 *
	 * @param chain строка символов
	 * @return first set для указанной строки
	 * */
	private Set<Terminal> first(Symbol[] chain) {
		Set<Terminal> auxiliarySet;
		auxiliarySet = new HashSet<>(firstSet.get(chain[0]));
		auxiliarySet.remove(epsilon);
		Set<Terminal> firstSetForChain = new HashSet<>(auxiliarySet);

		for (int i = 1; i < chain.length && firstSet.get(chain[i - 1]).contains(epsilon); i++) {
			auxiliarySet = new HashSet<>(firstSet.get(chain[i]));
			auxiliarySet.remove(epsilon);
			firstSetForChain.addAll(auxiliarySet);
		}

		boolean allContainEpsilon = true;
		for (Symbol s : chain) {
			if (!firstSet.get(s).contains(epsilon)) {
				allContainEpsilon = false;
				break;
			}
		}
		if (allContainEpsilon)
			firstSetForChain.add(epsilon);

		return firstSetForChain;
	}

	private void calculateFollow() {
		for (Symbol s : alphabet) {
			if (s.isNonTerminal())
				followSet.put(s, new HashSet<>());
		}

		Map<SimpleEntry<Symbol, Symbol>, Boolean> callTable = new HashMap<>();
		for (Symbol firstS : alphabet) {
			for (Symbol secondS : alphabet) {
				callTable.put(new SimpleEntry<>(firstS, secondS), false);
			}
		}

		NonTerminal firstSymbol = rules.get(0).getLeftSide();
		followSet.get(firstSymbol).add(endOfProgram);
		for (Symbol s : alphabet) {
			if (s.isNonTerminal()) {
				follow((NonTerminal) s, null, callTable);
			}
		}
	}

	/**Рассчитывает следующий набор для нетерминальных символов*/
	private void follow(NonTerminal s, Symbol caller,
			Map<SimpleEntry<Symbol, Symbol>, Boolean> callTable) {
		Boolean called = callTable.get(new SimpleEntry<Symbol, Symbol>(caller, s));
		if (called != null) {
			if (called)
				return;
			else
				callTable.put(new SimpleEntry<>(caller, s), true);
		}

		Set<Terminal> follow = followSet.get(s);
		Set<Terminal> auxiliarySet;

		List<SimpleEntry<NonTerminal, Symbol[]>> list = getLeftSideRightChain(s);
		for (SimpleEntry<NonTerminal, Symbol[]> pair : list) {
			Symbol[] rightChain = pair.getValue();
			NonTerminal leftSide = pair.getKey();
			if (rightChain.length != 0) {
				auxiliarySet = first(rightChain);
				auxiliarySet.remove(epsilon);
				follow.addAll(auxiliarySet);
				if (first(rightChain).contains(epsilon)) {
					follow(leftSide, s, callTable);
					follow.addAll(followSet.get(leftSide));
				}
			} else {
				follow(leftSide, s, callTable);
				follow.addAll(followSet.get(leftSide));
			}
		}
	}

	/**
	 * Создает грамматические правила из файла
	 *
	 * @param grammarFile файл с правилами грамматики
	 * @throws FileNotFoundException  если файл с указанным путем не существует
	 * */
	private void parseRules(File grammarFile) throws FileNotFoundException {
		nameToSymbol.put("EPSILON", epsilon);

		Scanner data = new Scanner(grammarFile);
		int code = 1;
		int ruleNumber = 0;
		while (data.hasNext()) {
			StringTokenizer t = new StringTokenizer(data.nextLine());
			String symbolName = t.nextToken();
			if (!nameToSymbol.containsKey(symbolName)) {
				NonTerminal s = new NonTerminal(code, symbolName);
				if (code == 1)
					startSymbol = s;
				nameToSymbol.put(symbolName, s);
				alphabet.add(s);
				code++;
			}
			t.nextToken();// ->

			NonTerminal leftSide = (NonTerminal) nameToSymbol.get(symbolName);
			while (t.hasMoreTokens()) {
				List<Symbol> rightSide = new ArrayList<>();
				do {
					symbolName = t.nextToken();
					if (!symbolName.equals("|")) {
						if (!nameToSymbol.containsKey(symbolName)) {
							Symbol s;
							if (Character.isUpperCase(symbolName.charAt(0)))
								s = new NonTerminal(code++, symbolName);
							else
								s = new Terminal(code++, symbolName);
							nameToSymbol.put(symbolName, s);
							alphabet.add(s);
						}
						rightSide.add(nameToSymbol.get(symbolName));
					}
				} while (!symbolName.equals("|") && t.hasMoreTokens());
				rules.add(new Rule(ruleNumber++, leftSide, rightSide.toArray(new Symbol[] {})));
			}
		}
	}

	/**Возвращает правила с указанной левой стороной
	 *
	 * @return набор правил, которые содержат указанный символ в левой части
	 * */
	private Set<Rule> getRulesWithLeftSide(NonTerminal nonTerminalSymbol) {
		Set<Rule> set = new HashSet<>();
		for (Rule r : rules) {
			if (r.getLeftSide().equals(nonTerminalSymbol))
				set.add(r);
		}
		return set;
	}

	/**
	 * Возвращает список пар. Первый элемент пары является левой стороной правила,
	 * если это правило содержит указанный символ {@code s} в правой части
	 * Второй элемент содержит символы после {@code s} в правой части правила
	 * */
	private List<SimpleEntry<NonTerminal, Symbol[]>> getLeftSideRightChain(Symbol s) {
		List<SimpleEntry<NonTerminal, Symbol[]>> list = new ArrayList<>();
		for (Rule r : rules) {
			Symbol[] rightChain = r.getRightSide();
			int index = Arrays.asList(rightChain).indexOf(s);
			if (index != -1) {
				rightChain = Arrays.copyOfRange(rightChain, index + 1, rightChain.length);
				list.add(new SimpleEntry<>(r.getLeftSide(), rightChain));
			}
		}
		return list;
	}

	/**Возвращает правило с указанной левой и правой стороной*/
	private Rule getRule(NonTerminal leftSide, Symbol[] rightSide) {
		Set<Rule> setOfRules = getRulesWithLeftSide(leftSide);
		for (Rule r : setOfRules) {
			if (rightSide.length != r.getRightSide().length)
				continue;
			for (int i = 0; i < rightSide.length; i++) {
				if (r.getRightSide()[i] != rightSide[i])
					break;
				else {
					if (i == rightSide.length - 1) {
						return r;
					}
				}
			}
		}
		return null;
	}
}
