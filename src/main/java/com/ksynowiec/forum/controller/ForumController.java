
package com.ksynowiec.forum.controller;

import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ksynowiec.forum.dao.ForumException;
import com.ksynowiec.forum.dto.AnswerDTO;
import com.ksynowiec.forum.dto.PostDTO;
import com.ksynowiec.forum.dto.SubjectDTO;
import com.ksynowiec.forum.service.ForumService;


/**
 * Rest controller
 */

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    
    private ForumService forumService;
    
    private void checkEmail(String email) {
        isNotEmpty(email, "Email");
        try {
            new InternetAddress(email);
        } catch (AddressException e) {
            throw new ForumException("Given email is not valid");
        }
    }
    
    private void isNotEmpty(String value, String name) {
        if (value != null && value.trim().length() == 0)
            throw new ForumException("Param " + name + " can't be empty");
    }
    
    @Autowired
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping("/getPost/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) throws Exception {
        PostDTO post = forumService.getPostDTO(id);
        if (post != null)
            return new ResponseEntity<PostDTO>(post, HttpStatus.OK);
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such post " + id);
        }
    }

    @GetMapping("/getAnswer/{id}")
    public ResponseEntity<AnswerDTO> getAnswer(@PathVariable Long id) throws Exception {
        AnswerDTO answer = forumService.getAnswerDTO(id);
        if (answer != null)
            return new ResponseEntity<AnswerDTO>(answer, HttpStatus.OK);
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such answer " + id);
        }
    }
    
    @PostMapping("/addPost")
    public ResponseEntity<String> addPost(@RequestParam String nick, @RequestParam String email, @RequestParam String subject,
            @RequestParam String content) throws Exception {
        try {
            checkEmail(email);
            isNotEmpty(nick, "nick");
            isNotEmpty(content, "content");
            isNotEmpty(subject, "subject");
            String secret = forumService.addPost(nick, email, subject, content);
            return new ResponseEntity<String>(secret, HttpStatus.CREATED);
        } catch (ForumException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }
    }

    @PostMapping("/addPostDTO")
    public ResponseEntity<String> addPost(@RequestBody PostDTO post) throws Exception {
        try {
            checkEmail(post.getEmail());
            isNotEmpty(post.getNick(), "nick");
            isNotEmpty(post.getContent(), "content");
            isNotEmpty(post.getSubject(), "subject");
            String secret = forumService.addPost(post);
            return new ResponseEntity<String>(secret, HttpStatus.CREATED);
        } catch (ForumException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }
    }

    /**
     * Update post or answer
     */
    
    @PutMapping("/updatePost/{secret}")
    public ResponseEntity<Void> updatePost(@PathVariable String secret, @RequestBody String content) throws Exception {
        try {
            isNotEmpty(content, "content");
            forumService.updatePost(secret, content);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (ForumException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }
    }
    
    /**
     * Delete post or answer
     */
    @DeleteMapping("/deletePost/{secret}")
    public ResponseEntity deletePost(@PathVariable String secret) throws Exception {
        try {
            forumService.deletePost(secret);
            return new ResponseEntity(secret, HttpStatus.OK);
        } catch (ForumException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }
    }

    @PostMapping("/answerPost/{postId}")
    public ResponseEntity<String> answerPost(@PathVariable Long postId, @RequestParam String nick, @RequestParam String email,
            @RequestParam String content) throws Exception {
        try {
            checkEmail(email);
            isNotEmpty(nick, "nick");
            isNotEmpty(content, "content");
            String secret = forumService.answerPost(postId, nick, email, content);
            return new ResponseEntity<String>(secret, HttpStatus.OK);
        } catch (ForumException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }
    }
    
    @PostMapping("/answerPostDTO")
    public ResponseEntity<String> answerPost(@RequestBody AnswerDTO answer) throws Exception {
        try {
            checkEmail(answer.getEmail());
            isNotEmpty(answer.getNick(), "nick");
            isNotEmpty(answer.getContent(), "content");
            String secret = forumService.answerPost(answer);
            return new ResponseEntity<String>(secret, HttpStatus.OK);
        } catch (ForumException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }
    }

    /**
     * get list answers for post
     */
    @GetMapping("/getTopSubjects")
    public ResponseEntity<List<SubjectDTO>> getTopSubjects(@RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit) throws Exception {
        try {
            List<SubjectDTO> result = forumService.getTopSubjects(offset, limit);
            return new ResponseEntity<List<SubjectDTO>>(result, HttpStatus.OK);
        } catch (ForumException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }

    }
    
    /**
     * get list answered posts
     */
    @GetMapping("/getAnswers/{postId}")
    public ResponseEntity<List<AnswerDTO>> getAnswers(@PathVariable Long postId, @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer before, @RequestParam(required = false) Integer after) throws Exception {
        try {
            List<AnswerDTO> result = forumService.getAnswers(postId, offset, before, after);
            return new ResponseEntity<List<AnswerDTO>>(result, HttpStatus.OK);
        } catch (ForumException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
        }
    }
    
}
