package org.qzerver.model.domain.action.executor.http;

import org.qzerver.model.domain.action.ActionResult;

public class HttpActionResult implements ActionResult {

    private int status;

    private String mime;

    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean isSucceed() {
        return false;
    }
}
