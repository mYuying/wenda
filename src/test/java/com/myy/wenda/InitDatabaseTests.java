package com.myy.wenda;

import com.myy.wenda.Service.FollowService;
import com.myy.wenda.dao.QuestionDAO;
import com.myy.wenda.dao.UserDAO;
import com.myy.wenda.model.EntityType;
import com.myy.wenda.model.Question;
import com.myy.wenda.model.User;
import com.myy.wenda.util.JedisAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {
    @Autowired
    UserDAO userDAO;

    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Test
    public void contextLoads(){
        Random random = new Random();
        jedisAdapter.getJedis().flushDB();

        for(int i=0;i<11;i++){
            User user = new User();
           // user.setId(i+1); user表中ID没有设置自增时，必须要有这个字段
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
            user.setName(String.format("USER%d",i));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);

            user.setPassword("newpassword");
            userDAO.updatePassword(user);

            for (int j = 1; j < i; ++j) {
                followService.follow(j, EntityType.ENTITY_USER, i);
            }

            Question question = new Question();
            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() +1000*3600*5*i);
            question.setCreatedDate(date);
            question.setUserId(i+1);
            question.setTitle(String.format("TITLE{%d}",i));
            question.setContent(String.format("balalalal content %d",i));
            questionDAO.addQuestion(question);
        }

        Assert.assertEquals("newpassword",userDAO.selectById(1).getPassword());
        userDAO.deleteById(1);
        Assert.assertNull(userDAO.selectById(1));

        //questionDAO.selectLatestQuestions()方法测试
        System.out.println(questionDAO.selectLatestQuestions(2,0,10));
    }

}
