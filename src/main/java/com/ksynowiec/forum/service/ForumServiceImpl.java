
package com.ksynowiec.forum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ksynowiec.forum.dao.ForumDao;
import com.ksynowiec.forum.dao.ForumException;
import com.ksynowiec.forum.dao.Secret;
import com.ksynowiec.forum.dto.AnswerDTO;
import com.ksynowiec.forum.dto.PostDTO;
import com.ksynowiec.forum.dto.SubjectDTO;
import com.ksynowiec.forum.model.Answer;
import com.ksynowiec.forum.model.Post;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ForumServiceImpl implements ForumService {

    @Value("${forum.max_limit}")
    private int MAX_LIMIT;

    @Autowired
    private ForumDao dao;

    @Override
    public PostDTO getPostDTO(Long id) {
        Post post = dao.getPost(id);
        if (post != null)
            return post.getPostDTO();
        else
            return null;
    }

    @Override
    public AnswerDTO getAnswerDTO(Long id) {
        Answer answer = dao.getAnswer(id);
        if (answer != null)
            return answer.getAnswerDTO();
        else
            return null;
    }
    
    private int checkAndGetParam(Integer param, Integer defaultValue, String name) {
        if (param != null)
            if (param.intValue() < 0)
                throw new ForumException(name + " param can't be negative");
            else
                return param;
        else
            return defaultValue;

    }

    @Override
    public String addPost(String nick, String email, String subject, String content) {
        Post post = dao.addPost(nick, email, subject, content);
        return Secret.encodeSecret(post);
    }
    
    @Override
    public String addPost(PostDTO post) {
        Post newPost = dao.addPost(post.getNick(), post.getEmail(), post.getSubject(), post.getContent());
        return Secret.encodeSecret(newPost);
    }
    
    @Override
    public void updatePost(String secret, String newContent) {
        dao.updatePost(secret, newContent);
    }
    
    @Override
    public void deletePost(String secret) {
        dao.deletePost(secret);

    }

    @Override
    public String answerPost(Long postId, String nick, String email, String content) {
        Answer newAnswer = dao.answerPost(postId, nick, email, content);
        return Secret.encodeSecret(newAnswer);
    }
    
    @Override
    public String answerPost(AnswerDTO answer) {
        Answer newAnswer = dao.answerPost(answer.getPostId(), answer.getNick(), answer.getEmail(), answer.getContent());
        return Secret.encodeSecret(newAnswer);
    }

    @Override
    public List<SubjectDTO> getTopSubjects(Integer offset, Integer limit) {
        int start = checkAndGetParam(offset, 0, "offset");
        int count = checkAndGetParam(limit, MAX_LIMIT, "limit");
        if (count > MAX_LIMIT)
            count = MAX_LIMIT;
        return dao.getTopSubjects(start, count);
    }

    @Override
    public List<AnswerDTO> getAnswers(Long postId, Integer middleOffset, Integer countBefore, Integer countAfter) {
        long answersCount = dao.getAnswersCount(postId);
        debug("\nFound Answers: " + answersCount);
        if (answersCount == 0)
            return new ArrayList<AnswerDTO>();
        
        int middle = checkAndGetParam(middleOffset, 0, "middleOffset");
        int before = checkAndGetParam(countBefore, MAX_LIMIT, "countBefore");
        int after = checkAndGetParam(countAfter, MAX_LIMIT, "countAfter");
        
        if (before >= middle)
            before = middle - 1;
        if (middle + after > answersCount)
            after = (int) (answersCount - middle);
        int count = before + after + 1;
        
        debug("\tGetting Answers for post ", postId);
        debug("Middle: ", middle);
        debug("Before: ", before);
        debug("After: ", after);
        debug("Count: ", count);
        
        if (count > MAX_LIMIT) {
            if (before == 0)
                count = MAX_LIMIT;
            else if (after == 0)
                count = MAX_LIMIT;
            else {
                float factor = (float) MAX_LIMIT / (float) count;
                before = Math.round(before * factor);
                after = Math.round(after * factor);
                count = before + after + 1;
                
                debug("Factor: ", factor);
                debug("Factor before: ", before);
                debug("Factor after: ", after);
                debug("Factor count: ", count);
                if (count > MAX_LIMIT)
                    count = MAX_LIMIT;
            }
        }
        int offset = middle - before - 1; //to get first element offset must be 0
        if (offset < 0)
            offset = 0;
        debug("DB offset: ", offset);
        debug("DB limit: ", count);
        List<AnswerDTO> result = dao.getAnswers(postId, offset, count);
        debug("Result count: ", result.size());
        return result;
    }

    private void debug(String text) {
        log.debug(text);
    }
    
    private void debug(String text, Object value) {
        if (log.isDebugEnabled()) // If not, there is no need to make text+value string
            log.debug(text + value);
    }

}
