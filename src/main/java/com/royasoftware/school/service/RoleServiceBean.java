package com.royasoftware.school.service;

import com.royasoftware.school.model.Role;
import com.royasoftware.school.repository.RoleRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manage the data from database from Role table user
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RoleServiceBean implements RoleService{


    /**
     * The Spring Data repository for Account entities.
     */
    @Autowired(required = false)
    private RoleRepository roleRepository;

    /**
     * Get by id
     * @param id
     * @return
     */
    @Override
    public Role findById(Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return role.get();
    }

    /**
     * File Role by code
     * @param code - the code of the role
     * @return Role object
     */
    @Override
    public Role findByCode(String code) {
       return roleRepository.findByCode(code);
    }
}
