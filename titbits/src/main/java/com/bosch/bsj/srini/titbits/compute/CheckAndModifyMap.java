package com.bosch.bsj.srini.titbits.compute;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CheckAndModifyMap {
	private static final String STAR_WARS = "Star Wars";

	private static ExecutorService executorService;

	private enum ComputationStrategy {
		CHECK_PUT, REPEAT_CHECK_REPLACE, COMPUTE_IF
	};

	static Map<String, BigDecimal> movieViewsMap = new ConcurrentHashMap<String, BigDecimal>();

	public static void main(String[] args) throws InterruptedException {

		for (ComputationStrategy aStrategy : ComputationStrategy.values()) {
			
			executorService = Executors.newFixedThreadPool(3);
			movieViewsMap.put(STAR_WARS, BigDecimal.ZERO);
			
			updateViewsInParallel(movieViewsMap, aStrategy);

			executorService.shutdown();

			try {
				while (!executorService.awaitTermination(1, TimeUnit.SECONDS));
				System.out.println(movieViewsMap);

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}

	private static void updateViewsInParallel(Map<String, BigDecimal> map, ComputationStrategy aStrategy) {

		Consumer<Map<String, BigDecimal>> consumer = fetchConsumer(aStrategy);

		for (int i = 1; i <= 10000; i++) {
			executorService.submit(() -> consumer.accept(map));
		}

	}

	private static Consumer<Map<String, BigDecimal>> fetchConsumer(ComputationStrategy aStrategy) {
		switch (aStrategy) {
		case CHECK_PUT:
			return checkAndPutConsumer;
		case REPEAT_CHECK_REPLACE:
			return repeatCheckAndReplaceConsumer;
		case COMPUTE_IF:
			return computeIfConsumer;
		}
		throw new IllegalArgumentException("No such strategy exception: " + aStrategy);
	}

	private static Consumer<Map<String, BigDecimal>> checkAndPutConsumer = (Map<String, BigDecimal> map) -> {
		BigDecimal views = map.get(STAR_WARS);
		if (views != null) {
			map.put(STAR_WARS, views.add(BigDecimal.ONE));
		}
	};
	private static Consumer<Map<String, BigDecimal>> repeatCheckAndReplaceConsumer = (Map<String, BigDecimal> map) -> {
		while (true) {
			BigDecimal views = map.get(STAR_WARS);
			if (views != null) {
				if (map.replace(STAR_WARS, views, views.add(BigDecimal.ONE))) {
					break;
				}
			}
		}
	};
	private static Consumer<Map<String, BigDecimal>> computeIfConsumer = (Map<String, BigDecimal> map) -> {
		map.computeIfPresent(STAR_WARS, (movie, views) -> views.add(BigDecimal.ONE));
	};

}
