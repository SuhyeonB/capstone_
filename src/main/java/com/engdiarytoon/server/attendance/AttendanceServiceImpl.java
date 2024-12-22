package com.engdiarytoon.server.attendance;

import com.engdiarytoon.server.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    @Transactional
    public Attendance markAttendance(User user) {
        LocalDate today = LocalDate.now();

        if (isAttendanceMarkedToday(user)) {
            throw new IllegalStateException("Attendance already marked for today.");
        }

        Attendance attendance = Attendance.builder()
                .user(user)
                .date(today)
                .build();

        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> getAttendanceHistory(User user, int year, int month)
    {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return attendanceRepository.findByUserAndDateBetween(user, startDate, endDate);
    }

    @Override
    public boolean isAttendanceMarkedToday(User user) {
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByUserAndDate(user, today).isPresent();
    }
}
