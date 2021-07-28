package com.suite.app.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.suite.app.base.Enum.DataType;
import com.suite.app.exception.WarningException;

/**
 * Classe Util para conversao e formatacao de objetos de tempo
 * @author Wenceslau
 *
 */
public class DateTimeUtils {

	/* METODOS CONVERSAO COM ENTRADA OBJECT DATE SQL DATE */

	/**
	 * Convert Date to LocalDate
	 * @param date
	 * @return
	 */
	public static LocalDate getLocalDate(Date date) {

		if (date == null)
			return null;
		Instant instant = date.toInstant();
		ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
		return zdt.toLocalDate();

	}

	/**
	 * Convert Date to LocalDateTime
	 * @param date
	 * @return
	 */
	public static LocalDateTime getLocalDateTime(Date date) {

		if (date == null)
			return null;
		Instant instant = date.toInstant();
		ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
		return zdt.toLocalDateTime();

	}

	/**
	 * Convert SQl Date to LocalDate
	 * @param date
	 * @return
	 */
	public static LocalDate getLocalDateFromSQLDate(java.sql.Date date) {

		if (date == null)
			return null;
		return date.toLocalDate();

	}

	/* METODOS CONVERSAO COM ENTRADA STRING */

	/**
	 * Formata uma string Date, DateTime, Time ou formato desconhecido em uma string no fomato do patternTo
	 * A string de entrada pode estar em qualquer formato possivel.
	 * O retorno sera no formato do PatternTo informado
	 * @param dataType
	 * @param strDate
	 * @param patternTo
	 * @return
	 */
	public static String getStringDateTime(DataType dataType, String strDate, String patternTo) {

		// @formatter:off
		if (strDate == null || strDate.toLowerCase().equals("null") || strDate.isEmpty())
			return null;

		String dateFormated = null;
		List<String> patterns = null;
				
		switch (dataType) {
		case DATE:
			patterns = listOfPatternsDate();
			break;
		case DATETIME:
			patterns = listOfPatternsDateTime();
			break;
		case TIME:
			patterns = listOfPatternsTime();
			break;
		case UNKNOW:
			patterns = listOfAllPatterns();
			break;
		default:
			break;
		}
		
		if (patterns == null) 
			throw new RuntimeException("msg_nao_foi_encontrado_p_p_o_t_d_d_i");
		//Nao foi econtrado patterns para o tipo de dados_informado
		
		for (String pattern : patterns) {
			dateFormated = formatString(strDate, pattern, patternTo);		
			if (dateFormated != null)
				break;
		}
			
		if (dateFormated == null)
			throw new WarningException("msg_nao_foi_possivel_c_o_v_e_d_o_h");
		//Nao foi possivel converter o valor em data em ou hora

		return dateFormated;
		// @formatter:on
	}

	/**
	 * Convert String Date to LocalDate
	 * @param value
	 * @return
	 */
	public static LocalDate getLocalDate(String value) {

		if (value == null || value.toLowerCase().equals("null") || value.isEmpty())
			return null;

		value = value.replace("00:00:00.0000", "")
				.replace("00:00:00.000", "")
				.replace("00:00:00.00", "")
				.replace("00:00:00.0", "")
				.replace("00:00:00", "")
				.trim();

		LocalDate localDate = null;

		List<String> patterns = listOfPatternsDate();

		for (String pattern : patterns) {

			localDate = formatLocalDate(value, pattern);
			if (localDate != null)
				break;

		}
		
		//Se nao converteu tenta conerte para datetime de volta p
		if (localDate == null) {
			patterns = listOfPatternsDateTime();
			LocalDateTime localDateTime = null;

			for (String pattern : patterns) {

				localDateTime = formatLocalDateTime(value, pattern);
				if (localDateTime != null)
					break;
				
			}
			
			if (localDateTime != null)
				localDate = localDateTime.toLocalDate();
		}
		

		if (localDate == null) 
			throw new WarningException("msg_nao_foi_possivel_c_o_v_e_d_o_h");
		//Nao foi possivel converter o valor em data em ou hora

		return localDate;

	}

