
package com.ksynowiec.forum.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.ksynowiec.forum.dto.AnswerDTO;
import com.ksynowiec.forum.dto.SubjectDTO;
import com.ksynowiec.forum.model.Answer;
import com.ksynowiec.forum.model.Post;


@Repository
public class ForumDaoImp implements ForumDao {

    @PersistenceContext
    EntityManager entityManager;
    
    @Override
    public Post getPost(Long id) {
        return entityManager.find(Post.class, id);
    }
    
    @Override
    public Answer getAnswer(Long id) {
        return entityManager.find(Answer.class, id);
    }

    @Override
    @Transactional
    public Post addPost(String nick, String email, String subject, String content) {
        Post record = new Post(nick, email, subject, content);
        entityManager.persist(record);
        return record;
    }
    
    @Override
    @Transactional
    public void updatePost(String secret, String newContent) {
        Secret s = Secret.decodeSecret(secret);
        String pql = new StringBuilder(128).append("update ").append(s.getEntityName()).append(
                " set content = :content, last_updated= :last_updated where id = :id and secret = :secret").toString();
        Query q = entityManager.createQuery(pql);
        q.setParameter("id", s.id);
        q.setParameter("last_updated", LocalDateTime.now());
        q.setParameter("secret", s.secret);
        q.setParameter("content", newContent);
        int result = q.executeUpdate();
        if (result == 0)
            throw new ForumException("Wrong secret");
    }

    @Override
    @Transactional
    public void deletePost(String secret) {
        Secret s = Secret.decodeSecret(secret);
        String pql = new StringBuilder(64).append("delete from ").append(s.getEntityName()).append(
                " where id = :id and secret = :secret").toString();
        Query q = entityManager.createQuery(pql);
        q.setParameter("id", s.id);
        q.setParameter("secret", s.secret);
        entityManager.flush(); //make sync before cascade delete
        int result = q.executeUpdate();
        if (result == 0)
            throw new ForumException("Wrong secret");
        entityManager.clear(); //cascade delete needs to clear EntityManager
    }
    
    @Override
    @Transactional
    public Answer answerPost(Long postId, String nick, String email, String content) {
        Post post = entityManager.getReference(Post.class, postId);
        Answer answer = new Answer(post, nick, email, content);
        entityManager.persist(answer);
        post.answer();
        entityManager.persist(post);
        return answer;
    }
    
    @Override
    public List<SubjectDTO> getTopSubjects(int offset, int limit) {
        String pql = "select new com.ksynowiec.forum.dto.SubjectDTO(p.id, p.createDate, p.lastUpdated, p.lastAnswered, p.nick, p.email, p.subject) from Post p order by lastAnswered desc NULLS LAST";
        Query q = entityManager.createQuery(pql, SubjectDTO.class);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        List<SubjectDTO> list = q.getResultList();
        return list;
    }
    
    @Override
    public List<AnswerDTO> getAnswers(Long postId, int offset, int limit) {
        Post post = entityManager.getReference(Post.class, postId);
        String pql = "select new com.ksynowiec.forum.dto.AnswerDTO(a.id, a.post.id, a.createDate, a.lastUpdated, a.nick, a.email, a.content) from Answer a where a.post=:postId order by a.createDate asc";
        Query q = entityManager.createQuery(pql, AnswerDTO.class);
        q.setParameter("postId", post);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        List<AnswerDTO> list = q.getResultList();
        return list;
    }
    
    @Override
    public long getAnswersCount(Long postId) {
        Post post = entityManager.getReference(Post.class, postId);
        String pql = "select count(*) from Answer a where a.post=:postId";
        Query q = entityManager.createQuery(pql, Long.class);
        q.setParameter("postId", post);
        Long result = (Long) q.getSingleResult();
        return result != null ? result : 0;
    }

}
