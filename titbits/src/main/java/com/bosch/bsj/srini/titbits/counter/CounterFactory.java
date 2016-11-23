package com.bosch.bsj.srini.titbits.counter;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterFactory {

	static enum CounterType {
		VULNERABLE, SYNCHRONIZED, ATOMIC
	};

	public static Counter createCounter(CounterType type) {
		switch (type) {
		case VULNERABLE:
			return new VulnerableCounter();
		case SYNCHRONIZED:
			return new SynchronizedCounter();
		case ATOMIC:
			return new AtomicCounter();
		}

		throw new IllegalArgumentException("No Such Counter type defined!");
	}

}

interface Counter {

	void increment();

	void decrement();

	int value();

}

class VulnerableCounter implements Counter {
	private int c = 0;

	@Override
	public void increment() {
		c++;
	}

	@Override
	public void decrement() {
		c--;
	}

	@Override
	public int value() {
		return c;
	}

}

class SynchronizedCounter implements Counter {
	private int c = 0;

	@Override
	public synchronized void increment() {
		c++;
	}

	@Override
	public synchronized void decrement() {
		c--;
	}

	@Override
	public synchronized int value() {
		return c;
	}

}

class AtomicCounter implements Counter {
	private AtomicInteger c = new AtomicInteger(0);

	@Override
	public void increment() {
		c.incrementAndGet();
	}

	@Override
	public void decrement() {
		c.decrementAndGet();
	}

	@Override
	public int value() {
		return c.get();
	}

}
