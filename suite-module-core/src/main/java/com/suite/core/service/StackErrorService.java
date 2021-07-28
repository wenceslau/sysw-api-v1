package com.suite.core.service;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.suite.core.base.ServiceCore;
import com.suite.core.dto.ChartBarDTO;
import com.suite.core.dto.ChartBarDataSetDTO;
import com.suite.core.dto.FileLogDTO;
import com.suite.core.model.StackError;
import com.suite.core.repository.StackErrorRepository;
import com.suite.core.repository.filter.StackErrorFillter;

/**
 * Service responsavel por manipular os dados recurso StackError
 * @author Wenceslau
 *
 */
@Service
public class StackErrorService extends ServiceCore {

	@Autowired
	private Environment env;

	@Autowired
	private StackErrorRepository stackErrorRepository;

	/**
	 * Retoirna uma lista dos recursos
	 * @return
	 */
	public List<StackError> list() {
		return stackErrorRepository.findAll();

	}

	/**
	 * Filtra uma lista de recursos
	 * @param filter
	 * @param pageable
	 * @return
	 */
	public Page<StackError> filter(StackErrorFillter filter, Pageable pageable) {
		return stackErrorRepository.filter(filter, pageable);

	}

	/**
	 * Filtra uma lista de recursos
	 * @param filter
	 * @param pageable
	 * @return
	 */
	public List<StackError> filter(StackErrorFillter filter) {
		// O SA pode ver tudo se estiver no setor default
		// if (!isDefaultSector())
		// filter.setCodeSector(getContextSectorCode());

		if (filter.getStartDateTimeError() == null)
			filter.setStartDateTimeError(LocalDateTime.now().plusDays(-10));

		if (filter.getEndDateTimeError() == null)
			filter.setEndDateTimeError(LocalDateTime.now());

		filter.setOrderBy("code");
		filter.setDesc(true);

		return stackErrorRepository.filter(filter);

	}

	/**
	 * Insere o recurso na base
	 * @param StackErrorFillter
	 * @return
	 */
	public StackError insert(StackError stackError) {
		return stackErrorRepository.save(stackError);

	}

	public List<FileLogDTO> listFileLog() {
		List<FileLogDTO> list = new ArrayList<>();
		String loggerPath = env.getProperty("logging.file", "");

		File file = new File(loggerPath);

		if (file.isFile())
			loggerPath = file.getParent();

		File dir = new File(loggerPath);

		if (dir.isDirectory()) {

			File[] files = dir.listFiles();

			Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

			for (File f : files) {

				if (f.isFile())
					list.add(new FileLogDTO(f.getParent(), f.getName(),
							LocalDateTime.ofInstant(Instant.ofEpochMilli(f.lastModified()), ZoneId.systemDefault())));

			}

		}

		return list;

	}

	/**
	 * Retorna objeto Chart para UI
	 * @return
	 */
	public ChartBarDTO chartErrorByDay() {
		// Long codeBusinessUnit = null;

		// Para o setor default traz de todas as BU, se nao tra da BU do setor logado
		// if (!isDefaultSector())
		// codeBusinessUnit = getSector().getBusinessUnit().getCode();

		StackErrorFillter filter = new StackErrorFillter();

		filter.setStartDateTimeError(LocalDateTime.now().plusDays(-15));
		filter.setEndDateTimeError(LocalDateTime.now());

		List<StackError> lstStErr = stackErrorRepository.filter(filter);

		// Recuper o historico de login da bu do usuari

		// List labels para o grupo de barra do grafico
		List<LocalDate> ldates = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		// Map com o nome de cada barra do grupo e uma lista de valor para cada barra
		Map<String, List<Long>> map = new HashMap<>();

		for (int i = (15 - 1); i >= 0; i--) {

			LocalDate ld = LocalDate.now().plusDays((i * -1));
			ldates.add(ld);
			labels.add(ld.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		}

		map.put("WARNING", new ArrayList<>());
		map.put("ERROR", new ArrayList<>());

		// Percorre a lista de datas para adicionar na lista da barra o valor do cout
		for (LocalDate ld : ldates) {

			// Recupera do retorno da base referente ao dia do laco
			List<StackError> listDat = lstStErr
					.stream()
					.filter(l -> l.getDateTimeError().getMonthValue() == ld.getMonthValue()
							&& l.getDateTimeError().getDayOfMonth() == ld.getDayOfMonth())
					.collect(Collectors.toList());

			List<StackError> listWarn = listDat.stream()
					.filter(l -> l.getMessage().trim().startsWith("WarningException"))
					.collect(Collectors.toList());

			map.get("WARNING").add(new Long(listWarn.size()));
			map.get("ERROR").add(new Long(listDat.size() - listWarn.size()));

		}

		// Lista de dataset
		List<ChartBarDataSetDTO> datasets = new ArrayList<>();

		ChartBarDataSetDTO clds = new ChartBarDataSetDTO();
		clds.setData(map.get("WARNING"));
		clds.setLabel("WARNING");
		clds.setBackgroundColor("orange");
		clds.setOrder(1);
		clds.setBorderColor(clds.getBackgroundColor());
		datasets.add(clds);

		clds = new ChartBarDataSetDTO();
		clds.setData(map.get("ERROR"));
		clds.setLabel("ERROR");
		clds.setBackgroundColor("red");
		clds.setOrder(2);
		clds.setBorderColor(clds.getBackgroundColor());
		datasets.add(clds);

		Collections.sort(datasets, (x1, x2) -> x1.getOrder().compareTo(x2.getOrder()));

		// Objeto que contem a lista de labels e a lista de data set
		ChartBarDTO chartLogon = new ChartBarDTO();
		chartLogon.setLabels(labels);
		chartLogon.setDatasets(datasets);

		return chartLogon;

	}

}
