package com.seattleacademy.team20;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
public class SkillController {
	@RequestMapping(value="/skillUpload" , method = RequestMethod.GET)
/*
	@RequestMapping・・・
	"http://localhost:8080/team20/skillUpload" にアクセスするとこのコントローラが実行される。
	【value属性】処理対象とするURLを指定。value属性が一つだけなら「value=」は省略可。
	【method属性】GETでアクセス元のリクエスト(method="get")を処理。
	（@GetMapping("/skillUpload")が省略型っぽいけど使えなかった。）
*/
    public String returnSkillUpload() {
        return "skillUpload";
    }
}

/*
ロガー出力(やっている人もいた、たぶんJSのconsole.log()みたいなもの)
public static final Logger logger = LoggerFactory.getLogger(SkillController.class);
logger.info("Welcome SkillUpload! The client locale is {}",locale);
*/