package com.maxbill.base.service;

import com.maxbill.base.bean.Connect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataService {

    void createConnectTable() throws Exception;

    Connect selectConnectById(String id);

    List<Connect> selectConnect() throws Exception;

    int insertConnect(Connect obj) throws Exception;

    int updateConnect(Connect obj) throws Exception;

    int deleteConnectById(@Param("id") String id) throws Exception;
}
