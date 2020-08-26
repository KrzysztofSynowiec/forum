
package com.ksynowiec.forum.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ksynowiec.forum.dao.ForumDao;
import com.ksynowiec.forum.dao.ForumException;
import com.ksynowiec.forum.dto.PostDTO;

import lombok.Getter;
import lombok.Setter;


/**
 * Entity for posts
 */

@Entity
@Table(indexes = {@Index(name = "post_last_anwered_i", columnList = "last_answered DESC")})
public class Post extends AbstractPost {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postSequence")
    @SequenceGenerator(name = "postSequence", sequenceName = "post_s", allocationSize = 30)
    @Getter
    @Setter
    @Id
    private Long id;
    
    public Post(String nick, String email, String subject, String content) {
        super(nick, email, content);
        if (isValidParam(subject, ForumDao.maxSubjectLength))
            this.subject = subject;
        else
            throw new ForumException("Subject can't be empty or longer than " + ForumDao.maxSubjectLength);
    }

    public Post() {}

    @Getter
    @Column(name = "last_answered")
    private LocalDateTime lastAnswered;
    
    public void answer() {
        lastAnswered = LocalDateTime.now();
    }
    
    @Getter
    @Setter
    @Column(length = ForumDao.maxSubjectLength, updatable = false, nullable = true)
    private String subject;

    public PostDTO getPostDTO() {
        return new PostDTO(id, createDate, lastUpdated, lastAnswered, nick, email, subject, content);
    }

    @Override
    public String getSecretPrefix() {
        return "P";
    }
    
}
