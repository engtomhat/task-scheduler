package taskscheduler.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import taskscheduler.exception.ResourceNotFoundException;
import taskscheduler.model.Consumer;
import taskscheduler.model.Schedule;
import taskscheduler.model.Task;
import taskscheduler.service.TaskSchedulerPlanner;
import taskscheduler.service.TaskSchedulerStore;

@RestController
@RequestMapping("/task-scheduler")
public class TaskSchedulerController {

	private TaskSchedulerStore store;
	private TaskSchedulerPlanner planner;

	public void setStore(TaskSchedulerStore store) {
		this.store = store;
	}

	public void setPlanner(TaskSchedulerPlanner planner) {
		this.planner = planner;
	}

	@RequestMapping(path = "/task/{taskId}", method = RequestMethod.GET)
	public Task getTask(@PathVariable String taskId) {
		Task task = store.getTask(taskId);
		return store.submitTask(task);
	}

	@RequestMapping(path = "/task", method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.CREATED)
	public Task submitTask(@RequestBody Task task) {
		return store.submitTask(task);
	}

	@RequestMapping(path = "/tasks", method = RequestMethod.GET)
	public Queue<Task> getTasks() {
		return store.getTasks();
	}

	@RequestMapping(path = "/consumer", method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.CREATED)
	public Consumer registerConsumer() {
		return store.registerConsumer();
	}

	@RequestMapping(path = "/consumer/{consumerId}/tasks", method = RequestMethod.GET)
	public List<Task> getConsumerTasks(@PathVariable String consumerId) {
		// Get consumer
		Consumer consumer = store.getConsumer(consumerId);
		if (consumer == null) {
			throw new ResourceNotFoundException(String.format("Consumer (%s) not found", consumerId));
		}
		// Find tasks
		return consumer.getAssignedTasks();
	}

	@RequestMapping(path = "/consumers", method = RequestMethod.GET)
	public Queue<Consumer> getConsumers() {
		return store.getConsumers();
	}

	@RequestMapping(path = "/plan", method = RequestMethod.GET)
	public Schedule getPlan(@RequestParam(name = "recalculate", defaultValue = "false") Boolean recalculate,
			@RequestParam(name = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
		if (startTime != null || recalculate) {
			// Recalculate plan based on startTime
			planner.findPlan(store, startTime != null ? startTime : LocalDateTime.now());
		}
		Schedule schedule = planner.getSchedule();
		if (!planner.isPlanFound()) {
			throw new ResourceNotFoundException(String.format(
					"Not enough consumers to schedule tasks. Current start time is: %s", schedule.getStartTime()));
		}
		return schedule;
	}

	@RequestMapping(path = "/reset", method = RequestMethod.DELETE)
	public void reset() {
		store.clear();
		planner.clear();
	}
}
