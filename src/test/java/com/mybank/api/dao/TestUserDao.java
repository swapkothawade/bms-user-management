package com.mybank.api.dao;

import com.mongodb.client.MongoClient;
import com.mybank.api.config.MongoDBConfiguration;
import com.mybank.api.auth.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@SpringBootTest(classes = {MongoDBConfiguration.class})
@EnableConfigurationProperties
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)

public class TestUserDao {

    private UserDao dao;

    private static String email = "swapnil.kothawade@gmail.com";

    @Autowired
    MongoClient mongoClient;

    @Value("${spring.mongodb.database}")
    String databaseName;

    @Before
    public void setup() {

        this.dao = new UserDao(mongoClient, databaseName);
    }
    @Test
    public void testUserExist(){

        User user = dao.getUser(email);
        assertNotNull(user);
        assertEquals("User email should match", "Kothawade",user.getLastname());
        String roles = user.getRole().stream().collect(Collectors.joining(","));
        System.out.println(roles);
    }
}
