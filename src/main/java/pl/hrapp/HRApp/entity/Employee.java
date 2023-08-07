package pl.hrapp.HRApp.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employees")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "surname")
    private String surname;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_Manager")
    private Boolean isManager;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "managing_employee_id")
    private Employee managingEmployee;

    @OneToMany(mappedBy = "managingEmployee")
    private Set<Employee> subordinates = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "job_id", nullable=false)
    private Job job;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "employee_project",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    Set<Project> projects = new HashSet<>();

    public Set<Project> getProjects() {
        return projects;
    }

    public void setTags(Set<Project> tags) {
        this.projects = tags;
    }

    public void addProject(Project project) {
        this.projects.add(project);
        project.getProjectEmployees().add(this);
    }

    public void removeProject(long ProjectId) {
        Project project = this.projects.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        if (project != null) {
            this.projects.remove(project);
            project.getProjectEmployees().remove(this);
        }
    }
}