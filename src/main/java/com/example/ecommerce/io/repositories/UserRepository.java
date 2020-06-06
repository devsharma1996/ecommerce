package com.example.ecommerce.io.repositories;

import com.example.ecommerce.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity,Long> {

    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    UserEntity findByEmailVerificationToken(String token);
}
