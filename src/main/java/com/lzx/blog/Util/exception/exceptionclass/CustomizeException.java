package com.lzx.blog.Util.exception.exceptionclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomizeException extends RuntimeException{

    private Integer code;

    private String message;

    private String ViewName;
}
