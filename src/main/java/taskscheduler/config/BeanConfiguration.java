package taskscheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import taskscheduler.controller.TaskSchedulerController;
import taskscheduler.service.TaskSchedulerPlanner;
import taskscheduler.service.TaskSchedulerStore;

@Configuration
public class BeanConfiguration {

	@Bean
	public TaskSchedulerPlanner taskSchedulerPlanner() {
		return new TaskSchedulerPlanner();
	}

	@Bean
	public TaskSchedulerStore taskSchedulerStore() {
		TaskSchedulerStore store = new TaskSchedulerStore();
		store.addObserver(taskSchedulerPlanner());
		return store;
	}

	@Bean
	public TaskSchedulerController taskSchedulerController() {
		TaskSchedulerController controller = new TaskSchedulerController();
		controller.setStore(taskSchedulerStore());
		controller.setPlanner(taskSchedulerPlanner());
		return controller;
	}

}
