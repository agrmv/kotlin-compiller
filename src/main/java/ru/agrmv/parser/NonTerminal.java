package ru.agrmv.parser;

/**
 * Класс {@code Terminal} представляет нетерминальный символ грамматики
 *
 * @author Aleksey Gromov
 * */


public class NonTerminal extends Symbol {

	/**
	 * Создает новый объект {@code NonTerminal} с указанным кодом и обозначением
	 *
	 * @param code код нетерминального символа
	 * @param name наименование обозначения нетерминальнорго символа в грамматике
	 * */
	public NonTerminal(int code, String name) {
		super(code, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (obj.getClass() != NonTerminal.class)
			return false;
		NonTerminal nts = (NonTerminal) obj;
		return this.getCode() == nts.getCode();
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

	@Override
	public boolean isNonTerminal() {
		return true;
	}
}