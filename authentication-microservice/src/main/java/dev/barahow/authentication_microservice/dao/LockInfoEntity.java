package dev.barahow.authentication_microservice.dao;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class LockInfoEntity {

    private boolean locked;
    private LocalDateTime lockTime;

    public LockInfoEntity() {}

    public LockInfoEntity(boolean locked, LocalDateTime lockTime) {
        this.locked = locked;
        this.lockTime = lockTime;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }
}
