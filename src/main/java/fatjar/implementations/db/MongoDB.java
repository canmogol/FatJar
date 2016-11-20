package fatjar.implementations.db;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import fatjar.DB;
import fatjar.Log;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

public class MongoDB implements DB {

    private static final String MONGODB_PROPERTIES = "mongodb.properties";
    private final MongoDatabase database;
    private final MongoClient mongoClient;

    public MongoDB(String name) throws IOException {
        Properties mongodbProperties = new Properties();
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(MONGODB_PROPERTIES);
        mongodbProperties.load(input);
        input.close();
        String connectionString = mongodbProperties.getProperty("connectionString");
        String databaseName = mongodbProperties.getProperty("databaseName");
        MongoClientURI mongoClientURI = new MongoClientURI(connectionString);
        mongoClient = new MongoClient(mongoClientURI);
        database = mongoClient.getDatabase(databaseName);
    }

    @Override
    public <T> long count(Class<T> tClass) {
        MongoCollection<Document> collection = database.getCollection(tClass.getName());
        return collection.count();
    }

    @Override
    public <T> long count(Class<T> tClass, Query query) {
        MongoCollection<Document> collection = database.getCollection(tClass.getName());
        Bson dbQuery = queryToBasicDBObject(query);
        return collection.count(dbQuery);
    }

    private Bson queryToBasicDBObject(Query query) {
        Bson root;
        if (query.getLeftQuery() != null && query.getRightQuery() != null) {
            if (AndOr.AND.equals(query.getAndOr())) {
                root = Filters.and(
                        Filters.eq(query.getLeftQuery().getField(), query.getLeftQuery().getValue()),
                        Filters.eq(query.getRightQuery().getField(), query.getRightQuery().getValue())
                );
            } else {
                root = Filters.or(
                        Filters.eq(query.getLeftQuery().getField(), query.getLeftQuery().getValue()),
                        Filters.eq(query.getRightQuery().getField(), query.getRightQuery().getValue())
                );
            }
        } else {
            switch (query.getSign()) {
                case LT:
                    root = Filters.lt(query.getField(), query.getValue());
                    break;
                case LTE:
                    root = Filters.lte(query.getField(), query.getValue());
                    break;
                case GT:
                    root = Filters.gt(query.getField(), query.getValue());
                    break;
                case GTE:
                    root = Filters.gte(query.getField(), query.getValue());
                    break;
                case NEQ:
                    root = Filters.ne(query.getField(), query.getValue());
                    break;
                default: // EQ
                    root = Filters.eq(query.getField(), query.getValue());
                    break;
            }
        }
        return root;
    }

    @Override
    public <T> Optional<T> insert(T t) {
        MongoCollection<Document> collection = database.getCollection(t.getClass().getName());
        collection.insertOne((Document) t);
        return Optional.of(t);
    }

    @Override
    public <T> T update(T t) {
        Document document = (Document) t;
        MongoCollection<Document> collection = database.getCollection(t.getClass().getName());
        return (T) collection.findOneAndUpdate(new BasicDBObject("_id", document.get("_id")), document);
    }


    @Override
    public <T> void delete(T t) {
        Document document = (Document) t;
        MongoCollection<Document> collection = database.getCollection(t.getClass().getName());
        DeleteResult deleteResult = collection.deleteOne(new BasicDBObject("_id", document.get("_id")));
        if (deleteResult.getDeletedCount() != 1) {
            Log.error("error while deleting from db, total delete count: " + deleteResult.getDeletedCount());
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> typeClass) {
        List<T> list = new LinkedList<>();
        MongoCollection<Document> collection = database.getCollection(typeClass.getName());
        collection.find().forEach((Consumer<Document>) document -> {
            list.add((T) document);
        });
        return list;
    }

    @Override
    public <T> T find(Class<T> typeClass, Object primary) {
        MongoCollection<Document> collection = database.getCollection(typeClass.getName());
        return (T) collection.find(new BasicDBObject("_id", primary));
    }

    @Override
    public <T> List<T> find(Class<T> typeClass, Query query) {
        List<T> list = new LinkedList<>();
        MongoCollection<Document> collection = database.getCollection(typeClass.getName());
        Bson dbQuery = queryToBasicDBObject(query);
        collection.find(dbQuery).forEach((Consumer<Document>) document -> {
            list.add((T) document);
        });
        return list;
    }
}
