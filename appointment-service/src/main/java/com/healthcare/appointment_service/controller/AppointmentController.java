package com.healthcare.appointment_service.controller;

import com.healthcare.appointment_service.domain.Appointment;
import com.healthcare.appointment_service.dto.AppointmentDto;
import com.healthcare.appointment_service.dto.Doctor;
import com.healthcare.appointment_service.dto.Patient;
import com.healthcare.appointment_service.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<AppointmentDto> createAppointment(@RequestBody AppointmentDto dto) {
        Patient patient = appointmentService.getPatientById(dto.patientId());
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Doctor doctor = appointmentService.getDoctorById(dto.doctorId());
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        var appointment = dto.toAppointment();
        appointment.setPatientName(patient.fullName());
        appointment.setPatientNumber(patient.phone());
        appointment.setDoctorName(doctor.fullName());
        var response = appointmentService.createAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentDto.fromAppointment(response));
    }

    @GetMapping("/getAppointment")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDoctor(@RequestParam String id) {
        var appointments = appointmentService.findAllByDoctorId(id);
        if(appointments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(AppointmentDto.toDtos(appointments));
    }

}
