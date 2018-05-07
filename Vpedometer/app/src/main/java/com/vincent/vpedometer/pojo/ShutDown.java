package com.vincent.vpedometer.pojo;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by Administrator on 2018/2/11 18:28
 */
@Table("shutdown")
public class ShutDown {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    int id;
    @Column("shut_down_date")
    String shutDownDate;
    @Column("total_steps")
    int totalSteps;

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShutDownDate() {
        return shutDownDate;
    }

    public void setShutDownDate(String shutDownDate) {
        this.shutDownDate = shutDownDate;
    }

    @Override
    public String toString() {
        return "ShutDown{" +
                "id=" + id +
                ", shutDownDate='" + shutDownDate + '\'' +
                ", totalSteps=" + totalSteps +
                '}';
    }
}
