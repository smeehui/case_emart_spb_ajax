package com.huy.service.role;

import com.huy.model.Role;
import com.huy.service.IGeneralService;

public interface IRoleService extends IGeneralService<Role> {
    Role findByName(String name);
}
