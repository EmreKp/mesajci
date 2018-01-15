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

    public void postMessage(String user, String title, String text, Date postDate, String deletePassword) {
        Message message = new Message();
        message.setUser(user);
        message.setDate(postDate);
        message.setTitle(title);
        message.setMessage(text);
        message.setDeletePass(deletePassword);
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
