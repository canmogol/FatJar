package sample;

import fatjar.implementations.db.MongoDB;
import org.bson.Document;
import org.bson.types.ObjectId;

public class MyMongoModel implements MongoDB.MongoModel {

    private static final String nameKey = "name";
    private static final String addressKey = "address";
    private static final String phoneKey = "phone";

    private final Document document;

    public MyMongoModel() {
        document = new Document();
    }

    public MyMongoModel(String name, String phone, String address) {
        this();
        document.put(nameKey, name);
        document.put(phoneKey, phone);
        document.put(addressKey, address);
    }

    public String getName() {
        return (String) document.get(nameKey);
    }

    public void setName(String name) {
        document.put(nameKey, name);
    }

    public String getPhone() {
        return (String) document.get(phoneKey);
    }

    public void setPhone(String phone) {
        document.put(phoneKey, phone);
    }

    public String getAddress() {
        return (String) document.get(addressKey);
    }

    public void setAddress(String address) {
        document.put(addressKey, address);
    }

    @Override
    public ObjectId getObjectId() {
        return document.getObjectId(MongoDB.MongoModel.OID);
    }

    @Override
    public void setObjectId(ObjectId id) {
        document.put(MongoDB.MongoModel.OID, id);
    }

    @Override
    public Document toDocument() {
        return document;
    }

    @Override
    public void fromDocument(Document document) {
        this.document.clear();
        document.entrySet().stream().forEach(entrySet -> {
            this.document.put(entrySet.getKey(), entrySet.getValue());
        });
    }

}
