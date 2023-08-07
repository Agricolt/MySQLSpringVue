package pl.hrapp.HRApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.hrapp.HRApp.dto.CommentRequest;
import pl.hrapp.HRApp.entity.Comment;
import pl.hrapp.HRApp.entity.Employee;
import pl.hrapp.HRApp.repository.CommentRepository;
import pl.hrapp.HRApp.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin("http://localhost:3000/")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Comment createComment(@RequestBody CommentRequest commentRequest) {
        Comment comment = new Comment();
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
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
        Optional<Comment> existingComment = commentRepository.findById(id);
        if (existingComment.isPresent()) {
            updatedComment.setId(id);
            commentRepository.save(updatedComment);
            return ResponseEntity.ok(updatedComment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            commentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
