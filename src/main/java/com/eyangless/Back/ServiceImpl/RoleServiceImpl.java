package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.Entity.Role;
import com.eyangless.Back.Repository.RoleRepository;
import com.eyangless.Back.Service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;
    @Override
    public Role creer(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public List<Role> findall() {
        return roleRepository.findAll();
    }
}
