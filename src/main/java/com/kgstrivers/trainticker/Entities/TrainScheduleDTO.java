package com.kgstrivers.trainticker.Entities;
import java.time.LocalDate;

public record TrainScheduleDTO(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        String runningDays,
        Boolean monday,
        Boolean tuesday,
        Boolean wednesday,
        Boolean thursday,
        Boolean friday,
        Boolean saturday,
        Boolean sunday,
        Boolean active
) {
    public static TrainScheduleDTO from(TrainSchedule s) {
        return new TrainScheduleDTO(
                s.getId(),
                s.getStartDate(),
                s.getEndDate(),
                s.getRunningDays(),
                s.getMonday(),
                s.getTuesday(),
                s.getWednesday(),
                s.getThursday(),
                s.getFriday(),
                s.getSaturday(),
                s.getSunday(),
                s.getActive()
        );
    }
}
