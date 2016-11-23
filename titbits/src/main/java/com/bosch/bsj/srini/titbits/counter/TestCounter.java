package com.bosch.bsj.srini.titbits.counter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.bosch.bsj.srini.titbits.counter.CounterFactory.CounterType;



public class TestCounter {
	public static void main(String[] args) throws InterruptedException, ExecutionException {

		final Counter c = CounterFactory.createCounter(CounterType.ATOMIC);
		int poolSize = 3;
		int jobCount = 3;
		Runnable r = () -> {
			for (int i = 0; i < 1000; i++) {
				c.increment();
			}
		};

		List<Future<?>> futures = new ArrayList<Future<?>>();

		ExecutorService pool = Executors.newFixedThreadPool(poolSize);
		for (int i = 0; i < jobCount; i++) {
			Future<?> aFuture = pool.submit(r);
			futures.add(aFuture);
		}
		
		// Blocking way
		//for(Future<?> future: futures)
		//	  future.get();
		
		// Non-Blocking way
		boolean allDone;
		do{
			allDone = true;
			for(Future<?> future: futures){
				allDone &= future.isDone();
			}
		}while(!allDone);
		
		
		System.out.println("All tasks are complete!");
		pool.shutdown();

		System.out.println("C value :" + c.value());
	}
}
