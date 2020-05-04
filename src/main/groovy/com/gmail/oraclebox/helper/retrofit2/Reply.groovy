package com.gmail.oraclebox.helper.retrofit2

import groovy.transform.ToString
import groovy.transform.builder.Builder

/**
 * Restful API standard response object
 */
@Builder
@ToString
class Reply {
    String jwtToken;
    String systemMessage;
    Object dataObject;
}