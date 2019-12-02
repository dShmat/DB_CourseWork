package com.ibagroup.collectme.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Report.
 */
@Entity
@Table(name = "report")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hours")
    private String hours;

    @Column(name = "activities")
    private String activities;

    @Column(name = "days_absent")
    private Integer daysAbsent;

    @ManyToOne
    @JsonIgnoreProperties("reports")
    private User user;

    @ManyToOne
    @JsonIgnoreProperties("reports")
    private Project project;

    @ManyToOne
    @JsonIgnoreProperties("reports")
    private Period period;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHours() {
        return hours;
    }

    public Report hours(String hours) {
        this.hours = hours;
        return this;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getActivities() {
        return activities;
    }

    public Report activities(String activities) {
        this.activities = activities;
        return this;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public Integer getDaysAbsent() {
        return daysAbsent;
    }

    public Report daysAbsent(Integer daysAbsent) {
        this.daysAbsent = daysAbsent;
        return this;
    }

    public void setDaysAbsent(Integer daysAbsent) {
        this.daysAbsent = daysAbsent;
    }

    public User getUser() {
        return user;
    }

    public Report user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public Report project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Period getPeriod() {
        return period;
    }

    public Report period(Period period) {
        this.period = period;
        return this;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Report report = (Report) o;
        if (report.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), report.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Report{" +
            "id=" + getId() +
            ", hours='" + getHours() + "'" +
            ", activities='" + getActivities() + "'" +
            ", daysAbsent=" + getDaysAbsent() +
            "}";
    }
}
