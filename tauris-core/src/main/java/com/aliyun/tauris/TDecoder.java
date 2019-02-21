package com.aliyun.tauris;


/**
 * 将文字或字符流解码成event
 * Created by ZhangLei on 16/12/7.
 */
public interface TDecoder extends TPlugin {

    default void init() {};

    /**
     * 用于中input中解码一条消息为event
     * @param source
     * @return
     * @throws DecodeException
     */
    TEvent decode(byte[] source) throws DecodeException;

    /**
     * 用于中input中解码一条消息为event
     * @param source
     * @return
     * @throws DecodeException
     */
    TEvent decode(String source) throws DecodeException;

    /**
     * 用于filter
     * @param source
     * @param event
     * @param target
     * @throws DecodeException
     */
    void decode(String source, TEvent event, String target) throws DecodeException;

}
