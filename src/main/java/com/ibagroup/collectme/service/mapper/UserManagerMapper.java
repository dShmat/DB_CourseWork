package com.ibagroup.collectme.service.mapper;

import com.ibagroup.collectme.domain.*;
import com.ibagroup.collectme.service.dto.UserManagerDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UserManager and its DTO UserManagerDTO.
 */
@Mapper(componentModel = "spring", uses = {ManagerMapper.class, UserMapper.class, PeriodMapper.class})
public interface UserManagerMapper extends EntityMapper<UserManagerDTO, UserManager> {

    @Mapping(source = "manager", target = "manager")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "period", target = "period")
    UserManagerDTO toDto(UserManager userManager);

    @Mapping(source = "manager", target = "manager")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "period", target = "period")
    UserManager toEntity(UserManagerDTO userManagerDTO);

    default UserManager fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserManager userManager = new UserManager();
        userManager.setId(id);
        return userManager;
    }
}
