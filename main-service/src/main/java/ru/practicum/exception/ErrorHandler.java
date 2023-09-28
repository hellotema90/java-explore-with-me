package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice()
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return new ApiError(e, "Ошибка 404. Нужный объект не найден", HttpStatus.NOT_FOUND.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final ForbiddenException e) {
        return new ApiError(e, "Ошибка 403. Для запрошенной операции условия не выполнены",
                HttpStatus.FORBIDDEN.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        return new ApiError(e, "Ошибка 400. Неправильно составлен запрос", HttpStatus.BAD_REQUEST.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        return new ApiError(e, "Ошибка 409. Название уже существует", HttpStatus.CONFLICT.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return new ApiError(e, "409. Название уже существует", HttpStatus.CONFLICT.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String strError = e.getMessage();
        String strSubString = "default message";
        int index = strError.lastIndexOf(strSubString);
        String strMessage = index == 0 ? "" : strError.substring(index + strSubString.length());
        strError = String.format("Ошибка 400. Неправильно составлен запрос: %s", strMessage.isBlank() ? strError : strMessage);
        return new ApiError(e, strError, HttpStatus.BAD_REQUEST.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        String strError = e.getMessage();
        String strSubString = "default message";
        int index = strError.lastIndexOf(strSubString);
        String strMessage = index == 0 ? "" : strError.substring(index + strSubString.length());
        strError = String.format("Ошибка 400. Неправильно составлен запрос %s", strMessage.isBlank() ? strError : strMessage);
        return new ApiError(e, strError, HttpStatus.BAD_REQUEST.name());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        return new ApiError(e, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.name());
    }
}
