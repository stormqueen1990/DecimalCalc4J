package util;

public class Utils {
	public static <T> T nvl(T n, T v) {
		if (n == null) {
			return v;
		}

		return n;
	}
}
