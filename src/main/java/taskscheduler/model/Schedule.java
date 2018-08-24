package taskscheduler.model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

public class Schedule {

	private LocalDateTime startTime;
	private Queue<Consumer> schedule;

	private Schedule(LocalDateTime startTime, Queue<Consumer> schedule) {
		this.startTime = startTime;
		this.schedule = schedule;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public Queue<Consumer> getSchedule() {
		return schedule;
	}

	public static class ScheduleBuilder {
		private LocalDateTime startTime;
		private Queue<Consumer> consumers;

		public ScheduleBuilder() {
			this.startTime = LocalDateTime.now();
			this.consumers = new LinkedList<>();
		}

		public ScheduleBuilder startsAt(LocalDateTime startTime) {
			this.startTime = startTime;
			return this;
		}

		public ScheduleBuilder withConsumers(Queue<Consumer> consumers) {
			this.consumers = consumers;
			return this;
		}

		public Schedule build() {
			return new Schedule(startTime, consumers);
		}
	}
}
