package com.meltwater.puppy.config;

public class PermissionsData {
    private String configure;
    private String write;
    private String read;

    public PermissionsData() {}

    public String getConfigure() {
        return configure;
    }

    public void setConfigure(String configure) {
        this.configure = configure;
    }

    public String getWrite() {
        return write;
    }

    public void setWrite(String write) {
        this.write = write;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "PermissionsData{" +
                "configure='" + configure + '\'' +
                ", write='" + write + '\'' +
                ", read='" + read + '\'' +
                '}';
    }
}
