package com.myy.wenda.dao;

import com.myy.wenda.model.Question;
import com.myy.wenda.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@Mapper
public interface QuestionDAO {
    //注意空格 防止SQL语句粘连在一起
    String TABLE_NAME =" question ";
    String INSERT_FIELDS = " title, content,created_date,user_id,comment_count ";
    String SELECT_FIELDS = " id, "+INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS,
            ") values(#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    //通过XML配置的方式连接数据库
    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Question getById(int id);

    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id = #{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);
}
