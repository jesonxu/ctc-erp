package com.dahantc.erp.controller.search.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.operate.UploadFileRespDto;

public class SearchHandler {

	private SearchHandler() {
	}

	public static SearchHandler getInstance() {
		return getSearchHandler.single;
	}

	private static class getSearchHandler {
		private static SearchHandler single = new SearchHandler();
	}

	private Map<Integer, ISearchService> searchServiceMap = new ConcurrentHashMap<>(8);

	public BaseResponse<PageResult<Object>> doSearch(int searchType, OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate,
			int pageSize, int nowPage) {
		ISearchService searchService = searchServiceMap.get(searchType);
		if (searchService != null) {
			return searchService.search(onlineUser, searchContent, searchStartDate, searchDate, pageSize, nowPage);
		}
		return null;
	}

	public BaseResponse<UploadFileRespDto> doExport(int searchType, OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate,
			String flowId) {
		ISearchService searchService = searchServiceMap.get(searchType);
		if (searchService != null) {
			return searchService.export(onlineUser, searchContent, searchStartDate, searchDate, flowId);
		}
		return BaseResponse.error("导出失败");
	}

	public ISearchService getSearchService(Integer searchType) {
		return searchServiceMap.get(searchType);
	}

	public void registerSearchService(Integer searchType, ISearchService service) {
		searchServiceMap.put(searchType, service);
	}
}
