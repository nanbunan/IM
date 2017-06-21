package com.example.administrator.im.model.bean;

/**
 * Created by Administrator on 2017/6/6.
 */
//选择联系人的bean
public class PickContactInfo  {
    private UserInfo user;//联系人
    private boolean isChecked;//联系人是否被选中

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
