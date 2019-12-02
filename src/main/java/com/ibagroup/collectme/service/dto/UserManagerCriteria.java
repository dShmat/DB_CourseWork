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
 * Criteria class for the UserManager entity. This class is used in UserManagerResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /user-managers?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UserManagerCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter managerId;

    private LongFilter userId;

    private LongFilter periodId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getManagerId() {
        return managerId;
    }

    public void setManagerId(LongFilter managerId) {
        this.managerId = managerId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final UserManagerCriteria that = (UserManagerCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(managerId, that.managerId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(periodId, that.periodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        managerId,
        userId,
        periodId
        );
    }

    @Override
    public String toString() {
        return "UserManagerCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (managerId != null ? "managerId=" + managerId + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (periodId != null ? "periodId=" + periodId + ", " : "") +
            "}";
    }

}
