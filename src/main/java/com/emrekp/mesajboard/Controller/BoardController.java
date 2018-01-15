package com.emrekp.mesajboard.Controller;

import com.emrekp.mesajboard.Model.Comment;
import com.emrekp.mesajboard.Model.Message;
import com.emrekp.mesajboard.Repository.CommentRepository;
import com.emrekp.mesajboard.Repository.MessageRepository;
import com.emrekp.mesajboard.Service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class BoardController {
    private SendService sendService;
    private MessageRepository messageRepository;
    private CommentRepository commentRepository;

    @Autowired
    public BoardController(SendService sendService, MessageRepository messageRepository, CommentRepository commentRepository) {
        this.sendService = sendService;
        this.messageRepository = messageRepository;
        this.commentRepository = commentRepository;
    }

    @RequestMapping("/")
    public String anaSayfa(Model model) {
        Iterator<Message> messageIterator = messageRepository.findAll().iterator();
        List<Message> messages = new ArrayList<>();
        messageIterator.forEachRemaining(messages::add);
        Collections.reverse(messages); //en yeniler üstte
        model.addAttribute("mesajlar", messages);

        List<List<Comment>> commentsList = new ArrayList<>();
        for (Message message : messages) {
            List<Comment> comments = commentRepository.findByMessageId(message.getId());
            commentsList.add(comments);
        }
        model.addAttribute("yorumlar", commentsList);

        Message formMsg = new Message();
        model.addAttribute("mesaj", formMsg);

        return "main";
    }

    @RequestMapping("/yenimesaj")
    public String msgForm(Model model) {
        Message message = new Message();
        model.addAttribute("mesaj", message);
        return "mesaj_yaz";
    }

    @RequestMapping("/gonder")
    public String sendMsg(@ModelAttribute(value = "user") String user, @ModelAttribute(value = "title") String title,
                          @ModelAttribute(value = "message") String text,
                          @ModelAttribute(value = "deletePass") String password, Model model) {
        sendService.postMessage(user, title, text, new Date(), password);
        return "gonderildi";
    }

    @RequestMapping("/yanit/{id}")
    public String commentView(@PathVariable("id") Long msgId, Model model) {
        Comment comment = new Comment();
        comment.setMessageId(msgId);
        model.addAttribute("yorum", comment);
        return "yanit_yaz";
    }

    @RequestMapping("/comment")
    public String answerMsg(@ModelAttribute(value = "user") String user, @ModelAttribute(value = "message") String text,
                            @ModelAttribute(value = "messageId") Long msgId, Model model){
        sendService.answer(user, text, new Date(), msgId);
        return "gonderildi";
    }

    @RequestMapping("/post-sil")
    public String deleteMsg(@RequestParam(value = "deletePassword") String password,
                            @RequestParam(value = "messageId") Long messageId, Model model) {
        String realPass = messageRepository.findById(messageId).getDeletePass();
        if (password.equals(realPass) && !realPass.isEmpty()) {
            messageRepository.delete(messageId);
            model.addAttribute("silindi","Silindi");
        } else {
            model.addAttribute("silindi","Silinmedi");
        }
        return "silindimi";
    }
}
