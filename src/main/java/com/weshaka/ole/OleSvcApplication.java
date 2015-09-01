package com.weshaka.ole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.weshaka.ole.repository.BeaconSubjectRepository;
import com.weshaka.ole.repository.CustomerRepository;

@SpringBootApplication
public class OleSvcApplication /*implements CommandLineRunner*/ {
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private BeaconSubjectRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(OleSvcApplication.class, args);
	}
/*
	@Override
	public void run(String... arg0) throws Exception {
		repository.deleteAll();

		// save a couple of customers
		UUID uuid = UUID.randomUUID();
		Beacon beacon1 = new Beacon();
		beacon1.setBeaconType(BeaconType.IBEACON);
		beacon1.setBrand("Adafruit");
		beacon1.setColor("BLACK");
		beacon1.setPowerlevel(1);
		beacon1.setUpc("12345");
		beacon1.setMac("C1:5C:A0:2A:EC:F0");
		beacon1.setModel("kkk");
		
		Location location = new Location();
		location.setCity("Sammamish");
		location.setCountry("USA");
		location.setState("WA");
		location.setLatitude(111);
		location.setLongitude(222);
		
		BeaconSubject bo1 = new BeaconSubject();
		bo1.setName("room1");
		bo1.setId(uuid.toString());
		bo1.setBeacon(beacon1);
		bo1.setBeaconSubjectType(BeaconSubjectType.ConferenceRoom);
		bo1.setLocation(location);
		
		repository.save(bo1);

		// fetch all customers
		System.out.println("Beacons found with findAll():");
		System.out.println("-------------------------------");
		for (BeaconSubject beaconSubject : repository.findAll()) {
			System.out.println(beaconSubject);
		}
		System.out.println();
	}
*/
}
