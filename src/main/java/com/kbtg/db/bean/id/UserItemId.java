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
public class UserItemId implements Serializable {

    private static final long serialVersionUID = 4790581741719647840L;
    private String userId;
    private String productId;

}
