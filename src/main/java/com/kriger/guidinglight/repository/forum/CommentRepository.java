package com.kriger.guidinglight.repository.forum;

import com.kriger.guidinglight.model.forum.Comment;
import com.kriger.guidinglight.model.forum.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT q FROM Comment q WHERE q.question = :question")
    List<Comment> findAllCommentsByQuestion(@Param("question") Question question);

}
