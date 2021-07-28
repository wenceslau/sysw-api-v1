package com.suite.app.util;

import java.text.Normalizer;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.util.Strings;

/**
 * Classe Util para conversao e formatacao de objetos string
 * @author Wenceslau
 *
 */
public class StringUtils extends org.springframework.util.StringUtils {

	/* STRINGS */

	/**
	 * Zulo
	 * @param _0
	 * @return
	 */
	public static String dec(String _0) {

		String _6 = new String(Base64.getDecoder().decode(_0.toString().getBytes()));
		String _7 = (_6.substring(20, 21)) + (_6.substring(4, 5)) + (_6.substring(12, 13)) + (_6.substring(45, 46)) +
				(_6.substring(6, 7)) + (_6.substring(21, 22)) + (_6.substring(9, 10)) + (_6.substring(39, 40));
		return _7;

	}

	/**
	 * Remove acentos
	 * @param str
	 * @return
	 */
	public static String deAccent(String str) {

		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = str.replaceAll("[^\\p{ASCII}]", "");
		return str;

	}

	/**
	 * Verifica se existe caracter especial
	 * @param str
	 * @return
	 */
	public static boolean hasSpecialCharacter(String str) {

		// RULE verifica a existencia de caractere especial
		Pattern p = Pattern.compile("[^-a-z0-9_^ç^áàâãéèêíìîóòôõúùû.() ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		return m.find();

	}

	/**
	 * Avaliar a necessidade desse metodo, teoricamente eh igual ao anterior
	 * @param str
	 * @return
	 */
	public static boolean hasSpecialCharacterObjetoDataBase(String str) {

		// RULE verifica a existencia de caractere especial
		Pattern p = Pattern.compile("[^-a-z0-9_^ç^áàâãéèêíìîóòôõúùû ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		return m.find();

	}

	/**
	 * Convert string com _ e padrao CamelCase
	 * @param value
	 * @return
	 */
	public static String camelCase(String value) {

		// String nao pode nula ou vazia
		if (StringUtils.isEmpty(value))
			return value;

		value = value.toLowerCase();
		char[] chars = value.toCharArray();

		for (int i = 0; i < chars.length; i++) {

			// Pega o char e compara com o _
			char c = chars[i];

			if (c == '_') {

				// A posicao do _ nao pode ser a ultima da string
				if (i != value.length() - 1) {

					// Pega o char posterior ao _ e capitaliza ele
					String strChar = String.valueOf(chars[i + 1]);
					chars[i + 1] = strChar.toUpperCase().charAt(0);

				}

			}

		}

		// Cria a string com os chars remove o underline e capitaliza e primeira letra
		value = new String(chars);
		value = value.replace("_", "");
		value = Character.toUpperCase(value.charAt(0)) + value.substring(1);

		return value;

	}

	/**
	 * Executa trim na string
	 * @param value
	 * @return
	 */
	public static String trim(String value) {

		// String nao pode nula ou vazia
		if (StringUtils.isEmpty(value))
			return value;

		return value.trim();

	}

	/**
	 * Convert string para maisucula
	 * @param value
	 * @return
	 */
	public static String upperCase(String value) {

		// String nao pode nula ou vazia
		if (StringUtils.isEmpty(value))
			return value;

		return value.toUpperCase().trim();

	}

	/**
	 * Converte para maisculo e underscor nos espacos
	 * @param value
	 * @return
	 */
	public static String upperAndUnderscor(String value) {

		if (value != null) {

			value = value.toUpperCase().trim();
			value = value.replace(" ", "_");

		}

		return value;

	}

	/**
	 * Converte para minusculo e underscor nos espacos
	 * @param value
	 * @return
	 */
	public static String lowerAndUnderscor(String value) {

		if (value != null) {

			value = value.toLowerCase().trim();
			value = value.replace(" ", "_");

		}

		return value;

	}

	/**
	 * Convert um array bayte em uma string hexadecimal
	 * @param ba
	 * @return
	 */
	public static String toHexString(String value) {

		if (value == null)
			return null;

		byte[] ba = value.getBytes();
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < ba.length; i++)
			str.append(String.format("%x", ba[i]));
		return str.toString();

	}

	/**
	 * Verifica se a String eh hexadecimal
	 * @param hex
	 * @return
	 */
	public static boolean isHexString(String hex) {

		try {

			fromHexString(hex);
			return true;

		} catch (Exception e) {

			System.err.println(e.getMessage());
			return false;

		}

	}

	/**
	 * Converte uma string hexadecima em uma strig asci
	 * @param hex
	 * @return
	 */
	public static String fromHexString(String hex) {

		StringBuilder str = new StringBuilder();

		for (int i = 0; i < hex.length(); i += 2) {

			str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));

		}

		return str.toString();

	}

	/**
	 * Retorna vazio se for nula a string
	 * @param value
	 * @return
	 */
	public static String strNullToEmpty(String value) {

		if (value == null)
			return "";

		return value;

	}

	/**
	 * Converte um objeto null para uma string vazia
	 * @param value
	 * @return
	 */
	public static Object nullToEmpty(Object value) {

		if (value != null)
			return value;
		return "";

	}

	public static void prepareMsgLogger(StringBuilder clsMthLn, StackTraceElement ste) {

		clsMthLn.append("[").append(ste.getClassName()).append(".").append(ste.getMethodName()).append(":").append(ste.getLineNumber());

		if (clsMthLn.length() > 61)
			clsMthLn.delete(1, clsMthLn.length() - 61);
		else
			for (int i = clsMthLn.length(); i <= 61; i++)
				clsMthLn.append(".");

		clsMthLn.append("] ");
		clsMthLn.append("[");
		clsMthLn.append(Utils.getUserRequest());
		clsMthLn.append("] ");

	}

	/**
	 * Zuloy
	 * @param _0
	 * @return
	 */
	public static String z(String _0) {

		char[] _1 = _0.toCharArray();
		String _2 = Strings.EMPTY;
		String _3 = RandomStringUtils.random(50, true, true);
		StringBuilder _4 = new StringBuilder(_3).replace(20, 21, _1[0] + _2).replace(4, 5, _1[1] + _2)
				.replace(12, 13, _1[2] + _2).replace(45, 46, _1[3] + _2).replace(6, 7, _1[4] + _2)
				.replace(21, 22, _1[5] + _2).replace(9, 10, _1[6] + _2).replace(39, 40, _1[7] + _2);

		String _5 = new String(Base64.getEncoder().encode(_4.toString().getBytes()));
		return _5;

	}

}
