
package com.suite.job.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.suite.app.listener.ResourceCreatedEvent;
import com.suite.core.base.ResourceCore;
import com.suite.job.model.Task;
import com.suite.job.model.viewer.TaskVwr;
import com.suite.job.repository.filter.TaskFilter;
import com.suite.job.service.TaskService;

@RestController
@RequestMapping("/task")
public class TaskResource extends ResourceCore {

	@Autowired
	private TaskService taskService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@GetMapping("/filter/pageable")
	@PreAuthorize("hasAnyAuthority('JOB_TASK_VIEWER','JOB_TASKO_LIST')")
	public Page<TaskVwr> filter(TaskFilter filter, Pageable pageable) {
		return null;

	}

	@GetMapping("/filter")
	@PreAuthorize("hasAnyAuthority('JOB_TASK_VIEWER','JOB_TASKO_LIST')")
	public List<TaskVwr> filter(TaskFilter filter) {
		return TaskVwr.build(taskService.filter(filter));

	}

	@GetMapping("/{code}")
	@PreAuthorize("hasAnyAuthority('JOB_TASK_VIEWER','JOB_TASKO_LIST')")
	public ResponseEntity<?> findByCode(@PathVariable Long code) {
		return ResponseEntity.status(HttpStatus.OK).body(TaskVwr.build(taskService.findByCode(code)));

	}

	@PostMapping
	@PreAuthorize("hasAuthority('JOB_TASK_INSERT')")
	public ResponseEntity<TaskVwr> save(@RequestBody Task task, HttpServletResponse response) {
		task = taskService.insert(task);

		// adiciona no header a url de get do recurso criado
		publisher.publishEvent(new ResourceCreatedEvent(this, response, task.getCode()));

		notify(formatTranslate("msg_o_[%s]_[%s]_f_c", formatTranslate("lbl_task"), task.getName()));

		return ResponseEntity.status(HttpStatus.CREATED).body(TaskVwr.build(task));

	}

	@PutMapping("/{code}")
	@PreAuthorize("hasAuthority('JOB_TASK_UPDATE')")
	public ResponseEntity<TaskVwr> edit(@PathVariable Long code, @RequestBody Task task) {
		TaskVwr taskVwr = TaskVwr.build(taskService.update(code, task));

		notify(formatTranslate("msg_o_[%s]_[%s]_f_e", formatTranslate("lbl_task"), taskVwr.getName()));

		return ResponseEntity.status(HttpStatus.OK).body(taskVwr);

	}

	@DeleteMapping("/{code}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('JOB_TASK_DELETE')")
	public void deleteByCode(@PathVariable Long code) {
		taskService.delete(code);

		notify(formatTranslate("msg_o_[%s]_[%s]_f_a", formatTranslate("lbl_task"), code));

	}

}
