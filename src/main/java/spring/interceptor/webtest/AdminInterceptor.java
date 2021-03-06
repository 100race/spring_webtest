package spring.interceptor.webtest;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import spring.utility.webtest.Utility;

public class AdminInterceptor extends HandlerInterceptorAdapter {

	//요청 주소 결과를 생성하기 전에 실행
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession(true);
		
		System.out.println("preHandle executed");
		
		//로그인시 관리자인지 확인
		String grade = Utility.checkNull((String)session.getAttribute("grade"));
		
		if(grade.equals("A")) {//관리자이면
			return true; //요청페이지로 계속 진행(admin/list)
		}else {
			//로그인 페이지로 이동
			response.sendRedirect(request.getContextPath()+"/member/login");
			return false; 
		}
		
	}
	
	//요청 처리 완료후 호출
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("postHandle executed");
		modelAndView.addObject("admin","관리자 관련 기능을 출력합니다.");
		
	}

	//jsp등 뷰페이지 출력 완전이전에 작동된다.
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("afterCompletion executed");
		
	}



}
