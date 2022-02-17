package com.kbtg.db.dao;

import com.kbtg.db.bean.UserItem;
import com.kbtg.db.bean.id.UserItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserItemRepo extends JpaRepository<UserItem, UserItemId> {

}
