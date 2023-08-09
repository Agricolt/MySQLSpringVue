package pl.hrapp.HRApp.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import pl.hrapp.HRApp.view.EmployeeViews;
import pl.hrapp.HRApp.view.ProjectViews;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(ProjectViews.BasicProject.class)
    private long id;

    @Column(name = "project_name", nullable = false)
    @JsonView(ProjectViews.BasicProject.class)
    private String projectName;

    @Column(name = "start_date")
    @JsonView(ProjectViews.BasicProject.class)
    private Date startDate;

    @Column(name = "end_date")
    @JsonView(ProjectViews.BasicProject.class)
    private Date endDate;

    @Column(name = "employees_number")
    @JsonView(ProjectViews.BasicProject.class)
    private Integer employeesNumber = 0;

    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.PERSIST}, mappedBy = "projects")
    Set<Employee> projectEmployees = new HashSet<>();

    public Set<Employee> getProjectEmployees() {
        return projectEmployees;
    }

    public void setProjectEmployees(Set<Employee> employees) {
        this.projectEmployees = employees;
    }

}
