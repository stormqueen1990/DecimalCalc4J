package util;

import java.math.BigDecimal;
import java.util.HashMap;

public class ValueMap extends HashMap<String, BigDecimal> {
	private static final long serialVersionUID = 7958735272430531616L;

	public BigDecimal put(String key, BigDecimal value) {
		return super.put(key, value);
	}

	public BigDecimal get(Object key) {
		return super.get(key);
	}
}
