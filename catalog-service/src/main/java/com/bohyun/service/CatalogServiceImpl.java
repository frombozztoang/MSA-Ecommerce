package com.bohyun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bohyun.jpa.CatalogEntity;
import com.bohyun.jpa.CatalogRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data  // ?
@Slf4j
@Service
public class CatalogServiceImpl implements CatalogService{
	
	CatalogRepository catalogRepository;
	
	@Autowired
	public CatalogServiceImpl(CatalogRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
	 
	
	@Override
	public Iterable<CatalogEntity> getAllCatalogs() {
		return catalogRepository.findAll();
	}

}
