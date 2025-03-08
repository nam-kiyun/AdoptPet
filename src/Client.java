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

	public void register() {// todo 아이디 저장, 비밀번호 저장, 닉네임저장, 기존 아이디비교
		Set<String> idSet = super.clientsMap.keySet(); // id 목록 set저장
		String userId = "", password = "", nickName = "";
		idInsertAgain: // id 중복시 라벨
		while (true) { // 계정생성 완료까지 반복
			System.out.print("사용할 아이디를 입력해주세요: "); // id 작성
			try {
				userId = br.readLine();

				for (String str : idSet) { // id 목록 반복
					if (str.equals(userId)) {// 작성한 id와 기존 유저 id 비교
						System.out.println("이미 사용중인 아이디 입니다.");
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
			Collection nickList = super.clientsMap.values();
			//현재 로그인 id를 키값으로 values, 즉 클라이언트 리스트를 컬렉션에 저장
			Iterator<Collection> iterator = nickList.iterator();
			//해당 컬렉션을 이터레이터

			
				while(iterator.hasNext()) {
					System.out.print("사용할 닉네임을 입력해주세요: ");
					try {
						nickName = br.readLine();
					} catch (IOException e) {
						
						e.printStackTrace();
					} 
					Client c= (Client)iterator.next();
					
					if(c.getNickName().equals(nickName)) {
						System.out.println("이미 사용중인 닉네임입니다. ");
					}
					else
					{
						super.clientsMap.get(nowUserId).setNickName(nickName);
						System.out.println("닉네임이 성공적으로 변경되었습니다.");
						break;
					}
				}
				
				

	
				super.clientsMap.put(userId, new Client(userId, password, nickName));
			System.out.println("회원가입이 정상적으로 완료 되었습니다.");
			return;

		}
	}
	
	public void editProfile() {
		System.out.print("비밀번호 확인: ");
		try {
			String insertPassWord = br.readLine();
			if (insertPassWord.equals(super.clientsMap.get(nowUserId).getPassword())) {
				// 현재 로그인된 아이디를 키값으로 해당 유저리스트 맵 벨류인 유저 객체의 패스워드를 겟 한 후 입력한 패스워드와 비교
				while (true) {
					System.out.print("변경할 정보를 선택해주세요(1.비밀번호, 2.닉네임)");// true 변경사항 선택
					int choice = Integer.parseInt(br.readLine());
					switch (choice) {
					case 1:
						System.out.print("변경할 비밀번호를 입력해주세요");// true 변경사항 선택
						String chpass = br.readLine();
						super.clientsMap.get(nowUserId).setPassword(chpass);
						// 클라이언트 맵 키값으로 현재 로그인 아이디 값으로 밸류값으로 클라이언트 객체불러와 set로 패스워드 변경
						System.out.println("비밀번호가 성공적으로 변경되었습니다.");
						return;

					case 2:
						Collection nickList = super.clientsMap.values();
						// 현재 로그인 id를 키값으로 values, 즉 클라이언트 리스트를 컬렉션에 저장
						Iterator<Collection> iterator = nickList.iterator();
						// 해당 컬렉션을 이터레이터
						String changeNickName = "";
						while (iterator.hasNext()) {
							System.out.print("변경할 닉네임을 입력해주세요: ");
							try {
								changeNickName = br.readLine();
							} catch (IOException e) {

								e.printStackTrace();
							}
							Client c = (Client) iterator.next();

							if (c.getNickName().equals(changeNickName)) {
								System.out.println("이미 사용중인 닉네임입니다. ");
							} else {
								super.clientsMap.get(nowUserId).setNickName(changeNickName);
								System.out.println("닉네임이 성공적으로 변경되었습니다.");
								break;
							}
						}

						System.out.println("닉네임을 성공적으로 변경했습니다..");
						return;

					default:
						System.out.print("올바른 값을 입력해주세요(1.비밀번호, 2.닉네임)");
						break;
					}
				}
			} else {
				System.out.println("비밀번호가 맞지 않습니다.");
				// 이전 메뉴 호출
			}
			;

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void deleteAccount() {
		System.out.print("회원탈퇴를 진행하려면 비밀번호를 입력해주세요: ");
		
			String insertPassWord="";
			try {
				insertPassWord = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (insertPassWord.equals(super.clientsMap.get(nowUserId).getPassword())) {
				// 현재 로그인된 아이디를 키값으로 해당 유저리스트 맵 벨류인 유저 객체의 패스워드를 겟 한 후 입력한 패스워드와 비교
				super.clientsMap.remove(insertPassWord); //해당 계정 map 에서 삭제
				this.nowUserId=""; //현재 로그인된 계정이 삭제되므로 현재 로그인 id값 초기화
				System.out.println("계정이 성공적으로 삭제되었습니다.");
				//초기 로그인 화면 호출
			}
			else {
				System.out.println("비밀번호가 맞지 않습니다.");
				//이전 메뉴 호출
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
