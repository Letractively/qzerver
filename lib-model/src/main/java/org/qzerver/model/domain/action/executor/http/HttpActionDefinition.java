package org.qzerver.model.domain.action.executor.http;

import org.qzerver.model.domain.action.ActionDefinition;

import java.util.Map;

public class HttpActionDefinition implements ActionDefinition {

    private static final HttpActionProtocol DEFAULT_PROTOCOL = HttpActionProtocol.HTTP;

    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 3000;

    private static final int DEFAULT_READ_TIMEOUT_MS = 600000;

    private String path;

    private String username;

    private String password;

    private Map<String, String> headers;

    private HttpActionProtocol protocol = DEFAULT_PROTOCOL;

    private int connectionTimeoutMs = DEFAULT_CONNECTION_TIMEOUT_MS;

    private int readTimeoutMs = DEFAULT_READ_TIMEOUT_MS;

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpActionProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(HttpActionProtocol protocol) {
        this.protocol = protocol;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void doSomething() {
    }
}
