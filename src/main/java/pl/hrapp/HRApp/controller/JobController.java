package pl.hrapp.HRApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.hrapp.HRApp.dto.JobRequest;
import pl.hrapp.HRApp.entity.Employee;
import pl.hrapp.HRApp.entity.Job;
import pl.hrapp.HRApp.repository.EmployeeRepository;
import pl.hrapp.HRApp.repository.JobRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:8081")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public List<JobRequest> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobRequest> getJobById(@PathVariable Long id) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            JobRequest dto = convertToDTO(jobOptional.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<JobRequest> addJob(@RequestBody JobRequest JobRequest) {
        // Convert DTO to Entity and save
        Job job = convertToEntity(JobRequest);
        jobRepository.save(job);
        return ResponseEntity.ok(convertToDTO(job));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobRequest> editJob(@PathVariable Long id, @RequestBody JobRequest JobRequest) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            // Update job properties from DTO
            job.setJobName(JobRequest.getJobName());
            job.setSalary(JobRequest.getSalary());
            // Other property updates as needed
            jobRepository.save(job);
            return ResponseEntity.ok(convertToDTO(job));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            List<Employee> employees = employeeRepository.findEmployeesByJobId(jobOptional.get().getId());
            for (Employee employee : employees) {
                employee.setJob(null);
            }
            jobRepository.delete(jobOptional.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private JobRequest convertToDTO(Job job) {
        JobRequest jobRequest = new JobRequest();
        jobRequest.setJobName(job.getJobName());
        jobRequest.setSalary(job.getSalary());
        jobRequest.setId(job.getId());

        return jobRequest;
    }

    private Job convertToEntity(JobRequest jobRequest) {
        Job job = new Job();
        job.setId(jobRequest.getId());
        job.setJobName(jobRequest.getJobName());
        job.setSalary(jobRequest.getSalary());

        return job;
    }
}
