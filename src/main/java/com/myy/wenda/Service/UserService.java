package com.myy.wenda.Service;

import com.myy.wenda.dao.LoginTicketDAO;
import com.myy.wenda.dao.UserDAO;
import com.myy.wenda.model.LoginTicket;
import com.myy.wenda.model.User;
import com.myy.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;
import java.util.*;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
   private LoginTicketDAO loginTicketDAO;

    public Map<String,String> register(String username,String password){
        Map<String,String> map = new HashMap<String,String>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if(user != null){
            map.put("msg","用户已经被注册");
            return map;
        }

        //密码强度
        user =  new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        //登陆  注册成功之后直接跳转到登录页面，所以注册的时候也需要记录ticket信息
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public Map<String,String> login(String username,String password){
        Map<String,String> map = new HashMap<String,String>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if(user == null){
            map.put("msg","用户名不存在");
            return map;
        }

        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","用户密码不正确");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime()+1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }
}
