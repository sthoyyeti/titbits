package com.bosch.bsj.srini.titbits.volatale;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

public class VolatileTester {
	//private static final Logger log = Logger.getLogger(VolatileTester.class.getSimpleName());
	

	public static void main(String[] args) {

		Temperature temp = new Temperature(false, 0);

		Producer producer = new Producer(temp);
		producer.setName("Producer");

		Consumer consumer = new Consumer(temp);
		consumer.setName("Consumer");

		producer.start();
		consumer.start();
	}

}

class Consumer extends Thread {
	private static final Logger log = Logger.getLogger(Consumer.class.getSimpleName());
	private Temperature temperature;
	SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");

	Consumer(Temperature aTemperature) {
		this.temperature = aTemperature;
	}

	public Temperature getTemperature() {
		return temperature;
	}

	public void run() {
		while (true) {

			while (!temperature.getUpdated());

			log.info(sdf.format(new Date()) + "   " + this.getName() + "  New value: " + temperature.getValue());
			temperature.setUpdated(false);

		}
	}

}

class Producer extends Thread {
	private static final Logger log = Logger.getLogger(Producer.class.getSimpleName());
	private Temperature temp;
	private Random random = new Random();

	Producer(Temperature aTemperature) {
		this.temp = aTemperature;
	}

	@SuppressWarnings("static-access")
	public void run() {

		while (true) {
			try {
				currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			temp.setValue(random.nextInt(100));
			temp.setUpdated(true);
			log.info(this.getName() + " new value = " + temp.getValue());
		}
	}

}

class Temperature {
	private volatile Boolean updated = false;

	private Integer value;

	public Boolean getUpdated() {
		return updated;
	}

	public Temperature(Boolean updated, Integer value) {
		super();
		this.updated = updated;
		this.value = value;
	}

	public void setUpdated(Boolean updated) {
		this.updated = updated;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
