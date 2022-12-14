package com.boh.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.boh.dto.UserDto;
import com.boh.jpa.UserEntity;

public interface UserService extends UserDetailsService{
	// 사용자 생성
	UserDto createUser(UserDto userDto);

	// 사용자 조회 
	UserDto getUserByUserId(String userId);
	
	// 전체 사용자 조회
	Iterable<UserEntity> getUserByAll();

	// 이메일로 사용자 상세 조회.
	UserDto getUserDetailsByEmail(String userName);

}
