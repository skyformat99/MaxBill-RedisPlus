package com.maxbill.base.dao;

import com.maxbill.base.bean.Connect;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataMapper {

    @Update("create table t_connect(id varchar(100),text varchar(100),name varchar(100),rpass varchar(100),spass varchar(100),rhost varchar(100),shost varchar(100),rport varchar(100),sport varchar(100),type varchar(1),time varchar(100),primary key (id))")
    void createConnectTable() throws Exception;

    @Select("SELECT COUNT(T.TABLENAME) FROM SYS.SYSTABLES T, SYS.SYSSCHEMAS S WHERE S.SCHEMANAME = 'APP' AND S.SCHEMAID = T.SCHEMAID AND T.TABLENAME=#{tableName}")
    int isExistsTable(@Param("tableName") String tableName);

    @Select("select * from t_connect where id=#{id}")
    Connect selectConnectById(@Param("id") String id);

    @Select("select * from t_connect")
    List<Connect> selectConnect() throws Exception;

    @Insert("insert into t_connect(id,text,name,rpass,spass,rhost,shost,rport,sport,type,time) values(#{o.id},#{o.text},#{o.name},#{o.rpass},#{o.spass},#{o.rhost},#{o.shost},#{o.rport},#{o.sport},#{o.type},#{o.time})")
    int insertConnect(@Param("o") Connect obj) throws Exception;

    @Update("update t_connect set text=#{o.text},name=#{o.name},rpass=#{o.rpass},spass=#{o.spass},rhost=#{o.rhost},shost=#{o.shost},rport=#{o.rport},sport=#{o.sport},type=#{o.type},time=#{o.time} where id=#{o.id}")
    int updateConnect(@Param("o") Connect obj) throws Exception;

    @Delete("delete from t_connect where id=#{id}")
    int deleteConnectById(@Param("id") String id);

}
