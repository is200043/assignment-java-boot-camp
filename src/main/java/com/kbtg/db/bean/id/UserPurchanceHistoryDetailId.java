package com.kbtg.db.bean.id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPurchanceHistoryDetailId implements Serializable {

    private static final long serialVersionUID = 3703167476848616670L;
    private String userPurchanceHistoryId;
    private String productId;

}
