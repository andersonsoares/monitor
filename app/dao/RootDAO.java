package dao;

import models.Root;

import org.bson.types.ObjectId;

import dao.base.BaseDAO;

public class RootDAO extends BaseDAO<Root> {

	public RootDAO() {
		super(Root.class);
	}

	
	public boolean setRemoved(ObjectId rootId) {
		
		Root root = this.findById(rootId);
		root.setRemoved((!root.isRemoved()));
		this.save(root);
		return root.isRemoved();
	}
}
