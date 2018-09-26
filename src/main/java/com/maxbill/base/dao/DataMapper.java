package com.maxbill.base.dao;

import com.maxbill.base.bean.Connect;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataMapper {

    @Update("create table t_connect(id varchar(100),text varchar(100),rhost varchar(100),rport varchar(100),rpass varchar(100),sname varchar(100),shost varchar(100),sport varchar(100),spass varchar(100),isha varchar(1),type varchar(1),time varchar(100),primary key (id))")
    void createConnectTable() throws Exception;

    @Select("SELECT COUNT(T.TABLENAME) FROM SYS.SYSTABLES T, SYS.SYSSCHEMAS S WHERE S.SCHEMANAME = 'APP' AND S.SCHEMAID = T.SCHEMAID AND T.TABLENAME=#{tableName}")
    int isExistsTable(@Param("tableName") String tableName);

    @Select("select * from t_connect where id=#{id}")
    Connect selectConnectById(@Param("id") String id);

    @Select("select * from t_connect")
    List<Connect> selectConnect() throws Exception;

    @Insert("insert into t_connect(id,text,rhost,rport,rpass,sname,shost,sport,spass,isha,type,time) values(#{o.id},#{o.text},#{o.rhost},#{o.rport},#{o.rpass},#{o.sname},#{o.shost},#{o.sport},#{o.spass},#{o.isha},#{o.type},#{o.time})")
    int insertConnect(@Param("o") Connect obj) throws Exception;

    @Update("update t_connect set text=#{o.text},rhost=#{o.rhost},rport=#{o.rport},rpass=#{o.rpass},sname=#{o.sname},shost=#{o.shost},sport=#{o.sport},spass=#{o.spass},isha=#{o.isha},type=#{o.type},time=#{o.time} where id=#{o.id}")
    int updateConnect(@Param("o") Connect obj) throws Exception;

    @Delete("delete from t_connect where id=#{id}")
    int deleteConnectById(@Param("id") String id);

}
