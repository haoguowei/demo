package com.hao.demo.spi;

import java.util.Iterator;
import java.util.ServiceLoader;


public class Test {
	
	public static void main(String[] args) {
		ServiceLoader<SearchService> loader = ServiceLoader.load(SearchService.class);
		Iterator<SearchService> searchs = loader.iterator();
		if(searchs.hasNext()){
			SearchService service = searchs.next();
			service.search();
		}
	}
}
