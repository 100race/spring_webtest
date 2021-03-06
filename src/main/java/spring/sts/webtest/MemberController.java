package spring.sts.webtest;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import spring.model.member.MemberDTO;
import spring.model.member.MemberMapper;
import spring.utility.webtest.Utility;

@Controller
public class MemberController {
	
	@Autowired
	private MemberMapper mapper;
	
	/* 회원 사진 수정 */
	@PostMapping("/member/updateFile")
	public String updateFile(MultipartFile fnameMF, String oldfile,
			HttpSession session, //id 필요
			HttpServletRequest request) { //절대경로 위해 필요
		
		String basePath = request.getRealPath("/storage"); //업로드 경로 잡으려면 request필요
		//oldfile먼저 삭제해줌
		
		if(oldfile !=null && !oldfile.equals("member.jpg")) { //원본파일 삭제
			Utility.deleteFile(basePath, oldfile); 
		}
		
		//storage 파일 저장, 디비의 파일명 변경
		Map map = new HashMap();
		map.put("id", session.getAttribute("id"));
		map.put("fname", Utility.saveFileSpring(fnameMF, basePath)); //스토리지에 변경파일 저장한다.
		
		
		//디비에 파일명 변경
		int cnt = mapper.updateFile(map);
		
		if(cnt==1) {
			return "redirect:./read"; //member의 read 로 보내주겠다
		}else {
			return "./error"; //member의 error 로 보내주겠다
		}
		
	}
	
	/* 내가만든 회원 사진 수정 */
	@GetMapping("/member/updateFile")
	public String updateFile() {
		//String id, Model model, HttpSession session
//		if(id == null) {
//			id = (String) session.getAttribute("id");
//			//메인에서 넘어가기 때문에 id정보는 전달하기보다 세션에서 가져와야됨
//		}
//		
//		MemberDTO dto = mapper.read(id);
//		
//		model.addAttribute("dto",dto);
//		
		return "/member/updateFile";
	}
	
	/* 관리자만 볼 수 있는 회원목록페이*/
	@RequestMapping("/admin/list")
	public String list(HttpServletRequest request) {
		
// 검색관련------------------------
		String col = Utility.checkNull(request.getParameter("col"));
		String word = Utility.checkNull(request.getParameter("word"));

		if (col.equals("total")) {
			word = "";
		}

		// 페이지관련-----------------------
		int nowPage = 1;// 현재 보고있는 페이지
		if (request.getParameter("nowPage") != null) {
			nowPage = Integer.parseInt(request.getParameter("nowPage"));
		}
		int recordPerPage = 3;// 한페이지당 보여줄 레코드갯수

		// DB에서 가져올 순번-----------------
		int sno = ((nowPage - 1) * recordPerPage) + 1;
		int eno = nowPage * recordPerPage;

		Map map = new HashMap();
		map.put("col", col);
		map.put("word", word);
		map.put("sno", sno);
		map.put("eno", eno);

		int total = mapper.total(map);

		List<MemberDTO> list = mapper.list(map);

		String paging = Utility.paging(total, nowPage, recordPerPage, col, word);

		// request에 Model사용 결과 담는다
		request.setAttribute("list", list);
		request.setAttribute("nowPage", nowPage);
		request.setAttribute("col", col);
		request.setAttribute("word", word);
		request.setAttribute("paging", paging);

		return "/member/list";

	}
	
	/* 회원정보 수정 페이지*/
	@GetMapping("/member/update")
	public String update(String id,Model model,HttpSession session) {
		
		if(id == null) {
			id = (String) session.getAttribute("id");
			//메인에서 넘어가기 때문에 id정보는 전달하기보다 세션에서 가져와야됨
		}
		
		MemberDTO dto = mapper.read(id);
		
		model.addAttribute("dto",dto);
		
		return "/member/update";
		
		
	}
	
	@PostMapping("/member/update")
	public String update(MemberDTO dto,Model model) {
		int cnt = mapper.update(dto);
		
		if(cnt==1) {
			model.addAttribute("id",dto.getId());
			return "redirect:./read"; //read페이지를 재요청
		}else {
			return "error";
		}
	}
	