	/**
	 * Convert String DateTime to LocalDateTime
	 * @param value
	 * @return
	 */
	public static LocalDateTime getLocalDateTime(String value) {

		if (value == null || value.toLowerCase().equals("null") || value.isEmpty())
			return null;

		// Remove o T do formato
		value = value.replace("T", " ");

		LocalDateTime localDateTime = null;

		List<String> patterns = listOfPatternsDateTime();

		for (String pattern : patterns) {

			localDateTime = formatLocalDateTime(value, pattern);
			if (localDateTime != null)
				break;

		}
		
		//Se nao converteu tenta conerte para date e adiciona 00:00:00
		if (localDateTime == null) {
			patterns = listOfPatternsDateTime();
			LocalDate localDate = null;

			for (String pattern : patterns) {

				localDate = formatLocalDate(value, pattern);
				if (localDate != null)
					break;
				
			}
			
			if (localDate != null)
				localDateTime = localDate.atTime(0,0,0);
		}

		if (localDateTime == null)
			throw new WarningException("msg_nao_foi_possivel_c_o_v_e_d_o_h");
		//Nao foi possivel converter o valor em data em ou hora

		return localDateTime;

	}

	/**
	 * Convert String Time to LocalTime
	 * @param value
	 * @return
	 */
	public static LocalTime getLocalTime(String value) {

		if (value == null || value.toLowerCase().equals("null") || value.isEmpty())
			return null;

		if (value.length() > 8)
			try {
				value = String.format("%tT", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value));
			} catch (Exception e) {
				throw new WarningException("msg_nao_foi_possivel_c_o_v_e_d_o_h");
			}

		LocalTime localTime = null;

		List<String> patterns = listOfPatternsTime();

		for (String pattern : patterns) {

			localTime = formatLocalTime(value, pattern);
			if (localTime != null)
				break;

		}

		if (localTime == null)
			throw new WarningException("msg_nao_foi_possivel_c_o_v_e_d_o_h");
		//Nao foi possivel converter o valor em data em ou hora

