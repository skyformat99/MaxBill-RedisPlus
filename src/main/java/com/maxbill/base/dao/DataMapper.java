package com.maxbill.base.dao;

import com.maxbill.base.bean.Connect;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataMapper {

    @Update("create table t_connect(id varchar(100),name varchar(100),pass varchar(100),host varchar(100),port varchar(100),time varchar(100),primary key (id))")
    void createConnectTable() throws Exception;

    @Select("select * from t_connect where id=#{id}")
    Connect selectConnectById(@Param("id") String id);

    @Select("select * from t_connect")
    List<Connect> selectConnect() throws Exception;

    @Insert("insert into t_connect(id,name,pass,host,port,time) values(#{o.id},#{o.name},#{o.pass},#{o.host},#{o.port},#{o.time})")
    int insertConnect(@Param("o") Connect obj) throws Exception;

    @Update("update t_connect set name=#{o.name},pass=#{o.pass},host=#{o.host},port=#{o.port},time=#{o.time} where id=#{o.id}")
    int updateConnect(@Param("o") Connect obj) throws Exception;

    @Delete("delete from t_connect where id=#{id}")
    int deleteConnectById(@Param("id") String id);

}
