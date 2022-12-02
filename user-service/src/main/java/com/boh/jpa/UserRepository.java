package com.boh.jpa;

import com.boh.dto.UserDto;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long>{

	UserEntity findByUserId(String userId);
	
	//find : select, By 다음에 오는것이 where절 컬럼
	UserEntity findByEmail(String email);

	UserEntity remove(UserDto findUser);
}
