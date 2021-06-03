package com.lzx.blog.popj;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 点赞和收藏表
 */
public class LikesFavorites {


    private Integer LFid;

    //用户名
    private String LFusername;

    //用户点赞的帖子id
    private Integer LFarticleid;

    //该用户是否点赞
    private Integer LFlikes;

    //该用户是否收藏
    private Integer LFfavorites;

    public void SetAll(String LFusername, Integer LFarticleid, Integer LFlikes, Integer LFfavorites) {
        this.LFusername = LFusername;
        this.LFarticleid = LFarticleid;
        this.LFlikes = LFlikes;
        this.LFfavorites = LFfavorites;
    }
}
