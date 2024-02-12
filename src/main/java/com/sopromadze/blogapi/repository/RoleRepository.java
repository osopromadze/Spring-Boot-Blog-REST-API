package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;

import java.util.Optional;

public interface RoleRepository  {
	Optional<Role> findByName(RoleName name);
}
