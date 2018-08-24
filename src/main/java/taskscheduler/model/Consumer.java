package taskscheduler.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Consumer {

	private String consumerId;
	private List<Task> assignedTasks;
	@JsonIgnore
	private Long timeElapsed;

	public Consumer() {
		consumerId = UUID.randomUUID().toString();
		clear();
	}

	public String getConsumerId() {
		return consumerId;
	}

	public List<Task> getAssignedTasks() {
		return assignedTasks;
	}

	public Long getTimeElapsed() {
		return timeElapsed;
	}

	public void addTask(Task task) {
		assignedTasks.add(task);
		timeElapsed += task.getDurationInMillis();
	}

	public void clear() {
		assignedTasks = new ArrayList<>();
		timeElapsed = 0L;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignedTasks == null) ? 0 : assignedTasks.hashCode());
		result = prime * result + ((consumerId == null) ? 0 : consumerId.hashCode());
		result = prime * result + ((timeElapsed == null) ? 0 : timeElapsed.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Consumer other = (Consumer) obj;
		if (assignedTasks == null) {
			if (other.assignedTasks != null)
				return false;
		} else if (!assignedTasks.equals(other.assignedTasks))
			return false;
		if (consumerId == null) {
			if (other.consumerId != null)
				return false;
		} else if (!consumerId.equals(other.consumerId))
			return false;
		if (timeElapsed == null) {
			if (other.timeElapsed != null)
				return false;
		} else if (!timeElapsed.equals(other.timeElapsed))
			return false;
		return true;
	}

	/**
	 * Comparator that orders consumers according to their earliest availability. A
	 * consumer with less elapsed time will free up first.
	 */
	public static class AvailabiltyComparator implements Comparator<Consumer> {

		@Override
		public int compare(Consumer o1, Consumer o2) {
			return o1.getTimeElapsed().compareTo(o2.getTimeElapsed());
		}

	}

}
