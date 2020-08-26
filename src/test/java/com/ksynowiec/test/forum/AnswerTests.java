
package com.ksynowiec.test.forum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;

import com.ksynowiec.forum.dao.Secret;
import com.ksynowiec.forum.dto.AnswerDTO;
import com.ksynowiec.test.ForumApplicationTests;


public class AnswerTests {
    
    private TestRestTemplate rest = new TestRestTemplate();
    private ForumApplicationTests test;
    private String url;
    private int COUNT;
    private int MAX_LIMIT;
    private int RANGE;
    private SortedMap<Long, String> answers = new TreeMap(); // Id and Secret

    public AnswerTests(ForumApplicationTests test) {
        this.test = test;
        url = test.getUrl();
        COUNT = test.getCOUNT();
        MAX_LIMIT = test.getMAX_LIMIT();
        RANGE = test.getRANGE();
    }
    
    //-------------------- main test --------------------------------
    
    public void getSortedAnswers(Long postId) {
        System.out.println("----Running getAnswers");
        LocalDateTime maxTime = checkSortedAnswers(postId, 0, 0, COUNT, null, COUNT > MAX_LIMIT ? MAX_LIMIT : COUNT);
        if (maxTime == null) // No answers
            return;

        LocalDateTime time = null;

        // Automatic test
        int range = RANGE / 2; // before and after
        int offset = range + 1; // middle offset
        int expected = 2 * range + 1; //getPosts in one step
        int toCompare = COUNT; // how many left to compare
        
        while (toCompare > 0) {
            time = checkSortedAnswers(postId, offset, range, range, time, expected < toCompare ? expected : toCompare);
            // first in next iteration is the same that last in this iteration
            int nextRange = 2 * range;
            offset += nextRange;
            toCompare -= nextRange;
        }

        if (COUNT > MAX_LIMIT)
            assertTrue(time.isAfter(maxTime), "Last date should be the latest");
        else
            assertEquals(time, maxTime, "Dates should be the same");
        
        // additional random tests for COUNT==60
        if (COUNT == 60) {
            time = checkSortedAnswers(postId, 20, 5, 5, null, 11);
            time = checkSortedAnswers(postId, 32, 7, 3, time, 11);
            time = checkSortedAnswers(postId, 50, 15, 15, time, 26);
            checkSortedAnswers(postId, 40, 30, 20, null, 50);
            checkSortedAnswers(postId, 20, 30, 20, null, 40);
            checkSortedAnswers(postId, 30, 20, 40, null, 50);
            checkSortedAnswers(postId, 35, 30, 30, null, 50);
            checkSortedAnswers(postId, 50, 30, 10, null, 41);
        }
        System.out.println("getSortedAnswers works fine");
    }
    
    private LocalDateTime checkSortedAnswers(long postId, int offset, int before, int after, LocalDateTime minTime, int expectedSize) {
        StringBuilder buf = new StringBuilder(192);
        buf.append(url).append("getAnswers/").append(postId).append("?offset=").append(offset).append("&before=").append(before).append(
                "&after=").append(after);
        AnswerDTO[] answers = rest.getForObject(buf.toString(), AnswerDTO[].class);
        assertEquals(expectedSize, answers.length, "Sizes should be the same");
        if (answers.length == 0)
            return null;
        
        LocalDateTime checkTime = answers[0].getCreateDate();
        if (minTime == null)
            minTime = LocalDateTime.now().minusDays(1);
        else
            assertTrue(checkTime.isEqual(minTime), "Dates should be the same");
        for (int i = 1; i < answers.length; i++) {
            checkTime = answers[i].getCreateDate();
            assertTrue(checkTime.isAfter(minTime), "Every next date should be later");
            minTime = checkTime;
        }
        return minTime;
    }
    
    //-------------------- rest tests --------------------------------
    
    public void addAnswers(Map<Long, String> posts, boolean dto, int count) {
        System.out.print("----Adding answers");
        for (Long postId : posts.keySet()) {
            for (int suffix = 1; suffix <= count; suffix++) {
                String secret = addAnswer(dto, postId, suffix);
                Secret s = Secret.decodeSecret(secret);
                answers.put(s.getId(), secret);
            }
            count = 1; // add only one answer to rest posts for faster execute
        }
        System.out.println(" - OK: added " + answers.size() + " answers\n");
    }
    
    private String addAnswer(boolean dto, Long postId, int suffix) {
        if (dto) {
            AnswerDTO answer = new AnswerDTO(postId, "kyno", "k1.synowiec@gmail.com", "Answer_" + suffix);
            HttpEntity<AnswerDTO> entity = new HttpEntity<>(answer);
            return rest.postForObject(url + "answerPostDTO", entity, String.class);
        } else {
            StringBuilder buf = new StringBuilder(256);
            buf.append(url).append("answerPost/").append(postId).append("?nick=kyno&email=k1.synowiec@gmail.com&content=Answer_").append(
                    suffix);
            return rest.postForObject(buf.toString(), null, String.class);
        }
    }
    
    public void updateAnswers() {
        System.out.println("----Updating answers");
        for (Map.Entry<Long, String> secret : answers.entrySet())
            updateAnswer(secret.getValue(), secret.getKey().toString()); // set content as Id for later compare
    }
    
    private void updateAnswer(String secret, String content) {
        StringBuilder buf = new StringBuilder(64);
        buf.append(url).append("updatePost/").append(secret);
        rest.put(buf.toString(), content);
    }
    
    public void checkAllAnswers() {
        System.out.print("----Checking all posts");
        for (Long id : answers.keySet()) {
            StringBuilder buf = new StringBuilder(64);
            buf.append(url + "getAnswer/").append(id);
            AnswerDTO answer = rest.getForObject(buf.toString(), AnswerDTO.class);
            assertEquals(id.toString(), answer.getContent(), "Id and content should be the same after update");
        }
        System.out.println(" - OK: " + answers.size() + " answers checked\n");
    }

    public SortedMap<Long, String> getAnswers() {
        return answers;
    }

}
