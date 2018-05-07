package vincent.chan.utils;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Random;

import vincent.chan.pojo.Role;

public class Tool {
	/**
	 * 生成随机四位字符串
	 * 
	 * @return
	 */
	public static String randomId() {

		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < 4; ++i) {
			int number = random.nextInt(62);// [0,62)

			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
	
	
	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}

	public static String randomId(int length) {

		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < length; ++i) {
			int number = random.nextInt(62);// [0,62)

			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	public static byte[] createChecksum(InputStream fis) throws Exception {
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}

	/**
	 * 获取文件的hash
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static String getMD5Checksum(InputStream in) throws Exception {
		byte[] b = createChecksum(in);
		String result = "";

		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static Role getInitRole(int lev, String uid, String name) {
		Role role = new Role();
		role.setAttack(10);
		role.setDefend(5);
		role.setLevel(lev);
		role.setHealthPoint(100);
		role.setMagicPoint(30);
		role.setSkill(0);
		role.setUserUid(uid);
		role.setName(name);
		role.setRid(randomId(5));

		return role;

	}


}
