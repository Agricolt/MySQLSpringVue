package pl.hrapp.HRApp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {
    private String firstName;
    private String surname;
    private String phoneNumber;
    private Boolean isManager;
    private Long managerId;
    private Long jobId;
}
