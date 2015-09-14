/**
 *
 */
package com.weshaka.ole.exceptions;

/**
 * @author ema
 */
public class CalendarServiceNotResponseException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 5497207388294001098L;

    public CalendarServiceNotResponseException(String msg) {
        super("Calendar service does not response with message '" + msg + "'.");
    }
}