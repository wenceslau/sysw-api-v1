package com.suite.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.plexus.util.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ResourceUtils;

import com.suite.app.util.Utils;
import com.suite.core.model.Parameter;
import com.suite.core.service.ParameterService;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Classe Util do modulo Core
 * @author Wenceslau
 *
 */
public class UtilsCore extends Utils {

	public static final String delimiterFileExport = "¨";

	public static final String delimiterLables = " | ";

	/*
	 * Contexto da aplicacao no Spring
	 * A variavel eh atribuida na classe CoreAppListener
	 * quando o evento applicationReadyEvent foi disparado
	 * A classe CoreAppListener injeta o contexo usando o Autowired
	 * e assim quardamos ele aqui para poder recuperar objetos do contexto
	 * onde ele nao esta disponivel, como nas classes Listener do Hibernate
	 */
	public static ApplicationContext context;

	/* CONTEXT */
	//
	// /**
	// * Identifica o usuario que esta logado
	// * @param securityContext
	// * @return
	// */
	// public static User getUserOnContext() {
	// // Sistema esta inserindo dados de script
	// if (initialize)
	// return null;
	//
	// Long code = getContextUserCode();
	//
	// if (code != null) {
	//
	// User user = new User();
	// user.setCode(code);
	// return user;
	//
	// }
	//
	// throw new RuntimeException("msg_nao_foi_encontrado_n_u_l");
	//
	// }

	// /**
	// * Identifica a setor do usuario logado
	// * @param securityContext
	// * @return
	// */
	// public static Sector getSectorOnContext() {
	// Long code = getContextSectorCode();
	//
	// if (code != null) {
	//
	// Sector sector = new Sector();
	// sector.setCode(code);
	//
	// return sector;
	//
	// }
	//
	// throw new RuntimeException("msg_nao_foi_encontrado_n_s_p_o_u_l");
	//
	// }

	/**
	 * Recuera do contexo o objeto baseado o nome. O objeto context eh injetado
	 * no CoreAppListener e depois reatribuido a variavel estatica do UtilsCore
	 * @param name
	 * @return
	 */
	public static Object getBean(String name) {

		if (context != null) {
			return context.getBean(name);

		} else {

			throw new RuntimeException("msg_o_contexto_da_a_e_n");

		}

	}

	/**
	 * Convert arquivo csv to xls
	 * @param csvFile
	 * @return
	 * @throws IOException
	 */
	public static File csvToXls(String nameFileXlsx, List<File> listFilesCSV, char separator, boolean delete) throws IOException {
		if (!nameFileXlsx.endsWith(".xlsx"))
			nameFileXlsx = nameFileXlsx + ".xlsx";
		File filsXls = new File(nameFileXlsx);

		/* Step -2 : Define POI Spreadsheet com.suite.app.objects */
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(); // create a blank workbook object

		int aux = 0;

		for (File csvFile : listFilesCSV) {

			aux++;
			String nameSheet = csvFile.getName();
			nameSheet = nameSheet.replace("-", " - ");
			int index = xssfWorkbook.getSheetIndex(nameSheet);
			if (index > 0)
				nameSheet = nameSheet + "_" + aux;

			XSSFSheet xssfSheet = xssfWorkbook.createSheet(nameSheet); // create a worksheet with caption score_details
			xssfSheet.setDefaultColumnWidth(20);
			CSVReader reader = new CSVReader(new FileReader(csvFile), separator);
			createSheetXlsxV2(filsXls, reader, xssfWorkbook, xssfSheet);
			if (delete)
				csvFile.delete();

		}

		xssfWorkbook.close();

		return filsXls;

	}

	/**
	 * 
	 * @param nameFileXlsx
	 * @param listFilesCSV
	 * @param separator
	 * @param mainColumnsName
	 * @param relationshipColumnsName
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public static File csvToXlsm(String nameFileXlsx, List<File> listFilesCSV, char separator,
			List<String> mainColumnsName, List<String> relationshipColumnsName)
			throws IOException, InvalidFormatException {
		File filsTemplate = ResourceUtils.getFile("classpath:templates/templateExportObject.xlsm");

		File usageTemplate = new File(Utils.getPathTemp() + "template" + UUID.randomUUID().getMostSignificantBits() + ".xlsm");

		// Cria uma copia do template
		FileUtils.copyFile(filsTemplate, usageTemplate);
		File filsXls = new File(nameFileXlsx + ".xlsm");

		/* Step -2 : Define POI Spreadsheet com.suite.app.objects */
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(OPCPackage.open(usageTemplate)); // create a blank workbook object

