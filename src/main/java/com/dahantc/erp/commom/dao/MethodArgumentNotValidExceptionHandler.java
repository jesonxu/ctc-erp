package com.dahantc.erp.commom.dao;

import java.util.ArrayList;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dahantc.erp.controller.BaseResponse;

@RestControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

	@ExceptionHandler(value = org.springframework.web.bind.MethodArgumentNotValidException.class)
	public BaseResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) throws Exception {
		BindingResult bindingResult = e.getBindingResult();
		return BaseResponse.error(bindingResult.getFieldError().getDefaultMessage());
	}

	@ExceptionHandler(value = org.springframework.validation.BindException.class)
	public BaseResponse<String> handleBindException(BindException e) throws Exception {
		BindingResult bindingResult = e.getBindingResult();
		return BaseResponse.error(bindingResult.getFieldError().getDefaultMessage());
	}

	@ExceptionHandler(value = javax.validation.ConstraintViolationException.class)
	public BaseResponse<String> handleConstraintViolationException(ConstraintViolationException e) throws Exception {
		Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
		ConstraintViolation<?> constraintViolation = new ArrayList<ConstraintViolation<?>>(constraintViolations).get(0);
		return BaseResponse.error(constraintViolation.getMessage());
	}

}