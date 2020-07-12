package com.lee.servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

public class FileDownloadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1. 获取要下载的文件路径
        String realPath = request.getParameter("downloadfile");
        //2. 获取下载的文件名
        if(realPath == null || realPath.length() == 0){
            //此处可以在前台进行处理
            realPath = "";
        }
        String fileName = realPath.substring(realPath.lastIndexOf("\\")+1);
        // 3.设置浏览器能支持下载
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        // 4.获取文件的输入流
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(realPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("没有该文件！");
            response.sendRedirect("error.jsp");
            return;
        }

        // 5.创建缓冲区
        int len = 0;
        byte[] buffer = new byte[1024];
        // 6.获取OutputStream对象
        ServletOutputStream out = response.getOutputStream();
        // 7.将FIleOutputStream流写入到缓冲区,使用OutputStream将缓冲区中的数据输出
        while((len = fis.read(buffer)) > 0){
            out.write(buffer, 0, len);
        }

        fis.close();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
