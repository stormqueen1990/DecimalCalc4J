package test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import parser.Parser;
import util.ValueMap;
import exception.LexicalException;

public class TestCalculation {
	
	@Test
	public void testSum() throws LexicalException, Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(5));
		values.put("val2", new BigDecimal(10));
		
		Parser p = new Parser("val1+val2");
		BigDecimal value = p.eval(p.lexicalVerifier(), values);
		assertEquals(value.compareTo(new BigDecimal("15")), 0);
	}
	
	@Test
	public void testMinus() throws LexicalException, Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(5));
		values.put("val2", new BigDecimal(10));
		
		Parser p = new Parser("val1-val2");
		BigDecimal value;
		value = p.eval(p.lexicalVerifier(), values);
		assertEquals(value.compareTo(new BigDecimal("-5")), 0);
	}
	
	@Test
	public void testMult() throws LexicalException, Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(3));
		values.put("val2", new BigDecimal(10));
		
		Parser p = new Parser("val1*val2");
		BigDecimal value;
		value = p.eval(p.lexicalVerifier(), values);
		assertEquals(value.compareTo(new BigDecimal("30")), 0);
	}
	
	@Test
	public void testDiv() throws LexicalException, Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(10));
		values.put("val2", new BigDecimal(2));
		
		Parser p = new Parser("val1/val2");
		BigDecimal value;
		value = p.eval(p.lexicalVerifier(), values);
		assertEquals(value.compareTo(new BigDecimal("5")), 0);
	}
	
	@Test
	public void testSumParen() throws LexicalException, Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(5));
		values.put("val2", new BigDecimal(8));
		values.put("val3", new BigDecimal(3));
		
		Parser p = new Parser("val1+(val2+val3)");
		BigDecimal value;
		value = p.eval(p.lexicalVerifier(), values);
		assertEquals(value.compareTo(new BigDecimal("16")), 0);
	}
	
	@Test
	public void testMinusParen() throws LexicalException, Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(5));
		values.put("val2", new BigDecimal(8));
		values.put("val3", new BigDecimal(3));
		
		Parser p = new Parser("val1-(val2-val3)");
		BigDecimal value;
		value = p.eval(p.lexicalVerifier(), values);
		assertEquals(value.compareTo(new BigDecimal("0")), 0);
	}
	
	@Test
	public void testMultParen() throws LexicalException, Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(5));
		values.put("val2", new BigDecimal(8));
		values.put("val3", new BigDecimal(3));
		
		Parser p = new Parser("val1*(val2*val3)");
		BigDecimal value;
		value = p.eval(p.lexicalVerifier(), values);
		assertEquals(value.compareTo(new BigDecimal("120")), 0);
	}
	
	@Test
	public void testDivParen() throws LexicalException, Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(12));
		values.put("val2", new BigDecimal(9));
		values.put("val3", new BigDecimal(3));
		
		Parser p = new Parser("val1/(val2/val3)");
		BigDecimal value;
		value = p.eval(p.lexicalVerifier(), values);
		assertEquals(value.compareTo(new BigDecimal("4")), 0);
	}
	
	@Test
	public void complexExp1() throws Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(5));
		values.put("val2", new BigDecimal(7));
		values.put("val3", new BigDecimal(8));
		values.put("val4", new BigDecimal(20));
		values.put("val5", new BigDecimal(80));
		
		Parser p = new Parser("val1+val2-(val3*val4/val5)+val1^2.0^3.0");
		BigDecimal value = p.eval(p.lexicalVerifier(), values);
		
		assertEquals(value.compareTo(new BigDecimal("390635")), 0);
	}
	
	@Test
	public void complexExp2() throws Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(5));
		values.put("val2", new BigDecimal(7));
		values.put("val3", new BigDecimal(8));
		values.put("val4", new BigDecimal(20));
		values.put("val5", new BigDecimal(20));
		
		Parser p = new Parser("val1+val2-(val3*val4/val5^2.0)+val1^2.0^3.0");
		BigDecimal value = p.eval(p.lexicalVerifier(), values);
		
		assertEquals(value.compareTo(new BigDecimal("390636.6")), 0);
	}
	
	@Test
	public void complexExp3() throws Exception {
		ValueMap values = new ValueMap();
		values.put("val1", new BigDecimal(5));
		values.put("val2", new BigDecimal(7));
		values.put("val3", new BigDecimal(8));
		values.put("val4", new BigDecimal(20));
		values.put("val5", new BigDecimal(20));
		
		Parser p = new Parser("val1+val2-((val3*val4/val5)^2.0)+val1^2.0^3.0");
		BigDecimal value = p.eval(p.lexicalVerifier(), values);
		
		assertEquals(value.compareTo(new BigDecimal("390573")), 0);
	}
}
