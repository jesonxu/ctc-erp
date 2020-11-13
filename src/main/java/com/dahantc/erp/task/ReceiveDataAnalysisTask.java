package com.dahantc.erp.task;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;

@WebServlet(name = "receiveDataAnalysis", urlPatterns = "/receiveDataAnalysis")
public class ReceiveDataAnalysisTask extends HttpServlet {

	private static final long serialVersionUID = -4671016974508315492L;

	private static final Logger logger = LogManager.getLogger(ReceiveDataAnalysisTask.class);

	private static final String analysisFileDir = "analysisfiles";

	private static final String RESULT_SUCCESS = "0";

	private static final String RESULT_ERROR = "-1";

	@Autowired
	private ICustomerService customerService;

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
		logger.info("接收到通信云系统推送的数据分析报告");
		String _result = RESULT_ERROR;
		try {
			_result = receiveFile(request, response);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			ServletOutputStream out = null;
			try {
				out = response.getOutputStream();
				if (out != null) {
					out.write(_result.getBytes());
					out.close();
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	private String receiveFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long _start = System.currentTimeMillis();
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
				String docFileName = dataFile.getOriginalFilename();
				String customerName = "未知客户";
				// taskId: customerId-yyyyMMddHHmm
				String[] taskInfo = taskId.split("-");
				Customer customer = customerService.read(taskInfo[0]);
				String customerId = taskInfo[0];
				if (customer == null) {
					logger.info("客户不存在，customerId：" + taskInfo[0]);
				} else {
					customerName = customer.getCompanyName();
				}
				if (StringUtils.isNotBlank(docFileName)) {
					String ext = docFileName.substring(docFileName.lastIndexOf(".") + 1, docFileName.length());
					Date today = DateUtil.convert(taskInfo[1], DateUtil.format10);

					// fileName: customerName-数据分析报告-yyyyMMddHHmm.pdf
					String reName = customerName + "-数据分析报告-" + taskInfo[1] + "." + ext;
					String resource = Constants.RESOURCE;

					String resourceDir = resource + File.separator + analysisFileDir + File.separator + customerId + File.separator + DateUtil.convert(today, "yyyyMM");
					File dir = new File(resourceDir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					String disPath = resourceDir + File.separator + reName;
					File disFile = new File(disPath);
					dataFile.transferTo(disFile);

					JSONObject dataAnalysis = new JSONObject();
					dataAnalysis.put("fileName", reName);
					dataAnalysis.put("filePath", disPath);

					logger.info("保存数据分析报告成功，耗时：" + (System.currentTimeMillis() - _start) + "，taskId：" + taskId + "，file：" + dataAnalysis.toJSONString());
				}
			}
		}
		return RESULT_SUCCESS;
	}
}
