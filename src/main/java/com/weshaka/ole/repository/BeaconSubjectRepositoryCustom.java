/**
 * 
 */
package com.weshaka.ole.repository;

import java.util.Optional;

import com.weshaka.ole.entity.BeaconSubject;

/**
 * @author ema
 *
 */
public interface BeaconSubjectRepositoryCustom {
    public Optional<BeaconSubject> findBeaconSubjectByBeaconMac(String macId);
}
