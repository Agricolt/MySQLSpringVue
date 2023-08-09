package pl.hrapp.HRApp.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String commentName;
    private String commentText;
    private Long employeeId;
}
