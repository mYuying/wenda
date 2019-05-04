package com.myy.wenda.dao;

import com.myy.wenda.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

@Service
@Mapper
public interface UserDAO {
    //注意空格 防止SQL语句粘连在一起
    String TABLE_NAME =" user ";
    String INSERT_FIELDS = " name, password, salt, head_url ";
    String SELECT_FIELDS = " id, "+INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS,
            ") values(#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id = #{id}"})
    User selectById(int id);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where name = #{name}"})
    User selectByName(String name);

    @Update({"update ",TABLE_NAME," set password = #{password} where id = #{id}"})
    void updatePassword(User user);

    @Delete({"delete from ",TABLE_NAME," where id = #{id}"})
    void deleteById(int id);
}
