package com.ibagroup.collectme.service.mapper;

import com.ibagroup.collectme.domain.*;
import com.ibagroup.collectme.service.dto.ManagerDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Manager and its DTO ManagerDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ManagerMapper extends EntityMapper<ManagerDTO, Manager> {

    @Mapping(source = "user", target = "user")
    ManagerDTO toDto(Manager manager);

    @Mapping(source = "user", target = "user")
    Manager toEntity(ManagerDTO managerDTO);

    default Manager fromId(Long id) {
        if (id == null) {
            return null;
        }
        Manager manager = new Manager();
        manager.setId(id);
        return manager;
    }
}
