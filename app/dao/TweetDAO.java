package dao;

import models.Tweet;
import dao.base.BaseDAO;

public class TweetDAO extends BaseDAO<Tweet> {

	public TweetDAO() {
		super(Tweet.class);
	}

}
