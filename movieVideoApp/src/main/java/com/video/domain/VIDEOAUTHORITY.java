package com.video.domain;

import org.springframework.web.bind.annotation.GetMapping;

public enum VIDEOAUTHORITY {
	
	PRIVATE(0),PUBLIC(1);
	
    private int value;

    VIDEOAUTHORITY(int value) {
        this.value = value;
    }

    public String getKey() {
        return name();
    }

    public int getValue() {
        return value;
    }
}
