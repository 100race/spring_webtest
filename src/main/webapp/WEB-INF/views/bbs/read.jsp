<%@ page contentType="text/html; charset=UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<title>Bootstrap Example</title>
<meta charset="utf-8">

<script type="text/javascript">
	function updateM() {
		var url = "update";
		url += "?bbsno=${dto.bbsno}";
		url += "&oldfile=${dto.filename}";

		location.href = url;
	}
	function deleteM() {
		var url = "delete";
		url += "?bbsno=${dto.bbsno}";
		url += "&oldfile=${dto.filename}";

		location.href = url;
	}
	function delete_Ajax() {
		var url = "delete_Ajax";
		url += "?bbsno=${dto.bbsno}";
		url += "&oldfile=${dto.filename}";

		location.href = url;
	}
	function replyM() {
		var url = "reply";
		url += "?bbsno=${dto.bbsno}";

		location.href = url;
	}

	function listM() {
		var url = "list";
		url += "?nowPage=${param.nowPage}";
		url += "&col=${param.col}";
		url += "&word=${param.word}";
		location.href = url;
	}
</script>

</head>
<body>
	<div class="container">

		<h2>조회</h2>
		<div class="panel panel-default">
			<div class="panel-heading">작성자</div>
			<div class="panel-body">${dto.wname}</div>

			<div class="panel-heading">제목</div>
			<div class="panel-body">${dto.title}</div>

			<div class="panel-heading">내용</div>
			<div class="panel-body">${dto.content}</div>

			<div class="panel-heading">조회수</div>
			<div class="panel-body">${dto.viewcnt}</div>

			<div class="panel-heading">등록일</div>
			<div class="panel-body">${dto.wdate}</div>

			<div class="panel-heading">파일</div>
			<div class="panel-body">${dto.filename}</div>
		</div>

		<div>
			<button type="button" class="btn" onclick="location.href='./create'">등록</button>
			<button type="button" class="btn" onclick="updateM()">수정</button>
			<button type="button" class="btn" onclick="deleteM()">삭제</button>
			<button type="button" class="btn" onclick="replyM()">답변</button>
			<button type="button" class="btn" onclick="listM()">목록</button>
			<button type="button" class="btn" onclick="delete_Ajax()">삭제2</button>
		</div>
		<br>
		<div class='row'><!-- 댓글 목록 시작 -->
			<div class="col-lg-12">
				<!-- panel start-->
				<div class="panel panel-default">

					<div class="panel-heading">
						<i class="fa fa-comments fa-fw"></i> 댓글
						<button id='addReplyBtn' class='btn btn-primary btn-xs pull-right'>New
							Reply</button>
					</div>

					<div class="panel-body">

						<ul class="chat list-group">
							<li class="left clearfix" data-rno="12">
								<div>
									<div class="header">
										<strong class="primary-font">user1</strong> <small
											class="pull-right text-muted">2019-05-12</small>
									</div>
									<p>Good job!</p>
								</div>
							</li>
						</ul>
						<!-- ul end  -->
					</div>

					<div class="panel-footer"></div>

				</div><!-- panel end-->
			</div><!--  col-lg-12 end -->
		</div><!-- row end -->

	</div><!-- container end -->
	
<!-- Modal 댓글 수정,삭제,등록 미니팝업창-->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
aria-labelledby="myModalLabel" aria-hidden="true">
<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal"
        aria-hidden="true">&times;</button>
      <h4 class="modal-title" id="myModalLabel">REPLY MODAL</h4>
    </div>
    <div class="modal-body">
      <div class="form-group">
        <label>내용</label> 
        <textarea cols="10" rows="3" class="form-control" name='content'>New Reply!!!!</textarea> 
      </div>      
    </div>
<div class="modal-footer">
<button id='modalModBtn' type="button" class="btn btn-warning">Modify</button>
<button id='modalRemoveBtn' type="button" class="btn btn-danger">Remove</button>
<button id='modalRegisterBtn' type="button" class="btn btn-primary">Register</button>
<button id='modalCloseBtn' type="button" class="btn btn-default">Close</button>
</div>          </div>
  <!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<!-- 댓글처리 ,서버와 비동기 통신 -->
<script type ="text/javascript"	src="${pageContext.request.contextPath }/js/breply.js"></script>
	
<!-- 페이지 로딩시 댓글 목록 처리-->
 <!-- jstl내부 javascript에서 사용가능 -->
  <script type="text/javascript">
  var bbsno = "${dto.bbsno}";
  var sno = "${sno}";
  var eno = "${eno}";
 <!-- 댓글용 paging, 게시판 검색 -->
  var nPage = "${nPage}";
  var nowPage = "${param.nowPage}";
  var colx = "${param.col}";
  var wordx = "${param.word}";
  <!-- 세션값 이용-->
  var session_id = '${sessionScope.id}';
  
  
  </script>
  <!-- 비동기 통신 요청 후 전달되는 데이터를 화면에 출력게 하는 코드 -->
  <script type="text/javascript" 
  		  src="${pageContext.request.contextPath }/js/replyprocess.js">
  
  </script>
	
	
	
	
	
	
	
	
	
	
	
	
</body>
</html>
