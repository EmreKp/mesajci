package com.emrekp.mesajboard.Repository;

import com.emrekp.mesajboard.Model.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
    Message findById(Long id);
}
