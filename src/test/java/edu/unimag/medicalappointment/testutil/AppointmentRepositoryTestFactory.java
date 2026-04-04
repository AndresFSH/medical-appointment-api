package edu.unimag.medicalappointment.testutil;

import edu.unimag.medicalappointment.domain.entity.*;

import java.time.LocalDateTime;

public class AppointmentRepositoryTestFactory {

    public static Appointment scheduledAppointment(Patient patient, Doctor doctor, Office office,
                                                   AppointmentType appointmentType, LocalDateTime startAt) {
        return Appointment.schedule(patient,doctor,office,appointmentType,startAt);
    }

    public static Appointment confirmedAppointment(Patient patient, Doctor doctor, Office office,
                                                   AppointmentType type, LocalDateTime startAt) {
        Appointment appointment = Appointment.schedule(patient,doctor,office,type,startAt);
        appointment.confirm();
        return appointment;
    }

    public static Appointment noShowAppointment(Patient patient, Doctor doctor, Office office,
                                                AppointmentType appointmentType, LocalDateTime startAt) {
        Appointment appointment = confirmedAppointment(patient, doctor, office, appointmentType, startAt);
        appointment.markNoShow();
        return appointment;
    }

    public static Appointment cancelledAppointmentFromScheduled(Patient patient, Doctor doctor, Office office,
                                                   AppointmentType appointmentType, LocalDateTime startAt,
                                                   String cancellationReason) {
        Appointment appointment = Appointment.schedule(patient,doctor,office,appointmentType,startAt);
        appointment.cancel(cancellationReason);
        return appointment;
    }

    public static Appointment cancelledAppointmentFromConfirmed(Patient patient, Doctor doctor, Office office,
                                                                AppointmentType appointmentType, LocalDateTime startAt,
                                                                String cancellationReason) {
        Appointment appointment = confirmedAppointment(patient, doctor, office, appointmentType, startAt);
        appointment.cancel(cancellationReason);
        return appointment;
    }

    public static Appointment completedAppointment(Patient patient, Doctor doctor, Office office,
                                                    AppointmentType type, LocalDateTime startAt,String observations) {
        Appointment appointment = confirmedAppointment(patient, doctor, office, type, startAt);
        appointment.complete(observations);
        return appointment;
    }

}
