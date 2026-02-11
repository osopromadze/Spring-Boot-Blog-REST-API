package com.sopromadze.blogapi.utils;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    public void run(String... args){
        if(!roleRepository.existsByName(RoleName.ROLE_ADMIN)){
            roleRepository.save(new Role(RoleName.ROLE_ADMIN));
        }
        if(!roleRepository.existsByName(RoleName.ROLE_USER)){
            roleRepository.save(new Role(RoleName.ROLE_USER));
        }

        System.out.println("Role Initialized Successfully");
    }
}
