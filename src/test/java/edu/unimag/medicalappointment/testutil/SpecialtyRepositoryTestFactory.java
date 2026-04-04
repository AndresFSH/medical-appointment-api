package edu.unimag.medicalappointment.testutil;

import edu.unimag.medicalappointment.domain.entity.Specialty;

import java.util.UUID;

public class SpecialtyRepositoryTestFactory {

    public static Specialty create(String name){
        return Specialty.builder().name(name).build();
    }

    public static Specialty create(){
        return  Specialty.builder().name("specialty"+ UUID.randomUUID()).build();
    }

}
