package pl.hrapp.HRApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.hrapp.HRApp.entity.Employee;
import pl.hrapp.HRApp.entity.Project;
import pl.hrapp.HRApp.repository.EmployeeRepository;
import pl.hrapp.HRApp.repository.ProjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        return projectOptional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project savedProject = projectRepository.save(project);
        return ResponseEntity.ok(savedProject);
    }

    @PostMapping("/{projectId}/assign-employee/{employeeId}")
    public ResponseEntity<Project> assignEmployeeToProject(
            @PathVariable Long projectId,
            @PathVariable Long employeeId
    ) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();

            Optional<Employee> employeeOptional = project.getProjectEmployees().stream()
                    .filter(employee -> employee.getId() == employeeId)
                    .findFirst();

            if (employeeOptional.isPresent()) {
                return ResponseEntity.badRequest().build(); // Employee is already assigned
            }

            Optional<Employee> assignedEmployeeOptional = employeeRepository.findById(employeeId);
            if (assignedEmployeeOptional.isPresent()) {
                Employee assignedEmployee = assignedEmployeeOptional.get();
                project.getProjectEmployees().add(assignedEmployee);
                projectRepository.save(project);
                return ResponseEntity.ok(project);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{projectId}/remove-employee/{employeeId}")
    public ResponseEntity<Project> removeEmployeeFromProject(
            @PathVariable Long projectId,
            @PathVariable Long employeeId
    ) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            Optional<Employee> employeeOptional = project.getProjectEmployees().stream()
                    .filter(employee -> employee.getId() == employeeId)
                    .findFirst();

            if (employeeOptional.isPresent()) {
                Employee assignedEmployee = employeeOptional.get();
                project.getProjectEmployees().remove(assignedEmployee);
                projectRepository.save(project);
                return ResponseEntity.ok(project);
            } else {
                return ResponseEntity.notFound().build(); // Employee not found in the project
            }
        } else {
            return ResponseEntity.notFound().build(); // Project not found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isPresent()) {
            projectRepository.delete(projectOptional.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
