package com.creant.graph.controller;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;

import com.creant.graph.exception.CustomGenericException;

/**
 * @author lamhm
 *
 */
@ControllerAdvice
public class GlobalExceptionController {

	@ExceptionHandler(CustomGenericException.class)
	public ModelAndView handleCustomException(CustomGenericException ex) {
		ModelAndView model = new ModelAndView("error");
		model.addObject("code", ex.getErrCode());
		model.addObject("message", ex.getErrMsg());
		return model;
	}


	@ExceptionHandler(NestedServletException.class)
	public ModelAndView handleCustomException(NestedServletException ex) {
		ModelAndView model = new ModelAndView("error");
		return model;
	}


	@ExceptionHandler(Exception.class)
	public ModelAndView handleAllException(Exception ex) {
		ModelAndView mv = new ModelAndView("error");
		if (ex instanceof MissingServletRequestParameterException) {
			mv.addObject("message", "Thiếu thông tin. Yêu cầu không được chấp nhận.");
		} else {
			mv.addObject("message", "Xảy ra lỗi! <br>" + ex.toString());
		}

		return mv;
	}


	@RequestMapping(value = "/error")
	public ModelAndView error() {
		ModelAndView mv = new ModelAndView("hello");
		mv.addObject("message", "Hello World!");
		return mv;
	}
}
