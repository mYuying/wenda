package com.myy.wenda.Service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    /*
    * 敏感词默认替换符*/
    private static final String DEFAULT_REPLACEMENT="敏感词";

    //前缀树节点类
    private class TrieNode{
        /**
         * true 关键词的终结 ； false 继续
         */
        private boolean end = false;

        /**
         * key下一个字符，value是对应的节点是TrieNode
         */
        private Map<Character,TrieNode> subNodes = new HashMap<>();
        /**
         * 向指定位置添加节点树
         */
        void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }
        /**
         * 获取下个节点
         */
        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeywordEnd(){
            return end;
        }

        void setKeywordEnd(boolean end){
            this.end = end;
        }

        public int getSubNodeCount(){
            return subNodes.size();
        }
    }

    /**
     * 根节点
     */
    private TrieNode rootNode = new TrieNode();


    /**
     * 判断是否是一个符号，不是正常字符（东亚文字或者字母）返回true
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    /*
    * 过滤敏感词*/
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }

        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin =0;//回滚数
        int position=0;//当前比较位置

        while(position < text.length()){
            char c = text.charAt(position);

            //空格直接跳过
            if(isSymbol(c)){
                if(tempNode == rootNode){
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            //当前位置的匹配结束
            if(tempNode == null){
                //以begin开始的字符不存在敏感词
                result.append(text.charAt(begin));
                //跳到下一个字符开始测试
                position = begin+1;
                begin = position;
                //回到树的初始节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                //发现敏感词，从begin到position的位置用replacement替代
                result.append(replacement);
                position = position+1;
                begin = position;
                tempNode = rootNode;
            }else{
                ++position;
            }
        }
        //记得将最后一次的判断添加到result中
        result.append(text.substring(begin));
        return result.toString();
    }

    //通过addWord()和afterPropertiesSet()函数将敏感词字典前缀树构造出来
    private void addWord(String lineTxt){
        TrieNode tempNode = rootNode;

        //循环每个字节
        for(int i=0;i<lineTxt.length();i++){
            Character c = lineTxt.charAt(i);

            //过滤空格
            if(isSymbol(c)){
                continue;
            }

            TrieNode node = tempNode.getSubNode(c);

            if(node == null){//没初始化
                node = new TrieNode();
                tempNode.addSubNode(c,node);
            }
            tempNode =node;

            if(i==lineTxt.length()-1){
                //关键词结束，设置结束标志
                tempNode.setKeywordEnd(true);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        try{
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while((lineTxt = bufferedReader.readLine())!= null){
                lineTxt= lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        }catch(Exception e){
            logger.error("读取敏感词文件失败"+e.getMessage());
        }
    }

    public static void main(String[] argv) {
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        s.addWord("好色");
        System.out.print(s.filter("你好X色**情XX"));
    }

}
