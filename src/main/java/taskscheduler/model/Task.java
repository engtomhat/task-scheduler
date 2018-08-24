package taskscheduler.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task implements Comparable<Task> {
	private String taskId;
	private Long durationInMillis;
	private LocalDateTime targetDateTime;

	private Task() {
	}

	public String getTaskId() {
		return taskId;
	}

	public Long getDurationInMillis() {
		return durationInMillis;
	}

	public void setDurationInMillis(Long durationInMillis) {
		this.durationInMillis = durationInMillis;
	}

	public LocalDateTime getTargetDateTime() {
		return targetDateTime;
	}

	public void setTargetDateTime(LocalDateTime targetDateTime) {
		this.targetDateTime = targetDateTime;
	}

	public static class TaskBuilder {
		private String taskId;
		private Long durationInMillis;
		private LocalDateTime targetDateTime;

		public TaskBuilder() {
			this.taskId = UUID.randomUUID().toString();
		}

		public Task build() {
			Task task = new Task();
			task.taskId = this.taskId;
			task.setDurationInMillis(this.durationInMillis);
			task.setTargetDateTime(this.targetDateTime);
			return task;
		}

		public TaskBuilder withDurationInMillis(Long durationInMillis) {
			this.durationInMillis = durationInMillis;
			return this;
		}

		public TaskBuilder withTargetDateTime(LocalDateTime targetDateTime) {
			this.targetDateTime = targetDateTime;
			return this;
		}
	}

	@Override
	public int compareTo(Task o) {
		if (this.getTargetDateTime().isBefore(o.getTargetDateTime())) {
			return -1;
		} else if (this.getTargetDateTime().isEqual(o.getTargetDateTime())) {
			return o.getDurationInMillis().compareTo(this.getDurationInMillis());
		} else {
			return 1;
		}
	}

	@Override
	public String toString() {
		return getTaskId();
	}
}
