package taskscheduler.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;
import java.util.Queue;

import taskscheduler.model.Consumer;
import taskscheduler.model.Schedule;
import taskscheduler.model.Schedule.ScheduleBuilder;
import taskscheduler.model.Task;

public class TaskSchedulerPlanner implements Observer {

	private boolean planFound;
	private Schedule schedule;
	
	public TaskSchedulerPlanner() {
		clear();
	}

	public boolean isPlanFound() {
		return planFound;
	}

	public void setPlanFound(boolean planFound) {
		this.planFound = planFound;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	@Override
	public void update(Observable o, Object arg) {
		TaskSchedulerStore store = (TaskSchedulerStore) o;
		findPlan(store, LocalDateTime.now());
	}

	/**
	 * Try formulating a plan to schedule all tasks.
	 * 
	 * @param store Task scheduler store carrying the queues of tasks and consumers
	 */
	public void findPlan(TaskSchedulerStore store, LocalDateTime startTime) {
		ScheduleBuilder scheduleBuilder = new Schedule.ScheduleBuilder().startsAt(startTime);
		store.clearConsumerTasks();
		Queue<Consumer> consumers = store.getConsumers();
		if (!consumers.isEmpty()) {
			Queue<Task> tasks = new PriorityQueue<>(store.getTasks());
			Consumer nextConsumer = null;
			LocalDateTime stepStartTime = null;
			LocalDateTime stepFinishTime = null;
			LocalDateTime start = startTime;
			int tasksProcessed = 0;
			int totalTasks = tasks.size();
			boolean isConsumerAvailable = true;
			Task nextTask = null;
			while (isConsumerAvailable && !tasks.isEmpty()) {
				nextTask = tasks.poll();
				nextConsumer = consumers.poll();
				stepStartTime = start.plus(Duration.ofMillis(nextConsumer.getTimeElapsed()));
				stepFinishTime = stepStartTime.plus(Duration.ofMillis(nextTask.getDurationInMillis()));
				if (stepFinishTime.isAfter(nextTask.getTargetDateTime())) {
					consumers.add(nextConsumer);
					isConsumerAvailable = false;
				} else {
					nextConsumer.addTask(nextTask);
					consumers.add(nextConsumer);
					tasksProcessed++;
				}
			}
			// If all tasks can be processed, we have a working plan
			setPlanFound(tasksProcessed > 0 && tasksProcessed == totalTasks);
			if (!isPlanFound()) {
				store.clearConsumerTasks();
			} else {
				scheduleBuilder.withConsumers(consumers);
			}
		}
		setSchedule(scheduleBuilder.build());
	}
	
	public void clear() {
		setPlanFound(false);
		setSchedule(new Schedule.ScheduleBuilder().build());
	}

}
