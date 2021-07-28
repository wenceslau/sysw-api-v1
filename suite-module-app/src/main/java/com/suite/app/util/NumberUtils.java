package com.suite.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import com.google.common.base.CharMatcher;
import com.ibm.icu.text.NumberFormat;

/**
 * Classe Util para conversao e formatacao de objetos de numeros
 * @author Wenceslau
 *
 */
public class NumberUtils {

	/* NUMBERS */

	private static Locale localeBR = new Locale("pt", "BR");

	/**
	 * Soma dois BigDecimal
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static BigDecimal sum(BigDecimal value1, BigDecimal value2) {

		if (value1 == null)
			value1 = getBigDecimal(0);

		if (value2 == null)
			value2 = getBigDecimal(0);

		value1 = value1.add(value2);

		return value1;

	}

	/**
	 * Subtrai dois BigDecimal
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static BigDecimal subtract(BigDecimal value1, BigDecimal value2) {

		if (value1 == null)
			value1 = getBigDecimal(0);

		if (value2 == null)
			value2 = getBigDecimal(0);

		value1 = value1.subtract(value2);

		return value1;

	}

	/**
	 * Multiplica dois BigDecimal
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal value1, BigDecimal value2) {

		if (value1 == null)
			value1 = getBigDecimal(0);

		if (value2 == null)
			value2 = getBigDecimal(0);

		value1 = value1.multiply(value2);

		return value1;

	}

	/**
	 * Divide dois BigDecimal
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static BigDecimal divide(BigDecimal value1, BigDecimal value2) {

		if (value1 == null)
			value1 = getBigDecimal(0);

		if (value2 == null)
			value2 = getBigDecimal(0);

		if (value2.intValue() != 0)
			value1 = value1.divide(value2, 4, RoundingMode.HALF_UP);

		return value1;

	}

	/**
	 * Convert int para BigDecimal
	 * @param value
	 * @return
	 */
	public static BigDecimal getBigDecimal(Integer value) {

		if (value == null)
			value = 0;
		return new BigDecimal(value);

	}

	public static boolean isNumeric(String value) {

		return org.apache.commons.lang3.math.NumberUtils.isCreatable(value);

	}

	public static String formarPtBR(Object number, int maxFraction) {

		if (number == null || number.toString().isEmpty())
			return "";

		NumberFormat nf = NumberFormat.getNumberInstance(localeBR);
		nf.setMaximumFractionDigits(maxFraction);
		nf.setMinimumFractionDigits(2);
				
		return nf.format(number);

	}

	public static Double StringToDouble(Object number) {

		if (number == null || number.toString().isEmpty())
			return null;

		String strValue = number + "";

		int countDot = CharMatcher.is('.').countIn(strValue);
		int countComma = CharMatcher.is(',').countIn(strValue);

		if (countDot > 0 && countComma > 0) {
			// Se tem ponto e virgula verifica quem veio primeiro
			// se foi o ponto ta no formato pt
			// se foi a virgula ta no formato en

			int posDot = strValue.indexOf(".");
			int posComma = strValue.indexOf(",");

			// pont veio primeiro, entao eh pt
			if (posDot < posComma)
				strValue = strValue.replace(".", "").replace(",", ".");
			else
				strValue = strValue.replace(",", "");

		} else if (countDot > 1 && countComma == 0) {
			// Se tem mais de 1 ponto e nao tem virgula
			// Veio no formato pt, e nao tem decimal

			strValue = strValue.replace(".", "");

		} else if (countComma > 1 && countDot == 0) {
			// Se tem mais de 1 virgula e nao tem ponto
			// Veio no formato en, e nao tem decimal

			strValue = strValue.replace(",", "");

		} else {
			// se nao eh nenhum dos itens troca uma possivel virgula
			// por ponto, se existir
			
			strValue = strValue.replace(",", ".");

		}

		return Double.parseDouble(strValue);

	}

}
