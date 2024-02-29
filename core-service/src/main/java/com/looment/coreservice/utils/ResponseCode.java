package com.looment.coreservice.utils;

public enum ResponseCode {
    INTERNAL_ERROR("2000", "General. Internal server error"),
    ANSWER_ALREADY_APPROVED("2001", "Answer already approved"),
    ANSWER_DUPLICATE("2002", "Answer already exists"),
    ANSWER_INVALID("2003", "Answer invalid"),
    ANSWER_NOT_EXISTS("2004", "Answer not exists"),
    BOUNTY_ALREADY_CLOSED("2005", "Bounty already closed"),
    BOUNTY_NOT_EXISTS("2006", "Bounty not exists"),
    BOUNTY_CATEGORY_INVALID("2007", "Bounty category invalid"),
    BOUNTY_DEADLINE_INVALID("2008", "Bounty deadline invalid"),
    COMMENT_NOT_EXISTS("2009", "Comment not exists"),
    POSTS_NOT_EXISTS("2010", "Post not exists"),
    POSTS_CATEGORY_INVALID("2011", "Post category invalid"),
    USER_EMAIL_CONFLICT("2012","User email already exists"),
    USER_EMAIL_INVALID("2013","User email invalid"),
    USER_FULLNAME_INVALID("2014","User fullname invalid"),
    USER_NOT_EXISTS("2015","User not exists"),
    USER_OTP_EXPIRED("2016","OTP expired"),
    USER_OTP_INVALID("2017","OTP invalid"),
    USER_PASSWORD_INVALID("2018","User password invalid"),
    USER_UNDERAGE("2019","User underage"),
    USER_USERNAME_CONFLICT("2020","User username already exists"),
    USER_USERNAME_INVALID("2021","User username invalid"),
    USERINFO_NOT_EXISTS("2022","UserInfo not exists"),
    PARENT_NOT_EXISTS("2023","Parent not exists"),
    FAILED_REST_CALL("2024","REST Call failed"),
    FAILED_MAPPING("2025","Error mapping"),
    EMPTY_STRING("2026","Failed map empty String"),
    BAD_REQUEST("2027","Bad Request"),
    ;


    private final String code;
    private final String message;

    ResponseCode(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String code(){
        return code;
    }

    public String message(){
        return message;
    }
}
