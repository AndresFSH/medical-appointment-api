package edu.unimag.medicalappointment.testutil;

import edu.unimag.medicalappointment.domain.entity.Doctor;
import edu.unimag.medicalappointment.domain.entity.Specialty;

import java.util.UUID;

public class DoctorRepositoryTestFactory {

    public static Doctor create(Specialty specialty){
        return Doctor.builder().fullName("doctor"+ UUID.randomUUID()).active(true).specialty(specialty).build();
    }

    public static Doctor create(String fullName, boolean active, Specialty specialty){
        return Doctor.builder().fullName(fullName).specialty(specialty).active(active).build();
    }

}
