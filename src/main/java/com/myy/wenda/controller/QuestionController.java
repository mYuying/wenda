package com.myy.wenda.controller;

import com.myy.wenda.Service.*;
import com.myy.wenda.dao.QuestionDAO;
import com.myy.wenda.model.*;
import com.myy.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.ListView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;


    @Autowired
    LikeService likeService;

    @RequestMapping(value = "question/add",method = {RequestMethod.POST})
    @ResponseBody
    public String addQuesion(@RequestParam("title") String title,
                             @RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setContent(content);
            question.setTitle(title);
            question.setCreatedDate(new Date());
            if(hostHolder.getUser() == null){
                //如果当前用户为空，则设置一个默认的当前用户
                question.setUserId(WendaUtil.ANONYMOUS_USERID);

                //测试addquestion时 json串的code返回值为999时的情况
                //return WendaUtil.getJSONString(999);
            }else{
                question.setUserId(hostHolder.getUser().getId());
            }

            if(questionService.addQuestion(question) >0 ){
                //如果增加问题成功，则返回json串，code为0；code为1表示失败
                return WendaUtil.getJSONString(0);
            }
        }catch(Exception e){
            logger.error("增加题目失败",e.getMessage());
        }
        return WendaUtil.getJSONString(1,"失败");
   }


   @RequestMapping(value = "/question/{qid}",method ={RequestMethod.GET})
    public String questionDetail(Model model,@PathVariable("qid") int qid){
        Question question = questionService.getById(qid);
        model.addAttribute("question",question);

        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        for(Comment comment : commentList){
            ViewObject vo = new ViewObject();
            vo.set("comment",comment);
            if (hostHolder.getUser() == null) {
                vo.set("liked", 0);
            } else {
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }

            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vo.set("user",userService.getUser(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments",vos);

       List<ViewObject> followUsers = new ArrayList<ViewObject>();
       // 获取关注的用户信息
       List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
       for (Integer userId : users) {
           ViewObject vo = new ViewObject();
           User u = userService.getUser(userId);
           if (u == null) {
               continue;
           }
           vo.set("name", u.getName());
           vo.set("headUrl", u.getHeadUrl());
           vo.set("id", u.getId());
           followUsers.add(vo);
       }
       model.addAttribute("followUsers", followUsers);
       if (hostHolder.getUser() != null) {
           model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
       } else {
           model.addAttribute("followed", false);
       }

        return "detail";
   }
}
