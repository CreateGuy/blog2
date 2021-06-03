package com.lzx.blog.popj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope("prototype")
public class HotSearchList {

    private Integer SiteId;
    private String PlatformName;
    public List<HotSearch> hotSearches = new ArrayList<HotSearch>();
}
