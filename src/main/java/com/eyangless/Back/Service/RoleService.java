package com.eyangless.Back.Service;

import com.eyangless.Back.Entity.Role;

import java.util.List;

public interface RoleService {
    public Role creer(Role role);
    public List<Role> findall();
}
