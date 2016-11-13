package fatjar.implementations.emdb;


import fatjar.DB;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public class EntityDB implements DB {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public EntityDB() {
        entityManagerFactory = Persistence.createEntityManagerFactory("DefaultPersistenceUnit");
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    private EntityManager getEntityManager() {
        if (entityManager == null) {
            entityManager = getEntityManagerFactory().createEntityManager();
        }
        return entityManager;
    }

    @Override
    public <T> long count(Class<T> tClass) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root root = criteriaQuery.from(tClass);
        criteriaQuery.select(builder.count(root));
        Long count = Long.valueOf(getEntityManager().createQuery(criteriaQuery).getSingleResult().toString());
        return count;
    }


    @Override
    public <T> long count(Class<T> tClass, Query query) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<T> root = criteriaQuery.from(tClass);
        criteriaQuery.where(queryToPredicate(builder, root, query));
        criteriaQuery.select(builder.count(root));
        Long count = Long.valueOf(getEntityManager().createQuery(criteriaQuery).getSingleResult().toString());
        return count;
    }

    @Override
    public <T> Optional<T> insert(T t) {
        T inserted = null;
        try {
            beginTransaction();
            getEntityManager().persist(t);
            inserted = t;
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            if (e instanceof IllegalArgumentException) {
                inserted = update(t);
            }
        }
        return Optional.ofNullable(inserted);
    }

    @Override
    public <T> T update(T t) {
        T updated = null;
        try {
            beginTransaction();
            updated = getEntityManager().merge(t);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
        }
        return updated;
    }

    @Override
    public <T> void delete(T t) {
        try {
            beginTransaction();
            if (!getEntityManager().contains(t)) {
                getEntityManager().remove(getEntityManager().merge(t));
            } else {
                getEntityManager().remove(t);
            }
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> typeClass) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(typeClass);
        Root<T> root = criteriaQuery.from(typeClass);
        criteriaQuery.select(root);
        List<T> list = getEntityManager().createQuery(criteriaQuery).getResultList();
        return list;
    }

    @Override
    public <T> T find(Class<T> typeClass, Object primary) {
        return getEntityManager().find(typeClass, primary);
    }

    private Predicate queryToPredicate(CriteriaBuilder builder, Root<?> root, Query query) {
        Predicate result = null;
        if (query.getLeftQuery() != null && query.getRightQuery() != null) {
            Predicate left = queryToPredicate(builder, root, query.getLeftQuery());
            Predicate right = queryToPredicate(builder, root, query.getRightQuery());
            Predicate[] predicates = new Predicate[]{left, right};
            if (AndOr.AND.equals(query.getAndOr())) {
                result = builder.and(predicates);
            } else {
                result = builder.or(predicates);
            }
        } else {
            switch (query.getSign()) {
                case EQ:
                    result = builder.equal(root.get(query.getField()), query.getValue());
                    break;
                case LT:
                    result = builder.lessThan(root.get(query.getField()), Double.valueOf(query.getValue().toString()));
                    break;
                case LTE:
                    result = builder.lessThanOrEqualTo(root.get(query.getField()), Double.valueOf(query.getValue().toString()));
                    break;
                case GT:
                    result = builder.greaterThan(root.get(query.getField()), Double.valueOf(query.getValue().toString()));
                    break;
                case GTE:
                    result = builder.greaterThanOrEqualTo(root.get(query.getField()), Double.valueOf(query.getValue().toString()));
                    break;
                case NEQ:
                    result = builder.notEqual(root.get(query.getField()), Double.valueOf(query.getValue().toString()));
                    break;
            }
        }
        return result;
    }

    private void beginTransaction() {
        if (!getEntityManager().getTransaction().isActive()) {
            getEntityManager().getTransaction().begin();
        }
    }

    private void commitTransaction() {
        if (getEntityManager().getTransaction().isActive()) {
            getEntityManager().getTransaction().commit();
        }
    }

    private void rollbackTransaction() {
        if (getEntityManager().getTransaction().isActive()) {
            getEntityManager().getTransaction().rollback();
        }
    }

}
