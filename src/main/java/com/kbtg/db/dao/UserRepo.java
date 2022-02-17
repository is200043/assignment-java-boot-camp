package com.kbtg.db.dao;

import com.kbtg.db.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {

}
