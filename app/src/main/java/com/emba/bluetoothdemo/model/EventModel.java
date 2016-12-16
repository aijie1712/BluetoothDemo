package com.emba.bluetoothdemo.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2016-12-16
 *
 * @desc 操作行为记录
 */

public class EventModel extends DataSupport{
    /**
     * 时间
     */
    private long time;
    /**
     * 行为
     */
    private String event;

    public EventModel() {
    }

    public EventModel(long time, String event) {
        this.time = time;
        this.event = event;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "time=" + time +
                ", event='" + event + '\'' +
                '}';
    }
}
