package ru.agrmv.parser;

/**
 * Класс {@code Terminal} представляет терминальный символ грамматики
 *
 * @author Aleksey Gromov
 * */

public class Terminal extends Symbol{

	/**
	 * Создает новый объект {@code Terminal} с указанным кодом и обозначением
	 *
	 * @param code код терминального символа
	 * @param name наименование обозначения терминальнорго символа в грамматике
	 * */
	public Terminal(int code, String name) {
		super(code,name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if(obj == null)
			return false;
		if (obj.getClass() != Terminal.class)
			return false;
		Terminal ts = (Terminal) obj;
		return this.getCode() == ts.getCode();
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public boolean isNonTerminal() {
		return false;
	}
}