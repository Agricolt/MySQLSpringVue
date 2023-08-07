package pl.hrapp.HRApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.hrapp.HRApp.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
