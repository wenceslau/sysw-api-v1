package com.suite.job.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

import com.suite.core.util.UtilsCore;
import com.suite.job.base.EnumJob.State;
import com.suite.job.base.ProcessorJob;
import com.suite.job.model.Task;

@Service
public class CoreMySqlBackup extends ProcessorJob {

	@Override
	public void execute(Task task) {
		
		State state = State.WAIT;
		String msgInfo = "";
		
		try {
			init(task);
		} catch (Exception e) {
			error("Erro na iniciar TASK " + task.getName(), e);
			return;
		}

		try {

			info("Executar JOB backup MYSQL");

			String msg = "";

			msg += startExecution(task, "db_cd_core");

			String subject = task.getValueProperty("SUBJECT", "Database backup");
			String recipient = task.getValueProperty("RECIPIENT", "wbaneto@yahoo.com.br");
			String templateBody = UtilsCore.templateEmail("CORE", msg);

			send(task, subject, templateBody, recipient, null);
			
			msgInfo += "Email backup sucessful sent";


		} catch (Exception e) {
			msgInfo =  e.toString();
			state = State.FAILURE;
			error("Erro na TASK " + task.getName(), e);

		}
		
		try {
			end(task, msgInfo, state);
		} catch (Exception e) {
			error("Erro na terminar TASK " + task.getName(), e);
		}

	}

	@Override
	protected String formatTranslate(String key, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	private String startExecution(Task task, String dbName) {
		String month = LocalDate.now().getMonth().name();
		int weekOfYear = LocalDateTime.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR);

		String date = month + "_week_" + weekOfYear;
		String pathFileName = task.getValueProperty("pathFileName", "c:\\tmp\\" + dbName);
		File f = backup(dbName, pathFileName + ".sql");
		if (f != null && f.exists())
			f = zip(f, pathFileName + "." + date + ".zip");

		String sts;
		if (f == null)
			sts = "[Database backup: " + dbName + ". FAILURE] ";
		else
			sts = "[Database backup: " + dbName + ". SUCCESS " + f.getAbsoluteFile() + "] ";

		return sts;
	}

	private File backup(String dbName, String pathFileNameBkp) {

		try {

			String mysqldumpExecutable = "\"C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe\"";

			Process process;

			// @formatter:off
			String cmd = mysqldumpExecutable+ " " 
					+ "--user=root " + "--password=!C0d3pl0y0 " 
					+ "--host=localhost "
					+ "--port=3306 " 
					+ "--result-file=\"" + pathFileNameBkp + "\" " 
					+ "--default-character-set=utf8 "
					+ "--single-transaction=TRUE " 
					+ "--databases \"" + dbName + "\"";
			// @formatter:on

			info("Executar: " + cmd.replace("!C0d3pl0y0", "*******"));

			process = Runtime.getRuntime().exec(cmd);

			String line;
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ((line = error.readLine()) != null)
				error(line);

			error.close();

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null)
				info(line);

			input.close();

			int exitCode = process.waitFor();

			info("Processo concluido: " + exitCode);

			File sql = new File(pathFileNameBkp);

			if (sql.exists())
				return sql;

		} catch (Exception e) {

			error("Ocorreu um erro ao executar backup banco " + dbName, e);
		}

		return null;

	}

	private File zip(File fileToZip, String target) {

		try {

			File zip = new File(target);
			if (zip.exists())
				zip.delete();

			FileOutputStream fos;

			fos = new FileOutputStream(target);
			ZipOutputStream zipOut = new ZipOutputStream(fos);

			FileInputStream fis = new FileInputStream(fileToZip);
			ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
			zipOut.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;

			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}

			zipOut.close();
			fis.close();
			fos.close();

			fileToZip.delete();

			zip = new File(target);

			if (zip.exists())
				return zip;

		} catch (Exception e) {
			error("Ocorreu um erro ao zipar arquivo " + target, e);
		}

		return null;
	}

}
