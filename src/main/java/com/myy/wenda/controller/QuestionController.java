package com.myy.wenda.controller;

import com.myy.wenda.Service.CommentService;
import com.myy.wenda.Service.QuestionService;
import com.myy.wenda.Service.UserService;
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
            vo.set("user",userService.getUser(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments",vos);
        return "detail";
   }
}
