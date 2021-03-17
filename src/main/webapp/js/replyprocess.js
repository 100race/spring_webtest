/**
댓글 부분 로딩될 때 목록을 부르고 댓글을 가져옴
댓글입력

replyService는 breply에 있음
 */
$(function() { //페이지가 로딩될 때 
	showList(); //자바스크립트는 호출 순서 상관없음. 선언 - 호출가능.
}); //paging loading end

/*
[댓글리스트]
*/
var replyUL = $(".chat");
function showList() {
	replyService.getList({ bbsno: bbsno, sno: sno, eno: eno }, //param
		function(list) { //callback
			var str = "";
			if (list == null || list.length == 0) {
				return;
			}

			for (var i = 0, len = list.length || 0; i < len; i++) {
				str += "<li class='list-group-item' data-rnum='" + list[i].rnum + "'>";
				str += "<div><div class='header'><strong class='primary-font'>" + list[i].id + "</strong>";
				str += "<small class='pull-right text-muted'>" + list[i].regdate + "</small></div>";
				str += replaceAll(list[i].content, '\n', '<br>') + "</div></li>";
			}

			replyUL.html(str); //innerHTML

			showReplyPage();
		}//function end

	);//getList end
}//showList end

function replaceAll(str, searchStr, replaceStr) {
	return str.split(searchStr).join(replaceStr);
}

//댓글페이지를 footer클래스에 붙여주는곳
var replyPageFooter = $(".panel-footer");
var param = "nPage=" + nPage;
param += "&nowPage=" + nowPage;
param += "&bbsno=" + bbsno;
param += "&col=" + colx;
param += "&word=" + wordx;

function showReplyPage() {
	replyService.getPage(param,
		function(paging) {
			console.log(paging);
			var str = "<div><small class='text-muted'>" + paging + "</small></div>";

			replyPageFooter.html(str); //paging으로 가져온 값을 innerHtML에 넣어줌
		})
}//showReplyPage end 

/*
[댓글입력창]
버튼 : 수정(modify),삭제(remove),등록(register),닫기(close)
 */

var modal = $(".modal"); //modal클래스로 modal창을 찾아온다
var modalInputContent = modal.find("textarea[name='content']");
var modalModBtn = $("#modalModBtn"); //modal id로 각 객체를 찾는다                                                                                        	
var modalRemoveBtn = $("#modalRemoveBtn");
var modalRegisterBtn = $("#modalRegisterBtn");

$("#modalCloseBtn").on("click", function(e) {
	modal.modal('hide'); //모달창을 다시 숨김
});

//[로그인에 따른 댓글등록버튼 처리]
$("#addReplyBtn").on("click", function(e) { 
	if(session_id==""){ //로그인이 안되어있다면
		if(confirm('로그인해야 댓글을 쓸 수 있습니다.')){
			var url = "../member/login"; //파라미터를 로그인페이지로 전달
			url += "?col="+colx;
			url += "&word="+wordx;
			url += "&nowPage="+nowPage;
			url += "&nPage="+nPage;
			url += "&bbsno="+bbsno;
			url += "&rurl=../bbs/read";
			location.href = url;
		}
	}else{ //로그인 되어있다면
		
	modalInputContent.val(""); //내용입력창 초기화
	modal.find("button[id != 'modalCloseBtn']").hide(); //close빼고 다 숨겨놓기
	modalRegisterBtn.show(); //그래도 등록버튼도 보여지게

	$(".modal").modal("show");
	}
});

//[댓글창 등록 버튼]
modalRegisterBtn.on("click", function(e) {

	if (modalInputContent.val() == '') {
		alert("댓글을 입력하세요")
		return;
	}

	var reply = {
		content: modalInputContent.val(), //입력창부분의 value값을 넣어줌
		id: 'user1',
		bbsno: bbsno
	};

	replyService.add(reply, function(result) {

		modal.find("input").val("");
		modal.modal("hide");

		showList();

	}); // add end

}); // modalRegisterBtn.on end


//[modal에 댓글 조회 클릭 이벤트 처리] 
$(".chat").on("click", "li", function(e) {

	var rnum = $(this).data("rnum"); //클릭한 곳의 rnum을 꺼내옴.

	replyService.get(rnum, function(reply) { //rnum에 해당하는 데이터를 가져와서 modal에 보여주는곳

		modalInputContent.val(reply.content);
		modal.data("rnum", reply.rnum);

		modal.find("button[id !='modalCloseBtn']").hide();

		modalModBtn.show();
		modalRemoveBtn.show();

		$(".modal").modal("show");

	}); //get end
}); //.chat click end

//[댓글 수정 버튼 처리]
modalModBtn.on("click",function(e){
	var reply = {
			rnum : modal.data("rnum"),
			content : modalInputContent.val()
	}
	
	replyService.update(reply,function(result){ //업데이트 되면 모달 숨기고, 리스트 다시 보여줌
		modal.modal("hide");
		showList();
	}); // update end
	
}) //[댓글 수정처리 END]

modalRemoveBtn.on("click",function(e){
	var rnum = modal.data("rnum");
	replyService.remove(rnum,function(result){
		modal.modal("hide");
		showList();
	});//remove end
}); //modalRemoveBtn on end
