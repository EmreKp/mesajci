package com.emrekp.mesajboard.Repository;

import com.emrekp.mesajboard.Model.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long>{
    List<Comment> findByMessageId(Long id);
}
