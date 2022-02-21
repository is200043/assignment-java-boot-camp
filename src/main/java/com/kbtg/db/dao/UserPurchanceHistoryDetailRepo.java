package com.kbtg.db.dao;

import com.kbtg.db.bean.UserPurchanceHistoryDetail;
import com.kbtg.db.bean.id.UserPurchanceHistoryDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPurchanceHistoryDetailRepo extends JpaRepository<UserPurchanceHistoryDetail, UserPurchanceHistoryDetailId> {

    List<UserPurchanceHistoryDetail> findByUserPurchanceHistoryDetailId_UserPurchanceHistoryId(String userPurchanceHistoryId);

}
