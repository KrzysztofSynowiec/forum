
package com.ksynowiec.forum.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ksynowiec.forum.dto.AnswerDTO;
import com.ksynowiec.forum.dto.PostDTO;
import com.ksynowiec.forum.dto.SubjectDTO;


/**
 * Service interface
 */

@Service
public interface ForumService {
    
    public PostDTO getPostDTO(Long id);

    public AnswerDTO getAnswerDTO(Long id);
    
    public String addPost(PostDTO post);
    
    public String addPost(String nick, String email, String subject, String content);
    
    public void updatePost(String secret, String newContent);
    
    public void deletePost(String secret);
    
    public String answerPost(Long postId, String nick, String email, String content);
    
    public String answerPost(AnswerDTO answer);
    
    /**
     * get list answered posts
     */
    
    public List<SubjectDTO> getTopSubjects(Integer offset, Integer limit);
    
    /**
     * get list answers for post
     */
    public List<AnswerDTO> getAnswers(Long postId, Integer offset, Integer before, Integer after);

}
