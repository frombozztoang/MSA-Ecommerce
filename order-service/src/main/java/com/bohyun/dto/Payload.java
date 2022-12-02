package com.bohyun.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {
	// db의 컬럼명과 일치 
	private String order_id;
	private String user_id;
	private String product_id;
	private int qty;
	private int viewNum;
	private int unit_price;
	private int total_price;
}
