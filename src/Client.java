import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Client extends User {
	private int wrongCount;
	private LocalDateTime banTime;
	private static String nowUserId;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	private String getInput(String message) {//String 값 입력받는 함수
	    System.out.print(message);
	    try {
	        return br.readLine();
	    } catch (IOException e) {
	        e.printStackTrace();
	        return "";
	    }
	}
	private boolean verifyPassword(String insertPassword) { //비밀번호 확인하는 함수
	    return insertPassword.equals(super.clientsMap.get(nowUserId).getPassword());
	    //parameter로 받은 패스워드와 현재 아이디를 키값으로 value인 Clint 객체에 접근해 해당 패스워드를 get해서 equals로 비교
	}
	
	private boolean isNicknameTaken(String nickName) { //닉네임 중복 체크 함수
		for (Client client : super.clientsMap.values()) {
			if (client.getNickName().equals(nickName)) {
				return true; //true면 이미 중복된 아이디 존재
			}
		}
		return false;
	}
	
	
	public void register() {// todo 아이디 저장, 비밀번호 저장, 닉네임저장, 기존 아이디비교
		Set<String> idSet = super.clientsMap.keySet(); // 기존 id 목록 set저장
		String userId, password, nickName; //입력 받을 id, password, nickName
		while (true) { //중복되지 않은 id 입력 받을 떄 까지 반복
				userId = getInput("사용할 아이디를 입력해주세요: ");// id 작성
				if (idSet.contains(userId)) {// id set에 작성한 id가 있다면 중복됨
					System.out.println("이미 사용중인 아이디입니다.");
				}
				else {
					break;
				}
			}
			password = getInput("사용할 비밀번호를 입력해주세요: ");// 비밀번호 작성

			while (true) {//중복되지 않은 nickName 입력 받을 떄 까지 반복
				nickName = getInput("사용할 닉네임을 입력해주세요: ");// 닉네임 작성
				if (isNicknameTaken(nickName)) {
					System.out.println("이미 사용중인 닉네임입니다. ");
				} else {
					break;
				}
			}
			super.clientsMap.put(userId, new Client(userId, password, nickName));
			System.out.println("계정이 성공적으로 생성되었습니다.");
	
		

	}

	
	
	public void editProfile() {
		String insertPassWord, changeNickName;

		insertPassWord = getInput("비밀번호 확인: ");
		if (verifyPassword(insertPassWord)) {
			// 현재 로그인된 아이디를 키값으로 해당 유저리스트 맵 벨류인 유저 객체의 패스워드를 겟 한 후 입력한 패스워드와 비교
			while (true) {
				String choice = getInput("변경할 정보를 선택해주세요(1.비밀번호, 2.닉네임)");
				switch (choice) {
				case "1":
					String chpass = getInput("변경할 비밀번호를 입력해주세요");
					super.clientsMap.get(nowUserId).setPassword(chpass);
					// 클라이언트 맵 키값으로 현재 로그인 아이디 값으로 밸류값으로 클라이언트 객체불러와 set로 패스워드 변경
					System.out.println("비밀번호가 성공적으로 변경되었습니다.");
					return;

				case "2":
					while (true) {
						changeNickName = getInput("변경할 닉네임을 입력해주세요: ");
						if (isNicknameTaken(changeNickName)) {
							System.out.println("해당 닉네임은 이미 사용중입니다.");
						}

						else {
							super.clientsMap.get(nowUserId).setNickName(changeNickName);
							System.out.println("닉네임을 성공적으로 변경했습니다..");
							return;
						}
					}
				default:
					System.out.print("올바른 값을 입력해주세요(1.비밀번호, 2.닉네임)");
					break;
				}
			}
		} else {
			System.out.println("비밀번호가 맞지 않습니다.");
			return;
		}

	}
	
	public void deleteAccount() {
		String insertPassWord = getInput("회원탈퇴를 진행하려면 비밀번호를 입력해주세요: ");// 비밀번호 입력받음

		if (verifyPassword(insertPassWord)) {// 입력받은 비밀번호 로그인된 계정의 비밀번호와 일치하는지 확인
			super.clientsMap.remove(nowUserId); // 해당 계정 map 에서 삭제
			this.nowUserId = ""; // 현재 로그인된 계정이 삭제되므로 현재 로그인 id값 초기화
			System.out.println("계정이 성공적으로 삭제되었습니다.");
			// 초기 로그인 화면 호출
		} else {
			System.out.println("비밀번호가 맞지 않습니다.");
			// 이전 메뉴 호출
		}

	}
	
	static void showUserList() {
		
	}
	

	@Override
	public void menu() {
		// TODO Auto-generated method stub
		
	}
	
	public int getWrongCount() {
		return wrongCount;
	}

	public void setWrongCount(int wrongCount) {
		this.wrongCount = wrongCount;
	}

	public LocalDateTime getBanTime() {
		return banTime;
	}

	public void setBanTime(LocalDateTime banTime) {
		this.banTime = banTime;
	}

	public Map<String, Client> getClientsMap() {
		return super.clientsMap;
	}

	

	Client(String userId, String password, String nickName) {//클라이언트 생성자(회원가입시)
		super.setUserId(userId);
		super.setPassword(password);
		super.setNickName(nickName);

	}
	public void setNowUserId() { //현재 로그인 id 정보 설정
		nowUserId = super.getUserId();	
	}
	
	public static String getNowUserId() { //현재 로그인 id 리턴
		return nowUserId;
	}
	
	}
