package vincent.chan.pojo;

public class User {

	String uid;
	String name;
	String password;
	String email;
	String totalstep;
	long registerTime;
	long loginTime;
	int loginCount;
	int gender;
	String code;
	Role role;

	public Role getRole() {
		return role;
	}

	public String getTotalstep() {
		return totalstep;
	}

	public void setTotalstep(String totalstep) {
		this.totalstep = totalstep;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public int getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}

	public int isGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "User [uid=" + uid + ", name=" + name + ", password=" + password
				+ ", email=" + email + ", registerTime=" + registerTime
				+ ", loginTime=" + loginTime + ", loginCount=" + loginCount
				+ ", gender=" + gender + ", code=" + code + "]";
	}

}
