package edu.unimag.medicalappointment.testutil;

import edu.unimag.medicalappointment.domain.entity.Office;
import edu.unimag.medicalappointment.domain.entity.enums.OfficeStatus;

import java.util.UUID;

public class OfficeRepositoryTestFactory {

    public static Office create(String name, String location, OfficeStatus status) {
        return Office.builder().name(name).location(location).status(status).build();
    }

    public static Office create(String name){
        return Office.builder().name(name).location("location"+ UUID.randomUUID()).status(OfficeStatus.AVAILABLE).build();
    }

}
