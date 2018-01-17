package com.emrekp.mesajboard.Repository;

import com.emrekp.mesajboard.Model.BoardThread;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ThreadRepository extends CrudRepository<BoardThread, Long> {
    BoardThread findBoardThreadByName(String name);
    BoardThread findById(Long id);
}
