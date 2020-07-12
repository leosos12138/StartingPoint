<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>发邮件</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/register.do" method="post">
    <p>用户名：<input type="text" name="username"></p>
    <p>密码：<input type="password" name="password"></p>
    <p>邮箱：<input type="text" name="email"></p>
        <input type="submit" value="注册">
</form>
</body>
</html>
