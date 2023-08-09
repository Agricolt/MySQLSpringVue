package pl.hrapp.HRApp.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import jakarta.persistence.*;
import pl.hrapp.HRApp.view.CommentViews;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(CommentViews.AllCommentView.class)
    private long id;

    @Column(name = "comment_name", nullable = false)
    @JsonView(CommentViews.AllCommentView.class)
    private String commentName;

    @Column(name = "comment_text")
    @JsonView(CommentViews.AllCommentView.class)
    private String commentText;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    @JsonView(CommentViews.AllCommentView.class)
    private Employee employee;
}