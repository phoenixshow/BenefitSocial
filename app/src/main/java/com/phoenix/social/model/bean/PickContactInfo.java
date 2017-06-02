package com.phoenix.social.model.bean;

/**
 * 选择联系人的Bean类
 */
public class PickContactInfo {
    private UserInfo user;//联系人
    private boolean isChecked;//是否被选择的标记

    public PickContactInfo() {
    }

    public PickContactInfo(UserInfo user, boolean isChecked) {
        this.user = user;
        this.isChecked = isChecked;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
