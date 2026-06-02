package com.domain.port;

import java.io.Serializable;
import java.util.UUID;

public class BaseId implements Serializable {
    public final UUID value;
    public BaseId(UUID value) {
        this.value = value;
    }
    
}
