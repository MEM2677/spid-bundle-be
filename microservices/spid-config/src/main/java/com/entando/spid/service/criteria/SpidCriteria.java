package com.entando.spid.service.criteria;

import java.io.Serializable;
import java.util.Objects;

import com.entando.spid.domain.Idp;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link Idp} entity. This class is used
 * in {@link com.entando.spid.web.rest.SpidResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /spids?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SpidCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter config;

    private Boolean distinct;

    public SpidCriteria() {}

    public SpidCriteria(SpidCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.config = other.config == null ? null : other.config.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SpidCriteria copy() {
        return new SpidCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getConfig() {
        return config;
    }

    public StringFilter config() {
        if (config == null) {
            config = new StringFilter();
        }
        return config;
    }

    public void setConfig(StringFilter config) {
        this.config = config;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SpidCriteria that = (SpidCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(config, that.config) && Objects.equals(distinct, that.distinct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, config, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpidCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (config != null ? "config=" + config + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
