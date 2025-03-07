import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class User implements Serializable{
	private String userId;
	private String password;
	private String nickName;

	private Map<String, User> userMap = new HashMap<String, User>();

	public User(String userId, String password) {
		this.userId = userId;
		this.password = password;
	}

	public void addUser(User user) {
		if (userMap.containsKey(user)) {
			System.out.println("이미 존재하는 아이디입니다.");
			return;
		} else {
			System.out.println("put");
			userMap.put(user.userId, user);
			save();
		}

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
				this.userId = userId;
				menu();
			}
		}

	}

	private void save() {
//		String path = "C:\\AdoptPet\\" + userId + ".txt";
		String path = "C:\\AdoptPet\\userList.txt";
		File file = new File(path);

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(file, true);
			out = new ObjectOutputStream(fos);
			out.writeObject(userMap);

			out.close();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		System.out.println("text save");
	}

	void load() {
		System.out.println("load start");
		String path = "C:\\AdoptPet\\userList.txt";
		File file = new File(path);

		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			
			userMap = (HashMap) in.readObject(); // 역질렬화
			System.out.println("역직렬화");
			Set<String> set = userMap.keySet();
			for (String user : set) {
//				if (user.contains(userId)) {
					String userID = userMap.get(user).userId;
					String nickName = userMap.get(user).nickName;
					
//				}
					System.out.println("아이디 " +userId + "닉네임 " + nickName);
			}
			in.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
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
