package com.ricardo.reggie.controller;

import com.ricardo.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info("文件上传");
        log.info(file.toString());
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID()+suffix;
        //创建目录对象
        File dir = new File(basePath);
        //判断路径目录是否存在
        if (!dir.exists()){
            //如果不存在，就需要创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //创建输入流对象
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(basePath+name);
            //得到输出流对象
            ServletOutputStream outputStream = response.getOutputStream();
            //设置接收的文件类型
            response.setContentType("image/jpeg");
            //创建byte数组接收文件
            byte[] bytes = new byte[1024];
            //边读边存
            int len;
            while ((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //释放资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
