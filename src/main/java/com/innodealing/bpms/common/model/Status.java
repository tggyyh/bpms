package com.innodealing.bpms.common.model;

public enum Status {
    //初始
    INIT(0),
    //待确认
    CONFIRM(1),
    //待处理
    HANDLE(2),
    //待审核
    CHECK(4),
    //待重新处理
    RE_HANDLE(8),
    //待重新审核
    RE_CHECK(16),
    //结束
    FINISH(32),
    //取消
    CANCEL(64),
    //删除
    DELETE(128);

    Status(int value) {
        this.value = value;
    }
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
