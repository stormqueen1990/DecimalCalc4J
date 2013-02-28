package parser;

import enumeration.TypeEnum;

public class Token {
	private String text;
	private TypeEnum type;
	private int initIndex;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public TypeEnum getType() {
		return type;
	}

	public void setType(TypeEnum type) {
		this.type = type;
	}
	
	public int getInitIndex() {
		return initIndex;
	}
	
	public void setInitIndex(int initIndex) {
		this.initIndex = initIndex;
	}

	@Override
	public String toString() {
		return "[" + text + ", " + type + ", " + initIndex + "]";
	}
}
