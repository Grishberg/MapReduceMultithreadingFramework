package com.grishberg.models;

public class UserInfoContainer {
    private String userId = "";
    private String userIp = "";

    public UserInfoContainer() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n\t\t{");
        sb.append("\"userId\":\"").append(userId);
        sb.append("\",\n\t\t\"userIp\":\"").append(userIp);
        sb.append("\"\n\t\t}");
        return sb.toString();
    }
}
