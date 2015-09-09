package com.weshaka.ole.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.weshaka.ole.entity.BeaconSubject;

public interface BeaconSubjectRepository extends MongoRepository<BeaconSubject, ObjectId> {

    public BeaconSubject findById(ObjectId id);

}
