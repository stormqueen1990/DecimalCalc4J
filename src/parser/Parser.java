package parser;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.LinkedList;

import util.Utils;
import util.ValueMap;
import enumeration.TypeEnum;
import exception.LexicalException;
import exception.ParsingException;

/**
 * Lexically verifies, parses and evaluates an expression string.
 * 
 * @author mauren
 */
public class Parser {
	private String expression;
	private int lookahead = 0;
	
	/**
	 * Constructs a new parser object.
	 * 
	 * @param expression
	 *            expression to be parsed and evaluated.
	 */
	public Parser(String expression) {
		this.expression = expression;
	}
	
	/**
	 * Creates a token.
	 * 
	 * @param text
	 *            token string value.
	 * @param type
	 *            token type.
	 * @param initIndex
	 *            start index where the token was found.
	 * @return a new instance of {@link Token}.
	 */
	private Token createToken(String text, TypeEnum type, int initIndex) {
		Token token = new Token();
		token.setText(text);
		token.setType(type);
		token.setInitIndex(initIndex);
		return token;
	}
	
	/**
	 * Verifies lexically the expression passed.
	 * 
	 * @return a list of tokens recognized.
	 * @throws LexicalException
	 *             when an unrecognized lexem is found.
	 */
	public LinkedList<Token> lexicalVerifier() throws LexicalException {
		LinkedList<Token> tokens = new LinkedList<Token>();
		int pos = 0;
		
		char currChar;
		StringBuilder tk = new StringBuilder();
		TypeEnum type = null;
		int initIndex = pos;
		while (pos < this.expression.length()) {
			currChar = this.expression.charAt(pos);
			
			if (Character.toLowerCase(currChar) >= 'a' && Character.toLowerCase(currChar) <= 'z') {
				if (!tk.toString().isEmpty() && Utils.nvl(type, TypeEnum.IDENTIFIER) != TypeEnum.IDENTIFIER) {
					tokens.add(this.createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.IDENTIFIER;
			} else if (currChar >= '0' && currChar <= '9') {
				if (!tk.toString().isEmpty() && Utils.nvl(type, TypeEnum.NUMBER) != TypeEnum.NUMBER) {
					if (type == TypeEnum.IDENTIFIER) {
						tk.append(currChar);
					} else {
						tokens.add(this.createToken(tk.toString(), type, initIndex));
						tk = new StringBuilder();
						initIndex = pos;
						
						tk.append(currChar);
						type = TypeEnum.NUMBER;
					}
				} else {
					tk.append(currChar);
					type = TypeEnum.NUMBER;
				}
			} else if (currChar == '.') {
				if (!tk.toString().isEmpty() && Utils.nvl(type, TypeEnum.NUMBER) != TypeEnum.NUMBER) {
					tokens.add(this.createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.NUMBER;
			} else if (currChar == '+') {
				if (!tk.toString().isEmpty()) {
					tokens.add(this.createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.PLUS;
			} else if (currChar == '-') {
				if (!tk.toString().isEmpty()) {
					tokens.add(this.createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.MINUS;
			} else if (currChar == '*') {
				if (!tk.toString().isEmpty()) {
					tokens.add(this.createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.MULT;
			} else if (currChar == '/') {
				if (!tk.toString().isEmpty()) {
					tokens.add(this.createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.DIV;
			} else if (currChar == '%') {
				if (!tk.toString().isEmpty()) {
					tokens.add(this.createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.MOD;
			} else if (currChar == '^') {
				if (!tk.toString().isEmpty()) {
					tokens.add(this.createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.POWER;
			} else if (currChar == '(') {
				if (!tk.toString().isEmpty()) {
					tokens.add(this.createToken(tk.toString(), type, initIndex));
					tk = new StringBuilder();
					initIndex = pos;
				}
				
				tk.append(currChar);
				type = TypeEnum.OPEN_BRACK;
			} else if (currChar == ')') {
				if (!tk.toString().isEmpty()) {
					tokens.add(this.createToken(tk.toString(), type, initIndex));
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
			tokens.add(this.createToken(tk.toString(), type, initIndex));
		}
		
		this.lookahead = 0;
		return tokens;
	}
	
	/**
	 * Initiates the parsing phase.
	 * 
	 * @param tokens
	 *            tokens recognized by the lexical verifier.
	 * @param values
	 *            values to translate identifiers.
	 * @return value of expression.
	 * @throws Exception
	 */
	public BigDecimal eval(LinkedList<Token> tokens, ValueMap values) throws ParsingException {
		return this.exp(tokens, values);
	}
	
	/**
	 * Expression representation.
	 * 
	 * @param tokens
	 *            the tokens to be parsed.
	 * @param values
	 *            variables values.
	 * @param value
	 *            previous expression parsing value.
	 * @return the current value for parsed expression.
	 * @throws ParsingException
	 */
	public BigDecimal exp(LinkedList<Token> tokens, ValueMap values) throws ParsingException {
		BigDecimal value = BigDecimal.ZERO;
		value = this.interm1(tokens, values, value);
		value = this.expLevel3(tokens, values, value);
		
		return value;
	}
	
	/**
	 * Intermediate parsing, level 1.
	 * 
	 * @param tokens
	 *            the tokens to be parsed.
	 * @param values
	 *            variables values.
	 * @param value
	 *            previous expression parsing value.
	 * @return the current value for parsed expression.
	 * @throws ParsingException
	 */
	public BigDecimal interm1(LinkedList<Token> tokens, ValueMap values, BigDecimal value) throws ParsingException {
		value = this.interm2(tokens, values, value);
		value = this.expLevel2(tokens, values, value);
		
		return value;
	}
	
	/**
	 * Intermediate parsing, level 2.
	 * 
	 * @param tokens
	 *            the tokens to be parsed.
	 * @param values
	 *            variables values.
	 * @param value
	 *            previous expression parsing value.
	 * @return the current value for parsed expression.
	 * @throws ParsingException
	 */
	public BigDecimal interm2(LinkedList<Token> tokens, ValueMap values, BigDecimal value) throws ParsingException {
		value = this.term(tokens, values);
		value = this.expLevel1(tokens, values, value);
		
		return value;
	}
	
	/**
	 * Parses the level 1 operators (power sign).
	 * 
	 * @param tokens
	 *            tokens recognized by the lexical verifier.
	 * @param values
	 *            values to translate identifiers.
	 * @param value
	 *            the last processed value.
	 * @return value for the evaluated operation.
	 * @throws ParsingException
	 *             when an unexpected token is found.
	 */
	public BigDecimal expLevel1(LinkedList<Token> tokens, ValueMap values, BigDecimal value) throws ParsingException {
		Token tk = null;
		// Verifies if there is a next token
		if (this.lookahead < tokens.size()) {
			tk = tokens.get(this.lookahead);
		}
		
		if (tk != null) {
			// Parses the operation value
			if (tk.getType() == TypeEnum.POWER) {
				this.lookahead++;
				BigDecimal operand = this.term(tokens, values);
				
				operand = this.expLevel1(tokens, values, operand);
				
				// Parses current operation
				value = value.pow(operand.intValue());
			}
		}
		
		return value;
	}
	
	/**
	 * Parses the level 2 operators (multiply and divide signs).
	 * 
	 * @param tokens
	 *            tokens recognized by the lexical verifier.
	 * @param values
	 *            values to translate identifiers.
	 * @param value
	 *            the last processed value.
	 * @return value for the evaluated operation.
	 * @throws ParsingException
	 *             when an unexpected token is found.
	 */
	public BigDecimal expLevel2(LinkedList<Token> tokens, ValueMap values, BigDecimal value) throws ParsingException {
		Token tk = null;
		// Verifies if there is a next token
		if (this.lookahead < tokens.size()) {
			tk = tokens.get(this.lookahead);
		}
		
		if (tk != null) {
			// Parses the operation value
			if (tk.getType() == TypeEnum.MULT) {
				this.lookahead++;
				BigDecimal op1 = value;
				BigDecimal op2;
				// Parses the higher precedence operations
				op2 = this.interm2(tokens, values, value);
				
				// Parses current operation
				value = op1.multiply(op2);
				value = this.expLevel2(tokens, values, value);
			} else if (tk.getType() == TypeEnum.DIV) {
				this.lookahead++;
				BigDecimal op1 = value;
				BigDecimal op2;
				// Parses the higher precedence operations
				op2 = this.interm2(tokens, values, value);
				
				// Parses current operation
				value = op1.divide(op2, 10, RoundingMode.HALF_UP);
				value = this.expLevel2(tokens, values, value);
			} else if (tk.getType() == TypeEnum.MOD) {
				this.lookahead++;
				BigDecimal op1 = value;
				BigDecimal op2;
				
				// Higher precedence operations
				op2 = this.interm2(tokens, values, value);
				
				// Current op
				value = op1.remainder(op2, new MathContext(10, RoundingMode.HALF_UP));
				value = this.expLevel2(tokens, values, value);
			}
		}
		
		return value;
	}
	
	/**
	 * Parses the level 3 operators (sum and minus signs).
	 * 
	 * @param tokens
	 *            tokens recognized by the lexical verifier.
	 * @param values
	 *            values to translate identifiers.
	 * @param value
	 *            the last processed value.
	 * @return value for the evaluated operation.
	 * @throws ParsingException
	 *             when an unexpected token is found.
	 */
	public BigDecimal expLevel3(LinkedList<Token> tokens, ValueMap values, BigDecimal value) throws ParsingException {
		Token tk = null;
		
		// Verifies if there is a next token
		if (this.lookahead < tokens.size()) {
			tk = tokens.get(this.lookahead);
		}
		
		if (tk != null) {
			// Parses the operation value
			if (tk.getType() == TypeEnum.PLUS) {
				this.lookahead++;
				BigDecimal op1 = value;
				BigDecimal op2;
				// Executes parsing for higher precedence operations
				op2 = this.interm1(tokens, values, op1);
				
				// Parses current operation
				value = op1.add(op2);
				value = this.expLevel3(tokens, values, value);
			} else if (tk.getType() == TypeEnum.MINUS) {
				this.lookahead++;
				BigDecimal op1 = value;
				BigDecimal op2;
				// Executes parsing for higher precedence operations
				op2 = this.interm1(tokens, values, op1);
				
				// Parses current operation
				value = op1.subtract(op2);
				value = this.expLevel3(tokens, values, value);
			}
		}
		
		return value;
	}
	
	/**
	 * Parses the terminal token and subexpressions.
	 * 
	 * @param tokens
	 *            tokens recognized by the lexical verifier.
	 * @param values
	 *            values to translate identifiers.
	 * @return terminal token value.
	 * @throws ParsingException
	 *             when an unexpected token is found.
	 */
	public BigDecimal term(LinkedList<Token> tokens, ValueMap values) throws ParsingException {
		Token tk = null;
		
		// Verifies if there is a next token
		if (this.lookahead < tokens.size()) {
			tk = tokens.get(this.lookahead);
		}
		
		boolean positive = true;
		if (tk != null) {
			if (tk.getType() == TypeEnum.PLUS) {
				this.lookahead++;
			} else if (tk.getType() == TypeEnum.MINUS) {
				this.lookahead++;
				positive = false;
			}
			
			if (this.lookahead >= tokens.size()) {
				throw new ParsingException("unexpected token at " + tk.getInitIndex());
			}
			
			tk = tokens.get(this.lookahead);
			
			BigDecimal value = null;
			// Parses the value, depending on terminal type
			if (tk.getType() == TypeEnum.NUMBER) {
				this.lookahead++;
				value = new BigDecimal(tk.getText());
				
				if (!positive) {
					value = value.negate();
				}
				
				return value;
			} else if (tk.getType() == TypeEnum.IDENTIFIER) {
				this.lookahead++;
				value = values.get(tk.getText());
				
				if (!positive) {
					value = value.negate();
				}
				
				return value;
			} else
			// Parses subexpressions
			if (tk.getType() == TypeEnum.OPEN_BRACK) {
				this.lookahead++;
				
				value = this.exp(tokens, values);
				
				tk = tokens.get(this.lookahead);
				if (tk.getType() == TypeEnum.CLOSE_BRACK) {
					this.lookahead++;
					return value;
				}
			}
		}
		
		// Throws an error when an unexpected token is found
		throw new ParsingException("unexpected token at " + tk.getInitIndex());
	}
}
