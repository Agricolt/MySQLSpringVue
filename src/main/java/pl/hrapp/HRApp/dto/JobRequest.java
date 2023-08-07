package pl.hrapp.HRApp.dto;

import lombok.Data;

@Data
public class JobRequest {
    private long id;
    private String jobName;
    private Float salary;
}
