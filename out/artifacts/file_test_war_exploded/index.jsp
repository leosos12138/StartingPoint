<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>文件上传下载</title>
  </head>
  <body>
  <h2>文件下载</h2>
  <%--获取服务器当前上下文路径--%>
  <form action="${pageContext.request.contextPath}/dowmload.do" method="get">
    <input type="text" name="downloadfile">
    <input type="submit">
  </form>
  <h2>文件上传</h2>
  <form action="${pageContext.request.contextPath}/upload.do" enctype="multipart/form-data" method="post">
    <p><input type="file" name="uploadfile1"></p>
    <p><input type="file" name="uploadfile2"></p>
    <p><input type="submit"> | <input type="reset"></p>
  </form>
  </body>
</html>
