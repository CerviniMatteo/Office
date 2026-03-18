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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class BackendApplication {

    private final String BASE64_IMAGE_1 = "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";
    private final String BASE64_IMAGE_2 = "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";
    private final String BASE64_IMAGE_3 = "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";

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    @Profile("ui")
    CommandLineRunner loadFakeData(Facade facade) {
        return args -> {

            LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

            List<Task> tasks = generatePlaceholderTasks(facade, monday);

            List<Employee> employees = List.of(
                    createEmployee(facade, "Matteo", "Cervini", BASE64_IMAGE_1),
                    createEmployee(facade, "Marco", "Rossi", BASE64_IMAGE_2),
                    createEmployee(facade, "Luca", "Verdi", BASE64_IMAGE_3)
            );

            System.out.println("Loaded " + tasks.size() + " tasks and " + employees.size() + " employees.");
        };
    }

    private List<Task> generatePlaceholderTasks(Facade facade, LocalDate monday) {

        List<String> descriptions = List.of(
                "Design the project architecture",
                "Implement the user authentication module",
                "Set up the development environment",
                "Develop the RESTful API endpoints",
                "Create unit tests for the service layer",
                "Conduct code reviews and optimize performance",
                "Integrate third-party services and APIs",
                "Prepare deployment scripts and documentation",
                "Perform user acceptance testing (UAT)",
                "Fix bugs identified during testing"
        );

        List<Task> tasks = new ArrayList<>();

        tasks.add(createAndSaveTask(
                facade,
                descriptions.get(0),
                monday.atTime(8, 0),
                monday.atTime(11, 30)
        ));

        tasks.add(createAndSaveTask(
                facade,
                descriptions.get(1),
                monday.atTime(12, 0),
                monday.atTime(17, 30)
        ));

        tasks.add(createAndSaveTask(
                facade,
                descriptions.get(2),
                monday.plusDays(1).atTime(8, 0),
                monday.plusDays(1).atTime(11, 30)
        ));

        tasks.add(createAndSaveTask(
                facade,
                descriptions.get(3),
                monday.plusDays(1).atTime(12, 0),
                monday.plusDays(1).atTime(17, 30)
        ));

        tasks.add(createAndSaveTask(
                facade,
                descriptions.get(4),
                monday.plusDays(2).atTime(8, 0),
                monday.plusDays(2).atTime(11, 30)
        ));

        tasks.add(createAndSaveTask(
                facade,
                descriptions.get(5),
                monday.plusDays(2).atTime(12, 0),
                monday.plusDays(2).atTime(17, 30)
        ));

        tasks.add(createAndSaveTask(
                facade,
                descriptions.get(6),
                monday.plusDays(3).atTime(8, 0),
                monday.plusDays(3).atTime(11, 30)
        ));

        tasks.add(createAndSaveTask(
                facade,
                descriptions.get(7),
                monday.plusDays(3).atTime(12, 0),
                monday.plusDays(3).atTime(17, 30)
        ));

        tasks.add(createAndSaveTask(
                facade,
                descriptions.get(8),
                monday.plusDays(4).atTime(8, 0),
                monday.plusDays(4).atTime(11, 30)
        ));

        tasks.add(createAndSaveTask(
                facade,
                descriptions.get(9),
                monday.plusDays(4).atTime(12, 0),
                monday.plusDays(4).atTime(17, 30)
        ));

        return tasks;
    }

    private Task createAndSaveTask(
            Facade facade,
            String description,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        Task task = facade.createTask(TaskState.TO_BE_STARTED);
        task.setDescription(description);
        task.setStartDate(startDateTime);
        task.setEndDate(endDateTime);
        return facade.saveTask(task);
    }

    private Employee createEmployee(
            Facade facade,
            String firstName,
            String lastName,
            String avatarBase64
    ) {
        return facade.saveEmployee(
                facade.createEmployee(firstName, lastName, avatarBase64)
        );
    }
}