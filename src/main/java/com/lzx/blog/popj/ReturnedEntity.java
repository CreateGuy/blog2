package com.lzx.blog.popj;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope("prototype")
/**
 * 返回前端显示数据的的实体
 */
public class ReturnedEntity {

    private Integer code1 = 0;
    //最开始是做多图片的上传，这是有多少张图片
    private Integer dataength = 0;

    //图片的路径
    private String[] dataaddress = new String[100];

    public void increase(){
        this.dataength++;
    }

    public void SetSingleData(String data){
        this.dataaddress[dataength] = data;
    }
}
