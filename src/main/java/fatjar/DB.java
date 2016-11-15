package fatjar;

import fatjar.implementations.emdb.EntityDB;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

public interface DB {

    static Optional<DB> create() {
        DB db = null;
        try {
            db = new EntityDB();
        } catch (PersistenceException e) {
            Log.error("could not create database, exception: " + e);
        }
        return Optional.ofNullable(db);
    }

    <T> long count(Class<T> tClass);

    <T> long count(Class<T> tClass, Query query);

    <T> Optional<T> insert(T t);

    <T> T update(T t);

    <T> void delete(T t);

    <T> List<T> findAll(Class<T> typeClass);

    <T> T find(Class<T> typeClass, Object primary);

    <T> List<T> find(Class<T> typeClass, Query query);

    enum AndOr {
        AND, OR
    }

    enum Sign {
        LT, GT, EQ, LTE, GTE, NEQ
    }

    class Query {

        private Sign sign;
        private String field;
        private Object value;

        private AndOr andOr;
        private Query leftQuery;
        private Query rightQuery;

        private Query() {
        }

        private Query(String field, Sign sign, Object value) {
            this.field = field;
            this.sign = sign;
            this.value = value;
        }

        public static Query create(String field, Sign sign, Object value) {
            return new Query(field, sign, value);
        }

        public static Query create(String field, Object value) {
            return new Query(field, Sign.EQ, value);
        }

        public static Query and(Query left, Query right) {
            Query query = new Query();
            query.andOr = AndOr.AND;
            query.leftQuery = left;
            query.rightQuery = right;
            return query;
        }

        public static Query or(Query left, Query right) {
            Query query = new Query();
            query.andOr = AndOr.OR;
            query.leftQuery = left;
            query.rightQuery = right;
            return query;
        }

        public Sign getSign() {
            return sign;
        }

        public String getField() {
            return field;
        }

        public Object getValue() {
            return value;
        }

        public AndOr getAndOr() {
            return andOr;
        }

        public Query getLeftQuery() {
            return leftQuery;
        }

        public Query getRightQuery() {
            return rightQuery;
        }
    }

}
