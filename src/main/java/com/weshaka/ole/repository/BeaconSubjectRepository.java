package com.weshaka.ole.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.weshaka.ole.entity.BeaconSubject;

public interface BeaconSubjectRepository extends MongoRepository<BeaconSubject, String> {

    public BeaconSubject findById(String id);

}
