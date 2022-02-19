package com.kbtg.web.service;

import com.kbtg.db.bean.UserItem;
import com.kbtg.web.common.bean.BasketItem;

import java.util.List;

public interface BasketService {

    List<UserItem> getUserItemListByUserId(String userId);

    void upsertUserItemByUserId(String userId, List<BasketItem> basketItemList);

}