	@GetMapping("/member/download")
	public void download(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ServletContext ctx = request.getSession().getServletContext();
		// 절대경로
		String dir = ctx.getRealPath(request.getParameter("dir"));
		String filename = request.getParameter("filename");
		byte[] files = FileUtils.readFileToByteArray(new File(dir, filename));
		response.setHeader("Content-disposition",
				"attachment; fileName=\"" + URLEncoder.encode(filename, "UTF-8") + "\";");
		// Content-Transfer-Encoding : 전송 데이타의 body를 인코딩한 방법을 표시함.
		response.setHeader("Content-Transfer-Encoding", "binary");
		/**
		 * Content-Disposition가 attachment와 함게 설정되었다면 'Save As'로 파일을 제안하는지 여부에 따라 브라우저가
		 * 실행한다.
		 */
		response.setContentType("application/octet-stream");
		response.setContentLength(files.length);
		response.getOutputStream().write(files);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
	
	/* 회원정보페이지 */
	@GetMapping("/member/read")
	public String read(String id, Model model,HttpSession session) {
		
		if(id == null) {
			id = (String) session.getAttribute("id");
			//메인에서 넘어가기 때문에 id정보는 전달하기보다 세션에서 가져와야됨
		}
		
		MemberDTO dto = mapper.read(id);
		
		model.addAttribute("dto",dto);
		
		return "/member/read";
	}
	
	@GetMapping("/member/logout")
	public String logout(HttpSession session) {
		//session.removeAttribute("id");
		//session.removeAttribute("grade");
		session.invalidate(); //session에 있는거 한꺼번에 다 지우겠다
		
		return "redirect:/";
	}
	
	@PostMapping("/member/login")
	public String login(@RequestParam Map<String,String> map,
			HttpSession session,
			HttpServletResponse response,
			HttpServletRequest request,
			Model model) {
		int cnt = mapper.loginCheck(map);
		
		if(cnt>0) {//회원이면
			String grade = mapper.getGrade(map.get("id"));
			session.setAttribute("id", map.get("id"));
			session.setAttribute("grade", grade);
			//Cookie 저장, id저장 여부 및 id
			Cookie cookie = null;
			String c_id = request.getParameter("c_id"); //체크된 값
			
			if(c_id != null) { 		//쿠키 저장하겠다고 했을시 쿠키 생성
				cookie = new Cookie("c_id","Y");
				cookie.setMaxAge(60 * 60 * 24 * 365); //쿠키를 1년정도 유지한다
				response.addCookie(cookie); //요청지(client)에 쿠키 저장
			
				cookie = new Cookie("c_id_val",map.get("id"));
				cookie.setMaxAge(60 * 60 * 24 * 365); //쿠키를 1년정도 유지한다
				response.addCookie(cookie); //요청지(client)에 쿠키 저장
				
			}else { 			//체크를 풀면 쿠키 삭제, 또는 생성않는다
				//쿠키는 삭제시 만들었던 데이터에 빈 값을 넣어준다
				cookie = new Cookie("c_id","");
				cookie.setMaxAge(0); 
				response.addCookie(cookie); //요청지(client)에 쿠키 저장
			
				cookie = new Cookie("c_id_val","");
				cookie.setMaxAge(0); 
				response.addCookie(cookie); //요청지(client)에 쿠키 저장
			}
		}//iccnt>0 end
		
		//로그인 성공 시 메인으로 갈지 게시글로갈지
		if(cnt>0) {
			if(map.get("rurl") != null && !map.get("rurl").equals("")) { //read페이지에서 왔을경우
				model.addAttribute("bbsno",map.get("bbsno"));
				model.addAttribute("nPage",map.get("nPage"));
				model.addAttribute("nowPage",map.get("nowPage"));
				model.addAttribute("col",map.get("col"));
				model.addAttribute("word",map.get("word"));
				
				
				return "redirect:" + map.get("rurl");
			}else {
				return "redirect:/"; //index(메인화면)으로 간다
			}
			
		}else {
			model.addAttribute("msg","아이디 또는 비밀번호를 잘못 입력 했거나 <br> 회원이 아닙니다. 회원가입하세요");
			return "/member/errorMsg";
		}
	}
			
	
	/*
	 * id에 대한 내용을 쿠키에 저장 할지
	 */
	@GetMapping("/member/login")
	public String login(HttpServletRequest request) {

		/*----쿠키설정 내용시작----------------------------*/
		String c_id = "";     // ID 저장 여부를 저장하는 변수, Y
		String c_id_val = ""; // ID 값
		 
		Cookie[] cookies = request.getCookies(); 
		Cookie cookie=null; 
		 
		if (cookies != null){ 
		 for (int i = 0; i < cookies.length; i++) { 
		   cookie = cookies[i]; 
		 
		   if (cookie.getName().equals("c_id")){ 
		     c_id = cookie.getValue();     // Y 
		   }else if(cookie.getName().equals("c_id_val")){ 
		     c_id_val = cookie.getValue(); // user1... 
		   } 
		 } 
		} 
		/*----쿠키설정 내용 끝----------------------------*/
		
		request.setAttribute("c_id", c_id);
		request.setAttribute("c_id_val", c_id_val);
		
		return "/member/login";
		
	}
	
	@PostMapping("/member/create")
	public String create(MemberDTO dto, HttpServletRequest request) {
		String upDir = request.getRealPath("/storage");
		String fname = Utility.saveFileSpring(dto.getFnameMF(), upDir);
		int size = (int) dto.getFnameMF().getSize();
		if(size>0) { //사진 업로드 성공하면
			dto.setFname(fname);
		}else {
			dto.setFname("member.jpg"); //없으면 디폴트사진
		}
		
		if(mapper.create(dto) > 0 ) {
			return "redirect:/";
		}else {
			return "error";
		}
	}
	
	@GetMapping(value="/member/emailcheck",produces="application/json;charset=utf-8")
	@ResponseBody
	public Map<String,String> emailcheck(String email){
		int cnt = mapper.duplicatedEmail(email); //member.xml에서 select문 실행 후 라인 수 반환
		Map<String,String> map = new HashMap<String,String>();
		if(cnt>0) {
			map.put("str",email+"는 중복되어서 사용할 수 없습니다.");
		}else {
			map.put("str",email+"는 사용가능한 이메일입니다.");
		}
		return map;
	}
	
	@GetMapping(value="/member/idcheck",produces="application/json;charset=utf-8")
	@ResponseBody
	public Map<String,String> idcheck(String id){
			int cnt = mapper.duplicatedId(id); //member.xml에서 select문 실행 후 라인 수 반환
			Map<String,String> map = new HashMap<String,String>();
			if(cnt>0) {
				map.put("str",id+"는 중복되어서 사용할 수 없습니다.");
			}else {
				map.put("str",id+"는 사용가능한 id입니다.");
			}
			return map;
	}
	

	
	@GetMapping("/member/agree")
	public String agree() {
		
		return "/member/agree";
	}
	
	@PostMapping("/member/createForm")
	public String create() { //create처리를 요청한다
		return "/member/create";
	}
}
