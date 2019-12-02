package com.ibagroup.collectme.domain;

import com.ibagroup.collectme.service.dto.ReportDTO;
import com.ibagroup.collectme.service.dto.UserDTO;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class UserReports implements Serializable {

    UserDTO user;

    List<ReportDTO> reports;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public List<ReportDTO> getReports() {
        return reports;
    }


    public void setReports(List<ReportDTO> reports) {
        this.reports = reports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserReports that = (UserReports) o;
        return Objects.equals(user, that.user) &&
            Objects.equals(reports, that.reports);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, reports);
    }

    @Override
    public String toString() {
        return "UserReports{" +
            "user=" + user +
            ", reports=" + reports +
            '}';
    }


}
