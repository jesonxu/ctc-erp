package com.dahantc.erp.dto.role;

import java.io.Serializable;

import com.dahantc.erp.vo.role.entity.Role;

/**
 * 角色信息
 * @author 8520
 */
public class RoleInfoDto implements Serializable {
    private static final long serialVersionUID = -7419344362228730100L;

    private String id;

    private String name;

    private Integer state;

    /**
     * 当前角色
     */
    private Integer now;

    public RoleInfoDto() {
    }

    public RoleInfoDto(Role role,int isNow) {
        this.id = role.getRoleid();
        this.name = role.getRolename();
        this.state = role.getStatus();
        this.now = isNow;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getNow() {
        return now;
    }

    public void setNow(Integer now) {
        this.now = now;
    }
}
