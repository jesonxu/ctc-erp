package com.dahantc.erp.controller.notInterestAccount;

import com.alibaba.fastjson.JSON;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.util.AccountConfigUtil;
import com.dahantc.erp.vo.noInterestAccount.INoInterestAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 不计息账号配置
 *
 * @author 8520
 */
@Controller
@RequestMapping("/notInterestAccount")
public class NotInterestAccountAction extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(NotInterestAccountAction.class);

    @Autowired
    private INoInterestAccountService noInterestAccountService;

    /**
     * 跳转到账号配置页面
     */
    @RequestMapping(value = "/toAccountConfigTable")
    public String toAccountConfigTable() {
        return "/views/manageConsole/notInterestAccount";
    }

    /**
     * 读取不计息的所有账号信息
     *
     * @param account 账号名称
     * @param desc    描述（暂时不开放）
     * @return 账号信息（账号如果有描述 用###分隔，前端做处理，后台不管）
     */
    @ResponseBody
    @PostMapping(value = "/readAll")
    public BaseResponse<List<String[]>> readAll(String account, String desc) {
        if (getOnlineUserAndOnther() == null) {
            return BaseResponse.error("请先登录");
        }
        AccountConfigUtil accountConfigUtil = AccountConfigUtil.getInstance();
        return BaseResponse.success(accountConfigUtil.readAll(account, desc));
    }

    /**
     * 导入添加账号
     *
     * @return 账号信息（账号如果有描述 用###分隔，前端做处理，后台不管）
     */
    @ResponseBody
    @PostMapping(value = "/importAdd")
    public BaseResponse<Boolean> importAdd(String files) {
        if (getOnlineUserAndOnther() == null) {
            BaseResponse.error("请先登录");
        }
        if (files == null || files.isEmpty()) {
            return BaseResponse.error("请求参数不能为空");
        }
        List<File> fileInfos = new ArrayList<>();
        try {
            List<UploadFileRespDto> fileList = JSON.parseArray(files, UploadFileRespDto.class);
            fileInfos.addAll(fileList.stream().map(file -> new File(file.getFilePath())).collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("参数解析异常");
        }
        if (fileInfos.isEmpty()) {
            return BaseResponse.error("上传文件错误");
        }
        return noInterestAccountService.importAdd(fileInfos);
    }

    /**
     * 添加账号（批量）
     *
     * @return 账号信息（账号如果有描述 用###分隔，前端做处理，后台不管）
     */
    @ResponseBody
    @PostMapping(value = "/add")
    public BaseResponse<Boolean> add(String accounts) {
        if (getOnlineUserAndOnther() == null) {
            BaseResponse.error("请先登录");
        }
        if (accounts == null || accounts.isEmpty()) {
            return BaseResponse.error("请求参数不能为空");
        }
        List<String> accountInfos = new ArrayList<>(Arrays.asList(accounts.split(",")));
        return noInterestAccountService.add(accountInfos);
    }


    /**
     * 添加账号（批量）
     *
     * @return 账号信息（账号如果有描述 用###分隔，前端做处理，后台不管）
     */
    @ResponseBody
    @PostMapping(value = "/delete")
    public BaseResponse<Boolean> delete(String accounts) {
        if (getOnlineUserAndOnther() == null) {
            BaseResponse.error("请先登录");
        }
        if (accounts == null || accounts.isEmpty()) {
            return BaseResponse.error("请求参数不能为空");
        }
        List<String> accountInfos = new ArrayList<>(Arrays.asList(accounts.split(",")));
        return noInterestAccountService.delete(accountInfos);
    }


    /**
     * 导入删除账号
     */
    @ResponseBody
    @PostMapping(value = "/importDelete")
    public BaseResponse<Boolean> importDelete(String files) {
        if (getOnlineUserAndOnther() == null) {
            BaseResponse.error("请先登录");
        }
        if (files == null || files.isEmpty()) {
            return BaseResponse.error("请求参数不能为空");
        }
        List<File> fileInfos = new ArrayList<>();
        try {
            List<UploadFileRespDto> fileList = JSON.parseArray(files, UploadFileRespDto.class);
            fileInfos.addAll(fileList.stream().map(file -> new File(file.getFilePath())).collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("参数解析异常");
        }
        if (fileInfos.isEmpty()) {
            return BaseResponse.error("上传文件错误");
        }
        return noInterestAccountService.importDelete(fileInfos);
    }
}