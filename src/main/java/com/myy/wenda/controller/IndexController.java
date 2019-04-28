package com.myy.wenda.controller;

import com.myy.wenda.Service.WendaService;
import com.myy.wenda.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    WendaService wendaService;

    @RequestMapping(path = {"/","/index"},method={RequestMethod.GET})
    @ResponseBody
    public String index(HttpSession httpSession){
        logger.info("VISIT HOME");
        return "hello myy"+httpSession.getAttribute("msg");
    }




    /*访问的网页地址类似：http://127.0.0.1:8080/profile/admin/2?type=1&key=a*/
    @RequestMapping(path = {"profile/{groupID}/{userID}"})
    @ResponseBody
    public String profile(@PathVariable("userID") int userID,
                          @PathVariable("groupID") String groupID,
                          @RequestParam(value = "type",defaultValue = "1") int type,
                          @RequestParam(value = "key",required = false) String key){
        return String.format("Profile Page of %s / %d,t:%d k:%s",groupID,userID,type,key);
    }

    //通过模板访问
    @RequestMapping(path = "/vm",method = {RequestMethod.GET})
    public String template(Model model){
        model.addAttribute("value1","xxx1");
        List<String> colors = Arrays.asList(new String[]{"RED","GREEN","BLUE"});
        model.addAttribute("colors",colors);

        Map<String,String> map = new HashMap<>();
        for(int i=0;i<4;i++){
            map.put(String.valueOf(i),String.valueOf(i*i));
        }
        model.addAttribute("map",map);
        model.addAttribute("user",new User("LEE"));
        return "home";
    }

    @RequestMapping(path={"/request"},method = {RequestMethod.GET})
    @ResponseBody
    public String request(Model model, HttpServletResponse httpServletResponse,
                          HttpServletRequest httpServletRequest,
                          HttpSession httpsession,
                          @CookieValue("JSESSIONID") String sessionId){
        StringBuilder sb = new StringBuilder();
        sb.append("COOKIEVALUE:"+sessionId);
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name+":"+ httpServletRequest.getHeader(name)+"<br>");
        }
        if(httpServletRequest.getCookies() != null){
            for(Cookie cookie:httpServletRequest.getCookies()){
                sb.append("Cookie:"+cookie.getName()+"value:"+cookie.getValue());
            }
        }
        sb.append(httpServletRequest.getMethod()+"<br>");
        sb.append(httpServletRequest.getQueryString()+"<br>");
        sb.append(httpServletRequest.getPathInfo()+"<br>");
        sb.append(httpServletRequest.getRequestURL()+"<br>");

        httpServletResponse.addHeader("myy","hello");
        httpServletResponse.addCookie(new Cookie("username","myy"));
        return sb.toString();
    }

    //从request页面跳转到index首页，同时将session消息传递过去
    @RequestMapping(path={"/redirect/{code}"},method={RequestMethod.GET})
    //  @ResponseBody
    public String redirect(@PathVariable("code") int code,
                           HttpSession httpSession){
        httpSession.setAttribute("msg","jump from redirect");
        return "redirect:/";
    }

    //另外一种跳转
    @RequestMapping(path={"/redirect2/{code}"},method={RequestMethod.GET})
    public RedirectView redirect2(@PathVariable("code") int code,
                                  HttpSession httpsession){
        httpsession.setAttribute("msg","jump from redirect2");
        RedirectView red = new RedirectView("/",true);
        if(code==301){//301 永久跳转
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }

    //异常处理
    @RequestMapping(path={"/admin"},method={RequestMethod.GET})
    @ResponseBody
    public String admin(@RequestParam("key") String key){
        if("admin".equals(key))
            return "hello admin";
        throw new IllegalArgumentException("参数不对");
    }

    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e){
        return "error:"+e.getMessage();
    }

}
