package com.mybank.api.dao;

import com.mongodb.client.MongoClient;
import com.mybank.api.config.MongoDBConfiguration;
import com.mybank.api.domain.AccountType;
import com.mybank.api.domain.Address;
import com.mybank.api.domain.UserProfile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest(classes = {MongoDBConfiguration.class})
@EnableConfigurationProperties
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class UserProfileDaoTest {
    @Autowired
    MongoClient mongoClient;

    @Value("${spring.mongodb.database}")
    String databaseName;

    private UserProfileDao userProfileDao;
    private UserDao userDao;
    @Before
    public void setup() {
        this.userDao = new UserDao(mongoClient,databaseName);
        this.userProfileDao = new UserProfileDao(mongoClient, databaseName);
    }

    @Test
    public void testAddUser(){
        boolean result = userProfileDao.addUser(getTempUserProfile());

        assertTrue(result);
    }


    @Test
    public void testgetUserProfile(){
        String email = "swapnil.kothawade@gmail.com";
        UserProfile profile = userProfileDao.getUserProfileByEmail(email);
        assertNotNull(profile);
        assertEquals("Swapnil",profile.getFirstName());
        assertEquals("USA",profile.getUserAddress().getCountry());
    }

    private UserProfile getTempUserProfile() {
        UserProfile profile = new UserProfile();
        profile.setFirstName("Swapnil");
        profile.setLastName("Kothawade");
        profile.setEmail("swapnil.kothawade@gmail.com");
        profile.setPassword("Password123");
        profile.setPan("1234567890");
       Set<String> roles = new HashSet<>();
        roles.add("USER");
        profile.setRoles(roles);
        profile.setUserAddress(getUserAddress());
        profile.setAccountType(AccountType.SAVING);
        profile.setContactNo("2023456789");
        profile.setDob(LocalDate.of(1982,11,26));
        return profile;
    }

    private Address getUserAddress() {
        Address address = new Address();
        address.setStreet("169 Manhattan Avenue");
        address.setCity("Jersey City");
        address.setState("New Jersey");
        address.setCountry("USA");
        address.setZip(07307);
        return address;
    }




}
