console.log('*****Reply Module...........')
/**
read.jsp에서 댓글에 줄 데이터를 가지고있어서 그걸 비동기 통신에 뿌리라고 요청함 여기서.
페이지는 열려있고 부분만 비동기로 바꾸려고 하는 통신.
 */
var replyService = (function(){
	
	function remove(rnum, callback, error){
		$.ajax({
			type:"delete",
			url:"./reply/"+rnum,
			success:function(result, status, xhr){
				if(callback){
					callback(result)
				}
			},
			error: function(xhr,status,er){
				if(error){
					error(er);
				}
			}
		})//ajax end
	}//remove end
	
	function update(reply,callback,error){
		console.log("rnum:"+reply.rnum);
		
		$.ajax({
			type:'put',
			url:'./reply/' + reply.rnum,
			data : JSON.stringify(reply),
			contentType:"application/json;charset=utf-8",
			success:function(result,status,xhr){
				if(callback){
					callback(result);
				}
			},
			error:function(xhr,status,er){
				if(error){
					error(er);
				}
			}
		})//ajax end
	}//update end
	
	function add(reply,callback,error){
		console.log("add reply...............")
		$.ajax({
			type:'post',
			url:'./reply/create', //replycontroller에 받는 url이 있어야함
			data:JSON.stringify(reply), //string형으로 전달할것이다
			contentType:"application/json;charset=utf-8",
			success:function(result,status,xhr){
				if(callback){
					callback(result)
				}
			},
			error:function(xhr,status,er){
				if(error){
					error(er)
				}
			}
		});
		
	}
	
	//받아오기
	function get(rnum,callback,error){ //(파라미터로 받아올 값, 처리할 함수, 에러)
		$.get("./reply/"+rnum+".json",function(result){
			if(callback){
				callback(result)
			}
		}).fail(function(xhr,status,error){
			if(error){
				error();
			}
		});
	}
	
	
	function getList(param,callback){
		var bbsno = param.bbsno;
		var sno = param.sno;
		var eno = param.eno;
		//alert(param.bbsno); 값 확인
		$.getJSON("./reply/list/"+bbsno+"/"+sno+"/"+eno+".json", //Ajax호출,요청. jQuery를 이용. url 상으로 데이터를 보내는중
				function(data){ //success일때 받아오는 함수. JSON으로 받아온 데이터가 data로 넘어감.
					if(callback){
						callback(data);
					}
				}
		);
	}
	
	function getPage(param,callback,error){
		$.ajax({
			type:'get',
			url:'./reply/page',
			data:param,
			contentType : "application/text;charset=utf-8" ,
			success : function(result,status,xhr){ //성공적으로 호출시
			//result는 위의 형식으로 서버, 컨트롤러에 요청했을 때 가져오는 결과데이터
				if(callback){
					callback(result); //결과화면
				}
			},
			error : function(xhr,status,er){//에러,상태정보,에러관련내용		
				if(error){
					error(er);
				}
			}
		});
	}
	
	;//함수선언
	
	return { //함수 호출 시 요청값 & return 값
		getList:getList, //요청된 값, return되는 함수
		getPage:getPage,
		add:add,
		get:get,
		update:update,
		remove:remove
	};
	
})();//function()호출(실행)
