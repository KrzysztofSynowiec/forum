
package com.ksynowiec.forum.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.ksynowiec.forum.dto.AnswerDTO;

import lombok.Getter;
import lombok.Setter;


/**
 * Entity for answers
 */

@Entity
@Table(indexes = {@Index(name = "answer_post_id_i", columnList = "post_id"),
        @Index(name = "answer_create_date_i", columnList = "create_date ASC")})
public class Answer extends AbstractPost {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answerSequence")
    @SequenceGenerator(name = "answerSequence", sequenceName = "answer_s", allocationSize = 50)
    @Getter
    @Setter
    @Id
    private Long id;
    
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "answer_post_id_fk"), nullable = true, insertable = true, updatable = true)
    private Post post;

    public Answer(Post post, String nick, String email, String content) {
        super(nick, email, content);
        this.post = post;
    }
    
    public Answer() {}

    public AnswerDTO getAnswerDTO() {
        return new AnswerDTO(id, post.getId(), createDate, lastUpdated, nick, email, content);
    }
    
    @Override
    public String getSecretPrefix() {
        return "A";
    }

}
