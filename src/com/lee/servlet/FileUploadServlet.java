package com.lee.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;

/*
* 通过引入外部依赖包实现文件上传（WEB-INF下lib目录）
* */
public class FileUploadServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //判断上传的文件是普通表还是带文件的表单
        try {
            if(!ServletFileUpload.isMultipartContent(request)){
                return;
            }
            //创建上传文件的保存路径，建议建立在WEB-INF路径下，保证用户无法访问，安全
            String uploadPath = this.getServletContext().getRealPath("WEB-INF/upload");
            File uploadFile = new File(uploadPath);
            if(!uploadFile.exists()){
                uploadFile.mkdir();
            }

            //缓存，创建临时文件（文件大小过大，就放入临时文件中）
            String tmpPath = this.getServletContext().getRealPath("WEB-INF/tmp");
            File tmpFile = new File(tmpPath);
            if(!tmpFile.exists()){
                tmpFile.mkdir();
            }

            //处理上传的文件，一般都需要通过流来获取，可以使用request.getIntputStream()，原生态的文件上传流获取很麻烦，可直接利用Apache的文件上传组件实现（已导入依赖包）

            //1. 创建DiskFileItemFactory对象，处理文件上传路径或者大小等限制要求
            DiskFileItemFactory factory = getDiskFileItemFactory(tmpFile);
            //2. 获取ServletFileUpload对象
            ServletFileUpload upload = getServletFileUpload(factory);
            //3. 处理上传的文件
            String msg = uploadParseRequest(upload, request, uploadPath);
            request.setAttribute("msg", msg);
            request.getRequestDispatcher("info.jsp").forward(request, response);
        } catch (FileUploadBase.FileUploadIOException e) {
            e.printStackTrace();
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static DiskFileItemFactory getDiskFileItemFactory(File file){
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //通过这个工厂设置一个缓冲区，当文件大小大于该缓冲区时，将其放入临时文件中
        factory.setSizeThreshold(1024*1024);
        factory.setRepository(file);//临时文件目录
        return factory;
    }
    public static ServletFileUpload getServletFileUpload(DiskFileItemFactory factory){
        ServletFileUpload upload = new ServletFileUpload(factory);
        //监听文件上传进度
        upload.setProgressListener(new ProgressListener() {
            @Override
            public void update(long l, long l1, int i) {
                System.out.println("总大小：" + l1 + ", 上传进度：" + (double)i/l + "%");
            }
        });

        //处理乱码问题
        upload.setHeaderEncoding("UTF-8");
        upload.setFileSizeMax(1024*1024*10);

        return upload;
    }
    public String uploadParseRequest(ServletFileUpload upload, HttpServletRequest request, String uploadPath) throws FileUploadException, IOException {
        String msg = "";
        //把前端请求解析，封装成一个FileItem对象
        List<FileItem> fileItems = upload.parseRequest(request);
        for(FileItem fileItem: fileItems){
            if(fileItem.isFormField()){//判断是否为带文件的表单
                String name = fileItem.getFieldName();
                String value = null;
                try {
                    value = fileItem.getString("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println(name + ":" + value);
            }else {
                String uploadFileName = fileItem.getName();
                System.out.println("上传的文件名：" + uploadFileName);

                if(uploadFileName.trim().equals("") || uploadFileName == null){
                    continue;
                }

                //获得上传文件名
                String fileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);
                //获取文件的后缀名
                String fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);

                System.out.println("文件信息 [ 件名： " + fileName + "文件类型：" + fileExtName);

                //使用UUID保证文件夹的唯一性
                String uuidPath = UUID.randomUUID().toString();

                //==================文件处理完毕==========================
                //存到哪？uploadPath
                //文件真实存在的路径realPath
                String realPath = uploadPath + "/" + uuidPath;
                //给每个文件创建一个对应的文件夹
                File realPathFile = new File(realPath);
                if(!realPathFile.exists()){
                    realPathFile.mkdir();
                }

                //==================存放地址完毕==========================
                //获取文件上传的流
                InputStream inputStream = fileItem.getInputStream();
                //创建文件输出流
                FileOutputStream fos = new FileOutputStream(realPath + "/" + fileName);
                //创建文件缓存区
                byte[] buffer = new byte[1024];
                //读取文件
                int len = 0;
                while((len = inputStream.read(buffer)) > 0){
                    fos.write(buffer, 0, len);
                }
                //关闭文件
                fos.close();
                inputStream.close();
                msg = "文件上传成功！";
                fileItem.delete();
                //================文件传输完毕===================
            }
        }
        return msg;
    }
}
