package com.lzx.blog.popj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * ๆ้่กจ
 */
public class Authority {
    private int id;

    private String username;

    private String authority;
}
