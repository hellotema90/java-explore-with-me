package ru.practicum.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.util.ConvertDataTime;

import java.time.LocalDateTime;

@Slf4j
@Getter
public class ApiError {
    private final String message;
    private final String reason;
    private final String status;
    private final String timestamp;

    public ApiError(Exception e, String reason, String status) {
        log.error(e.getMessage(), e);
        this.status = status;
        this.reason = reason;
        this.message = e.getMessage();
        this.timestamp = LocalDateTime.now().format(ConvertDataTime.DATE_TIME_FORMATTER);
    }
}
