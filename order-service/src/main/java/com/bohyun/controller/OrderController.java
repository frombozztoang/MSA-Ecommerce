package com.bohyun.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bohyun.dto.OrderDto;
import com.bohyun.jpa.OrderEntity;
import com.bohyun.messagequeue.KafkaProducer;
import com.bohyun.messagequeue.OrderProducer;
import com.bohyun.service.OrderService;
import com.bohyun.vo.RequestOrder;
import com.bohyun.vo.ResponseOrder;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/order-service/**")
@Slf4j
public class OrderController {
	Environment env;
	OrderService orderService;
	KafkaProducer kafkaProducer;
	OrderProducer orderProducer;
	
	@Autowired
	public OrderController(Environment env, OrderService orderService, KafkaProducer kafkaProducer, OrderProducer orderProducer) {
		this.env = env;
		this.orderService = orderService;
		this.kafkaProducer = kafkaProducer;
		this.orderProducer = orderProducer;
	}
	
	@GetMapping("/health_check")
	public String healthCheck() {
		return String.format("It's Working in Order Servie on PORT %s", env.getProperty("local.server.port"));
	}
	
	@PostMapping("/{userId}/orders")
	public ResponseEntity<ResponseOrder> createOrder(@RequestBody RequestOrder requestOrder, @PathVariable("userId") String userId) {
		log.info("Before add orders data");

		
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		
		OrderDto orderDto = mapper.map(requestOrder, OrderDto.class);
		orderDto.setUserId(userId);

		// jpa ?????? ??????
		OrderDto createOrder = orderService.createOrder(orderDto);		
		ResponseOrder responseOrder = mapper.map(createOrder, ResponseOrder.class);

		// kafka
		orderDto.setOrderId(UUID.randomUUID().toString());
		orderDto.setTotalPrice(requestOrder.getUnitPrice()*requestOrder.getQty());

		// send this order to the kafka (send topic)
//		kafkaProducer.send("example-catalog-topic", orderDto);
//		orderProducer.send("orders", orderDto);

		// sink connect ??????
		// kafka connect ??????  ./bin/connect-distributed ./etc/kafka/connect-distributed.properties

//		ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

		//responseUser??? responseEntity body??? ????????? ??????.

		log.info("After add orders data");
		return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
	}
	

	@GetMapping("/{userId}/orders")
	public ResponseEntity<List<ResponseOrder>> getUsers(@PathVariable("userId") String userId) throws Exception{
		log.info("Before retrieve orders data");
		Iterable<OrderEntity> userList = orderService.getOrdersByUserId(userId);
		
		List<ResponseOrder> result = new ArrayList<>();
		userList.forEach(item -> {
			result.add(new ModelMapper().map(item, ResponseOrder.class));
		});
		
		try {
			Thread.sleep(1000);
			throw new Exception("?????? ??????");
		}catch (InterruptedException e) {
			log.warn(e.getMessage());
		}
		
		log.info("After retrieve orders data");
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/posts/read/{id}")
	public String read(@PathVariable Long id, Model model) {
		OrderDto dto = OrderService.getOrderByOrderId(String.valueOf(id));
		OrderService.updateView(id); // views ++
		model.addAttribute("posts", dto);

		return "posts-read";
	}
	
	
}
