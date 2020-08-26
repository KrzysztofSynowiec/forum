
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
import com.ksynowiec.forum.dto.PostDTO;
import com.ksynowiec.forum.dto.SubjectDTO;
import com.ksynowiec.test.ForumApplicationTests;


public class PostTests {

    private TestRestTemplate rest = new TestRestTemplate();
    private ForumApplicationTests test;
    private String url;
    private int COUNT;
    private int MAX_LIMIT;
    private int RANGE;
    
    private SortedMap<Long, String> posts = new TreeMap(); // Id and Secret

    public PostTests(ForumApplicationTests test) {
        this.test = test;
        url = test.getUrl();
        COUNT = test.getCOUNT();
        MAX_LIMIT = test.getMAX_LIMIT();
        RANGE = test.getRANGE();
    }

    //-------------------- main test --------------------------------
    public LocalDateTime getTopSubjects() {
        System.out.print("----Running getTopSubjects");
        LocalDateTime minTime = checkTopSubjects(0, COUNT, null, COUNT > MAX_LIMIT ? MAX_LIMIT : COUNT); // get all or MAX_LIMIT posts
        if (minTime == null)
            return null;
        LocalDateTime time = null;
        int start = 0;
        int toCompare = COUNT;
        while (toCompare > 0) {
            time = checkTopSubjects(start, RANGE, time, RANGE < toCompare ? RANGE : toCompare);
            start += RANGE - 1; // need to compare: last element in this iteration will be first in nest iterqation
            toCompare -= RANGE; // rest of posts
        }
        if (COUNT > MAX_LIMIT)
            assertTrue(time.isBefore(minTime), "Last date should be the earliest");
        else
            assertEquals(time, minTime, "Dates should be the same");
        System.out.println(" - OK\n");
        return minTime;
    }

    private LocalDateTime checkTopSubjects(int offset, int limit, LocalDateTime maxTime, int expectedSize) {
        StringBuilder buf = new StringBuilder(128);
        buf.append(url).append("getTopSubjects?offset=").append(offset).append("&limit=").append(limit);
        SubjectDTO[] posts = rest.getForObject(buf.toString(), SubjectDTO[].class);
        if (posts.length == 0)
            return null;
        assertEquals(expectedSize, posts.length, "Sizes should be the same");

        LocalDateTime checkTime = posts[0].getLastAnswered();
        if (maxTime == null)
            maxTime = LocalDateTime.now();
        else
            assertTrue(checkTime.isEqual(maxTime), "Dates should be the same");
        
        for (int i = 1; i < posts.length; i++) {
            checkTime = posts[i].getLastAnswered();
            assertTrue(checkTime.isBefore(maxTime), "Every next date should be earlier");
            maxTime = checkTime;
        }
        return maxTime;
    }

    //-------------------- rest tests --------------------------------
    public void addPosts(boolean usingDTO, int count) {
        System.out.print("----Adding posts");
        for (int suffix = 1; suffix <= count; suffix++) {
            String secret = addPost(usingDTO, suffix);
            Secret s = Secret.decodeSecret(secret);
            posts.put(s.getId(), secret);
        }
        System.out.println(" - OK: " + posts.size() + " posts added\n");
    }

    private String addPost(boolean usingDTO, int suffix) {
        if (usingDTO) {
            PostDTO post = new PostDTO("kyno", "k1.synowiec@gmail.com", "Title_" + suffix, "Description_" + suffix);
            HttpEntity<PostDTO> entity = new HttpEntity<>(post);
            return rest.postForObject(url + "addPostDTO", entity, String.class);
        } else {
            StringBuilder buf = new StringBuilder(256);
            buf.append(url).append("addPost?nick=kyno&email=k1.synowiec@gmail.com&subject=Title_").append(suffix).append(
                    "&content=Description_").append(suffix);
            return rest.postForObject(buf.toString(), null, String.class);
        }
    }
    
    public void updatePosts() {
        System.out.println("----Updating posts");
        for (Map.Entry<Long, String> secret : posts.entrySet())
            updatePost(secret.getValue(), secret.getKey().toString()); // set content as Id for later compare
    }

    private void updatePost(String secret, String content) {
        StringBuilder buf = new StringBuilder(64);
        buf.append(url).append("updatePost/").append(secret);
        rest.put(buf.toString(), content);
    }

    public void checkAllPosts() {
        System.out.print("----Checking all posts");
        for (Long id : posts.keySet()) {
            StringBuilder buf = new StringBuilder(64);
            buf.append(url + "getPost/").append(id);
            PostDTO post = rest.getForObject(buf.toString(), PostDTO.class);
            assertEquals(id.toString(), post.getContent(), "Id and content should be the same after update");
        }
        System.out.println(" - OK: " + posts.size() + " posts checked\n");
    }
    
    public void deleteAllCreatedPosts() {
        System.out.print("----Deleting posts");
        for (Map.Entry<Long, String> secret : posts.entrySet()) {
            rest.delete(url + "deletePost/" + secret.getValue());
        }
        System.out.println(" - OK: Deleted " + posts.size() + "posts\n");
    }

    public SortedMap<Long, String> getPosts() {
        return posts;
    }

}
