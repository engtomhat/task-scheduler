package taskscheduler.service;

import java.util.Observable;
import java.util.PriorityQueue;
import java.util.Queue;

import taskscheduler.model.Consumer;
import taskscheduler.model.Task;

public class TaskSchedulerStore extends Observable {

	private Queue<Task> tasks;
	private Queue<Consumer> consumers;

	public TaskSchedulerStore() {
		clear();
	}

	public void clear() {
		tasks = new PriorityQueue<>();
		consumers = new PriorityQueue<>(new Consumer.AvailabiltyComparator());
	}

	public Queue<Task> getTasks() {
		return tasks;
	}

	public Queue<Consumer> getConsumers() {
		return consumers;
	}

	public Task submitTask(Task task) {
		Task newTask = new Task.TaskBuilder().withDurationInMillis(task.getDurationInMillis())
				.withTargetDateTime(task.getTargetDateTime()).build();
		tasks.add(newTask);
		setChanged();
		notifyObservers();
		return newTask;
	}

	public Consumer registerConsumer() {
		Consumer newConsumer = new Consumer();
		consumers.add(newConsumer);
		setChanged();
		notifyObservers();
		return newConsumer;
	}

	public Consumer getConsumer(String consumerId) {
		Consumer result = null;
		for (Consumer consumer : consumers) {
			if (consumer.getConsumerId().equals(consumerId)) {
				result = consumer;
				break;
			}
		}
		return result;
	}

	/**
	 * Resets consumer task allocations by clearing all the tasks assigned from
	 * previous plans.
	 */
	public void clearConsumerTasks() {
		for (Consumer consumer : consumers) {
			consumer.clear();
		}
	}

	public Task getTask(String taskId) {
		Task result = null;
		for(Task task: tasks) {
			if(task.getTaskId().equals(taskId)) {
				result = task;
				break;
			}
		}
		return result;
	}

}
