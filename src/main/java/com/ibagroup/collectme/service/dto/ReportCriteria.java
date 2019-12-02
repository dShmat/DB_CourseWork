package com.ibagroup.collectme.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the Report entity. This class is used in ReportResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /reports?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ReportCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter hours;

    private StringFilter activities;

    private IntegerFilter daysAbsent;

    private LongFilter userId;

    private LongFilter projectId;

    private LongFilter periodId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getHours() {
        return hours;
    }

    public void setHours(StringFilter hours) {
        this.hours = hours;
    }

    public StringFilter getActivities() {
        return activities;
    }

    public void setActivities(StringFilter activities) {
        this.activities = activities;
    }

    public IntegerFilter getDaysAbsent() {
        return daysAbsent;
    }

    public void setDaysAbsent(IntegerFilter daysAbsent) {
        this.daysAbsent = daysAbsent;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
        this.projectId = projectId;
    }

    public LongFilter getPeriodId() {
        return periodId;
    }

    public void setPeriodId(LongFilter periodId) {
        this.periodId = periodId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReportCriteria that = (ReportCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(hours, that.hours) &&
            Objects.equals(activities, that.activities) &&
            Objects.equals(daysAbsent, that.daysAbsent) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(periodId, that.periodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        hours,
        activities,
        daysAbsent,
        userId,
        projectId,
        periodId
        );
    }

    @Override
    public String toString() {
        return "ReportCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (hours != null ? "hours=" + hours + ", " : "") +
                (activities != null ? "activities=" + activities + ", " : "") +
                (daysAbsent != null ? "daysAbsent=" + daysAbsent + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (projectId != null ? "projectId=" + projectId + ", " : "") +
                (periodId != null ? "periodId=" + periodId + ", " : "") +
            "}";
    }

}