		// Coloca na primeira aba todas as colunas do objeto principal
		XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
		// xssfSheet.setDefaultColumnWidth(20);
		Row row = xssfSheet.createRow(0);

		CellStyle backStyle = xssfWorkbook.createCellStyle();
		backStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		backStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		for (int i = 0; i < mainColumnsName.size(); i++) {
			xssfSheet.setColumnWidth(i, 200 * 27);
			row.createCell(i).setCellValue("null".equalsIgnoreCase(mainColumnsName.get(i)) ? "" : mainColumnsName.get(i));
			row.getCell(i).setCellStyle(backStyle);
		}

		FileOutputStream output_file = new FileOutputStream(filsXls);
		xssfWorkbook.write(output_file);
		output_file.close();

		int cellIndex = 0;

		for (File csvFile : listFilesCSV) {

			CSVReader reader = new CSVReader(new FileReader(csvFile), separator);
			xssfSheet = xssfWorkbook.getSheetAt(1);

			createSheetXlsm(filsXls, reader, xssfWorkbook, xssfSheet, cellIndex, relationshipColumnsName.get(cellIndex));
			cellIndex++;

		}

		xssfWorkbook.close();

		for (File csvFile : listFilesCSV) {

			if (csvFile.exists())
				csvFile.delete();

		}

		usageTemplate.delete();

