package edu.unimag.medicalappointment.testutil;

import edu.unimag.medicalappointment.domain.entity.AppointmentType;

import java.util.UUID;

public class AppointmentTypeRepositoryTestFactory {

    public static AppointmentType create(String name, Integer durationMinutes){
        return AppointmentType.builder().name(name).durationMinutes(durationMinutes).build();
    }

    public static AppointmentType create(){
        return AppointmentType.builder().name("name"+ UUID.randomUUID()).durationMinutes(60).build();
    }

}
