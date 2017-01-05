package fatjar.implementations.db;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.result.DeleteResult;
import fatjar.DB;
import fatjar.Log;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

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
        MongoCollection<Document> collection = database.getCollection(tClass.getSimpleName());
        return collection.count();
    }

    @Override
    public <T> long count(Class<T> tClass, Query query) {
        MongoCollection<Document> collection = database.getCollection(tClass.getSimpleName());
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
        MongoModel mongoModel = (MongoModel) t;
        try {
            MongoCollection<Document> collection = database.getCollection(t.getClass().getSimpleName());
            collection.insertOne(mongoModel.toDocument());
        } catch (Exception e) {
            Log.error("could not insert object: " + t + " got exception: " + e, e);
        }
        return Optional.of(t);
    }

    @Override
    public <T> T update(T t) {
        MongoModel mongoModel = (MongoModel) t;
        Document document = mongoModel.toDocument();
        MongoCollection<Document> collection = database.getCollection(t.getClass().getSimpleName());
        BasicDBObject query = new BasicDBObject(MongoModel.OID, mongoModel.getObjectId());
        collection.findOneAndUpdate(query, new Document("$set", document), (new FindOneAndUpdateOptions()).upsert(true));
        return t;
    }


    @Override
    public <T> void delete(T t) {
        MongoModel mongoModel = (MongoModel) t;
        MongoCollection<Document> collection = database.getCollection(t.getClass().getSimpleName());
        BasicDBObject query = new BasicDBObject(MongoModel.OID, mongoModel.getObjectId());
        DeleteResult deleteResult = collection.deleteOne(query);
        if (deleteResult.getDeletedCount() != 1) {
            Log.error("error while deleting from db, total delete count: " + deleteResult.getDeletedCount());
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> typeClass) {
        List<T> list = new LinkedList<>();
        MongoCollection<Document> collection = database.getCollection(typeClass.getSimpleName());
        collection.find().forEach((Consumer<Document>) document -> {
            modelFromDocument(typeClass, list, document);
        });
        return list;
    }

    private <T> void modelFromDocument(Class<T> typeClass, List<T> list, Document document) {
        try {
            T t = typeClass.newInstance();
            if (MongoModel.class.isAssignableFrom(typeClass)) {
                MongoModel mongoModel = (MongoModel) t;
                mongoModel.fromDocument(document);
            } else {
                Log.error("class is not an instance of MongoModel, will not be able to set document to T object, class: " + typeClass);
            }
            list.add(t);
        } catch (InstantiationException | IllegalAccessException e) {
            Log.error("could not create mongo model of class: " + typeClass + " got exception: " + e, e);
        }
    }

    @Override
    public <T> T find(Class<T> typeClass, Object primary) {
        T t = null;
        MongoCollection<Document> collection = database.getCollection(typeClass.getSimpleName());
        BasicDBObject query = new BasicDBObject(MongoModel.OID, primary);
        FindIterable<Document> documents = collection.find(query);
        Document document = documents.first();
        try {
            t = typeClass.newInstance();
            if (MongoModel.class.isAssignableFrom(typeClass)) {
                MongoModel mongoModel = (MongoModel) t;
                mongoModel.fromDocument(document);
            } else {
                Log.error("class is not an instance of MongoModel, will not be able to set document to T object, class: " + typeClass);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            Log.error("could not create mongo model of class: " + typeClass + " got exception: " + e, e);
        }
        return t;
    }

    @Override
    public <T> List<T> find(Class<T> typeClass, Query query) {
        List<T> list = new LinkedList<>();
        MongoCollection<Document> collection = database.getCollection(typeClass.getSimpleName());
        Bson dbQuery = queryToBasicDBObject(query);
        collection.find(dbQuery).forEach((Consumer<Document>) document -> {
            modelFromDocument(typeClass, list, document);
        });
        return list;
    }

    public interface MongoModel extends ToDocument, FromDocument {
        String OID = "_id";

        ObjectId getObjectId();

        void setObjectId(ObjectId id);
    }

    public interface FromDocument {
        void fromDocument(Document document);
    }

    public interface ToDocument {
        Document toDocument();
    }

}
