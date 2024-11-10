package com.engdiarytoon.server.attendance;

import com.engdiarytoon.server.user.User;

import java.util.List;

public interface AttendanceService {

    // Mark attendance for today
    Attendance markAttendance(User user);

    // Get attendance history for a user
    List<Attendance> getAttendanceHistory(User user);

    // Check if user has already marked attendance for today
    boolean isAttendanceMarkedToday(User user);
}
