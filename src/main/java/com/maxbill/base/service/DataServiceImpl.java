package com.maxbill.base.service;

import com.maxbill.base.bean.Connect;
import com.maxbill.base.dao.DataMapper;
import com.maxbill.tool.DateUtil;
import com.maxbill.tool.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private DataMapper dataMapper;

    public void createConnectTable() {
        this.dataMapper.createConnectTable();
    }

    public int isExistsTable(String tableName) {
        return this.dataMapper.isExistsTable(tableName);
    }

    public Connect selectConnectById(String id) {
        return this.dataMapper.selectConnectById(id);
    }

    public List<Connect> selectConnect() {
        return this.dataMapper.selectConnect();
    }

    public int insertConnect(Connect connect) {
        connect.setId(KeyUtil.getUUIDKey());
        connect.setOnssl("0");
        connect.setSpkey("");
        connect.setTime(DateUtil.formatDateTime(new Date()));
        if ("0".equals(connect.getType())) {
            connect.setSname("--");
        } else {
            connect.setRhost("127.0.0.1");
        }
        return this.dataMapper.insertConnect(connect);
    }

    public int updateConnect(Connect connect) {
        connect.setOnssl("0");
        connect.setSpkey("");
        connect.setTime(DateUtil.formatDateTime(new Date()));
        if ("0".equals(connect.getType())) {
            connect.setSname("--");
        } else {
            connect.setRhost("127.0.0.1");
        }
        return this.dataMapper.updateConnect(connect);
    }

    public int deleteConnectById(String id) {
        return this.dataMapper.deleteConnectById(id);
    }

}
