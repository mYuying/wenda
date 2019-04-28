package com.myy.wenda.Service;

import org.springframework.stereotype.Service;

@Service
public class WendaService {
    public String getMessage(int UserId){
        return "hello Message:"+String.valueOf(UserId);
    }
}
