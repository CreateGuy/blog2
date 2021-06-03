package com.lzx.blog.popj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
/**
 * 评论表
 */

public class Comment extends Replyf {

    /**
     * 代表回复的是楼主还是楼层（和楼中楼）
     */
    private Integer flag;

    @Override
    public String toString() {
        return "Comment{" + super.toString()+" "+
                "flag=" + flag +
                '}';
    }
}
