<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head>
    <title>Java + Tomcat CI/CD Demo</title>
    <style>
        body { font-family: sans-serif; max-width: 42rem; margin: 4rem auto; line-height: 1.5; }
        code { background: #f2f2f2; padding: 2px 6px; border-radius: 4px; }
    </style>
</head>
<body>
    <h1>Java + Tomcat CI/CD Demo</h1>
    <p>This WAR was built from source with <strong>Maven</strong>, packaged into a
       <strong>Docker</strong> image running <strong>Tomcat</strong>, and deployed by a
       <strong>Jenkins</strong> pipeline.</p>
    <p>Try the servlet: <a href="hello?name=Recruiter"><code>/hello?name=Recruiter</code></a></p>
</body>
</html>
