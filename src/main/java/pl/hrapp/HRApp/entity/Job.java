package pl.hrapp.HRApp.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    private Float salary;

    @OneToMany(
            mappedBy = "job"
    )
    @JsonBackReference
    private Set<Employee> employees = new HashSet<>();

    public void addEmployee(Employee employee) {
        this.getEmployees().add(employee);
    }

    public void removeEmployee(Long id) {
        this.getEmployees().removeIf(p -> p.getId() == id);
    }
}