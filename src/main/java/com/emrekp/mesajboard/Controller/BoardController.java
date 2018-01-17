package com.emrekp.mesajboard.Controller;

import com.emrekp.mesajboard.Model.BoardThread;
import com.emrekp.mesajboard.Model.Comment;
import com.emrekp.mesajboard.Model.Message;
import com.emrekp.mesajboard.Repository.CommentRepository;
import com.emrekp.mesajboard.Repository.MessageRepository;
import com.emrekp.mesajboard.Repository.ThreadRepository;
import com.emrekp.mesajboard.Service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class BoardController {
    @Autowired
    private SendService sendService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ThreadRepository threadRepository;

    public BoardController(ThreadRepository threadRepository) {
        //initialization
        BoardThread genel = threadRepository.findBoardThreadByName("genel");
        if (genel == null) {
            genel = new BoardThread();
            genel.setName("genel");
            threadRepository.save(genel);
        }
    }

    @RequestMapping("/")
    public String anaSayfa(Model model) {
        /*Iterator<Message> messageIterator = messageRepository.findAll().iterator();
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

        Iterable<BoardThread> threads = threadRepository.findAll();
        model.addAttribute("konular", threads);

        Message formMsg = new Message();
        model.addAttribute("mesaj", formMsg);

        return "main";*/
        return "redirect:/konu/0";
    }

    @RequestMapping("/yenimesaj")
    public String msgForm(Model model) {
        Message message = new Message();
        model.addAttribute("mesaj", message);
        return "mesaj_yaz";
    }

    @RequestMapping("/gonder")
    public String sendMsg(Message message, @RequestParam("thread") Long threadId, Model model) {
        message.setThreadId(threadId);
        sendService.postMessage(message);
        return "gonderildi";
    }

    @RequestMapping("/yanit/{id}")
    public String commentView(@PathVariable("id") Long msgId, Model model) {
        Comment comment = new Comment();
        comment.setMessageId(msgId);
        Message asilMsg = messageRepository.findById(msgId);
        model.addAttribute("asilMesaj", asilMsg.getTitle());
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

    @RequestMapping("/admin")
    public String adminView() {
        return "admin/main";
    }

    @GetMapping("/admin/konular")
    public String threadsView(Model model) {
        BoardThread thread = new BoardThread();
        model.addAttribute("konu", thread);

        Iterator<BoardThread> threadIterator = threadRepository.findAll().iterator();
        List<BoardThread> threads = new ArrayList<>();
        threadIterator.forEachRemaining(threads::add);
        threads.remove(0); //silineceklerden geneli çıkar
        model.addAttribute("konular", threads);
        return "admin/konu";
    }

    @PostMapping("/admin/konular")
    public String addDeleteThread(@ModelAttribute BoardThread thread, @RequestParam("silId") Long silID,
                                  @RequestParam(value="eklesil") String ekleSil, Model model) {
        String success="";
        if (ekleSil.equals("ekle")) {
            BoardThread checkThread = threadRepository.findBoardThreadByName(thread.getName());
            if (checkThread != null) {
                throw new RuntimeException("Aynı isimli thread eklenilemezdir.");
            } else {
                threadRepository.save(thread);
                success = "Thread başarıyla eklendi.";
            }
        } else if (ekleSil.equals("sil")) {
            //şimdilik hata aklıma gelmiyo
            threadRepository.delete(silID);
            success = "Thread başarıyle silindi.";
        }
        model.addAttribute("olduMu", success);
        return "halloldu";
    }

    @RequestMapping("/konu/{id}")
    public String onlyThread(@PathVariable("id") Long id, Model model){
        model.addAttribute("konuID", id); //id'yi şablonda kullanabilmek için
        //check konu id
        BoardThread check = threadRepository.findById(id);
        if (check == null && id != 0) {
            id = 0L; //exception atmaktansa bu daha iyi bence zaten url değişecek
        }

        List<Message> messages;
        if (id != 0) {
            messages = messageRepository.findAllByThreadId(id);
        } else {
            messages = messageRepository.findAll();
        }
        Collections.reverse(messages); //en yeniler üstte
        model.addAttribute("mesajlar", messages);

        List<List<Comment>> commentsList = new ArrayList<>();
        for (Message message : messages) {
            List<Comment> comments = commentRepository.findByMessageId(message.getId());
            commentsList.add(comments);
        }
        model.addAttribute("yorumlar", commentsList);

        //konular
        Iterable<BoardThread> threads = threadRepository.findAll();
        model.addAttribute("konular", threads);

        Message formMsg = new Message();
        model.addAttribute("mesaj", formMsg);

        return "main";
    }
}
