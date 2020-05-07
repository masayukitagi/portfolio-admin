<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
  <h1>
	Hello world!
  </h1>

<!--
【form要素】値を送信する方法を定義。
【action属性】どこに値を渡すかを定義。
    action="/skillUpload" で skillUpload に値を渡す。
    <%=request.getContextPath()%>/skillUpload でも可能。（他ファイルを参照）
【method属性】どのように値を渡すかを定義。GETとPOSTがある。
    画面の繊維をコントローラーで制御している。→コントローラーへ
 -->

  <form method="get" action="http://localhost:8080/team20/skillUpload">
    <button>
      move to skillUpload
    </button>
  </form>

<P>  The time on the server is ${serverTime}. </P>
</body>
</html>
