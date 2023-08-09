package pl.hrapp.HRApp.repository;

import pl.hrapp.HRApp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findEmployeesById(Long projectId);
    List<Employee> findEmployeesByJobId(Long jobId);
    List<Employee> findEmployeesByIsManager(Boolean isManager);
}