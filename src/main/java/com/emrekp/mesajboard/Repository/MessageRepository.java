package com.emrekp.mesajboard.Repository;

import com.emrekp.mesajboard.Model.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
    Message findById(Long id);
    List<Message> findAll(); //iterable olarak var ama liste olsun
    List<Message> findAllByThreadId(Long id);
}
