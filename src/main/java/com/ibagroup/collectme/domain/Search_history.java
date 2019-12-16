package com.ibagroup.collectme.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Search_history.
 */
@Entity
@Table(name = "search_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Search_history implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origin")
    private String origin;

    @Column(name = "start_time")
    private LocalDate start_time;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public Search_history origin(String origin) {
        this.origin = origin;
        return this;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public LocalDate getStart_time() {
        return start_time;
    }

    public Search_history start_time(LocalDate start_time) {
        this.start_time = start_time;
        return this;
    }

    public void setStart_time(LocalDate start_time) {
        this.start_time = start_time;
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
        Search_history search_history = (Search_history) o;
        if (search_history.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), search_history.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Search_history{" +
            "id=" + getId() +
            ", origin='" + getOrigin() + "'" +
            ", start_time='" + getStart_time() + "'" +
            "}";
    }
}
