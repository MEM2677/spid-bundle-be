package com.entando.spid.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Idp.
 */

public class Idp implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String config;


    public Long getId() {
        return this.id;
    }

    public Idp id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfig() {
        return this.config;
    }

    public Idp config(String config) {
        this.setConfig(config);
        return this;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Idp)) {
            return false;
        }
        return id != null && id.equals(((Idp) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Idp{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", config='" + getConfig() + "'" +
            "}";
    }
}
