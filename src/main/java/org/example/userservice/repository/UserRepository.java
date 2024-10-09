package org.example.userservice.repository;

import org.example.userservice.model.entity.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository <AppUser,String>{
    AppUser findByEmail(String email);
}
