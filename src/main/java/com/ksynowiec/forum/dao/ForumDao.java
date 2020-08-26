
package com.ksynowiec.forum.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ksynowiec.forum.dto.AnswerDTO;
import com.ksynowiec.forum.dto.SubjectDTO;
import com.ksynowiec.forum.model.Answer;
import com.ksynowiec.forum.model.Post;


@Repository
public interface ForumDao {

    public static final int maxNickLength = 64;
    public static final int maxEmailLength = 64;
    public static final int maxSubjectLength = 256;

    public Post getPost(Long id);
    
    public Answer getAnswer(Long id);
    
    public Post addPost(String nick, String email, String subject, String content);

    public void updatePost(String secret, String newContent);
    
    public void deletePost(String secret);
    
    public Answer answerPost(Long postId, String nick, String email, String content);

    public List<SubjectDTO> getTopSubjects(int offset, int limit);
    
    public List<AnswerDTO> getAnswers(Long postId, int offset, int limit);

    public long getAnswersCount(Long postId);
}
