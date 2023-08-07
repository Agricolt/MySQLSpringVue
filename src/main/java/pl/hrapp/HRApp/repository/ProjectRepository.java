package pl.hrapp.HRApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.hrapp.HRApp.entity.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findProjectsById(Long employeeId);
}
