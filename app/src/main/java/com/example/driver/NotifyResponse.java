package com.example.driver;

public class NotifyResponse {
    private int success;
    private int failure;
    private double topic_msg_id;

    public NotifyResponse(double topic_msg_id, int success, int failure)
    {
        this.topic_msg_id = topic_msg_id;
        this.success = success;
        this.failure =failure;

    }

    public double getTopicMsgId(){
        return topic_msg_id;
    }

    public int getSuccess()
    {
        return success;
    }

}
