
package com.ksynowiec.forum.dao;

import java.util.Random;

import com.ksynowiec.forum.model.AbstractPost;


public class Secret {
    
    /**
     * Class to generate and decode secret
     */
    private final static int SECRET_LOWER = 100000000;
    private final static int SECRET_UPPER = 999999999;
    private final static int SECRET_LENGHT = 10; // lenght with prefix
    
    //Fields used only in this package, final Secret  is: prefix+secret+id
    final Long id;
    final Integer secret;
    final char prefix;

    private Secret(char prefix, Long id, Integer secret) {
        this.prefix = prefix;
        this.id = id;
        this.secret = secret;
    }

    public static Integer generateSecret() {
        return (int) (new Random().nextDouble() * (SECRET_UPPER - SECRET_LOWER)) + SECRET_LOWER;
    }
    
    public static Secret decodeSecret(String secret) {
        if (secret == null || secret.length() <= SECRET_LENGHT)
            throw new IllegalArgumentException("Wrong secret");
        try {
            char prefix = secret.charAt(0);
            Integer realSecret = Integer.valueOf(secret.substring(1, SECRET_LENGHT));
            Long id = Long.valueOf(secret.substring(SECRET_LENGHT));
            return new Secret(prefix, id, realSecret);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Wrong secret");
        }
    }

    public static String encodeSecret(AbstractPost post) {
        return new StringBuilder(128).append(post.getSecretPrefix()).append(post.getSecret()).append(post.getId()).toString();
    }

    public String getEntityName() {
        return prefix == 'P' ? "Post" : "Answer";
    }
    
    @Override
    public String toString() {
        return new StringBuilder(128).append(prefix).append(secret).append(id).toString();
    }
    
    // for tests
    public Long getId() {
        return id;
    }

}
