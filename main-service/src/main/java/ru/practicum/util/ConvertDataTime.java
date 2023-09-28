package ru.practicum.util;

import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public interface ConvertDataTime {
    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    LocalDateTime MIN_DATE_TIME = LocalDateTime.of(1900, 1, 1, 1, 1, 1);
    LocalDateTime MAX_DATE_TIME = LocalDateTime.of(4000, 1, 1, 1, 1, 1);

    static LocalDateTime formatDateTime(String dateTime) {
        LocalDateTime newDateTime;
        if (dateTime == null || dateTime.isBlank()) {
            throw new ValidationException("Дата и время должны быть установлены");
        }
        try {
            newDateTime = LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Не верный формат даты и времени: " + dateTime);
        }
        return newDateTime;
    }

    static String dateTimeToString(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(dateTime);
    }
}
