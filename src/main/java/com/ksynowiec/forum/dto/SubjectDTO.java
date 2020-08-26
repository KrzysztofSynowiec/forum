
package com.ksynowiec.forum.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;


/**
 * Post DTO without content, result of getTopSubjects method
 */
public class SubjectDTO extends AbstractDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String nick;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String subject;
    @Getter
    @Setter
    private LocalDateTime createDate;
    @Getter
    @Setter
    private LocalDateTime lastUpdated;
    @Getter
    @Setter
    private LocalDateTime lastAnswered;
    
    public SubjectDTO() {
        super();
    }
    
    public SubjectDTO(Long id, LocalDateTime createDate, LocalDateTime lastUpdated, LocalDateTime lastAnswered, String nick, String email,
            String subject) {
        this.id = id;
        this.nick = nick;
        this.email = email;
        this.subject = subject;
        this.createDate = createDate;
        this.lastUpdated = lastUpdated;
        this.lastAnswered = lastAnswered;
    }
    
}
