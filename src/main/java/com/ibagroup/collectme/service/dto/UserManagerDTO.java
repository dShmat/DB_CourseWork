package com.ibagroup.collectme.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the UserManager entity.
 */
public class UserManagerDTO implements Serializable {

    private Long id;


    private ManagerDTO manager;

    private UserDTO user;

    private PeriodDTO period;

    public Long getId() {
        return id;
    }

    public ManagerDTO getManager() {
        return manager;
    }

    public void setManager(ManagerDTO manager) {
        this.manager = manager;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public PeriodDTO getPeriod() {
        return period;
    }

    public void setPeriod(PeriodDTO period) {
        this.period = period;
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

        UserManagerDTO userManagerDTO = (UserManagerDTO) o;
        if (userManagerDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userManagerDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserManagerDTO{" +
            "id=" + getId() +
            ", manager=" + getManager() +
            ", user=" + getUser() +
            ", period=" + getPeriod() +
            "}";
    }
}
