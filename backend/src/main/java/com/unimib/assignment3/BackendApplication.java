package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    CommandLineRunner loadFakeTasks(Facade facade) {
        return args -> {
            Task task1 = facade.createTask(TaskState.TO_BE_STARTED);
            task1.setDescription("Design the project architecture");
            facade.saveTask(task1);
            Task task2 = facade.createTask(TaskState.TO_BE_STARTED);
            task2.setDescription("Implement the user authentication module");
            facade.saveTask(task2);
            Task task3 = facade.createTask(TaskState.TO_BE_STARTED);
            task3.setTaskState(TaskState.STARTED);
            task3.setTaskState(TaskState.DONE);
            task3.setDescription("Set up the development environment");
            facade.saveTask(task3);
            Task task4 = facade.createTask(TaskState.TO_BE_STARTED);
            task4.setDescription("Develop the RESTful API endpoints");
            facade.saveTask(task4);
            Task task5 = facade.createTask(TaskState.TO_BE_STARTED);
            task5.setTaskState(TaskState.STARTED);
            task5.setDescription("Create unit tests for the service layer");
            facade.saveTask(task5);
            Task task6 = facade.createTask(TaskState.TO_BE_STARTED);
            task6.setTaskState(TaskState.STARTED);
            task6.setDescription("Conduct code reviews and optimize performance");
            facade.saveTask(task6);
            Task task7 = facade.createTask(TaskState.TO_BE_STARTED);
            task7.setDescription("Integrate third-party services and APIs");
            facade.saveTask(task7);
            Task task8 = facade.createTask(TaskState.TO_BE_STARTED);
            task8.setDescription("Prepare deployment scripts and documentation");
            facade.saveTask(task8);
            Task task9 = facade.createTask(TaskState.TO_BE_STARTED);
            task9.setDescription("Perform user acceptance testing (UAT)");
            task9.setTaskState(TaskState.STARTED);
            task9.setTaskState(TaskState.DONE);
            facade.saveTask(task9);
            Task task10 = facade.createTask(TaskState.TO_BE_STARTED);
            task10.setDescription("Fix bugs identified during testing");
            facade.saveTask(task10);
        };
    }
}
