package taskscheduler.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

public class Schedule {

	private LocalDateTime startTime;
	private Queue<Consumer> schedule;
	private Long totalTime;

	private Schedule(LocalDateTime startTime, Queue<Consumer> schedule, Long totalTime) {
		this.startTime = startTime;
		this.schedule = schedule;
		this.totalTime = totalTime;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public Queue<Consumer> getSchedule() {
		return schedule;
	}

	public Long getTotalTime() {
		return totalTime;
	}

	public LocalDateTime getEndTime() {
		return startTime.plus(Duration.ofMillis(this.totalTime));
	}

	public static class ScheduleBuilder {
		private LocalDateTime startTime;
		private Queue<Consumer> consumers;
		private Long totalTime;

		public ScheduleBuilder() {
			this.startTime = LocalDateTime.now();
			this.consumers = new LinkedList<>();
			this.totalTime = 0L;
		}

		public ScheduleBuilder startsAt(LocalDateTime startTime) {
			this.startTime = startTime;
			return this;
		}

		public ScheduleBuilder withConsumers(Queue<Consumer> consumers) {
			this.consumers = consumers;
			setTotalTime(consumers);
			return this;
		}

		private void setTotalTime(Queue<Consumer> consumers) {
			Long totalTime = 0L;
			for (Consumer consumer : consumers) {
				if (consumer.getTimeElapsed() > totalTime) {
					totalTime = consumer.getTimeElapsed();
				}
			}
			this.totalTime = totalTime;
		}

		public Schedule build() {
			return new Schedule(startTime, consumers, totalTime);
		}
	}
}
