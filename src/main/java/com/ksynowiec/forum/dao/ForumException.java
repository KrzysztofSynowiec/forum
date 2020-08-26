
package com.ksynowiec.forum.dao;

/**
 * Forum Exception to catch in Controller
 */

public class ForumException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ForumException(String message) {
        super(message);
    }
}
