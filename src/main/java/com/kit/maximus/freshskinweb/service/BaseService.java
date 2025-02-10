package com.kit.maximus.freshskinweb.service;

import java.util.List;
import java.util.Map;

public interface BaseService<request,response> {
    response add(request request);
    boolean delete(Long id);
    boolean delete(List<Long> id);

    boolean  deleteTemporarily(Long id);
    boolean  deleteTemporarily(List<Long> id);

    response update(Long id,request request);
    List<response> update(List<request> listRequest);
    Map<String, Object> getAll(int page, int size);

    //bo sung 1 ham khoi phuc: 1 san pham va nhieu

    //khi xóa mềm -> status thành false, deleted thành true
}
