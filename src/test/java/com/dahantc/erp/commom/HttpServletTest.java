package com.dahantc.erp.commom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class HttpServletTest extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String _result = "{\"status\":\"success\"}";
		try {
//			String _message = request.getParameter("report");
//			System.out.println(request.getHeader("appkey"));
//			System.out.println("接收到数据：" + _message);
//			System.out.println("数据流接收：" + getDateFromJson(request));
			_result = receiveFile(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ServletOutputStream out = null;
			try {
				out = response.getOutputStream();
				if (out != null) {
					out.write(_result.getBytes());
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getDateFromJson(HttpServletRequest request) throws IOException, UnsupportedEncodingException {
		StringBuffer _strBuf = new StringBuffer();
		BufferedReader _br = null;
		String _str = "";
		try {
			_br = request.getReader();
			while ((_str = _br.readLine()) != null) {
				_strBuf.append(_str.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _strBuf.toString();
	}

	private String receiveFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(httpRequest.getSession().getServletContext());
		MultipartHttpServletRequest multipartRequest = commonsMultipartResolver.resolveMultipart(httpRequest);
		MultipartFile dataFile = null;
		String taskId = null;
		if (multipartRequest != null) {
			taskId =  multipartRequest.getParameter("taskId");
			System.out.println("taskId：" + taskId);
			dataFile = multipartRequest.getFile("dataFile");
			if (dataFile != null) {
				System.out.println("dataFile：" + dataFile != null ? dataFile.getOriginalFilename() : "");
				uploadPhonesFile(dataFile, "C:/Users/周小林/Desktop/" + dataFile.getOriginalFilename());
			}
		}
		return "0";
	}

	private void uploadPhonesFile(MultipartFile dataFile, String disPath) throws IOException {
		FileOutputStream fos = new FileOutputStream(new File(disPath), true);
		InputStream inputStream = dataFile.getInputStream();
		byte[] by = new byte[1024];
		int len = 0;
		if (inputStream != null) {
			while ((len = inputStream.read(by)) != -1) {
				fos.write(by, 0, len);
			}
			fos.flush();
		}
		fos.close();
	}

}
