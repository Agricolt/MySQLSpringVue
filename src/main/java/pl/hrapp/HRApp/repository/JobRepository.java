package pl.hrapp.HRApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.hrapp.HRApp.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}