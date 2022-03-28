package demo.azure.cosmo.entity;

import java.util.List;

//@Data
//@Builder
public class SalesOrder {
	private String id;
	private String partyid;
	private String accountid;
	private String purchase_order_number;
	private String order_date;
	private float subtotal;
	private float tax_amount;
	private float freight;
	private float total_due;
	private List<ItemDetails> Items;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getPartyid() {
		return partyid;
	}
	public void setPartyid(String partyid) {
		this.partyid = partyid;
	}

	public String getAccountid() {
		return accountid;
	}

	public void setAccountid(String accountid) {
		this.accountid = accountid;
	}

	public String getPurchase_order_number() {
		return purchase_order_number;
	}

	public void setPurchase_order_number(String purchase_order_number) {
		this.purchase_order_number = purchase_order_number;
	}

	public String getOrder_date() {
		return order_date;
	}

	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}

	public float getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(float subtotal) {
		this.subtotal = subtotal;
	}

	public float getTax_amount() {
		return tax_amount;
	}

	public void setTax_amount(float tax_amount) {
		this.tax_amount = tax_amount;
	}

	public float getFreight() {
		return freight;
	}

	public void setFreight(float freight) {
		this.freight = freight;
	}

	public float getTotal_due() {
		return total_due;
	}

	public void setTotal_due(float total_due) {
		this.total_due = total_due;
	}

	public List<ItemDetails> getItems() {
		return Items;
	}

	public void setItems(List<ItemDetails> items) {
		Items = items;
	}

}