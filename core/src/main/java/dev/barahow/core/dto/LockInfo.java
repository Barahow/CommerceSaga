package dev.barahow.core.dto;




import java.time.LocalDateTime;

public class LockInfo {
    private boolean locked;
    private LocalDateTime lockTime;

    public LockInfo() {}

    public LockInfo(boolean locked, LocalDateTime lockTime) {
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
