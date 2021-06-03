package com.lzx.blog.popj.expand;

import com.lzx.blog.popj.Replyf;
import com.lzx.blog.popj.Replyz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true) //去除使用lombok注解，在派生类上的警告
/**
 * 回复帖子的实体，包括主表和副表
 */
public class ReplyArticle extends Replyz implements Comparable<ReplyArticle> {

    private List<Replyf> Replyfs;


    @Override
    public String toString() {
        return super.toString()+ "ReplyArticle{" +
                "replyfs=" + Replyfs +
                '}';
    }

    @Override
    public int compareTo(ReplyArticle o) {
        return this.getFloor()-o.getFloor();
    }
}
