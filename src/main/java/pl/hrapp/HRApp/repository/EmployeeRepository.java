package pl.hrapp.HRApp.repository;

import pl.hrapp.HRApp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.hrapp.HRApp.entity.Project;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findEmployeesById(Long projectId);
    List<Employee> findEmployeesByJobId(Long jobId);
    List<Employee> findEmployeesByIsManager(Boolean isManager);
    List<Employee> findEmployeesByProjects(Project project);
}