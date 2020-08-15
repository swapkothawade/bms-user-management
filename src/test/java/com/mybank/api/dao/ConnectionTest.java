package com.mybank.api.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest()
@EnableConfigurationProperties
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ConnectionTest {

  private UserDao dao;

  @Value("${spring.mongodb.uri}")
  private String mongoUri;
  @Autowired
  MongoClient mongoClient;

  @Value("${spring.mongodb.database}")
  String databaseName;

  @Before
  public void setup() throws IOException {
       this.dao = new UserDao(mongoClient, databaseName);
  }

  @Test
  public void testUserCount() {
    long expected =   1;
    Assert.assertEquals("Check your connection string", expected, 1);
  }

  @Test
  public void testConnectionFindsDatabase() {

    MongoClient mc = MongoClients.create(mongoUri);
    boolean found = false;
    for (String dbname : mc.listDatabaseNames()) {
      if (databaseName.equals(dbname)) {
        found = true;
        break;
      }
    }
    Assert.assertTrue(
        "We can connect to MongoDB, but couldn't find expected database. Check the restore step",
        found);
  }

  @Test
  public void testConnectionFindsCollections() {

    MongoClient mc = MongoClients.create(mongoUri);
    // needs to find at least these collections
    List<String> collectionNames = Arrays.asList("users");

    int found = 0;
    for (String colName : mc.getDatabase(databaseName).listCollectionNames()) {

      if (collectionNames.contains(colName)) {
        found++;
      }
    }

    Assert.assertEquals(
        "Could not find all expected collections. Check your restore step",
        found,
        collectionNames.size());
  }

  @Test
  public void testInsertOne(){
    MongoClient mongoClient = MongoClients.create(mongoUri);
    MongoDatabase db = mongoClient.getDatabase(databaseName);
    MongoCollection employeesCollection =
            db.getCollection("employees");

    Document doc1 = new Document("_id", 11)
            .append("name", "Edgar Martinez")
            .append("salary", "8.5M");
    Document doc2 = new Document("_id", 3)
            .append("name", "Alex Rodriguez")
            .append("salary", "18.3M");
    Document doc3 = new Document("_id", 24)
            .append("name", "Ken Griffey Jr.")
            .append("salary", "12.4M");
    Document doc4 = new Document("_id", 11)
            .append("name", "David Bell")
            .append("salary", "2.5M");
    Document doc5 = new Document("_id", 19)
            .append("name", "Jay Buhner")
            .append("salary", "5.1M");

    List<WriteModel> requests = Arrays.asList(
            new InsertOneModel<>(doc1),
            new InsertOneModel<>(doc2),
            new InsertOneModel<>(doc3),
            new InsertOneModel<>(doc4),
            new InsertOneModel<>(doc5));
    try {
      employeesCollection.bulkWrite(requests);
    } catch (Exception e) {
      System.out.println("ERROR: " + e.toString());
    }
  }
}
