package pl.hrapp.HRApp.controller;

import org.springframework.http.ResponseEntity;
import pl.hrapp.HRApp.dto.EmployeeRequest;
import pl.hrapp.HRApp.entity.Employee;
import pl.hrapp.HRApp.entity.Job;
import pl.hrapp.HRApp.entity.Project;
import pl.hrapp.HRApp.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.hrapp.HRApp.repository.JobRepository;
import pl.hrapp.HRApp.repository.ProjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/employees")
    public List<Employee> fetchEmployees(){
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        return employee.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Employee createEmployee(@RequestBody EmployeeRequest employeeRequest) {
        Employee employee = new Employee();
        employee.setFirstName(employeeRequest.getFirstName());
        employee.setSurname(employeeRequest.getSurname());
        employee.setPhoneNumber(employeeRequest.getPhoneNumber());
        employee.setIsManager(employeeRequest.getIsManager());

        // Fetch and set the manager employee
        if (employeeRequest.getManagerId() != null) {
            Optional<Employee> manager = employeeRepository.findById(employeeRequest.getManagerId());
            manager.ifPresent(employee::setManagingEmployee);
        }

        // Fetch and set the job
        if (employeeRequest.getJobId() != null) {
            Optional<Job> job = jobRepository.findById(employeeRequest.getJobId());
            job.ifPresent(employee::setJob);
        }
        return employeeRepository.save(employee);
    }

    @PostMapping("/{employeeId}/assign-project/{projectId}")
    public ResponseEntity<Employee> assignProjectToEmployee(
            @PathVariable Long employeeId,
            @PathVariable Long projectId
    ) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();

            Optional<Project> projectOptional = employee.getProjects().stream()
                    .filter(project -> project.getId() == projectId)
                    .findFirst();

            if (projectOptional.isPresent()) {
                return ResponseEntity.badRequest().build(); // Project is already assigned
            }

            Optional<Project> assignedProjectOptional = projectRepository.findById(projectId);
            if (assignedProjectOptional.isPresent()) {
                Project assignedProject = assignedProjectOptional.get();
                employee.addProject(assignedProject);
                employeeRepository.save(employee);
                return ResponseEntity.ok(employee);
            } else {
                return ResponseEntity.notFound().build(); // Project not found
            }
        } else {
            return ResponseEntity.notFound().build(); // Employee not found
        }
    }

    @DeleteMapping("/{employeeId}/remove-project/{projectId}")
    public ResponseEntity<Employee> removeProjectFromEmployee(
            @PathVariable Long employeeId,
            @PathVariable Long projectId
    ) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            Optional<Project> projectOptional = employee.getProjects().stream()
                    .filter(project -> project.getId() == projectId)
                    .findFirst();

            if (projectOptional.isPresent()) {
                Project assignedProject = projectOptional.get();
                employee.removeProject(assignedProject.getId());
                employeeRepository.save(employee);
                return ResponseEntity.ok(employee);
            } else {
                return ResponseEntity.notFound().build(); // Project not found in the employee's projects
            }
        } else {
            return ResponseEntity.notFound().build(); // Employee not found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            employeeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}