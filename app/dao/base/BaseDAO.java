package dao.base;

import java.util.List;

import org.bson.types.ObjectId;

import system.Singletons;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * Generic DAO implementation using Morphia
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class BaseDAO<T> {
	
	protected Class<T> entityClazz;
	protected static Datastore ds = Singletons.datastore;

	public BaseDAO(Class <T> entityClazz) {
		this.entityClazz = entityClazz;
	}
	
	
    /**
     * The underlying collection for this DAO
     */
    public DBCollection getCollection() {
        return ds.getCollection(entityClazz);
    }

    public void drop() {
    	ds.getCollection(entityClazz).drop();
    	ds.ensureCaps();
    	ds.ensureIndexes();
    }

    public Query<T> createQuery() {
        return ds.createQuery(entityClazz);
    }

    public UpdateOperations<T> createUpdateOperations() {
        return ds.createUpdateOperations(entityClazz);
    }

    public Class<T> getEntityClass() {
        return entityClazz;
    }

    public Key<T> save(T entity) {
        return ds.save(entity);
    }
    
    public Iterable<Key<T>> saveCollection(List<T> entities) {
    	return ds.save(entities);
    }

    public Key<T> save(T entity, WriteConcern wc) {
        return ds.save(entity, wc);
    }

    public UpdateResults<T> updateFirst(Query<T> q, UpdateOperations<T> ops) {
        return ds.updateFirst(q, ops);
    }

    public UpdateResults<T> update(Query<T> q, UpdateOperations<T> ops) {
        return ds.update(q, ops);
    }

    public WriteResult delete(T entity) {
        return ds.delete(entity);
    }

    public WriteResult delete(T entity, WriteConcern wc) {
        return ds.delete(entity, wc);
    }

    public WriteResult deleteById(ObjectId id) {
        return ds.delete(entityClazz, id);
    }

    
    public WriteResult deleteByQuery(Query<T> q) {
        return ds.delete(q);
    }

    public T get(ObjectId id) {
        return ds.get(entityClazz, id);
    }

    public boolean exists(String key, Object value) {
        return exists(ds.find(entityClazz, key, value));
    }

    public boolean exists(Query<T> q) {
        return ds.getCount(q) > 0;
    }

    public long count() {
        return ds.getCount(entityClazz);
    }

    public long count(String key, Object value) {
        return count(ds.find(entityClazz, key, value));
    }

    public long count(Query<T> q) {
        return ds.getCount(q);
    }

    public T findOne(String key, Object value) {
        return ds.find(entityClazz, key, value).get();
    }

    public T findOne(Query<T> q) {
        return q.get();
    }

    public QueryResults<T> find() {
        return createQuery();
    }
    
    public T findById(ObjectId objectId) {
    	return ds.get(entityClazz,objectId);
    }

    public QueryResults<T> find(Query<T> q) {
        return q;
    }
    
    public List<T> listAll() {
    	return ds.find(entityClazz).asList();
    }
}
