import java.util.HashMap;
import java.util.Map;

public abstract class User {
	private String userId;
	private String password;
	private String nickName;

	private Map<String, User> userMap = new HashMap<String, User>();

	public User(String userId, String password) {
		this.userId = userId;
		this.password = password;
	}

	public void addUser(User user) {
		userMap.put(user.userId, user);
	}

	public void login(String userId, String password) {
		if (!userMap.containsKey(userId)) {
			System.out.println("없는 아이디 입니다.");
			return;
		} else {
			if (!userMap.get(userId).getPassword().equals(password)) {
				System.out.println("wrong password");
				return;
			} else {

				System.out.println("login success");
				menu();
			}
		}

	}

	public abstract void menu();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
