package com.kbtg.web.component;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class ValidateCommon {

    public boolean validateMobileNo(String mobileNo) throws RuntimeException {
        Pattern p = Pattern.compile("[0]{1}[6|8|9]{1}[0-9]{8}");
        Matcher matcher = p.matcher(mobileNo.trim());
        if (!matcher.matches()) {
            throw new RuntimeException("Invalid phone number format");
        }
        return true;
    }

    public Optional<String> checkFormatMobileNo(String mobileNo) {
        try {
            if (!isNotEmpty(mobileNo)) {
                this.validateMobileNo(mobileNo.trim());
                return Optional.ofNullable(mobileNo);
            }
        } catch (RuntimeException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }
}
