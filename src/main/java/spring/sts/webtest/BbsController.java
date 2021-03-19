package spring.sts.webtest;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import spring.model.bbs.BbsDTO;
import spring.model.bbs.BbsMapper;
import spring.model.bbs.BbsService;
import spring.model.bbs.BbsVO;
import spring.model.reply.ReplyMapper;
import spring.utility.webtest.Utility;

@Controller
public class BbsController {
//	@Autowired
//	private BbsDAO dao;

	@Autowired
	private BbsMapper mapper;
	@Autowired
	private ReplyMapper rmapper;
	@Autowired
	private BbsService service;
	
	/* JPA 이용한*/
	@PostMapping("/bbs/createJPA")
	public String create(BbsVO dto, HttpServletRequest request) {
		String basePath = request.getRealPath("/storage");
		dto.setFilename(Utility.saveFileSpring(dto.getFilenameMF(), basePath));
		if(dto.getFilenameMF()!=null) { //업로드된 파일처리
			dto.setFilename(Utility.saveFileSpring(dto.getFilenameMF(), basePath));
			dto.setFilesize((int)dto.getFilenameMF().getSize());
		}
		
		try {//orm을 하고있다. or매핑
			service.insert(dto);
			return "redirect:./list";
		}catch(Exception e) {
			return "./error";
		}
	}

