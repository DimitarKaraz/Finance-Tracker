package com.example.financetracker.controller;

import com.example.financetracker.exceptions.*;
import com.example.financetracker.model.dto.ExceptionDTO;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDTO> handleBadRequestException(BadRequestException e){
        return ResponseEntity.badRequest().body(new ExceptionDTO(HttpStatus.BAD_REQUEST, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        e.printStackTrace();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.get(fieldErrors.size() - 1).getDefaultMessage();
        return ResponseEntity.badRequest().body(new ExceptionDTO(HttpStatus.BAD_REQUEST, message, LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDTO> handleInvalidFormatException(InvalidFormatException e){
        return ResponseEntity.status(400).body(new ExceptionDTO(HttpStatus.BAD_REQUEST, "Invalid input data.", LocalDateTime.now()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ExceptionDTO> handleUnauthorizedException(UnauthorizedException e){
        // This exception is thrown if the userId of the current principal doesn't exist in the database.
        // Therefore, it is a good idea to log out the user (and redirect him to login page)
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        new SecurityContextLogoutHandler().logout(request, null, null);

        return ResponseEntity.status(401).body(new ExceptionDTO(HttpStatus.UNAUTHORIZED, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ExceptionDTO> handleForbiddenException(ForbiddenException e){
        return ResponseEntity.status(403).body(new ExceptionDTO(HttpStatus.FORBIDDEN, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionDTO> handleNotFoundException(NotFoundException e){
        return ResponseEntity.status(404).body(new ExceptionDTO(HttpStatus.NOT_FOUND, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodNotAllowed.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ExceptionDTO> handleMethodNotAllowedException(MethodNotAllowed e){
        return ResponseEntity.status(405).body(new ExceptionDTO(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(NotImplementedException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ResponseEntity<ExceptionDTO> handleNotImplementedException(NotImplementedException e){
        return ResponseEntity.status(501).body(new ExceptionDTO(HttpStatus.NOT_IMPLEMENTED, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionDTO> handleException(Exception e){
        e.printStackTrace();
        return ResponseEntity.status(500).body(new ExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), LocalDateTime.now()));
    }


    @ExceptionHandler(FileTransferException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDTO> handleFileTransferException(FileTransferException e){
        return ResponseEntity.badRequest().body(new ExceptionDTO(HttpStatus.BAD_REQUEST, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class,
                        MethodArgumentTypeMismatchException.class,
                        PageNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionDTO> handleNoHandlerFoundException() {
        return ResponseEntity.status(404).body(new ExceptionDTO(HttpStatus.NOT_FOUND,
                "PAGE NOT FOUND. (See if you are using appropriate HTTP request method and url.)", LocalDateTime.now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ExceptionDTO> handleFileTransferException(AccessDeniedException e){
        // This exception is thrown if the userId of the current principal doesn't exist in the database.
        // Therefore, it is a good idea to log out the user (and redirect him to login page)
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        new SecurityContextLogoutHandler().logout(request, null, null);
        return ResponseEntity.status(401).body(new ExceptionDTO(HttpStatus.UNAUTHORIZED, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(400).body(new ExceptionDTO(HttpStatus.BAD_REQUEST, "Invalid input data.", LocalDateTime.now()));
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionDTO> handleBadSqlGrammarException(BadSqlGrammarException e) {
        return ResponseEntity.status(500).body(new ExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR,
                "Sorry, we are experiencing some trouble.", LocalDateTime.now()));
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDTO> handleFileSizeLimitExceededException(FileSizeLimitExceededException e) {
        return ResponseEntity.status(500).body(new ExceptionDTO(HttpStatus.BAD_REQUEST,
                "The file you are trying to upload is too big, it cannot exceed 50mb.", LocalDateTime.now()));
    }


}
