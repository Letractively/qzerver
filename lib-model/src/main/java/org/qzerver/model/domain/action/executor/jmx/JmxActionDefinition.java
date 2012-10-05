package org.qzerver.model.domain.action.executor.jmx;

import org.qzerver.model.domain.action.ActionDefinition;

import java.util.List;

public class JmxActionDefinition implements ActionDefinition {

    private static final String TYPE_IDENTIFIER = "http";

    private String url;

    private String username;

    private String password;

    private String bean;

    private String method;

    private List<String> parameters;

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getType() {
        return TYPE_IDENTIFIER;
    }
}
