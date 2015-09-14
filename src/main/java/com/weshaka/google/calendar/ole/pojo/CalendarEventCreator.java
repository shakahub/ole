/**
 *
 */
package com.weshaka.google.calendar.ole.pojo;

import java.io.Serializable;

/**
 * @author ema
 */
public class CalendarEventCreator implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8971352030677476392L;
    private String email;
    private String id;

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

}
