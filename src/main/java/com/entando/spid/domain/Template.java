package com.entando.spid.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Template.
 */

public class Template implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String config;

    public String getConfig() {
        return this.config;
    }

    public Template config(String config) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Template template = (Template) o;
        return Objects.equals(name, template.name)
            && Objects.equals(config, template.config);
    }

    @Override
    public String toString() {
        return "Template{" +
            "name='" + name + '\'' +
            ", config='" + config + '\'' +
            '}';
    }
}
