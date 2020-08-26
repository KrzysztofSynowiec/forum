
package com.ksynowiec.forum.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;


/**
 * Post DTO with content.
 */

public class AnswerDTO extends AbstractDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Getter
    @Setter
    private Long postId;
    @Getter
    @Setter
    private String content;
    
    public AnswerDTO() {
        super();
    }

    public AnswerDTO(Long id, Long postId, LocalDateTime createDate, LocalDateTime lastUpdated, String nick, String email, String content) {
        super(id, createDate, lastUpdated, nick, email);
        this.postId = postId;
        this.content = content;
    }
    
    // For testing
    public AnswerDTO(Long postId, String nick, String email, String content) {
        super(nick, email);
        this.postId = postId;
        this.content = content;
    }

}
