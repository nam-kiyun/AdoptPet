import java.time.LocalDateTime;
import java.io.*;
import java.util.*;

public class Client extends User {
	private int wrongCount;
	private LocalDateTime banTime;
	private Map<String, Client> clientsMap;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public void register() {// todo 아이디 저장, 비밀번호 저장, 닉네임저장, 기존 아이디비교
		Set<String> idSet = clientsMap.keySet(); // id 목록 set저장
		String userId = "", password = "", nickName = "";
		idInsertAgain: // id 중복시 라벨
		while (true) { // 계정생성 완료까지 반복
			System.out.print("사용할 아이디를 입력해주세요: "); // id 작성
			try {
				userId = br.readLine();

				for (String str : idSet) { // id 목록 반복
					if (str.equals(userId)) {// 작성한 id와 기존 유저 id 비교
						System.out.print("이미 사용중인 아이디 입니다.");
						break idInsertAgain; // 사용중인 아이디라면 처음 id를 입력받는 반복문 부터 다시 반복 라벨
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.print("사용할 비밀번호를 입력해주세요: "); // 비밀번호
			try {
				password = br.readLine(); // 비밀번호 작성

			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.print("사용할 닉네임을 입력해주세요: "); // 닉네임
			try {
				nickName = br.readLine(); // 닉네임 작성

			} catch (IOException e) {
				e.printStackTrace();
			}

			clientsMap.put(userId, new Client(userId, password, nickName));
			System.out.print("회원가입이 정상적으로 완료 되었습니다.");
			return;

		}
	}
	
	public void editProfile() {
		
	}
	
	public void deleteAccount() {
		
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
		return clientsMap;
	}

	public void setClientsMap(Map<String, Client> clientsMap) {
		this.clientsMap = clientsMap;
	}

	Client(String userId, String password, String nickName) {//클라이언트 생성자(회원가입시)
		super.setUserId(userId);
		super.setPassword(password);
		super.setNickName(nickName);

	}
	}
