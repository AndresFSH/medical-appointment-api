package edu.unimag.medicalappointment;

import org.springframework.boot.SpringApplication;

public class TestMedicalAppointmentApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(MedicalAppointmentApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
