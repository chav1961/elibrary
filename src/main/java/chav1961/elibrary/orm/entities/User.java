package chav1961.elibrary.orm.entities;

import java.net.URI;
import java.sql.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "QCP_USER")
public class User {
	 @Id
	 @GeneratedValue(strategy = GenerationType.UUID)
	 @Column(name = "US_Id")
	 private UUID		userId;
	 @Column(name = "US_UserName")
	 private String		userName;
	 @Column(name = "US_NickName")
	 private String		nickName;
	 @Column(name = "US_Avatar")
	 private URI 		avatar;
	 @Column(name = "US_Created")
	 private Date		created;
	 @Column(name = "US_LastSuccessfulLogon")
	 private Date		lastSuccessfulLogon;
	 @Column(name = "US_LastFailedLogon")
	 private Date		lastFailedLogon;
	 @Column(name = "US_CanLogon")
	 private boolean	canLogon;
	 @Column(name = "US_IsLocked")
	 private boolean	isLocked;
	 
	 public User() {
	 }

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public URI getAvatar() {
		return avatar;
	}

	public void setAvatar(URI avatar) {
		this.avatar = avatar;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastSuccessfulLogon() {
		return lastSuccessfulLogon;
	}

	public void setLastSuccessfulLogon(Date lastSuccessfulLogon) {
		this.lastSuccessfulLogon = lastSuccessfulLogon;
	}

	public Date getLastFailedLogon() {
		return lastFailedLogon;
	}

	public void setLastFailedLogon(Date lastFailedLogon) {
		this.lastFailedLogon = lastFailedLogon;
	}

	public boolean isCanLogon() {
		return canLogon;
	}

	public void setCanLogon(boolean canLogon) {
		this.canLogon = canLogon;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", nickName=" + nickName + ", avatar=" + avatar
				+ ", created=" + created + ", lastSuccessfulLogon=" + lastSuccessfulLogon + ", lastFailedLogon="
				+ lastFailedLogon + ", canLogon=" + canLogon + ", isLocked=" + isLocked + "]";
	}
}
