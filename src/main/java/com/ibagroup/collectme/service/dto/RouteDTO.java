package com.ibagroup.collectme.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Route entity.
 */
public class RouteDTO implements Serializable {

    private Long id;

    private Double distance;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RouteDTO routeDTO = (RouteDTO) o;
        if (routeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), routeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RouteDTO{" +
            "id=" + getId() +
            ", distance=" + getDistance() +
            "}";
    }
}
