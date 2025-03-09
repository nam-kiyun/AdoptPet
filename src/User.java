import java.io.*;
import java.util.*;

public abstract class User implements Serializable {
	private String userId;
	private String password;
	private String nickName;
	/*
		데이터 로드 중 오류 발생! 파일이 손상되었을 수 있습니다.
		java.io.InvalidClassException: User; local class incompatible: stream classdesc serialVersionUID = 1, local class serialVersionUID = 3598836385826725148
	 */
	private static final long serialVersionUID = 1L;

	private static Map<String, User> userMap = new HashMap<>();
	public static final String path = "C:\\AdoptPet\\userList.txt"; // 저장할 파일 경로

	public User(String userId, String password) {
		this.userId = userId;
		this.password = password;
	}

	public static Map<String, User> getUserMap() {
		return userMap;
	}

	// 데이터 저장 메서드 (직렬화)
	public static void save() {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
			out.writeObject(userMap);
			System.out.println("사용자 데이터 저장 완료!");
		} catch (IOException e) {
			System.err.println("데이터 저장 중 오류 발생!");
			e.printStackTrace();
		}
	}

	// 데이터 로드 메서드
	public static void load() {
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("기존 데이터가 없습니다. 새로운 파일을 생성합니다.");
			return;
		}
		// 파일이 존재하면 데이터 로드 시도
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			Object obj = in.readObject();
			if (obj instanceof HashMap) {
				userMap = (HashMap<String, User>) obj;
				System.out.println("사용자 데이터 로드 완료! 현재 등록된 계정 수: " + userMap.size());
			}
		} catch (Exception e) {
			System.err.println("데이터 로드 중 오류 발생! 파일이 손상되었을 수 있습니다.");
			e.printStackTrace();
		}
	}
	// 프로그램 실행시 시행될 데이터 로드와 ,
	public static void initialize() {
		load();
		System.out.println("사용자 데이터를 불러왔습니다.");
		// 기본 Admin 계정 생성
		System.out.println("기본 Admin 계정을 확인.");

		if (!userMap.containsKey("admin")) {
			Admin defaultAdmin = new Admin("admin", "admin123");
			defaultAdmin.setNickName("관리자");
			userMap.put("admin", defaultAdmin);
			save();
			System.out.println("기본 Admin 계정이 생성되었습니다. (ID: admin, PW: admin123)");
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
