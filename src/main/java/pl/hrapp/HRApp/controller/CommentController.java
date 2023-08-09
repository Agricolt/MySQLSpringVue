package pl.hrapp.HRApp.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.hrapp.HRApp.dto.CommentRequest;
import pl.hrapp.HRApp.entity.Comment;
import pl.hrapp.HRApp.entity.Employee;
import pl.hrapp.HRApp.repository.CommentRepository;
import pl.hrapp.HRApp.repository.EmployeeRepository;
import pl.hrapp.HRApp.view.CommentViews;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:8081")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    @JsonView(CommentViews.AllCommentView.class)
    public List<pl.hrapp.HRApp.entity.Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<pl.hrapp.HRApp.entity.Comment> getCommentById(@PathVariable Long id) {
        Optional<pl.hrapp.HRApp.entity.Comment> comment = commentRepository.findById(id);
        return comment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{employeeId}")
    @JsonView(CommentViews.AllCommentView.class)
    public List<Comment> getCommentByEmployeeId(@PathVariable Long employeeId) {
        List<Comment> comments = commentRepository.findCommentsByEmployeeId(employeeId);
        return comments;
    }

    @PostMapping
    public pl.hrapp.HRApp.entity.Comment createComment(@RequestBody CommentRequest commentRequest) {
        pl.hrapp.HRApp.entity.Comment comment = new pl.hrapp.HRApp.entity.Comment();
        comment.setCommentName(commentRequest.getCommentName());
        comment.setCommentText(commentRequest.getCommentText());

        // Fetch and set the manager employee
        if (commentRequest.getEmployeeId() != null) {
            Optional<Employee> employee = employeeRepository.findById(commentRequest.getEmployeeId());
            employee.ifPresent(comment::setEmployee);
        }
        return commentRepository.save(comment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<pl.hrapp.HRApp.entity.Comment> updateComment(@PathVariable Long id, @RequestBody CommentRequest updatedComment) {
        Optional<pl.hrapp.HRApp.entity.Comment> existingComment = commentRepository.findById(id);
        Optional<Employee> existingEmployee = employeeRepository.findById(updatedComment.getEmployeeId());
        if (existingComment.isPresent()) {
            pl.hrapp.HRApp.entity.Comment comment = new pl.hrapp.HRApp.entity.Comment();
            comment.setId(id);
            comment.setCommentName(updatedComment.getCommentName());
            comment.setCommentText(updatedComment.getCommentText());
            if (existingEmployee.isPresent()) {
                comment.setEmployee(existingEmployee.get());
            }
            commentRepository.save(comment);
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        Optional<pl.hrapp.HRApp.entity.Comment> comment = commentRepository.findById(id);
        Optional<Employee> existingEmployee = employeeRepository.findById(id);
        if (existingEmployee.isPresent()) {
            existingEmployee.get().removeComment(id);
        }
        if (comment.isPresent()) {
            commentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
