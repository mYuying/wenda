package com.myy.wenda.Service;

import com.myy.wenda.dao.QuestionDAO;
import com.myy.wenda.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public Question getById(int id) {
        return questionDAO.getById(id);
    }

    public int addQuestion(Question question){
        //html过滤，防止用户输入时有HTML脚本导致出错
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        //敏感词过滤

        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return questionDAO.addQuestion(question) >0 ? question.getId():0;
    }


    public List<Question> getLastedQuestions(int userId,int offset,int limit){
        return questionDAO.selectLatestQuestions(userId,offset,limit);
    }
}
