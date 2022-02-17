package com.kbtg.db.dao;

import com.kbtg.db.bean.UserPurchanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPurchanceHistoryRepo extends JpaRepository<UserPurchanceHistory, String> {

}