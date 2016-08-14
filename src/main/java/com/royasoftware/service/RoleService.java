package com.royasoftware.service;

import com.royasoftware.model.Role;

/**
 * Created by christospapidas on 25012016--.
 */
public interface RoleService {

    Role findById(Long id);

    Role findByCode(String code);

}
