package com.jpmorgan.tradelogger.core.test;

import java.math.BigDecimal;

import org.junit.Test;

import com.jpmorgan.tradelogger.core.MessageReceiver;
import com.jpmorgan.tradelogger.core.Sale;
import com.jpmorgan.tradelogger.message.AdjustmentOperation;
import com.jpmorgan.tradelogger.message.AdjustmentSaleMessage;
import com.jpmorgan.tradelogger.message.BatchSaleMessage;
import com.jpmorgan.tradelogger.message.SimpleSaleMessage;

import junit.framework.TestCase;

public class MessegeProcessorTest extends TestCase {

	private MessageReceiver receiver;

	@Override
	public void setUp() {
		System.out.println("\n\n*********************************************************");
		this.receiver = new MessageReceiver();
	}

	@Test
	public void testSendingUnder50Messages() {
		System.out.println("Starting test " + new Object() {
		}.getClass().getEnclosingMethod().getName() + "...");
		for (int i = 0; i < 22; i++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("1.00"), "apples"), receiver));
		}
		assertTrue(receiver.getProduct2SaleListMap().containsKey("apples"));
		assertTrue(receiver.getProduct2SaleListMap().get("apples").size() == 22);
		assertTrue(receiver.getProduct2SaleListMap().get("apples").stream().map(sale -> sale.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add).equals(new BigDecimal("22.00")));

		System.out.println("TEST " + new Object() {
		}.getClass().getEnclosingMethod().getName() + " finished.");
	}

	@Test
	public void testSendingOver50Messages() {
		System.out.println("Starting test " + new Object() {
		}.getClass().getEnclosingMethod().getName() + "...");
		for (int i = 0; i < 54; i++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("2.00"), "peaches"), receiver));
		}
		assertTrue(receiver.getProduct2SaleListMap().containsKey("peaches"));
		System.out.println("Queue size: " + receiver.getProduct2SaleListMap().get("peaches").size());
		assertTrue(receiver.getProduct2SaleListMap().get("peaches").size() == 50);
		assertTrue(receiver.getProduct2SaleListMap().get("peaches").stream().map(sale -> sale.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add).equals(new BigDecimal("100.00")));

		System.out.println("TEST " + new Object() {
		}.getClass().getEnclosingMethod().getName() + " finished.");
	}

	@Test
	public void testBatchSaleMessages() {
		System.out.println("Starting test " + new Object() {
		}.getClass().getEnclosingMethod().getName() + "...");
		for (int i = 0; i < 9; i++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("3.00"), "pears"), receiver));
		}
		this.receiver.process(new BatchSaleMessage(new Sale(new BigDecimal("4.00"), "pears"), 10, receiver));
		for (int j = 0; j < 10; j++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("2.00"), "pears"), receiver));
		}

		assertTrue(receiver.getProduct2SaleListMap().containsKey("pears"));
		assertTrue(receiver.getMsgQueue().size() == 20);
		assertTrue(receiver.getProduct2SaleListMap().get("pears").size() == 29);
		assertTrue(receiver.getProduct2SaleListMap().get("pears").stream().map(sale -> sale.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add).equals(new BigDecimal("87.00")));
		System.out.println("TEST " + new Object() {
		}.getClass().getEnclosingMethod().getName() + " finished.");
	}

	@Test
	public void testAdjustmentAddSaleMessages() {
		System.out.println("Starting test " + new Object() {
		}.getClass().getEnclosingMethod().getName() + "...");

		for (int i = 0; i < 4; i++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("5.00"), "bananas"), receiver));
		}
		this.receiver.process(new AdjustmentSaleMessage(new Sale(new BigDecimal("10.00"), "bananas"),
				AdjustmentOperation.ADD, new BigDecimal(1.00), receiver));

		for (int j = 0; j < 5; j++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("6.00"), "bananas"), receiver));
		}

		assertTrue(receiver.getProduct2SaleListMap().containsKey("bananas"));
		assertTrue(receiver.getMsgQueue().size() == 10);
		assertTrue(receiver.getProduct2SaleListMap().get("bananas").size() == 10);
		assertTrue(receiver.getProduct2SaleListMap().get("bananas").stream().map(sale -> sale.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add).equals(new BigDecimal("65.00")));
		System.out.println("TEST " + new Object() {
		}.getClass().getEnclosingMethod().getName() + " finished.");
	}

	@Test
	public void testAdjustmentSubtractSaleMessages() {
		System.out.println("Starting test " + new Object() {
		}.getClass().getEnclosingMethod().getName() + "...");

		for (int i = 0; i < 4; i++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("5.00"), "bananas"), receiver));
		}
		this.receiver.process(new AdjustmentSaleMessage(new Sale(new BigDecimal("10.00"), "bananas"),
				AdjustmentOperation.SUBTRACT, new BigDecimal(1.00), receiver));

		for (int j = 0; j < 5; j++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("6.00"), "bananas"), receiver));
		}

		assertTrue(receiver.getProduct2SaleListMap().containsKey("bananas"));
		assertTrue(receiver.getMsgQueue().size() == 10);
		assertTrue(receiver.getProduct2SaleListMap().get("bananas").size() == 10);
		assertTrue(receiver.getProduct2SaleListMap().get("bananas").stream().map(sale -> sale.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add).equals(new BigDecimal("55.00")));
		System.out.println("TEST " + new Object() {
		}.getClass().getEnclosingMethod().getName() + " finished.");
	}

	@Test
	public void testAdjustmentMultiplySaleMessages() {
		System.out.println("Starting test " + new Object() {
		}.getClass().getEnclosingMethod().getName() + "...");

		for (int i = 0; i < 4; i++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("5.00"), "bananas"), receiver));
		}
		this.receiver.process(new AdjustmentSaleMessage(new Sale(new BigDecimal("10.00"), "bananas"),
				AdjustmentOperation.MULTIPLY, new BigDecimal(2.00), receiver));

		for (int j = 0; j < 5; j++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("6.00"), "bananas"), receiver));
		}

		assertTrue(receiver.getProduct2SaleListMap().containsKey("bananas"));
		assertTrue(receiver.getMsgQueue().size() == 10);
		assertTrue(receiver.getProduct2SaleListMap().get("bananas").size() == 10);
		assertTrue(receiver.getProduct2SaleListMap().get("bananas").stream().map(sale -> sale.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add).equals(new BigDecimal("90.00")));
		System.out.println("TEST " + new Object() {
		}.getClass().getEnclosingMethod().getName() + " finished.");
	}

	@Test
	public void testAdjustmentDivideSaleMessages() {
		System.out.println("Starting test " + new Object() {
		}.getClass().getEnclosingMethod().getName() + "...");

		for (int i = 0; i < 4; i++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("4.00"), "bananas"), receiver));
		}
		this.receiver.process(new AdjustmentSaleMessage(new Sale(new BigDecimal("10.00"), "bananas"),
				AdjustmentOperation.DIVIDE, new BigDecimal(2.00), receiver));

		for (int j = 0; j < 5; j++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("6.00"), "bananas"), receiver));
		}

		assertTrue(receiver.getProduct2SaleListMap().containsKey("bananas"));
		assertTrue(receiver.getMsgQueue().size() == 10);
		assertTrue(receiver.getProduct2SaleListMap().get("bananas").size() == 10);
		assertTrue(receiver.getProduct2SaleListMap().get("bananas").stream().map(sale -> sale.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add).equals(new BigDecimal("43.00")));
		System.out.println("TEST " + new Object() {
		}.getClass().getEnclosingMethod().getName() + " finished.");
	}

	@Test
	public void testSequenceOfAdjustmentSaleMessages() {
		System.out.println("Starting test " + new Object() {
		}.getClass().getEnclosingMethod().getName() + "...");

		for (int i = 0; i < 24; i++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("2.00"), "kiwi"), receiver));
		}
		this.receiver.process(new AdjustmentSaleMessage(new Sale(new BigDecimal("2.00"), "kiwi"),
				AdjustmentOperation.ADD, new BigDecimal(1.00), receiver));

		for (int j = 0; j < 24; j++) {
			this.receiver.process(new SimpleSaleMessage(new Sale(new BigDecimal("4.00"), "kiwi"), receiver));
		}
		this.receiver.process(new AdjustmentSaleMessage(new Sale(new BigDecimal("4.00"), "kiwi"),
				AdjustmentOperation.SUBTRACT, new BigDecimal(1.00), receiver));

		assertTrue(receiver.getProduct2SaleListMap().containsKey("kiwi"));
		assertTrue(receiver.getMsgQueue().size() == 50);
		assertTrue(receiver.getProduct2SaleListMap().get("kiwi").size() == 50);
		assertTrue(receiver.getProduct2SaleListMap().get("kiwi").stream().map(sale -> sale.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add).equals(new BigDecimal("125.00")));
		System.out.println("TEST " + new Object() {
		}.getClass().getEnclosingMethod().getName() + " finished.");
	}

	@Override
	public void tearDown() {
		this.receiver = null;
		System.out.println("*********************************************************\n\n");
	}

}
