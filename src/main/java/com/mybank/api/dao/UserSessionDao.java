package com.mybank.api.dao;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mybank.api.auth.UserSessionToken;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Component
@RefreshScope
public class UserSessionDao extends AbstractBMSDao {

    protected MongoClient mongoClient;
    @Value("${spring.mongodb.uri}")
    private String connectionString;
    private final Logger log;
    private final MongoCollection<UserSessionToken> usersSessionCollection;

    @Autowired
    public UserSessionDao(
            MongoClient mongoClient, @Value("${spring.mongodb.database}") String databaseName) {
        super(mongoClient, databaseName);
        CodecRegistry pojoCodecRegistry =
                fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        usersSessionCollection = db.getCollection("user_session", UserSessionToken.class).withCodecRegistry(pojoCodecRegistry);
        log = LoggerFactory.getLogger(this.getClass());

    }

    public boolean saveUserSession(String username, String token){
        UserSessionToken userSession = new UserSessionToken(username,token);
        usersSessionCollection.insertOne(userSession);
        return true;
    }

    public boolean isUserSessionValid(String username,String token){
        FindIterable<UserSessionToken> userSession = usersSessionCollection.find(and(eq("username",username),eq("token",token)));
        if(userSession.iterator().hasNext())
            return true;
        return false;
    }


    public boolean deleteUserSession(String token){
        DeleteResult result = usersSessionCollection.deleteOne(eq("token",token));
       return  result.getDeletedCount() > 0 ?  true :  false;
    }
}
