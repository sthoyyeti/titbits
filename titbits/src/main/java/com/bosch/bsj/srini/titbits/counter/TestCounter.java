package com.bosch.bsj.srini.titbits.counter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.bosch.bsj.srini.titbits.counter.CounterFactory.CounterType;

public class TestCounter {
	private static List<Future<?>> futures = new ArrayList<Future<?>>();
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {

		for (CounterType counterType : CounterType.values()) {
			futures.clear();
			final Counter c = CounterFactory.createCounter(counterType);

			ExecutorService pool = Executors.newFixedThreadPool(3);
			for (int i = 0; i < 3; i++) {
				Future<?> aFuture = pool.submit(() -> {
					for (int j = 0; j < 1000; j++) {
						c.increment();
					}
				});
				futures.add(aFuture);
			}

			//waitTillTasksDoneInBlockingWay();
			waitTillTasksDoneInNonBlockingWay();			

			System.out.print(counterType + ":- All tasks complete...");
			pool.shutdown();
			System.out.println("C Value :" + c.value());
		}
		
		
	}
	private void waitTillTasksDoneInBlockingWay() throws InterruptedException, ExecutionException{
		 for(Future<?> future: futures)
		   future.get();
	}
	
	private static void waitTillTasksDoneInNonBlockingWay(){
		boolean allDone;
		do {
			allDone = true;
			for (Future<?> future : futures) {
				allDone &= future.isDone();
			}
		} while (!allDone);
	}
}
