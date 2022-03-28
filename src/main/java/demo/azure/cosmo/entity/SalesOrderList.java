package demo.azure.cosmo.entity;

import java.util.ArrayList;
import java.util.List;

public class SalesOrderList {

	public SalesOrderList() {
		// TODO Auto-generated constructor stub
	}
	
	List<SalesOrder> todoItems = new ArrayList<SalesOrder>();

	public List<SalesOrder> getTodoItems() {
		return todoItems;
	}

	public void setTodoItems(List<SalesOrder> todoItems) {
		this.todoItems = todoItems;
	}

}
