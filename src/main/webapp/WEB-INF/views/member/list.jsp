<%@ page contentType="text/html; charset=UTF-8" %> 
<%@ page import="java.util.*,spring.model.member.*" %>
<%
    List<MemberDTO> list = (List<MemberDTO>) request.getAttribute("list");
    String paging = (String) request.getAttribute("paging"); 
    String col = (String) request.getAttribute("col"); 
    String word = (String) request.getAttribute("word"); 
    String root = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
  <title>회원목록</title>
  <meta charset="utf-8">
  <script type="text/javascript">
  function read(id){
	  var url = "<%=root%>/member/read";
	  url += "?id="+id;
	  
	  location.href=url;
	  
  }
  </script>
</head>
<body>
<div class="container">
<h2 class="col-sm-offset-2 col-sm-10">회원목록</h2>
<br>
<form class="form-inline"
      method="post"
      action="list.jsp"
>
<div class="form-group">
	<select name="col" class="form-control">
	<option value="mname"
	<%if(col.equals("mname")) out.print("selected"); %>
	>성명</option>
	<option value="id"
	<%if(col.equals("id")) out.print("selected"); %>
	>아이디</option>
	<option value="email"
	<%if(col.equals("email")) out.print("selected"); %>
	>이메일</option>
	<option value="total"
	<%if(col.equals("total")) out.print("selected"); %>
	>전체출력</option>
	</select>
</div>

<div class="form-group">
	<input type="text" class="form-control" 
	name="word" required="required" value="<%=word%>">
</div>
<button class="btn btn-default">검색</button>
<button class="btn btn-default" type="button" 
onclick="location.href='createForm.jsp'">등록</button>
</form>

<br>
<% for(int i=0; i<list.size(); i++){ 
	MemberDTO dto = list.get(i);
%>
<table class="table table-bordered">
<tr>
	<td rowspan="5" class="col-sm-2">
	<img src="<%=root %>/storage/<%=dto.getFname() %>"
	 class="img-rounded" width="200px" height="200px">
	</td>
	<th class="col-sm-2">아이디</th>
	<td class="col-sm-8"><a href="javascript:read('<%=dto.getId() %>')"><%=dto.getId() %></a></td>
</tr>
<tr>
	<th class="col-sm-2">성명</th>
	<td class="col-sm-8"><%=dto.getMname() %></td>
</tr>
<tr>
	<th class="col-sm-2">전화번호</th>
	<td class="col-sm-8"><%=dto.getTel() %></td>
</tr>
<tr>
	<th class="col-sm-2">이메일</th>
	<td class="col-sm-8"><%=dto.getEmail() %></td>
</tr>
<tr>
	<th class="col-sm-2">주소</th>
	<td class="col-sm-8">
	(<%=dto.getZipcode()%>)
	<%=dto.getAddress1() %> <%=dto.getAddress2() %>
	</td>
</tr>
</table>
<%
}
%>
<%=paging %>

</div>
</body>
</html>