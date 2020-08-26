
package com.ksynowiec.forum.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;


/**
 * Abstract DTO.
 */

public abstract class AbstractDTO {

    public static int maxNickLength = 64;
    public static final int maxEmailLength = 64;
    public static final int maxSubjectLength = 256;

    @Getter
    @Setter
    protected Long id;
    @Getter
    @Setter
    protected LocalDateTime createDate;
    @Getter
    @Setter
    protected LocalDateTime lastUpdated;
    @Getter
    @Setter
    protected String nick;
    @Getter
    @Setter
    protected String email;

    public AbstractDTO() {}

    public AbstractDTO(Long id, LocalDateTime createDate, LocalDateTime lastUpdated, String nick, String email) {
        this.id = id;
        this.createDate = createDate;
        this.lastUpdated = lastUpdated;
        this.nick = nick;
        this.email = email;
    }
    
    // For testing
    public AbstractDTO(String nick, String email) {
        this.nick = nick;
        this.email = email;
    }

}
