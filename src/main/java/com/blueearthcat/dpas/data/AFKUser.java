package com.blueearthcat.dpas.data;

import com.darksoldier1404.dppc.data.DataCargo;
import org.bukkit.configuration.file.YamlConfiguration;

public class AFKUser implements DataCargo {
    private int point;
    private int afkTime;

    public AFKUser() {
    }

    public AFKUser(int point, int afkTime) {
        this.point = point;
        this.afkTime = afkTime;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getAfkTime() {
        return afkTime;
    }

    public void setAfkTime(int afkTime) {
        this.afkTime = afkTime;
    }

    @Override
    public YamlConfiguration serialize() {
        YamlConfiguration data = new YamlConfiguration();
        data.set("point", point);
        data.set("afkTime", afkTime);
        return data;
    }

    @Override
    public AFKUser deserialize(YamlConfiguration data) {
        if (data != null) {
            int point = data.getInt("point", 0);
            int afkTime = data.getInt("afkTime", 0);
            return new AFKUser(point, afkTime);
        }
        return new AFKUser(0, 0);
    }
}
