package com.sjsu.rollbits.dao.interfaces.service;

import com.sjsu.rollbits.dao.interfaces.model.GroupUser;
import com.sjsu.rollbits.dao.interfaces.GroupUserDao;
import java.util.List;

public class GroupUserService {

	private  GroupUserDao groupuserDao;

	public GroupUserService() {
		groupuserDao = new GroupUserDao();
	}

	public  void persist(GroupUser entity) {
		groupuserDao.openCurrentSessionwithTransaction();
		groupuserDao.persist(entity);
		groupuserDao.closeCurrentSessionwithTransaction();
	}

	public  void update(GroupUser entity) {
		groupuserDao.openCurrentSessionwithTransaction();
		groupuserDao.update(entity);
		groupuserDao.closeCurrentSessionwithTransaction();
	}

	public  GroupUser findById(int id) {
		groupuserDao.openCurrentSession();
		GroupUser groupusers = groupuserDao.findById(id);
		groupuserDao.closeCurrentSession();
		return groupusers;
	}

	public  void delete(int id) {
		groupuserDao.openCurrentSessionwithTransaction();
		GroupUser groups = groupuserDao.findById(id);
		groupuserDao.delete(groups);
		groupuserDao.closeCurrentSessionwithTransaction();
	}

	public  List<GroupUser> findAll() {
		groupuserDao.openCurrentSession();
		List<GroupUser> groupusers = groupuserDao.findAll();
		groupuserDao.closeCurrentSession();
		return groupusers;
	}

	public  List<GroupUser> findGroupsForUser(String uname) {
		groupuserDao.openCurrentSession();
		List<GroupUser> groupusers = groupuserDao.findGroupsforUser(uname);
		groupuserDao.closeCurrentSession();
		return groupusers;
	}

	public  void deleteAll() {
		groupuserDao.openCurrentSessionwithTransaction();
		groupuserDao.deleteAll();
		groupuserDao.closeCurrentSessionwithTransaction();
	}

	public GroupUserDao GroupUserDao() {
		return groupuserDao;

	}
}
