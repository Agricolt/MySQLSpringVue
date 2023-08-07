package pl.hrapp.HRApp.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequest {
    private String projectName;
    private Date startDate;
    private Date endDate;
}
