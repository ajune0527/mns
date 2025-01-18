package me.bytebeats.mns.meta;

import java.util.Objects;

public class FundFirm {
    private String code = "";
    private String name = "";

    public FundFirm(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean contains(String keyword) {
        if (keyword != null && !"".equals(keyword)) {
            return code.contains(keyword) || name.contains(keyword);
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FundFirm fundFirm = (FundFirm) o;
        return Objects.equals(code, fundFirm.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "FundFirm{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
