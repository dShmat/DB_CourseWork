package com.ibagroup.collectme.service.mapper;

import com.ibagroup.collectme.domain.*;
import com.ibagroup.collectme.service.dto.PeriodDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Period and its DTO PeriodDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PeriodMapper extends EntityMapper<PeriodDTO, Period> {



    default Period fromId(Long id) {
        if (id == null) {
            return null;
        }
        Period period = new Period();
        period.setId(id);
        return period;
    }
}
