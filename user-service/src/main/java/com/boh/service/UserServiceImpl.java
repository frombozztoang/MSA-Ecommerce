package com.boh.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.boh.client.OrderServiceClient;
import com.boh.dto.UserDto;
import com.boh.jpa.UserEntity;
import com.boh.jpa.UserRepository;
import com.boh.vo.ResponseOrder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
	UserRepository userRepository;
	BCryptPasswordEncoder passwordEncoder;
	Environment env;
	RestTemplate restTemplate;
	OrderServiceClient orderServiceClient;
	CircuitBreakerFactory circuitBreakerFactory;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository, 
							BCryptPasswordEncoder passwordEncoder, 
							Environment env, 
							RestTemplate restTemplate,
							OrderServiceClient orderServiceClient,
							CircuitBreakerFactory circuitBreakerFactory) {
		// 생성자로 의존성 주입.
		// BCryptPasswordEncoder 는 한번도 선언 된 적이 없기 때문에 @Service 생성자 파라미터로 추가 할수 없다.
		// 이를 해결하기 위해 @Service가 실행 되기 전에 @Bean으로 등록 해주어야 한다. -> UserServiceApplication
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.env = env;
		this.restTemplate = restTemplate;
		this.orderServiceClient = orderServiceClient;
		this.circuitBreakerFactory = circuitBreakerFactory;
	}

	@Override
	public UserDto createUser(UserDto userDto) {
		userDto.setUserId(UUID.randomUUID().toString());

		// 데이터 베이스에 저장하기 위해 UserEntity가 필요함.
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = mapper.map(userDto, UserEntity.class);
		
		// 패스워드 암호화
		userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
		
		// insert into database
		userRepository.save(userEntity);

		UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

		return returnUserDto;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) {
			throw new UsernameNotFoundException("User not found");
		}
		
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
		
		/* Using a feign client */		
		/* Feign Exception Handling - FeignErrorDecoder */
		//List<ResponseOrder> orderList= orderServiceClient.getOrders(userId);	
		
		
		log.info("Before call orders microservice");
		// circuitbreaker
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
		List<ResponseOrder> orderList = 
				circuitBreaker.run(() -> orderServiceClient.getOrders(userId)
				, throwable -> new ArrayList<>());
		log.info("After call orders microservice");		
		
		userDto.setOrders(orderList);
		
		return userDto;
	}

	@Override
	public Iterable<UserEntity> getUserByAll() {		
		return userRepository.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) {
			throw new UsernameNotFoundException(email);
		}
		
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),true,true,true,true,new ArrayList<>());
	}

	public UserDto deleteById(String userId) {
		UserDto findUser = getUserByUserId(userId);

		if(findUser != null) {
			userRepository.remove(findUser);
		}

		return findUser;
	}

	@Override
	public UserDto getUserDetailsByEmail(String userName) {
		UserEntity userEntity = userRepository.findByEmail(userName);
		
		if(userEntity == null) {
			throw new UsernameNotFoundException(userName);
		}
		
		return new ModelMapper().map(userEntity, UserDto.class);
	}

	
	
	
}
