package com.bosch.bsj.srini.titbits.compute;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ComputeIfExample {
    public static final String STAR_WARS = "Star Wars";
    public static ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws InterruptedException {
	Map<String, BigDecimal> map = new ConcurrentHashMap<String, BigDecimal>();
	map.put(STAR_WARS, BigDecimal.ZERO);

	updateViewsInParallel(map);

	// Thread.sleep(5000);

	executorService.shutdown();

	try {
	    while (!executorService.awaitTermination(1, TimeUnit.SECONDS))
		;
	    System.out.println(map);

	} catch (InterruptedException e) {

	    e.printStackTrace();
	}

    }

    private static void updateViewsInParallel(Map<String, BigDecimal> map) {
	for (int i = 1; i <= 10000; i++) {
	    executorService.submit(() -> incrementOneView3(map));
	}

    }

    private static void incrementOneView1(Map<String, BigDecimal> map) {
	BigDecimal views = map.get(STAR_WARS);
	if (views != null) {
	    map.put(STAR_WARS, views.add(BigDecimal.ONE));
	}
	return;
    }

    private static void incrementOneView2(Map<String, BigDecimal> map) {

	while (true) {
	    BigDecimal views = map.get(STAR_WARS);
	    if (views != null) {
		if (map.replace(STAR_WARS, views, views.add(BigDecimal.ONE))) {
		    break;
		}
	    }
	}

    }

    private static void incrementOneView3(Map<String, BigDecimal> map) {

	map.computeIfPresent(STAR_WARS, (movie, views) -> views.add(BigDecimal.ONE));

    }
}
