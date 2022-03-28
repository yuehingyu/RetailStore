package demo.azure.cosmo.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import demo.azure.cosmo.common.keys;
import demo.azure.cosmo.entity.SalesOrder;

import demo.azure.cosmo.sale.crud.SaleDocDbDao;

/**
 *
 * Controller to return sale results
 */
@RestController
@ComponentScan("demo.azure.cosmo.sale.crud")
public class RequestsController {
	/**
	 *
	 * 
	 */

	@Autowired
	SaleDocDbDao todoHandler;

	@RequestMapping(value = "/retail/sale/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public SalesOrder getSalesOrder(@PathVariable String id) throws JsonProcessingException {

		SalesOrder item = todoHandler.readSaleItem(id);

		return item;
	}
	
	@RequestMapping(value = "/retail/sale/list/customer/{pkey}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public List<SalesOrder> getSalesOrderListByCustomer(@PathVariable String pkey) throws JsonProcessingException {


		List<SalesOrder> items = todoHandler.readSaleItems(pkey,keys.PARTYID);

		return items;
	}
	
	@RequestMapping(value = "/retail/sale/list/account/{pkey}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public List<SalesOrder> getSalesOrderListByAccount(@PathVariable String pkey) throws JsonProcessingException {


		List<SalesOrder> items = todoHandler.readSaleItems(pkey,keys.ACCOUNTID);

		return items;
	}
	
	
	@RequestMapping(value = "/retail/sale/add", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public SalesOrder addsale(@RequestBody SalesOrder saleItem) throws JsonProcessingException {

		SalesOrder item = todoHandler.createSaleItem(saleItem);
		
		return item;
		
	}
	
	@RequestMapping(value = "/retail/sale/delete/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Boolean deleteSalesOrder(@PathVariable String id){
		
		Boolean status=false;

		status = todoHandler.deleteSaleItem(id);

		return status;
	}
	

}
