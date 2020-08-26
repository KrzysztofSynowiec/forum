
package com.ksynowiec.forum.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;


/**
 * Post DTO with content.
 */

public class PostDTO extends AbstractDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    LocalDateTime lastAnswered;

    @Getter
    @Setter
    private String subject;

    @Getter
    @Setter
    private String content;

    public PostDTO() {
        super();
    }

    public PostDTO(Long id, LocalDateTime createDate, LocalDateTime lastUpdated, LocalDateTime lastAnswered, String nick, String email,
            String subject, String content) {
        super(id, createDate, lastUpdated, nick, email);
        this.subject = subject;
        this.content = content;
        this.lastAnswered = lastAnswered;
    }

    // For testing
    public PostDTO(String nick, String email, String subject, String content) {
        super(nick, email);
        this.subject = subject;
        this.content = content;
    }
}
