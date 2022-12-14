package com.boh.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boh.dto.UserDto;
import com.boh.jpa.UserEntity;
import com.boh.service.UserService;
import com.boh.vo.Greeting;
import com.boh.vo.RequestUser;
import com.boh.vo.ResponseUser;

import io.micrometer.core.annotation.Timed;

@RestController
//@RequestMapping(value = "/user-service/**")
@RequestMapping(value = "/")
public class UserController {
	Environment env;
	private UserService userSerivce;

	@Autowired
	public UserController(Environment env, UserService userService) {
		this.env = env;
		this.userSerivce = userService;
	}

	@Autowired
	private Greeting greeting;

	@GetMapping("/health_check")
	@Timed(value="uers.status", longTask = true)
	public String healthCheck() {
		return String.format("It's Working in User Servie")
				+ "\n\tport(local.server.port)="+env.getProperty("local.server.port")
				+ "\n\tport(server.port)="+env.getProperty("server.port")
				+ "\n\ttoken secret="+env.getProperty("token.secret")
				+ "\n\ttoken expiration time="+env.getProperty("token.expiration_time");
	}

	@GetMapping("/welcome")
	@Timed(value="uers.welcome", longTask = true)
	public String welcome() {
		// return env.getProperty("greeting.message");
		return greeting.getMessage();
	}

	@PostMapping("/users")
	public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserDto userDto = mapper.map(user, UserDto.class);

		userSerivce.createUser(userDto);
		
		ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
		//responseUser??? responseEntity body??? ????????? ??????.
		
		return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<ResponseUser>> getUsers(){
		Iterable<UserEntity> userList = userSerivce.getUserByAll();
		
		List<ResponseUser> result = new ArrayList<>();
		userList.forEach(v -> {
			result.add(new ModelMapper().map(v, ResponseUser.class));
		});
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	

	@GetMapping("/users/{userId}")
	public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId){
		UserDto userDto = userSerivce.getUserByUserId(userId);
		
		ResponseUser responseUser = new ModelMapper().map(userDto, ResponseUser.class);
		
		
		return ResponseEntity.status(HttpStatus.OK).body(responseUser);
	}
	
}
