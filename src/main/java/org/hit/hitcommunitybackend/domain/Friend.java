package org.hit.hitcommunitybackend.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "friends")
@IdClass(FriendId.class)
public class Friend {

    @Id
    @Column(name = "uid1", nullable = false)
    private Integer uid1;

    @Id
    @Column(name = "uid2", nullable = false)
    private Integer uid2;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendshipStatus status;

    @Column(name = "since", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime since;

    // Default constructor
    public Friend() {
        this.since = LocalDateTime.now(); // Ensure since is set to current timestamp
    }

    // Constructor with parameters
    public Friend(Integer uid1, Integer uid2, FriendshipStatus status) {
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.status = status;
        this.since = LocalDateTime.now(); // Ensure since is set to current timestamp
    }

    // Getters and Setters

    public Integer getUid1() {
        return uid1;
    }

    public void setUid1(Integer uid1) {
        this.uid1 = uid1;
    }

    public Integer getUid2() {
        return uid2;
    }

    public void setUid2(Integer uid2) {
        this.uid2 = uid2;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public LocalDateTime getSince() {
        return since;
    }

    public void setSince(LocalDateTime since) {
        this.since = since;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "uid1=" + uid1 +
                ", uid2=" + uid2 +
                ", status=" + status +
                ", since=" + since +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return Objects.equals(uid1, friend.uid1) &&
                Objects.equals(uid2, friend.uid2) &&
                status == friend.status &&
                Objects.equals(since, friend.since);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid1, uid2, status, since);
    }

}

