package com.emrekp.mesajboard.Service;

import com.emrekp.mesajboard.Model.Comment;
import com.emrekp.mesajboard.Model.Message;
import com.emrekp.mesajboard.Repository.CommentRepository;
import com.emrekp.mesajboard.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SendService {
    private MessageRepository messageRepository;
    private CommentRepository commentRepository;

    @Autowired
    public SendService(MessageRepository messageRepository, CommentRepository commentRepository) {
        this.messageRepository = messageRepository;
        this.commentRepository = commentRepository;
    }

    public void postMessage(Message message) {
        message.setDate(new Date());
        //thread id ek parametre gerektirdiği için ayarlanması controllerda yapılıyor
        messageRepository.save(message);
    }

    public void answer(String user, String text, Date postDate, Long messageId) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setMessage(text);
        comment.setPostDate(postDate);
        comment.setMessageId(messageId);
        commentRepository.save(comment);
    }
}