		return filsXls;

	}

	/* EXCEL - CSV */

	/**
	 * Cria um sheet no arquivo xlsx para o csv reader informado
	 * @param filsXls
	 * @param reader
	 * @param new_workbook
	 * @param sheet
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unused")
	private static void createSheetXlsx(File filsXls, CSVReader reader, XSSFWorkbook new_workbook, XSSFSheet sheet)
			throws IOException, FileNotFoundException {
		/* Variables to loop through the CSV File */
		String[] nextLine; /* for every line in the file */
		int lnNum = 0; /* line number */

		/* Step -3: Define logical Map to consume CSV file data into excel */
		Map<Integer, Object[]> excel_data = new HashMap<Integer, Object[]>(); // create a map and define data

		/* Step -4: Populate data into logical Map */
		while ((nextLine = reader.readNext()) != null) {

			lnNum++;

			List<Object> obs = new ArrayList<Object>();
			for (int i = 0; i < nextLine.length; i++)
				obs.add(nextLine[i]);

			excel_data.put(lnNum, obs.toArray());

		}

		/* Step -5: Create Excel Data from the map using POI */
		Set<Integer> keyset = excel_data.keySet();

		int rownum = 0;

		for (Integer key : keyset) { // loop through the data and add them to the cell

			Row row = sheet.createRow(rownum++);
			Object[] objArr = excel_data.get(key);
			int cellnum = 0;

			for (Object obj : objArr) {

				Cell cell = row.createCell(cellnum++);
				cell.setCellValue((String) obj);

				// if (rownum == 1) {
				// CellStyle style = new_workbook.createCellStyle();
				// style.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
				// Font headerFont = new_workbook.createFont();
				// headerFont.setBold(true);
				// style.setFont(headerFont);
				// // cell.setCellStyle(style);
				// row.setRowStyle(style);
				// }
			}

			/* Write XLS converted CSV file to the output file */
			FileOutputStream output_file = new FileOutputStream(filsXls); // create XLS file
			new_workbook.write(output_file);// write converted XLS file to output stream
			output_file.close(); // close the file

		}

		reader.close();

	}

	/**
	 * Cria um sheet no arquivo xlsx para o csv reader informado
	 * @param filsXls
	 * @param reader
	 * @param new_workbook
	 * @param sheet
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void createSheetXlsxV2(File filsXls, CSVReader reader, XSSFWorkbook new_workbook, XSSFSheet sheet)
			throws IOException, FileNotFoundException {
		String[] nextLine;
		int lnNum = 0;

		CellStyle backStyle = new_workbook.createCellStyle();
		backStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		backStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		Map<Integer, String[]> excel_data = new HashMap<Integer, String[]>(); // create a map and define data

		while ((nextLine = reader.readNext()) != null)
			excel_data.put(lnNum++, (nextLine));

		int rownum = 0;

		for (String[] values : excel_data.values()) {

			Row row = sheet.createRow(rownum++);

			for (int i = 0; i < values.length; i++) {

				try {
					if (values[i] != null && values[i].startsWith("§"))
						row.createCell(i).setCellValue(values[i].replaceFirst("§", ""));		
					else
						row.createCell(i).setCellValue(new Double(values[i]));
				} catch (Exception e) {
					row.createCell(i).setCellValue("null".equalsIgnoreCase(values[i]) ? "" : values[i]);
				}

				if (rownum == 1)
					row.getCell(i).setCellStyle(backStyle);
			}

		}

		FileOutputStream output_file = new FileOutputStream(filsXls);
		new_workbook.write(output_file);
		output_file.close();

		reader.close();

	}

	/**
	 * Cria um sheet no arquivo xlsx para o csv reader informado
	 * 
	 * @param filsXls
	 * @param reader
	 * @param new_workbook
	 * @param sheet
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void createSheetXlsm(File filsXls, CSVReader reader, XSSFWorkbook new_workbook, XSSFSheet sheet,
			int cellIndex, String relationshipColumnName) throws IOException, FileNotFoundException {
		String[] nextLine;
		int lnNum = 0;

		Map<Integer, String[]> excel_data = new HashMap<Integer, String[]>(); // create a map and define data

		while ((nextLine = reader.readNext()) != null)
			excel_data.put(lnNum++, (nextLine));

		int rownum = 0;
		boolean firstRow = true;

		for (String[] values : excel_data.values()) {

			Row row = sheet.getRow(rownum);
			if (row == null)
				row = sheet.createRow(rownum);

			String valueRow = "";

			if (firstRow) {

				valueRow = relationshipColumnName;
				firstRow = false;

			} else {

				for (int i = 0; i < values.length; i++) {

					String delimiter = i + 2 < values.length ? " | " : " ¬ ";
					String value = "null".equalsIgnoreCase(values[i]) ? "" : values[i];
					valueRow = valueRow + value + delimiter;

				}

				valueRow = valueRow.trim().substring(0, valueRow.trim().length() - 2);

			}

			row.createCell(cellIndex).setCellValue(valueRow);
			rownum++;

		}

		FileOutputStream output_file = new FileOutputStream(filsXls);
		new_workbook.write(output_file);
		output_file.close();

		reader.close();

	}

	/**
	 * Retorna list de string separada po ',' a partir do xlsx
	 * @param fileXlsx
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws EncryptedDocumentException
	 */
	public static List<String> xlsxToCsvList(File fileXlsx, String delimiter)
			throws IOException, EncryptedDocumentException, InvalidFormatException {
		List<String> lines = new ArrayList<>();

		Workbook workbook = WorkbookFactory.create(fileXlsx);
		Sheet sheet = workbook.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		Iterator<Row> rowIterator = sheet.iterator();

		// boolean firstLine = true;
		Short numCols = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		while (rowIterator.hasNext()) {

			Row row = rowIterator.next();

			if (numCols == null)
				numCols = row.getLastCellNum();
			// firstLine = false;

			String[] csvdata = new String[numCols];
			Arrays.fill(csvdata, "");

			boolean hasValue = false;

			for (int j = 0; j < csvdata.length; j++) {

				Cell cell = row.getCell(j);

				if (cell != null) {

					if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {

						if (DateUtil.isCellDateFormatted(cell))
							csvdata[j] = sdf.format(cell.getDateCellValue());
						else
							csvdata[j] = dataFormatter.formatCellValue(cell);

					} else {

						csvdata[j] = dataFormatter.formatCellValue(cell);

					}

				}

				// verifica se existe ao menos um registro com dados para a linha ser considerada valida
				if (hasValue == false && !csvdata[j].trim().isEmpty())
					hasValue = true;

			}

			if (hasValue)
				lines.add(String.join(delimiter, csvdata));

		}

		workbook.close();
		return lines;

	}

	public static String templateEmail(String appLogged, String value) {
		return "<div>\r\n" +
				UtilsCore.headerTemplateEmail(appLogged) +
				"  <br>\r\n" +
				"  <hr>\r\n" +
				"  <br>\r\n" +
				"  <div style=\"font-size: 13px; text-align: left;\">\r\n" +
				"  	 " + value + " " +
				"  </div>\r\n" +
				"  <br>\r\n" +
				"  <hr>\r\n" +
				"  <br>\r\n" +
				UtilsCore.footerTemplateEmail(appLogged) +
				"</div>";

	}

	public static String headerTemplateEmail(String appLogged) {
		ParameterService parameterService = (ParameterService) getBean("parameterService");

		Parameter parSysName = parameterService.findByKeyOrNull("SYSTEM_NAME");

		String header = "<br>\r\n" +
				"  <div style=\"font-weight: bold; text-align: center; background-color: #156090; color: white; border: 1px solid #156090\">\r\n"
				+
				"    " + (parSysName != null ? parSysName.getValue() : "unknow") + " - " + appLogged + "\r\n" +
				"  </div>\r\n";

		return header;

	}

	public static String footerTemplateEmail(String appLogged) {
		ParameterService parameterService = (ParameterService) getBean("parameterService");

		Parameter parCopyRigth = parameterService.findByKeyOrNull("SYSTEM_COPY_RIGTH");

		String os = getOsName();
		String arch = System.getProperty("os.arch");
		String hostname = "unknow";

		String modules = Utils.contextMap + "";

		try {

			hostname = InetAddress.getLocalHost().getHostName();

		} catch (UnknownHostException e) {}

		String ipAddress = "unknow";

		try {

			ipAddress = InetAddress.getLocalHost().getHostAddress();

		} catch (UnknownHostException e) {}

		String footer = " <div style=\"font-size: 12px;background-color: #444;text-align: center;font-weight: bold; color: white; border: 1px solid #444\">\r\n"
				+ "Copyright © " + LocalDate.now().getYear() + " " + (parCopyRigth != null ? parCopyRigth.getValue() : "unknow")
				+ "\r\n"
				+ "  </div>\r\n"
				+ "   <hr>\r\n"
				+ "   <div style=\"font-size: 11px;background-color: #444;text-align: center;font-weight: bold; color: white; border: 1px solid #444\">\r\n"
				+ "		Email enviado da aplicação [" + appLogged + "] no servidor: " + os + ", " + arch + ", " + hostname + ", "
				+ ipAddress + " \r\n"
				+ "  </div>"
				+ "   <hr>\r\n"
				+ "   <div style=\"font-size: 9px; text-align: center;font-weight: bold; color: grey\">\r\n"
				+ "	  " + modules + " \r\n"
				+ "  </div>";

		return footer;

	}

	public static String footerTemplateEmailResume(String appLogged) {
		ParameterService parameterService = (ParameterService) getBean("parameterService");

		Parameter parCopyRigth = parameterService.findByKeyOrNull("SYSTEM_COPY_RIGTH");

		String hostname = "unknown";

		String modules = Utils.contextMap + "";

		try {

			hostname = InetAddress.getLocalHost().getHostName();

		} catch (UnknownHostException e) {}

		String ipAddress = "unknow";

		try {

			ipAddress = InetAddress.getLocalHost().getHostAddress();

		} catch (UnknownHostException e) {}

		String footer = " <div style=\"font-size: 12px;background-color: #444;text-align: center;font-weight: bold; color: white; border: 1px solid #444\">\r\n"
				+ "Copyright © " + LocalDate.now().getYear() + " " + (parCopyRigth != null ? parCopyRigth.getValue() : "unknow")
				+ "\r\n"
				+ "  </div>\r\n"
				+ "   <hr>\r\n"
				+ "   <div style=\"font-size: 11px;background-color: #444;text-align: center;font-weight: bold; color: white; border: 1px solid #444\">\r\n"
				+ "		Email enviado da aplicação [" + appLogged + "], " + hostname + ", " + ipAddress + " \r\n"
				+ "  </div>"
				+ "   <hr>\r\n"
				+ "   <div style=\"font-size: 8px; text-align: center;font-weight: bold; color: grey\">\r\n"
				+ "	  " + modules + " \r\n"
				+ "  </div>";

		return footer;

	}

	public static String messageException(Exception ex) {
		if (ex instanceof NumberFormatException)
			return "String not convert to number. ";

		return "";
	}

}
