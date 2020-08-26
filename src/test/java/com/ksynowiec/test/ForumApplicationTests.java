
package com.ksynowiec.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import com.ksynowiec.forum.ForumApplication;
import com.ksynowiec.test.forum.AnswerTests;
import com.ksynowiec.test.forum.PostTests;

import lombok.Getter;


@SpringBootTest(classes = ForumApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ForumApplicationTests {
    
    @Getter
    @Value("${forum.max_limit}")
    private int MAX_LIMIT;
    @Getter
    @Value("${forum.test_count}")
    private int COUNT;
    @Getter
    @Value("${forum.test_range}")
    private int RANGE;
    @LocalServerPort
    private int port;
    @Getter
    
    private String url;
    private TestRestTemplate rest = new TestRestTemplate();

    public static void main(String[] args) {
        new ForumApplicationTests().test();
    }

    public ForumApplicationTests() {
        if (MAX_LIMIT == 0)
            MAX_LIMIT = 50;
        if (port == 0)
            port = 8888;
        if (COUNT == 0)
            COUNT = 60;
        if (RANGE == 0)
            RANGE = 10;
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
        }
        
    }

    @Test
    public void test() {
        url = "http://localhost:" + port + "/api/forum/";
        AnswerTests answerTests = new AnswerTests(this);
        PostTests postTests = new PostTests(this);
        LocalDateTime time = postTests.getTopSubjects();
        assertTrue(time == null, "Database must be empty");
        
        postTests.addPosts(false, COUNT);
        answerTests.addAnswers(postTests.getPosts(), false, COUNT);
        
        postTests.getTopSubjects();
        answerTests.getSortedAnswers(postTests.getPosts().firstKey());
        
        postTests.addPosts(true, COUNT);
        answerTests.addAnswers(postTests.getPosts(), true, COUNT);
        
        postTests.updatePosts();
        answerTests.updateAnswers();
        
        postTests.checkAllPosts();
        answerTests.checkAllAnswers();
        postTests.deleteAllCreatedPosts();

        System.out.println("All tests passed.");
    }
}
