package com.ibagroup.collectme.service.mapper;

import com.ibagroup.collectme.domain.*;
import com.ibagroup.collectme.service.dto.ManagerProjectDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ManagerProject and its DTO ManagerProjectDTO.
 */
@Mapper(componentModel = "spring", uses = {ManagerMapper.class, ProjectMapper.class, PeriodMapper.class})
public interface ManagerProjectMapper extends EntityMapper<ManagerProjectDTO, ManagerProject> {

/*    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "period.id", target = "periodId")*/
    @Mapping(source = "manager", target = "manager")
    @Mapping(source = "project", target = "project")
    @Mapping(source = "period", target = "period")
    ManagerProjectDTO toDto(ManagerProject managerProject);
/*
    @Mapping(source = "managerId", target = "manager")
    @Mapping(source = "projectId", target = "project")
    @Mapping(source = "periodId", target = "period")*/
    @Mapping(source = "manager", target = "manager")
    @Mapping(source = "project", target = "project")
    @Mapping(source = "period", target = "period")
    ManagerProject toEntity(ManagerProjectDTO managerProjectDTO);

    default ManagerProject fromId(Long id) {
        if (id == null) {
            return null;
        }
        ManagerProject managerProject = new ManagerProject();
        managerProject.setId(id);
        return managerProject;
    }
}