	@GetMapping("/bbs/fileDown")
	public void fileDown(HttpServletRequest request, HttpServletResponse response) throws IOException {

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

	/**
	 * 
	 * @param dto
	 * @param oldfile
	 * @param request
	 * @return data값. ResponseBody를 선언해줌으로써 뷰네임 아니고 data값을 전달한다고 명명
	 */
	@PostMapping(value = "/bbs/delete_Ajax", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, String> delete_Ajax(@RequestBody BbsDTO dto, HttpServletRequest request) {
		boolean cflag = false;
		int cnt = mapper.checkRefnum(dto.getBbsno()); // 부모글인지 체크
		if (cnt > 0)
			cflag = true;

		String upDir = request.getRealPath("/storage"); // 절대경로 불러오기
		Map map = new HashMap();
		map.put("bbsno", dto.getBbsno());
		map.put("passwd", dto.getPasswd());

		boolean pflag = false;
		boolean flag = false;

		if (!cflag) {
			int cnt2 = mapper.passCheck(map); // 부모글(답변있으면) 비밀번호확인
			if (cnt2 > 0)
				pflag = true;
		}
		if (pflag) { // 비밀번호 맞으면 지워줌
			if (dto.getFilename() != null)
				Utility.deleteFile(upDir, dto.getFilename());
			int cnt3 = mapper.delete(dto.getBbsno());
			if (cnt3 > 0)
				flag = true;
		}

		Map<String, String> map2 = new HashMap<String, String>();

		if (cflag) {
			map2.put("str", "답변있는 글이므로 삭제할 수 없습니다");
		} else if (!pflag) {
			map2.put("str", "패스워드가 잘못입력되었습니다");
			map2.put("color", "blue");
		} else if (flag) {
			map2.put("str", "삭제 처리되었습니다");
		} else {
			map2.put("str", "삭제중 에러가 발생했습니다");
		}

		return map2; // delete_Ajax.jsp에 data로 전달

	}

	/*
	 * delete의 Ajax처리 페이지
	 */
	@GetMapping("/bbs/delete_Ajax")
	public String delete_Ajax() {

		return "/bbs/delete_Ajax";
	}

	/*
	 * 정보를 가지고 삭제를 누르면 post로 진행 
	 * + 댓글삭제 트랜잭션 (AOP사용) 구현 추가 2021.03.18
	 */
	@PostMapping("/bbs/delete") // 핸들러매핑. 요청url에 어떤 메소드가 매핑되는지. 컨트롤러에서 꼭 써줘야함.
	public String delete(int bbsno, String passwd, String oldfile, HttpServletRequest request) {

		String upDir = request.getRealPath("/storage"); // 절대경로 불러오기
		Map map = new HashMap();
		map.put("bbsno", bbsno);
		map.put("passwd", passwd);

		boolean pflag = false;
		int cnt = mapper.passCheck(map);
		
		String url = "/bbs/passwdError";
		
		if(cnt > 0) {
			
			try {
				service.delete(bbsno);  // AOP로 구현한 트랜잭션으로 댓글 - 게시글 삭제하기
				url = "redirect:/bbs/list"; //성공시 갈 곳
				if (oldfile != null)
					Utility.deleteFile(upDir, oldfile);
			}catch(Exception e) {
				e.printStackTrace();
				url = "/bbs/error"; //실패시 갈 곳
			}
			
		}
		
//		if (cnt > 0)
//			pflag = true;
//		boolean flag = false;
//		if (pflag) {
//			if (oldfile != null)
//				Utility.deleteFile(upDir, oldfile);
//			int cnt2 = mapper.delete(bbsno);
//			if (cnt2 > 0)
//				flag = true;
//		}
//
//		if (!pflag) {
//			return "/bbs/passwdError"; // foward : 속성값 유지
//		} else if (flag) {
//			return "redirect:/bbs/list"; // redirect : 초기화
//		} else {
//			return "/bbs/error";
//		}
		
		return url;
	}

	/*
	 * url로 삭제 요청되면 삭제해도 되는건지 확인하고 flag를 보낸다.
	 */
	@GetMapping("/bbs/delete")
	public String delete(int bbsno, Model model) {
		boolean flag = false;
		int cnt = mapper.checkRefnum(bbsno);
		if (cnt > 0)
			flag = true;

		model.addAttribute("flag", flag);

		return "/bbs/delete";
	}

	@PostMapping("/bbs/reply")
	public String reply(BbsDTO dto, HttpServletRequest request) {

		String basePath = request.getRealPath("/storage");
		if (dto.getFilenameMF() != null) {
			dto.setFilename(Utility.saveFileSpring(dto.getFilenameMF(), basePath));
			dto.setFilesize((int) dto.getFilenameMF().getSize());
		}

		Map map = new HashMap();
		map.put("grpno", dto.getGrpno());
		map.put("ansnum", dto.getAnsnum());

		mapper.upAnsnum(map); // 이미 답변이 있다면 답변 순번 높이기

		boolean flag = false;
		int cnt = mapper.createReply(dto);
		if (cnt > 0)
			flag = true;

		if (flag) {
			return "redirect:/bbs/list"; // 뷰에 대한 정보를 리턴. 리다이렉트로 재요청
		} else {
			return "/bbs/error";
		}
	}

	@GetMapping("/bbs/reply")
	public String reply(int bbsno, Model model) {

		model.addAttribute("dto", mapper.readReply(bbsno));
		return "/bbs/reply";
	}

	@PostMapping("/bbs/update")
	public String update(BbsDTO dto, String oldfile, HttpServletRequest request) {
		String basePath = request.getRealPath("/storage");

		if (dto.getFilenameMF() != null) {
			// 기존에 올렸던 파일이 있을시 삭제
			if (oldfile != null) {
				Utility.deleteFile(basePath, oldfile);
			}
			dto.setFilename(Utility.saveFileSpring(dto.getFilenameMF(), basePath));
			dto.setFilesize((int) dto.getFilenameMF().getSize());
		}

		/*
		 * map value : dto에서 가져온 글번호, 입력된비번
		 */
		Map map = new HashMap();
		map.put("bbsno", dto.getBbsno());
		map.put("passwd", dto.getPasswd());

		boolean pflag = false;
		int cnt = mapper.passCheck(map);
		if (cnt > 0)
			pflag = true;
		boolean flag = false;

		if (pflag) {
			int cnt2 = mapper.update(dto); // 패스워드 에러 나지 않으면 update 실행
			if (cnt2 > 0)
				flag = true;
		}

		if (!pflag) {
			request.setAttribute("pflag", pflag);
			return "/bbs/passwdError"; // 패스워드 에러 - Foward
		} else if (flag) {
			return "redirect:/bbs/list"; // 업데이트 완료되어 이동 - Redirect
		} else {

			request.setAttribute("flag", flag);
			return "/bbs/error"; // 업데이트 실패 - Foward
		}

	}

	@GetMapping("/bbs/update")
	public String update(int bbsno, Model model) {

		model.addAttribute("dto", mapper.read(bbsno));

		// 타일즈에서 없다면 internal에서 찾는다
		return "/bbs/update";
	}

	@GetMapping("/bbs/read")
	public String read(int bbsno, Model model, HttpServletRequest request) {

		// int bbsno = Integer.parseInt(request.getParameter("bbsno"));
		// read함수의 매개변수로 request대신 바로 bbsno를 받아 주석처리 된 부분

		// dao.upViewcnt(bbsno); mybatis로 전환
		mapper.upViewcnt(bbsno);
		// BbsDTO dto = dao.read(bbsno); mybatis로 전환
		BbsDTO dto = mapper.read(bbsno);
		String content = dto.getContent().replaceAll("\r\n", "<br>");
		dto.setContent(content);
		model.addAttribute("dto", dto); // request 대신 model로 변경
		
		/* 댓글관련 처리. sno, eno, npage를 넘겨줄곳 */
		int nPage = 1;
		if(request.getParameter("nPage")!=null) {
			nPage = Integer.parseInt(request.getParameter("nPage"));
		}
		int recordPerPage = 3; //한 페이지당 보여 줄 레코드 갯수

		int sno = ((nPage -1) * recordPerPage) + 1 ; //글의 시작번호 - 1페이지의 1번글
		int eno = nPage * recordPerPage; //글의 페이지상 끝번호 - 1페이지의 3번글
		
		Map map = new HashMap();
		map.put("sno", sno);
		map.put("eno", eno);
		map.put("nPage", nPage);
		
		model.addAllAttributes(map);
		
		/* 댓글 처리 끝 */
		
		return "/bbs/read";
	}

	// GET&POST 모두 처리하겠다는 requestMapping. 맨앞 슬래시는 쓰든안쓰든 상관없음.
	@RequestMapping("bbs/list")
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

		// List<BbsDTO> list = dao.list(map); mybatis로 이전
		List<BbsDTO> list = mapper.list(map);

		String paging = Utility.paging(total, nowPage, recordPerPage, col, word);

		// request에 Model사용 결과 담는다
		request.setAttribute("list", list);
		request.setAttribute("nowPage", nowPage);
		request.setAttribute("col", col);
		request.setAttribute("word", word);
		request.setAttribute("paging", paging);
		request.setAttribute("rmapper", rmapper); //매퍼를 가져감. 

		return "/bbs/list";
	}

	@GetMapping("/bbs/create")
	public String create() {
		return "/bbs/create"; // 리턴되는 view 이름
	}

	@PostMapping("/bbs/create")
	public String create(BbsDTO dto, HttpServletRequest request) {
		String basePath = request.getRealPath("/storage");
		if (dto.getFilenameMF() != null) {
			dto.setFilename(Utility.saveFileSpring(dto.getFilenameMF(), basePath));
			dto.setFilesize((int) dto.getFilenameMF().getSize());
		}

		// boolean flag = dao.create(dto); //mybatis로 바꿔준부분
		boolean flag = false;

		int cnt = mapper.create(dto);
		if (cnt > 0)
			flag = true; // 성공한 열의 개수를 return받아서 성공하면 true

		if (flag) {
			return "redirect:/bbs/list"; // 뷰에 대한 정보를 리턴. 리다이렉트로 재요청
		} else {
			return "error";
		}
	}
}
