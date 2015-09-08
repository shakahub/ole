/**
 *
 */
package com.weshaka.ole.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.weshaka.ole.entity.BeaconSubject;

/**
 * @author ema
 */
public class BeaconSubjectRepositoryImpl implements BeaconSubjectRepositoryCustom {
    @Autowired
    private MongoTemplate mongoTemplate;

    /*
     * (non-Javadoc)
     * @see com.weshaka.ole.repository.BeaconSubjectRepositoryCustom#
     * findBeaconSubjectByBeaconMac(java.lang.String)
     */
    @Override
    public Optional<BeaconSubject> findBeaconSubjectByBeaconMac(String macId) {
        System.out.printf("MacId %s", macId);
        // BasicQuery query = new BasicQuery("{ 'beacon.mac' : '"+macId+"' }");
        final BeaconSubject result = mongoTemplate.findOne(query(where("beacon.mac").is(macId)), BeaconSubject.class);
        System.out.printf("BeaconSubject %s", result);
        return Optional.ofNullable(result);
    }

}
