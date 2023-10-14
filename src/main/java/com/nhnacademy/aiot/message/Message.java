package com.nhnacademy.aiot.message;

import org.json.JSONObject;

/**
 * 노드 간 데이터 전달을 위한 메세지입니다.
 */
public class Message extends JSONObject implements Comparable<Message> {

    private static final int DEFAULT_PRIORITY = 0;

    private final int priority;
    private final long expiredTimeMillis;

    /**
     * 우선순위를 가지는 메세지를 생성합니다.
     * 
     * @param priority 메세지의 우선순위입니다.
     *        <ul>
     *        <li>우선순위가 <strong>높을수록 먼저 처리</strong>됩니다.
     *        <li>되도록 보통 우선순위(0)를 사용하도록 합니다.
     *        </ul>
     * @param lifeTimeMillis 메세지가 유효한 시간(ms)입니다.
     */
    public Message(int priority, long lifeTimeMillis) {
        this.priority = priority;
        this.expiredTimeMillis = System.currentTimeMillis() + lifeTimeMillis;
    }

    /**
     * <strong>보통</strong> 우선순위를 가지는 메세지를 생성합니다.
     * 
     * @param lifeTimeMillis 메세지가 유효한 시간(ms)입니다.
     */
    public Message(long lifeTimeMillis) {
        this(DEFAULT_PRIORITY, lifeTimeMillis);
    }

    /**
     * 메세지가 만료되었는지 확인합니다.
     * 
     * @return 메세지가 만료되었으면 <code>true</code>를 반환합니다.
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiredTimeMillis;
    }

    @Override
    @SuppressWarnings("squid:S1210")
    public int compareTo(Message otherMessage) {
        if (priority < otherMessage.priority) {
            return 1;
        }
        if (priority > otherMessage.priority) {
            return -1;
        }
        return 0;
    }

}
