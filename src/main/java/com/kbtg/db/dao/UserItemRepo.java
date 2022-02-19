package com.kbtg.db.dao;

import com.kbtg.db.bean.UserItem;
import com.kbtg.db.bean.id.UserItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserItemRepo extends JpaRepository<UserItem, UserItemId> {

    List<UserItem> findByUserItemId_UserIdEquals(String userId);

}
