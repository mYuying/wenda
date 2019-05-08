package com.myy.wenda.controller;

import com.myy.wenda.Service.CommentService;
import com.myy.wenda.Service.QuestionService;
import com.myy.wenda.Service.SensitiveService;
import com.myy.wenda.Service.UserService;
import com.myy.wenda.model.Comment;
import com.myy.wenda.model.EntityType;
import com.myy.wenda.model.HostHolder;
import com.myy.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    SensitiveService sensitiveService;

    @RequestMapping(path={"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try{
            //过滤content
            content = HtmlUtils.htmlEscape(content);
            content = sensitiveService.filter(content);
            Comment comment = new Comment();
            if(hostHolder.getUser()!= null){
                comment.setUserId(hostHolder.getUser().getId());
            }else{
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
                //return "redirect:/reglogin" 或者重定向到登录页面
            }
            comment.setContent(content);
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);

            commentService.addComment(comment);

            //更新问题里的评论数量
            int count = commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),count);

            //怎么异步化

        }catch(Exception e){
            logger.error("增加评论失败"+e.getMessage());
        }
        return "redirect:/question/"+String.valueOf(questionId);
    }
}
