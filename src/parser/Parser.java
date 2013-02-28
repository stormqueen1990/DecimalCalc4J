package parser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import util.Utils;
import util.ValueMap;
import enumeration.TypeEnum;
import exception.LexicalException;
import exception.ParsingException;

public class Parser {
	private String expression;
	
	public Parser(String expression) {
		this.expression = expression;
	}
	
	private Token createToken(String text, TypeEnum type, int initIndex) {
		Token token = new Token();
		token.setText(text);
		token.setType(type);
		token.setInitIndex(initIndex);
		return token;
	}
	
	public List<Token> lexicalVerifier() throws Exception {
		List<Token> tokens = new LinkedList<Token>();
		int pos = 0;
		
		char currChar;
		StringBuilder tk = new StringBuilder();
		TypeEnum type = null;
		int initIndex = pos;
		while (pos < expression.length()) {
			currChar = expression.charAt(pos);
			
			if (Character.toLowerCase(currChar) >= 'a' && Character.toLowerCase(currChar) <= 'z') {
				if (!tk.toString().isEmpty() && Utils.nvl(type, TypeEnum.IDENTIFIER) != TypeEnum.IDENTIFIER) {
					tokens.add(createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.IDENTIFIER;
			} else if ((currChar >= '0' && currChar <= '9') || currChar == '.') {
				if (!tk.toString().isEmpty() && Utils.nvl(type, TypeEnum.NUMBER) != TypeEnum.NUMBER) {
					tokens.add(createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.NUMBER;
			} else if (currChar == '+') {
				if (!tk.toString().isEmpty()) {
					tokens.add(createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.SUM;
			} else if (currChar == '-') {
				if (!tk.toString().isEmpty()) {
					tokens.add(createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.MINUS;
			} else if (currChar == '*') {
				if (!tk.toString().isEmpty()) {
					tokens.add(createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.MULT;
			} else if (currChar == '/') {
				if (!tk.toString().isEmpty()) {
					tokens.add(createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.DIV;
			} else if (currChar == '%') {
				if (!tk.toString().isEmpty()) {
					tokens.add(createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.MOD;
			} else if (currChar == '^') {
				if (!tk.toString().isEmpty()) {
					tokens.add(createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.POWER;
			} else if (currChar == '(') {
				if (!tk.toString().isEmpty()) {
					tokens.add(createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.OPEN_BRACK;
			} else if (currChar == ')') {
				if (!tk.toString().isEmpty()) {
					tokens.add(createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.CLOSE_BRACK;
			} else {
				throw new LexicalException("Unrecognized character: " + currChar);
			}
			
			pos++;
		}
		
		if (!tk.toString().isEmpty()) {
			tokens.add(createToken(tk.toString(), type, initIndex));
		}
		
		return tokens;
	}
	
	public BigDecimal parse(LinkedList<Token> tokens, ValueMap values, int initialIndex, BigDecimal lastValue)
			throws Exception {
		BigDecimal expValue = BigDecimal.ZERO;
		ListIterator<Token> it = tokens.listIterator(initialIndex);
		Token prev = null;
		Token curr = null;
		Token next = null;
		
		if (it.hasNext()) {
			curr = it.next();
			if (initialIndex > 0) {
				prev = tokens.get(initialIndex - 1);
			}
			
			if (it.hasNext()) {
				next = it.next();
			} else {
				next = null;
			}
			
			if (curr != null) {
				if (curr.getType() == TypeEnum.NUMBER || curr.getType() == TypeEnum.IDENTIFIER) {
					BigDecimal operand;
					
					if (curr.getType() == TypeEnum.NUMBER) {
						operand = new BigDecimal(curr.getText());
					} else {
						operand = values.get(curr.getText());
					}
					
					if (prev != null
							&& (prev.getType() == TypeEnum.POWER || prev.getType() == TypeEnum.MULT || prev.getType() == TypeEnum.DIV)) {
						expValue = operand;
					} else if (next == null) {
						expValue = operand;
					} else if (next.getType() == TypeEnum.MULT || next.getType() == TypeEnum.DIV) {
						if (!it.hasNext()) {
							throw new ParsingException(String.format(
									"Error at position %d: expected NUMBER or IDENTIFIER, found EOL.",
									next.getInitIndex() + next.getText().length()));
						}
						
						BigDecimal subExpValue = parse(tokens, values, it.nextIndex(), lastValue);
						
						if (next.getType() == TypeEnum.MULT) {
							expValue = operand.multiply(subExpValue);
						} else {
							expValue = operand.divide(subExpValue, 10, RoundingMode.HALF_EVEN);
							expValue = expValue.setScale(2, RoundingMode.HALF_EVEN);
						}
					} else if (next.getType() == TypeEnum.SUM || next.getType() == TypeEnum.MINUS) {
						if (!it.hasNext()) {
							throw new ParsingException(String.format(
									"Error at position %d: expected NUMBER or IDENTIFIER, found EOL.",
									next.getInitIndex() + next.getText().length()));
						}
						
						BigDecimal subExpValue = parse(tokens, values, it.nextIndex(), lastValue);
						
						if (next.getType() == TypeEnum.SUM) {
							expValue = operand.add(subExpValue);
						} else {
							expValue = operand.subtract(subExpValue);
						}
						
					}
				} else if (curr.getType() == TypeEnum.SUM || curr.getType() == TypeEnum.MINUS
						|| curr.getType() == TypeEnum.MULT || curr.getType() == TypeEnum.DIV) {
					if (initialIndex == 0) {
						throw new ParsingException(String.format("Dangling %s sign at 0.", curr.getText()));
					}
					
					expValue = parse(tokens, values, it.nextIndex(), lastValue);
					// expValue = lastValue.
				}
			}
			
			expValue = parse(tokens, values, it.nextIndex(), expValue);
		}
		
		return expValue;
	}
	
	public static void main(String[] args) {
		Parser parser = new Parser("a*b+c");
		try {
			ValueMap values = new ValueMap();
			values.put("a", new BigDecimal(5));
			values.put("b", new BigDecimal(3));
			values.put("c", new BigDecimal(5));
			List<Token> tokens = parser.lexicalVerifier();
			
			System.out.println(tokens);
			
			System.out.println(parser.parse((LinkedList<Token>) tokens, values, 0, BigDecimal.ZERO));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
