package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;


@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Profile("ui")
    @Bean
    CommandLineRunner loadFakeTasks(Facade facade) {
        return args -> {

            Task task1 = facade.createTask(TaskState.TO_BE_STARTED);
            task1.setDescription("Design the project architecture");
            task1 = facade.saveTask(task1);

            Task task2 = facade.createTask(TaskState.TO_BE_STARTED);
            task2.setDescription("Implement the user authentication module");
            task2 = facade.saveTask(task2);

            Task task3 = facade.createTask(TaskState.TO_BE_STARTED);
            task3.setDescription("Set up the development environment");
            task3 = facade.saveTask(task3);
            facade.changeTaskState(task3.getTaskId(), TaskState.STARTED);
            facade.changeTaskState(task3.getTaskId(), TaskState.DONE);


            Task task4 = facade.createTask(TaskState.TO_BE_STARTED);
            task4.setDescription("Develop the RESTful API endpoints");
            task4 = facade.saveTask(task4);
            facade.changeTaskState(task4.getTaskId(), TaskState.STARTED);

            Task task5 = facade.createTask(TaskState.TO_BE_STARTED);
            task5.setDescription("Create unit tests for the service layer");
            task5 = facade.saveTask(task5);
            facade.changeTaskState(task5.getTaskId(), TaskState.STARTED);

            Task task6 = facade.createTask(TaskState.TO_BE_STARTED);
            task6.setDescription("Conduct code reviews and optimize performance");
            task6 = facade.saveTask(task6);
            facade.changeTaskState(task6.getTaskId(), TaskState.STARTED);

            Task task7 = facade.createTask(TaskState.TO_BE_STARTED);
            task7.setDescription("Integrate third-party services and APIs");
            task7 = facade.saveTask(task7);

            Task task8 = facade.createTask(TaskState.TO_BE_STARTED);
            task8.setDescription("Prepare deployment scripts and documentation");
            task8 = facade.saveTask(task8);

            Task task9 = facade.createTask(TaskState.TO_BE_STARTED);
            task9.setDescription("Perform user acceptance testing (UAT)");
            task9 = facade.saveTask(task9);
            facade.changeTaskState(task9.getTaskId(), TaskState.STARTED);
            facade.changeTaskState(task9.getTaskId(), TaskState.DONE);

            Task task10 = facade.createTask(TaskState.TO_BE_STARTED);
            task10.setDescription("Fix bugs identified during testing");
            task10 = facade.saveTask(task10);

            Employee employee = facade.saveEmployee(facade.createEmployee("Matteo", "Cervini",
                    "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"));


            facade.assignEmployeeToTask(task5.getTaskId(), employee.getWorkerId());
            facade.assignEmployeeToTask(task6.getTaskId(), employee.getWorkerId());
            facade.assignEmployeeToTask(task10.getTaskId(), employee.getWorkerId());

            Employee employee2 = facade.saveEmployee(facade.createEmployee("Marco", "Rossi",
            "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"));

            facade.assignEmployeeToTask(task1.getTaskId(), employee2.getWorkerId());
            facade.assignEmployeeToTask(task2.getTaskId(), employee2.getWorkerId());
            facade.assignEmployeeToTask(task7.getTaskId(), employee2.getWorkerId());
            facade.assignEmployeeToTask(task6.getTaskId(), employee2.getWorkerId());
        };
    }
}
