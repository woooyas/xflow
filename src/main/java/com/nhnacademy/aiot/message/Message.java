package com.nhnacademy.aiot.message;

import org.json.JSONObject;

/**
 * 노드 간 정보 교환을 위한 메세지 클래스입니다.
 */
public class Message extends JSONObject implements Comparable<Message> {

    private final int priority;
    private final long expiredTimeMillis;

    /**
     * 설정한 우선순위를 가지는 메세지를 생성합니다.
     * 
     * @param priority 메세지의 우선순위입니다.
     *        <ul>
     *        <li>우선순위가 <strong>높을수록 먼저 처리</strong>됩니다.
     *        <li>우선순위가 꼬이면 처리되지 않는 메세지가 생길 수 있으니 되도록 사용하지 마세요.
     *        </ul>
     *        <p>
     * @param lifeTimeMillis 메세지가 유효한 시간(ms)입니다.
     *        <ul>
     *        <li><code>1000</code> 입력 시, 메세지가 생성되고 1,000ms 후에 만료됩니다.
     *        </ul>
     */
    public Message(int priority, long lifeTimeMillis) {
        super();
        this.priority = priority;
        expiredTimeMillis = System.currentTimeMillis() + lifeTimeMillis;
    }

    /**
     * <strong>보통</strong> 우선순위를 가지는 메세지를 생성합니다.
     * 
     * @param lifeTimeMillis 메세지가 유효한 시간(ms)입니다.
     *        <ul>
     *        <li><code>1000</code> 입력 시, 메세지가 생성되고 1,000ms 후에 만료됩니다.
     *        </ul>
     */
    public Message(long lifeTimeMillis) {
        this(0, lifeTimeMillis);
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
    public int compareTo(Message o) {
        if (getPriority() < o.getPriority()) {
            return 1;
        }
        if (getPriority() > o.getPriority()) {
            return -1;
        }
        return 0;
    }

    private int getPriority() {
        return priority;
    }

}
