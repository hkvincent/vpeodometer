package vincent.chan.pojo;

public class Role {
	String rid;
	String name;
	int level;
	int attack;
	int defend;
	int health_point;
	int magic_point;
	int skill;
	String userUid;

	@Override
	public String toString() {
		return "Role [rid=" + rid + ", name=" + name + ", level=" + level
				+ ", attack=" + attack + ", defend=" + defend
				+ ", healthPoint=" + health_point + ", magicPoint="
				+ magic_point + ", skill=" + skill + ", userUid=" + userUid
				+ "]";
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getDefend() {
		return defend;
	}

	public void setDefend(int defend) {
		this.defend = defend;
	}

	public int getHealthPoint() {
		return health_point;
	}

	public void setHealthPoint(int healthPoint) {
		this.health_point = healthPoint;
	}

	public int getMagicPoint() {
		return magic_point;
	}

	public void setMagicPoint(int magicPoint) {
		this.magic_point = magicPoint;
	}

	public int getHealth_point() {
		return health_point;
	}

	public void setHealth_point(int health_point) {
		this.health_point = health_point;
	}

	public int getMagic_point() {
		return magic_point;
	}

	public void setMagic_point(int magic_point) {
		this.magic_point = magic_point;
	}

	public int getSkill() {
		return skill;
	}

	public void setSkill(int skill) {
		this.skill = skill;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

}
