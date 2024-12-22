package com.engdiarytoon.server.attendance;

import com.engdiarytoon.server.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // Mark attendance for today
    @PostMapping
    public ResponseEntity<String> markAttendance(@AuthenticationPrincipal User user) {
        try {
            attendanceService.markAttendance(user);
            return ResponseEntity.ok("Success");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Attendance already marked for today.");
        }
    }

    // Get attendance history for the authenticated user
    @GetMapping("/history")
    public ResponseEntity<List<Attendance>> getAttendanceHistory(
            @AuthenticationPrincipal User user,
            @RequestParam("year") int year, @RequestParam("month") int month) {
        List<Attendance> attendanceHistory = attendanceService.getAttendanceHistory(user, year, month);
        return ResponseEntity.ok(attendanceHistory);
    }
}
