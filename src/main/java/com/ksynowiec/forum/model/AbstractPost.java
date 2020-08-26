
package com.ksynowiec.forum.model;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.domain.Persistable;

import com.ksynowiec.forum.dao.ForumDao;
import com.ksynowiec.forum.dao.ForumException;
import com.ksynowiec.forum.dao.Secret;

import lombok.Getter;
import lombok.Setter;


/**
 * Abstract Entity
 */

@MappedSuperclass
public abstract class AbstractPost implements Persistable<Long> {

    public AbstractPost() {}

    @Getter
    @Setter
    @Column(updatable = false, nullable = false)
    protected Integer secret;

    @Getter
    @Setter
    @Column(name = "create_date", updatable = false, nullable = false)
    protected LocalDateTime createDate;

    @Getter
    @Setter
    @Column(name = "last_updated")
    protected LocalDateTime lastUpdated;

    @Getter
    @Setter
    @Column(length = ForumDao.maxNickLength, updatable = false, nullable = false)
    protected String nick;

    @Getter
    @Setter
    @Column(length = ForumDao.maxEmailLength, updatable = false, nullable = false)
    protected String email;

    @Getter
    @Setter
    @Column(length = ForumDao.maxSubjectLength, updatable = false, nullable = true)
    protected String subject;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    protected String content;
    
    public AbstractPost(String nick, String email, String content) {
        if (isValidParam(nick, ForumDao.maxNickLength))
            this.nick = nick;
        else
            throw new ForumException("Nick can't be empty or longer than " + ForumDao.maxNickLength);
        if (isValidParam(email, ForumDao.maxEmailLength))
            this.email = email;
        else
            throw new ForumException("Email can't be empty or longer than " + ForumDao.maxEmailLength);
        if (isValidParam(content, Integer.MAX_VALUE))
            this.content = content;
        else
            throw new ForumException("Content can't be empty");
        
        createDate = LocalDateTime.now();
        lastUpdated = createDate;
        secret = Secret.generateSecret();
    }
    
    protected static boolean isValidParam(String value, int maxLength) {
        if (value != null) {
            int len = value.length();
            return len > 0 && len <= maxLength;
        }
        return false;
    }
    
    public void setUpdated() {
        lastUpdated = LocalDateTime.now();
    }

    public boolean checkSecret(Integer secret) {
        return this.secret.equals(secret);
    }

    public abstract String getSecretPrefix();

    @Override
    public boolean isNew() {
        return getId() == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AbstractPost))
            return false;
        AbstractPost that = (AbstractPost) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public String toString() {
        return String.format("Record of type %s with id: %s", this.getClass().getName(), getId());
    }
}
