package com.vincent.vpedometer.pojo;

/**
 * Created by Administrator on 2018/2/28 21:03
 */
public class User {

    String uid;
    String name;
    String password;
    String email;
    String totalstep;
    long registerTime;

    public String getTotalstep() {
        return totalstep;
    }

    public void setTotalstep(String totalstep) {
        this.totalstep = totalstep;
    }

    public int getGender() {
        return gender;
    }

    public GameCharecter getRole() {
        return role;
    }

    public void setRole(GameCharecter role) {
        this.role = role;
    }

    long loginTime;
    int loginCount;
    int gender;
    String code;
    GameCharecter role;

    public User(String userName, String password) {
        this.name = userName;
        this.password = password;
    }

    public User() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public int isGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public GameCharecter getGameCharecter() {
        return role;
    }

    public void setGameCharecter(GameCharecter gameCharecter) {
        this.role = gameCharecter;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", registerTime=" + registerTime +
                ", loginTime=" + loginTime +
                ", loginCount=" + loginCount +
                ", gender=" + gender +
                ", code='" + code + '\'' +
                ", gameCharecter=" + role +
                '}';
    }
}
