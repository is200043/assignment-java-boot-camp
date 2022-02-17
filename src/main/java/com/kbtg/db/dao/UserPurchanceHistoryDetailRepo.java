package com.kbtg.db.dao;

import com.kbtg.db.bean.UserPurchanceHistoryDetail;
import com.kbtg.db.bean.id.UserPurchanceHistoryDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPurchanceHistoryDetailRepo extends JpaRepository<UserPurchanceHistoryDetail, UserPurchanceHistoryDetailId> {

}
