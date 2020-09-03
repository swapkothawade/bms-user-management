
package com.mybank.api.dao;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.HashSet;
import java.util.Set;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mybank.api.auth.User;
import com.mybank.api.domain.UserProfile;
import com.mybank.api.exception.CustomException;

@Component
@RefreshScope
public class UserProfileDao extends AbstractBMSDao {

    protected MongoClient mongoClient;
    @Value("${spring.mongodb.uri}")
    private String connectionString;
    private final Logger log;
    private final MongoCollection<UserProfile> userProfileCollection;
    private final MongoCollection<User> loginCollection;



    @Autowired
    public UserProfileDao(MongoClient mongoClient, @Value("${spring.mongodb.database}")
            String databaseName) {
        super(mongoClient, databaseName);

            CodecRegistry pojoCodecRegistry =
                    fromRegistries(
                            MongoClientSettings.getDefaultCodecRegistry(),
                            fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        userProfileCollection = db.getCollection("user_profile", UserProfile.class).withCodecRegistry(pojoCodecRegistry);
        loginCollection = db.getCollection("user_login_detail", User.class).withCodecRegistry(pojoCodecRegistry);
            log = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Add User in UserProfile collection. Throws exception in case of any issue while writing document.
     * @param userProfile
     * @return
     */
    public boolean addUser(UserProfile userProfile) {
    	log.info("Adding Profile foe User {} " , userProfile.getFirstName());
    	try {
            userProfileCollection.insertOne(userProfile);
            loginCollection.insertOne(getUser(userProfile));
            log.info("Profile created for  User {} " , userProfile.getFirstName());
            return true;
        }catch(MongoWriteException exception){
            log.error(exception.getMessage());   
        	throw new CustomException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    	
    }

    private User getUser(UserProfile userProfile) {
        User user = new User();
        user.setEmail(userProfile.getEmail());
        user.setPassword(userProfile.getPassword());
        user.setName(userProfile.getFirstName());
        user.setLastname(userProfile.getLastName());
        user.setPassword(userProfile.getPassword());
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRole(roles);

        return  user;
    }

    public UserProfile getUserProfileByEmail(String email){
        Bson filter = eq("email",email);
        try{
            FindIterable<UserProfile> itr = userProfileCollection.find(filter);
            return itr.iterator().hasNext() ? itr.first() : null;
        }catch(MongoException exception){
            throw new CustomException(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public boolean updateProfile(UserProfile userProfile) {
        Bson filter = eq("email",userProfile.getEmail());
        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", userProfile);
        try{
            userProfileCollection.updateOne(filter,updateObject);
            return true;
        }catch(MongoException exception){
            throw new CustomException(exception.getMessage(),HttpStatus.NOT_MODIFIED);
        }


    }
}

