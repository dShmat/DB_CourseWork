package com.ibagroup.collectme.service.dto;
import com.ibagroup.collectme.domain.Manager;
import com.ibagroup.collectme.domain.Period;
import com.ibagroup.collectme.domain.Project;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the ManagerProject entity.
 */
public class ManagerProjectDTO implements Serializable {

    private Long id;

    private ManagerDTO manager;

    private ProjectDTO project;

    private PeriodDTO period;

    public void setManager(ManagerDTO manager) {
        this.manager = manager;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public ManagerDTO getManager() {
        return manager;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public PeriodDTO getPeriod() {
        return period;
    }

    public void setPeriod(PeriodDTO period) {
        this.period = period;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ManagerProjectDTO managerProjectDTO = (ManagerProjectDTO) o;
        if (managerProjectDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), managerProjectDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ManagerProjectDTO{" +
            "id=" + getId() +
            ", manager=" + getManager() +
            ", project=" + getProject() +
            ", period=" + getPeriod() +
            "}";
    }
}
