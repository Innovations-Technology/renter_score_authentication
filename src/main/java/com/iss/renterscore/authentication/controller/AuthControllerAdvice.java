package com.iss.renterscore.authentication.controller;

import com.iss.renterscore.authentication.exceptions.*;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Locale;

@RestControllerAdvice
public class AuthControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(AuthControllerAdvice.class);

    private final MessageSource messageSource;

    @Autowired
    public AuthControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse processValidationError(MethodArgumentNotValidException exception, WebRequest request) {
        BindingResult result = exception.getBindingResult();
        List<ObjectError> allErrors = result.getAllErrors();
        String data = String.join("\n", processAllErrors(allErrors));
        return new ApiResponse(data, false, exception.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

     /*   Utility function to generate localized error messages   */
    private List<String> processAllErrors(List<ObjectError> allErrors) {
        return allErrors.stream().map(this::resolveLocalizedErrorMessage).toList();
    }

    /*   Utility function to resolve localized error messages   */
    private String resolveLocalizedErrorMessage(ObjectError objectError) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String localizedErrorMessage = messageSource.getMessage(objectError, currentLocale);
        logger.info(localizedErrorMessage);
        return localizedErrorMessage;
    }

    /*   Utility function to route the request uri   */
    private String resolvePathFromWebRequest(WebRequest request) {
        try {
            return ((ServletWebRequest) request).getRequest().getAttribute("javax.servlet.forward.request_uri").toString();
        } catch (Exception ex) {
            return null;
        }
    }

    @ExceptionHandler(value = AppException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse handleAppException(AppException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = ResourceAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiResponse handleResourceAlreadyInUseException(ResourceAlreadyInUseException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResponse handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse handleBadRequestException(BadRequestException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResponse handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = UserLoginException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public ApiResponse handleUserLoginException(UserLoginException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public ApiResponse handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = UserRegistrationException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public ApiResponse handleUserRegistrationException(UserRegistrationException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = PasswordResetLinkException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public ApiResponse handlePasswordResetLinkException(PasswordResetLinkException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = PasswordResetException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public ApiResponse handlePasswordResetException(PasswordResetException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = MailSendException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ApiResponse handleMailSendException(MailSendException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = InvalidTokenRequestException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public ApiResponse handleInvalidTokenRequestException(InvalidTokenRequestException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = UpdatePasswordException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public ApiResponse handleUpdatePasswordException(UpdatePasswordException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public ApiResponse handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ApiResponse handleTokenExpiredException(TokenExpiredException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = UserLogoutException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public ApiResponse handleUserLogoutException(UserLogoutException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ApiResponse handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }

    @ExceptionHandler(value = CreationException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public ApiResponse handleCreationException(CreationException ex, WebRequest request) {
        return new ApiResponse(ex.getMessage(), false, ex.getClass().getSimpleName(), resolvePathFromWebRequest(request));
    }
}
