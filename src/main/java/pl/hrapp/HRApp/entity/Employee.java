package pl.hrapp.HRApp.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import pl.hrapp.HRApp.view.CommentViews;
import pl.hrapp.HRApp.view.EmployeeViews;

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
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(EmployeeViews.EmployeeIdFirstNameSurname.class)
    private long id;

    @Column(name = "first_name", nullable = false)
    @JsonView(EmployeeViews.EmployeeIdFirstNameSurname.class)
    private String firstName;

    @Column(name = "surname")
    @JsonView(EmployeeViews.EmployeeIdFirstNameSurname.class)
    private String surname;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_Manager")
    private Boolean isManager;

    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<pl.hrapp.HRApp.entity.Comment> comments = new ArrayList<>();

    @ManyToOne(cascade={CascadeType.PERSIST})
    @JoinColumn(name = "managing_employee_id")
    @JsonManagedReference
    private Employee managingEmployee;

    @OneToMany(mappedBy = "managingEmployee")
    @JsonBackReference
    private Set<Employee> subordinates = new HashSet<Employee>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    @JsonManagedReference
    private Job job;

    @JsonManagedReference
    @ManyToMany
    @JoinTable(
            name = "employee_project",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    Set<Project> projects = new HashSet<>();

    public void addProject(Project project) {
        this.getProjects().add(project);
        project.getProjectEmployees().add(this);
    }

    public void removeProject(long projectId) {
        this.projects.removeIf(p -> p.getId() == projectId);
    }

    public void addComment(pl.hrapp.HRApp.entity.Comment comment) {
        this.getComments().add(comment);
    }

    public void removeComment(long commentId) {
        this.getComments().removeIf(p -> p.getId() == commentId);
    }

}