package org.qzerver.model.agent.action.providers.executor.sshcommand;

import java.io.Serializable;

public class SshCommandActionOutput implements Serializable {

    private byte[] data;

    private SshCommandActionOutputStatus status;

    public SshCommandActionOutput() {
    }

    public SshCommandActionOutput(SshCommandActionOutputStatus status, byte[] data) {
        this.data = data;
        this.status = status;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public SshCommandActionOutputStatus getStatus() {
        return status;
    }

    public void setStatus(SshCommandActionOutputStatus status) {
        this.status = status;
    }

}
