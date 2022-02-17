package com.kbtg.db.bean;

import com.kbtg.db.bean.id.UserItemId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserItem {

    @EmbeddedId
    private UserItemId userItemId;
    private Integer qty;
    private BigDecimal price;
    private String options;

}
