package com.suite.job.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suite.app.util.Utils;
import com.suite.job.base.ServiceJob;
import com.suite.job.model.Task;
import com.suite.job.repository.TaskRepository;
import com.suite.job.repository.filter.TaskFilter;

@Service
public class TaskService extends ServiceJob {

	@Autowired
	private TaskRepository taskRepository;

	public Task findByCode(Long code) {
		if (code == null)
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "Task", code), 1);

		Optional<Task> parameter = taskRepository.findById(code);

		if (!parameter.isPresent())
			throw new EmptyResultDataAccessException(formatTranslate(msgResourceNotFound, "Task", code), 1);

		return parameter.get();

	}

	public Task insert(Task task) {
		info("Insert: " + task);

		// relaciona lista propriedade com o objeto DataService
		relationshipTaskProperty(task, task);

		return save(task);

	}

	public Task update(Long code, Task task) {
		info("Update: " + task);

		Task dbTask = findByCode(code);

		// relaciona lista propriedade do objeto DataService ao objeto que sera salvo
		if (task.getTaskProperties() != null) {
			dbTask.getTaskProperties().clear();
			dbTask.getTaskProperties().addAll(task.getTaskProperties());
			relationshipTaskProperty(task, dbTask);
		}

		// Ignora os campos do array na copia do valor das atributos
		BeanUtils.copyProperties(task, dbTask, new String[] { "code", "lastRun" });

		return save(dbTask);

	}

	public void delete(Long code) {
		info("Delete: " + code);

		Task toDelete = findByCode(code);

		taskRepository.delete(toDelete);

	}

	public List<Task> filter(TaskFilter filter) {
		// O setor default ve tudo
		if (isDefaultSector())
			return taskRepository.findAll();

		return taskRepository.findAllByCodeBusinessUnit(getSector().getBusinessUnit().getCode());

	}

	/**
	 * Faz o relacionamento entre as propriedades e o dataservice
	 * @param task
	 * @param taskToSave
	 */
	public void relationshipTaskProperty(Task task, Task taskToSave) {

		// Verifica se tem propriedades
		if (taskToSave.getTaskProperties() != null) {
			// atualiza o objeto DataService de cada Property
			taskToSave.getTaskProperties().forEach(p -> {
				p.setTask(taskToSave);
				p.setStatus(true);
				// Se nao tiver nesse padrao, aplica a regra
				if (!p.getName().toUpperCase().startsWith("TASK_"))
					p.setName("TASK_" + p.getName());

				p.setName(p.getName().trim().toUpperCase());
				p.setValue(p.getValue().trim());

			});

		}

	}

	/*
	 * < Metodos expostos pelo repository >
	 * 
	 * Apenas o service da entidade pode acessar
	 * o repositorio da entidade.
	 * Os metodos sao expostos para serem usados
	 * por outras classes service que precisar acessar
	 * os dados da entidade
	 */

	public Task save(Task task) {
		return taskRepository.save(task);

	}

	public Task findByBenRun(String benRun) {
		return taskRepository.findByBenRunIgnoreCase(benRun);

	}

	public Task saveBySystem(Task task) {
		// Define que o save exectuado esta sendo feito pelo sitema, assim o usuario da acao é definido como sistema
		Utils.userSystem = true;
		return taskRepository.save(task);

	}

}
