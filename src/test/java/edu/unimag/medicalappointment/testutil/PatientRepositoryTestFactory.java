package edu.unimag.medicalappointment.testutil;

import edu.unimag.medicalappointment.domain.entity.Patient;
import edu.unimag.medicalappointment.domain.entity.enums.PatientStatus;

import java.util.UUID;

public class PatientRepositoryTestFactory {

    public static Patient create(){
        return Patient.builder().fullName("fullName").email("patient"+ UUID.randomUUID()+"@mail.com")
                .status(PatientStatus.ACTIVE).build();
    }

    public static Patient create(String name, String email, PatientStatus status){
        return Patient.builder().fullName(name).email(email).status(status).build();
    }

}
