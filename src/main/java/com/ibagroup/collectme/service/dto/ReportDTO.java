package com.ibagroup.collectme.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Report entity.
 */
public class ReportDTO implements Serializable {

    private Long id;

    private String hours;

    private String activities;

    private Integer daysAbsent;

    private UserDTO user;

    private ProjectDTO project;

    private PeriodDTO period;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
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

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public Integer getDaysAbsent() {
        return daysAbsent;
    }

    public void setDaysAbsent(Integer daysAbsent) {
        this.daysAbsent = daysAbsent;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReportDTO reportDTO = (ReportDTO) o;
        if (reportDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), reportDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ReportDTO{" +
            "id=" + getId() +
            ", hours='" + getHours() + "'" +
            ", activities='" + getActivities() + "'" +
            ", daysAbsent=" + getDaysAbsent() +
            ", user=" + getUser() +
            ", project=" + getProject() +
            ", period=" + getPeriod() +
            "}";
    }
}
