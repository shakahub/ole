/**
 *
 */
package com.weshaka.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Future;

import com.google.api.services.calendar.model.Events;

/**
 * @author ema
 */
public interface CalendarService {
    Future<Events> getPrimaryEvents(String orderBy, Integer limit) throws IOException, GeneralSecurityException;
}
