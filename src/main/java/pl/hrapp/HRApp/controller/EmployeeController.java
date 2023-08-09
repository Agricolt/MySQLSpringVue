package pl.hrapp.HRApp.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.hrapp.HRApp.dto.EmployeeRequest;
import pl.hrapp.HRApp.entity.Comment;
import pl.hrapp.HRApp.entity.Employee;
import pl.hrapp.HRApp.entity.Job;
import pl.hrapp.HRApp.entity.Project;
import pl.hrapp.HRApp.repository.CommentRepository;
import pl.hrapp.HRApp.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.hrapp.HRApp.repository.JobRepository;
import pl.hrapp.HRApp.repository.ProjectRepository;
import pl.hrapp.HRApp.view.ProjectViews;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/employees")
    public List<Employee> fetchEmployees(){
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        return employee.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/managers")
    public List<Employee> getEmployeeByIsManager() {
        List<Employee> employees = employeeRepository.findEmployeesByIsManager(true);
        return employees;
    }

    @GetMapping("/project/{projectId}")
    public List<Employee> getEmployeeByProject(@PathVariable Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        List<Employee> employees = new ArrayList<>();
        if (optionalProject.isPresent()) {
            employees = employeeRepository.findEmployeesByProjects(optionalProject.get());
        }
        return employees;
    }

    @GetMapping("/projectCandidates/{projectId}")
    public List<Employee> getEmployeeWithoutThisProject(@PathVariable Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        List<Employee> allEmployees = new ArrayList<>();
        if (optionalProject.isPresent()) {
            allEmployees = employeeRepository.findAll();
            List<Employee> projectEmployees = employeeRepository.findEmployeesByProjects(optionalProject.get());

            Iterator<Employee> itr = allEmployees.iterator();
            while (itr.hasNext()) {
                Employee el = itr.next();
                for (Employee employee : projectEmployees) {
                    if (el == employee) {
                        itr.remove();
                        continue;
                    }
                }
            }
        }
        return allEmployees;
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

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequest employeeRequest) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            employee.setFirstName(employeeRequest.getFirstName());
            employee.setSurname(employeeRequest.getSurname());
            employee.setPhoneNumber(employeeRequest.getPhoneNumber());
            employee.setIsManager(employeeRequest.getIsManager());

            // Fetch and set the manager employee
            if (employeeRequest.getManagerId() != null) {
                Optional<Employee> manager = employeeRepository.findById(employeeRequest.getManagerId());
                manager.ifPresent(employee::setManagingEmployee);
            } else {
                employee.setManagingEmployee(null); // Clear the manager if not provided
            }

            // Fetch and set the job
            if (employeeRequest.getJobId() != null) {
                Optional<Job> job = jobRepository.findById(employeeRequest.getJobId());
                job.ifPresent(employee::setJob);
            } else {
                employee.setJob(null); // Clear the job if not provided
            }

            Employee updatedEmployee = employeeRepository.save(employee);
            return ResponseEntity.ok(updatedEmployee);
        } else {
            return ResponseEntity.notFound().build(); // Employee not found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();

            // Remove the employee from the projects they are assigned to
            for (Project project : employee.getProjects()) {
                project.getProjectEmployees().remove(employee);
            }

            // Update managingEmployee of subordinates if applicable
            if (employee.getManagingEmployee() != null) {
                employee.getManagingEmployee().getSubordinates().remove(employee);
            }

            // Update subordinates of managingEmployee if applicable
            if (!employee.getSubordinates().isEmpty()) {
                for (Employee subordinate : employee.getSubordinates()) {
                    subordinate.setManagingEmployee(null);
                }
            }

            // Delete comments associated with the employee
            for (Comment comment : employee.getComments()) {
                commentRepository.delete(comment);
            }
            employee.getComments().clear();

            // Finally, delete the employee
            employeeRepository.delete(employee);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build(); // Employee not found
        }
    }
}