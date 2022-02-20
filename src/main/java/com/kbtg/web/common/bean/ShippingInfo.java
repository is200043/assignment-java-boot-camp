package com.kbtg.web.common.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingInfo {

    @NotEmpty
    private String userPurchanceHistoryId;
    @NotEmpty
    @Size(min = 20, max = 150, message = "Name size 20-150")
    private String name;
    @NotEmpty
    private String address;
    @NotEmpty
    private String district;
    @NotEmpty
    private String province;
    @NotEmpty
    private String zipcode;
    @NotEmpty
    private String mobileNo;
    @Email(message = "Email should be valid")
    private String email;

}
