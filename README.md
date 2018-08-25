# task-scheduler
A Task Scheduler which tries to split tasks among available consumers.

### How it works
- A task would have a duration and a target date/time that must be met.
- A consumer can run any number of tasks in a sequential manner.
- A consumer can not run multiple tasks at the same time.
- A consumer doesn't need take any breaks between assigned tasks.
- Scheduler carries a queue of tasks and consumers in storage. (Priority queues)
- Tasks are prioritized based on which has an earlier target. In the case of tasks having the same target, the task with the longer duration runs first.
- Consumers keep an internal variable carrying the time needed by all assigned tasks. At any time, when a consumer is needed, the one with less time elapsed is assumed to be the first to free up.
- A plan is considered successful only if all tasks can be completed by their respective target date/time.
- Scheduler eagerly tries to calculate a plan whenever a task or consumer is added.
It assumes a start time equal to the time of creating the task or consumer.
- Scheduler accepts a (startTime) parameter which controls when to start the schedule of tasks. If (startTime) is not provided, the time of the request will be used. [Check instruction 4](#4-get-plan-get)

### Installation
I used Spring Boot to build the project. The easiest way to build it would be to run the following commands:
```sh
$ mvn clean package
$ mvn spring-boot:run
```
This should run the application on localhost:8080

### Using the application
#### 1. Submit Task (POST)
This endpoints adds a task to the pool of tasks to be scheduled

###### Example Request
```javascript
POST http://localhost:8080/task-scheduler/task

Body:
{
   "durationInMillis":{long},
   "targetDateTime":"{date in format yyyy-MM-dd'T'HH:mm:ss.SSSZ}"
}
```
###### Example Response {TASK_BODY}
```javascript
{
    "taskId": "{UUID String of ID}",
    "durationInMillis": {long},
    "targetDateTime": "{date in format yyyy-MM-dd'T'HH:mm:ss.SSSZ}"
}
```
###### Error Response
```javascript
- If input was formatted incorrectly
{
    "timestamp": "{Time of error}",
    "status": 400,
    "error": "Bad Request",
    "message": "{ERROR_MESSAGE}",
    "path": "/task-scheduler/task"
}
```

#### 2. Register Consumer (POST)
This endpoint adds a consumer to the pool of consumers ready to accept tasks

###### Example Request
```javascript
POST http://localhost:8080/task-scheduler/consumer
```
###### Example Response {CONSUMER_BODY}
```javascript
{
    "consumerId": "{UUID String of ID}",
    "assignedTasks": [{TASK_BODY}, {TASK_BODY}]
}
```

#### 3. Get Consumer Tasks (GET)
This endpoint retrieves the tasks assigned to a consumer

###### Example Request
```javascript
GET http://localhost:8080/task-scheduler/consumer/{CONSUMER_ID}/tasks
```
###### Example Response (Array of assigned task objects)
```javascript
[{TASK_BODY},{TASK_BODY}]
```
###### Error Response
```javascript
- If no consumer matches the {CONSUMER_ID} passed
{
    "timestamp": "{Time of error}",
    "status": 404,
    "error": "Not Found",
    "message": "Consumer (CONSUMER_ID) not found",
    "path": "/task-scheduler/consumer/{CONSUMER_ID}/tasks"
}
```
#### 4. Get Plan (GET)
Retrieve the schedule of tasks (if any). This endpoint also allows you to recalculate plans based on current time or a different start date. 

###### Example Request
```javascript
GET http://localhost:8080/task-scheduler/plan

Request parameters:
(optional) startTime : date in format yyyy-MM-dd'T'HH:mm:ss.SSSZ
- If provided, the scheduler will try recalculating a plan based on the start time passed.
- If not provided, the scheduler will return the existing plan (if any) 

(optional) recalculate: (true/false) defaults to false
- If passed as true, it will enforce the scheduler to recalculate a plan based on the startTime. If no startTime is provided, it will recalculate based on current time.
```
###### Example Response
```javascript
{
    "startTime": "{Date/Time of starting the plan}",
    "schedule": [
        {
            "consumerId": "{UUID String}",
            "assignedTasks": [{TASK_BODY}, {TASK_BODY}]
        },
        {
            "consumerId": "{UUID String}",
            "assignedTasks": [{TASK_BODY}]
        }
    ],
    "totalTime": {Duration in millis that will plan will take},
    "endTime": "{Date/Time of completing the plan}"
}
```
###### Error Response
```javascript
- If no plan was found
{
    "timestamp": "{Time of error}",
    "status": 404,
    "error": "Not Found",
    "message": "Not enough consumers to schedule tasks. Current start time is: {Date/Time}",
    "path": "/task-scheduler/plan"
}

- If input parameters were malformed
{
    "timestamp": "{Time of error}",
    "status": 400,
    "error": "Bad Request",
    "message": "{ERROR_MESSAGE}",
    "path": "/task-scheduler/plan"
}
```

#### 5. Get Tasks (GET)
Retrieve all tasks

###### Example Request
```javascript
GET http://localhost:8080/task-scheduler/tasks
```
###### Example Response
```javascript
[
    {TASK_BODY},
    {TASK_BODY}
]
```

#### 6. Get Consumers (GET)
Retrieve all consumers

###### Example Request
```javascript
GET http://localhost:8080/task-scheduler/consumers
```
###### Example Response
```javascript
[
    {CONSUMER_BODY},
    {CONSUMER_BODY}
]
```

#### 7. Reset Scheduler (DELETE)
Reset the scheduler. This removes any tasks, consumers, and plans stored.

###### Example Request
```javascript
DELETE http://localhost:8080/task-scheduler/reset
```
###### Example Response
```javascript
200 OK
```
