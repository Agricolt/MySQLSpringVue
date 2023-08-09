package pl.hrapp.HRApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.hrapp.HRApp.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByEmployeeId(Long employeeId);
}
