package mud.utils;

/*
 * Copyright (c) 2013 Jeremy N. Harton
 * 
 * Released under the MIT License:
 * LICENSE.txt, http://opensource.org/licenses/MIT
 * 
 * NOTE: license provided with code controls, if any
 * changes are made to the one referred to.
 */

import java.util.LinkedList;
import java.util.List;

import mud.Coins;
import mud.objects.Item;

public class Auction {
	private int duration;
	private Item item;
	private Coins initial_price;
	private Coins buyout_price;
	
	private Bid currentBid;
	
	private LinkedList<Bid> bids;
	
	/**
	 * create an auction for the specified items, setting
	 * the initial price
	 * 
	 * @param auctionItem
	 * @param initial
	 */
	public Auction(Item auctionItem, Coins initial) {
		this.item = auctionItem;
		this.initial_price = initial;
		
		this.currentBid = null;
		
		bids = new LinkedList<Bid>();
	}
	
	/**
	 * create an auction for the specified items, setting
	 * the initial price and a buyout price
	 * 
	 * @param auctionItem
	 * @param initial
	 * @param buyout
	 */
	public Auction(Item auctionItem, Coins initial, Coins buyout) {
		this(auctionItem, initial);
		
		this.buyout_price = buyout;
	}
	
	public List<Bid> getBids() {
		return this.bids;		
	}
	
	public boolean placeBid(Bid newBid) {
		
		boolean success = false;
		
		if( currentBid != null) {
			if( newBid.getAmount().numOfCopper() > currentBid.getAmount().numOfCopper() ) {
				currentBid = newBid;
				this.bids.add(newBid);
				success = true;
			}
		}
		else {
			currentBid = newBid;
			this.bids.add(newBid);
			success = true;
		}
		
		return success;
	}
	
	public boolean retractBid(Bid oldBid) {
		return false;
	}
	
	public Bid getCurrentBid() {
		return this.currentBid;
	}
}