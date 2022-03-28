package demo.azure.cosmo.sale.crud;

import java.util.List;

import demo.azure.cosmo.entity.SalesOrder;

public interface SalesDao {
	/**
	 * @return A list of TodoItems
	 */
	public List<SalesOrder> readSaleItems(String pkey, Enum<?> key_type);

	/*
	 * @param todoItem
	 * @return whether the todoItem was persisted by return original item
	 * @and time stamp
	 */
	public SalesOrder createSaleItem(SalesOrder saleItem);

	/**
	 * @param id
	 * @return the Sale Item
	 */
	public SalesOrder readSaleItem(String id);

	/**
	 * @param id
	 * @return the Sale Item
	 */
	public SalesOrder updateSaleItem(SalesOrder saleItem);

	/**
	 *
	 * @param id
	 * @return whether the delete was successful.
	 */
	public boolean deleteSaleItem(String id);
}