		return localTime;

	}

	/* METODOS CONVERSAO COM ENTRADA TEMPORAL */

	/**
	 * Convert um objeto Temporal (LocalDate, LocalDateTime, LocalTime)
	 * em uma string no formato do patternTo
	 * @param value
	 * @return
	 */
	public static String getStringFromTemporal(Temporal value, String patternTo) {

		if (value == null)
			return "null";

		if (value instanceof LocalDate) {

			return ((LocalDate) value).format(DateTimeFormatter.ofPattern(patternTo));

		} else if (value instanceof LocalDateTime) {

			return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(patternTo));

		} else if (value instanceof LocalTime) {

			return ((LocalTime) value).format(DateTimeFormatter.ofPattern(patternTo));

		}

		throw new WarningException("msg_nao_foi_possivel_c_o_v_e_d_o_h");
		//Nao foi possivel converter o valor em data em ou hora

	}

	/* METODOS PRIVADOS DE CONVERSAO */

	/**
	 * Retorna lista de patterns possiveis de datas
	 * @return
	 */
	private static List<String> listOfAllPatterns() {

		String[] patterns1 = { "dd/MM/yyyy", "dd/MM/yyyy HH:mm", "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm:ss.S",
				"dd/MM/yyyy HH:mm:ss.SSS" };
		String[] patterns2 = { "yyyy/MM/dd", "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss.S",
				"yyyy/MM/dd HH:mm:ss.SSS" };

		String[] patterns3 = { "dd-MM-yyyy", "dd-MM-yyyy HH:mm", "dd-MM-yyyy HH:mm:ss", "dd-MM-yyyy HH:mm:ss.S",
				"dd-MM-yyyy HH:mm:ss.SSS" };
		String[] patterns4 = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.S",
				"yyyy-MM-dd HH:mm:ss.SSS" };

		String[] patterns5 = { "HH:mm", "HH:mm:ss", "HH:mm:ss.S", "HH:mm:ss.SSS" };

		String[] patterns6 = { "dd/MM/yy", "dd/MM/yy HH:mm", "dd/MM/yy HH:mm:ss", "dd/MM/yy HH:mm:ss.S", "dd/MM/yy HH:mm:ss.SSS" };
		String[] patterns7 = { "yy/MM/dd", "yy/MM/dd HH:mm", "yy/MM/dd HH:mm:ss", "yy/MM/dd HH:mm:ss.S", "yy/MM/dd HH:mm:ss.SSS" };

		String[] patterns8 = { "dd-MM-yy", "dd-MM-yy HH:mm", "dd-MM-yy HH:mm:ss", "dd-MM-yy HH:mm:ss.S", "dd-MM-yy HH:mm:ss.SSS" };
		String[] patterns9 = { "yy-MM-dd", "yy-MM-dd HH:mm", "yy-MM-dd HH:mm:ss", "yy-MM-dd HH:mm:ss.S", "yy-MM-dd HH:mm:ss.SSS" };

		String[] patterns10 = { "HHmm", "HHmmss", "HHmmssS", "HHmmssSSS" };

		String[] patterns11 = { "ddMMyyyyHHmm", "ddMMyyyyHHmmss", "ddMMyyyyHHmmssS", "ddMMyyyyHHmmssSSS" };
		String[] patterns12 = { "yyyyMMddHHmm", "yyyyMMddHHmmss", "yyyyMMddHHmmssS", "yyyyMMddHHmmssSSS" };

		String[] patterns13 = { "ddMMyyyy", "ddMMyyyy HH:mm", "ddMMyyyy HH:mm:ss", "ddMMyyyy HH:mm:ss.S", "ddMMyyyy HH:mm:ss.SSS" };
		String[] patterns14 = { "yyyyMMdd", "yyyyMMdd HH:mm", "yyyyMMdd HH:mm:ss", "yyyyMMdd HH:mm:ss.S", "yyyyMMdd HH:mm:ss.SSS" };

		String[] patterns15 = { "ddMMyyHHmm", "ddMMyyHHmmss", "ddMMyyHHmmssS", "ddMMyyHHmmssSSS" };
		String[] patterns16 = { "yyMMddHHmm", "yyMMddHHmmss", "yyMMddHHmmssS", "yyMMddHHmmssSSS" };

		String[] patterns17 = { "ddMMyy", "ddMMyy HH:mm", "ddMMyy HH:mm:ss", "ddMMyy HH:mm:ss.S", "ddMMyy HH:mm:ss.SSS" };
		String[] patterns18 = { "yyMMdd", "yyMMdd HH:mm", "yyMMdd HH:mm:ss", "yyMMdd HH:mm:ss.S", "yyMMdd HH:mm:ss.SSS" };

		List<String> patterns = new ArrayList<>();

		patterns.addAll(Arrays.asList(patterns1));
		patterns.addAll(Arrays.asList(patterns2));
		patterns.addAll(Arrays.asList(patterns3));
		patterns.addAll(Arrays.asList(patterns4));
		patterns.addAll(Arrays.asList(patterns5));
		patterns.addAll(Arrays.asList(patterns6));
		patterns.addAll(Arrays.asList(patterns7));
		patterns.addAll(Arrays.asList(patterns8));
		patterns.addAll(Arrays.asList(patterns9));
		patterns.addAll(Arrays.asList(patterns10));
		patterns.addAll(Arrays.asList(patterns11));
		patterns.addAll(Arrays.asList(patterns12));
		patterns.addAll(Arrays.asList(patterns13));
		patterns.addAll(Arrays.asList(patterns14));
		patterns.addAll(Arrays.asList(patterns15));
		patterns.addAll(Arrays.asList(patterns16));
		patterns.addAll(Arrays.asList(patterns17));
		patterns.addAll(Arrays.asList(patterns18));
		return patterns;

	}

	/**
	 * Retorna lista de patterns possiveis de Date
	 * @return
	 */
	private static List<String> listOfPatternsDate() {

		String[] patterns1 = { "dd/MM/yyyy" };
		String[] patterns2 = { "yyyy/MM/dd" };

		String[] patterns3 = { "dd-MM-yyyy" };
		String[] patterns4 = { "yyyy-MM-dd" };

		String[] patterns5 = { "dd/MM/yy" };
		String[] patterns6 = { "yy/MM/dd" };

		String[] patterns7 = { "dd-MM-yy" };
		String[] patterns8 = { "yy-MM-dd" };

		String[] patterns9 = { "ddMMyyyy" };
		String[] patterns10 = { "yyyyMMdd" };

		String[] patterns11 = { "ddMMyy" };
		String[] patterns12 = { "yyMMdd" };

		List<String> patterns = new ArrayList<>();

		patterns.addAll(Arrays.asList(patterns1));
		patterns.addAll(Arrays.asList(patterns2));
		patterns.addAll(Arrays.asList(patterns3));
		patterns.addAll(Arrays.asList(patterns4));
		patterns.addAll(Arrays.asList(patterns5));
		patterns.addAll(Arrays.asList(patterns6));
		patterns.addAll(Arrays.asList(patterns7));
		patterns.addAll(Arrays.asList(patterns8));
		patterns.addAll(Arrays.asList(patterns9));
		patterns.addAll(Arrays.asList(patterns10));
		patterns.addAll(Arrays.asList(patterns11));
		patterns.addAll(Arrays.asList(patterns12));
		return patterns;

	}

	/**
	 * Retorna lista de patterns possiveis de DateTime
	 * @return
	 */
	private static List<String> listOfPatternsDateTime() {

		String[] patterns1 = { "dd/MM/yyyy HH:mm", "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm:ss.S", "dd/MM/yyyy HH:mm:ss.SSS" };
		String[] patterns2 = { "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss.S", "yyyy/MM/dd HH:mm:ss.SSS" };

		String[] patterns3 = { "dd-MM-yyyy HH:mm", "dd-MM-yyyy HH:mm:ss", "dd-MM-yyyy HH:mm:ss.S", "dd-MM-yyyy HH:mm:ss.SSS" };
		String[] patterns4 = { "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd HH:mm:ss.SSS" };

		String[] patterns6 = { "dd/MM/yy HH:mm", "dd/MM/yy HH:mm:ss", "dd/MM/yy HH:mm:ss.S", "dd/MM/yy HH:mm:ss.SSS" };
		String[] patterns7 = { "yy/MM/dd HH:mm", "yy/MM/dd HH:mm:ss", "yy/MM/dd HH:mm:ss.S", "yy/MM/dd HH:mm:ss.SSS" };

		String[] patterns8 = { "dd-MM-yy HH:mm", "dd-MM-yy HH:mm:ss", "dd-MM-yy HH:mm:ss.S", "dd-MM-yy HH:mm:ss.SSS" };
		String[] patterns9 = { "yy-MM-dd HH:mm", "yy-MM-dd HH:mm:ss", "yy-MM-dd HH:mm:ss.S", "yy-MM-dd HH:mm:ss.SSS" };

		String[] patterns11 = { "ddMMyyyyHHmm", "ddMMyyyyHHmmss", "ddMMyyyyHHmmssS", "ddMMyyyyHHmmssSSS" };
		String[] patterns12 = { "yyyyMMddHHmm", "yyyyMMddHHmmss", "yyyyMMddHHmmssS", "yyyyMMddHHmmssSSS" };

		String[] patterns13 = { "ddMMyyyy HH:mm", "ddMMyyyy HH:mm:ss", "ddMMyyyy HH:mm:ss.S", "ddMMyyyy HH:mm:ss.SSS" };
		String[] patterns14 = { "yyyyMMdd HH:mm", "yyyyMMdd HH:mm:ss", "yyyyMMdd HH:mm:ss.S", "yyyyMMdd HH:mm:ss.SSS" };

		String[] patterns15 = { "ddMMyyHHmm", "ddMMyyHHmmss", "ddMMyyHHmmssS", "ddMMyyHHmmssSSS" };
		String[] patterns16 = { "yyMMddHHmm", "yyMMddHHmmss", "yyMMddHHmmssS", "yyMMddHHmmssSSS" };

		String[] patterns17 = { "ddMMyy HH:mm", "ddMMyy HH:mm:ss", "ddMMyy HH:mm:ss.S", "ddMMyy HH:mm:ss.SSS" };
		String[] patterns18 = { "yyMMdd HH:mm", "yyMMdd HH:mm:ss", "yyMMdd HH:mm:ss.S", "yyMMdd HH:mm:ss.SSS" };

		List<String> patterns = new ArrayList<>();

		patterns.addAll(Arrays.asList(patterns1));
		patterns.addAll(Arrays.asList(patterns2));
		patterns.addAll(Arrays.asList(patterns3));
		patterns.addAll(Arrays.asList(patterns4));
		patterns.addAll(Arrays.asList(patterns6));
		patterns.addAll(Arrays.asList(patterns7));
		patterns.addAll(Arrays.asList(patterns8));
		patterns.addAll(Arrays.asList(patterns9));
		patterns.addAll(Arrays.asList(patterns11));
		patterns.addAll(Arrays.asList(patterns12));
		patterns.addAll(Arrays.asList(patterns13));
		patterns.addAll(Arrays.asList(patterns14));
		patterns.addAll(Arrays.asList(patterns15));
		patterns.addAll(Arrays.asList(patterns16));
		patterns.addAll(Arrays.asList(patterns17));
		patterns.addAll(Arrays.asList(patterns18));
		return patterns;

	}

	/**
	 * Retorna lista de patterns possiveis de Time
	 * @return
	 */
	private static List<String> listOfPatternsTime() {

		String[] patterns1 = { "HH:mm", "HH:mm:ss", "HH:mm:ss.S", "HH:mm:ss.SSS" };

		String[] patterns2 = { "HHmm", "HHmmss", "HHmmssS", "HHmmssSSS" };

		List<String> patterns = new ArrayList<>();

		patterns.addAll(Arrays.asList(patterns1));
		patterns.addAll(Arrays.asList(patterns2));

		return patterns;

	}

	/**
	 * Formata a String Date, DateTieme ou Time de um pattern para o outro
	 * @param value
	 * @param patternFrom
	 * @param patternTo
	 * @return
	 */
	private static String formatString(String value, String patternFrom, String patternTo) {

		try {

			return formatLocalDate(value, patternFrom).format(DateTimeFormatter.ofPattern(patternTo));

		} catch (Exception e1) {

			try {

				return formatLocalDateTime(value, patternFrom).format(DateTimeFormatter.ofPattern(patternTo));

			} catch (Exception e2) {

				try {

					return formatLocalTime(value, patternFrom).format(DateTimeFormatter.ofPattern(patternTo));

				} catch (Exception e3) {

					return null;

				}

			}

		}

	}

	/**
	 * Formata um string no pattern de entrada para LocalDate
	 * @param value
	 * @param patternFrom
	 * @return
	 */
	private static LocalDate formatLocalDate(String value, String patternFrom) {

		try {

			return LocalDate.parse(value, DateTimeFormatter.ofPattern(patternFrom));

		} catch (Exception e1) {

			return null;

		}

	}

	/**
	 * Formata um string no pattern de entrada para LocalDateTime
	 * @param value
	 * @param patternFrom
	 * @return
	 */
	private static LocalDateTime formatLocalDateTime(String value, String patternFrom) {

		try {

			// Remove o T do formato
			value = value.replace("T", " ");
			
			return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(patternFrom));

		} catch (Exception e1) {

			return null;

		}

	}

	/**
	 * Formata um string no pattern de entrada para LocalTime
	 * @param value
	 * @param patternFrom
	 * @return
	 */
	private static LocalTime formatLocalTime(String value, String patternFrom) {

		try {

			return LocalTime.parse(value, DateTimeFormatter.ofPattern(patternFrom));

		} catch (Exception e1) {

			return null;

		}

	}

	public static void main(String[] args) {

		System.out.println(LocalDateTime.parse("21091980 09:33:23.9", DateTimeFormatter.ofPattern("ddMMyyyy HH:mm:ss.S")));

	}

}
