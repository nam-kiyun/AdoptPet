import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Client extends User implements Serializable{

	private static String nowUserId;// 현재 로그인된 Client의 id를 저장하는 변수
	

	public static String getInput(String message) {// String 값 입력받는 함수
		System.out.print(message);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	private boolean isValidId(String id) { // id 패턴 체크함수
		final String ID_RULES = "^[A-Za-z](?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9]{7,14}$"; // 영문자로 시작, 영문자와 숫자가 포함된, 8~15길이,
																						// 공백과 특수문자 사용불가
		return Pattern.matches(ID_RULES, id);// 패턴에 맞으면 true
	}

	private boolean verifyPassword(String insertPassword) { // 비밀번호 일치 확인 함수
		return insertPassword.equals(super.getUserMap().get(nowUserId).getPassword());
		// parameter로 받은 패스워드와 현재 아이디를 키값으로 value인 Clint 객체에 접근해 해당 패스워드를 get해서 equals로
		// 비교
	}

	private boolean isValidPassword(String insertPassword) {// 비밀번호 패턴 체크 함수
		final String PASSWORD_RULES = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,15}$";
		// 대,소문자를 구별하는 영문자 최소 1개 포함, 숫자 최소 1개 포함, 특수문자 최소 1개 포함, 8~15길이
		return Pattern.matches(PASSWORD_RULES, insertPassword); // 패턴에 맞으면 true
	}

	private boolean isNicknameTaken(String nickName) { // 닉네임 중복 체크 함수
		for (User client : super.getUserMap().values()) {
			if (client.getNickName().equals(nickName)) {
				return true; // true면 이미 중복된 아이디 존재
			}
		}
		return false;
	}

	private boolean isValidNickname(String nickname) { // 닉네임 패턴 체크 함수
		final String NICKNAME_RULES = "^[A-Za-z가-힣][A-Za-z가-힣0-9]{1,7}$";
		// 영문자나 한글로 시작, 영문자와 한글 그리고 숫자가 포함된, 2~8 길이, 공백과 특수문자는 사용불가
		return Pattern.matches(NICKNAME_RULES, nickname);// 패턴에 맞으면 true
	}

	public void register() {// todo 아이디 저장, 비밀번호 저장, 닉네임저장, 기존 아이디비교
		Set<String> idSet = super.getUserMap().keySet(); // 기존 id 목록 set저장
		String userId, password, nickName; // 입력 받을 id, password, nickName
		while (true) { // id 입력 반복문
			userId = getInput("사용할 아이디를 입력해주세요(영문+숫자 8~15): ");// id 작성
			if (isValidId(userId)) {// id 패턴이 맞으면 중복여부 확인
				if (idSet.contains(userId)) {// id set에 작성한 id가 있다면 중복됨
					System.out.println("이미 사용중인 아이디입니다.");
				} else {// 패턴에 맞고, 중복되지 않으면 반복종료
					break;
				}
			} else {// 형식에 맞지 않을 경우
				System.out.println("아이디는 영문자로 시작해야 하며, 영문자와 숫자를 포함하고, 길이는 8~15자여야 하며, 공백과 특수문자는 사용할 수 없습니다.");
			}

		}

		while (true) {// password 입력 반복문
			password = getInput("사용할 비밀번호를 입력해주세요(영문+숫자+특수문자, 8~15): ");// 비밀번호 작성
			if (isValidPassword(password)) {// password 패턴이 맞으면 반복종료
				break;
			} else {// 형식에 맞지 않을 경우
				System.out.println("비밀번호는 영문자, 숫자, 특수문자를 포함해야 하며, 길이는 8~15자여야 하고, 공백은 사용할 수 없습니다.");
			}
		}

		while (true) {// nickName 입력 반복문
			nickName = getInput("사용할 닉네임을 입력해주세요(영문,한글+(숫자), 2~8): ");// 닉네임 작성
			if (isValidNickname(nickName)) {// nickName이 패턴에 맞으면 중복여부 확인
				if (isNicknameTaken(nickName)) {// nickName 중복체크
					System.out.println("이미 사용중인 닉네임입니다. ");
				} else {
					break;// nickName이 패턴에 맞고 중복되지 않으면 종료
				}
			} else {// 형식에 맞지 않을 경우
				System.out.println("닉네임은 영문자 또는 한글로 시작해야 하며, 영문자, 한글, 숫자를 포함하고, 길이는 2~8자여야 하며, 공백과 특수문자는 사용할 수 없습니다.");
			}
		}
		super.getUserMap().put(userId, new Client(userId,  User.hashPassword(password), nickName));
		System.out.println("계정이 성공적으로 생성되었습니다.");
		save(); //회원가입 이후 파일저장

	}

	public void editProfile() {
		String insertPassWord, changeNickName;

		insertPassWord = getInput("회원정보 수정을 진행하려면 비밀번호를 입력해주세요: ");
		if (verifyPassword( User.hashPassword(insertPassWord))) {
			// 현재 로그인된 아이디를 키값으로 해당 유저리스트 맵 벨류인 유저 객체의 패스워드를 겟 한 후 입력한 패스워드와 비교
			while (true) {
				String choice = getInput("변경할 정보를 선택해주세요(1.비밀번호, 2.닉네임): ");
				switch (choice) {
				case "1":
					while (true) {
						String chpass = getInput("변경할 비밀번호를 입력해주세요: ");
						if (isValidPassword(chpass)) {// password 패턴 확인
							super.getUserMap().get(nowUserId).setPassword(chpass);
							// 클라이언트 맵 키값으로 현재 로그인 아이디 값으로 밸류값으로 클라이언트 객체불러와 set로 패스워드 변경
							System.out.println("비밀번호가 성공적으로 변경되었습니다.");
							save();
							break;
						} else {
							System.out.println("비밀번호는 영문자, 숫자, 특수문자를 포함해야 하며, 길이는 8~15자여야 하고, 공백은 사용할 수 없습니다.");
						}
					}
					return;

				case "2":
					while (true) {
						changeNickName = getInput("변경할 닉네임을 입력해주세요: ");
						if (isValidNickname(changeNickName)) { // nickName 패턴 체크
							if (isNicknameTaken(changeNickName)) {// nickName 형식 맞으면 중복 체크
								System.out.println("해당 닉네임은 이미 사용중입니다.");
							}

							else {
								super.getUserMap().get(nowUserId).setNickName(changeNickName); // 패턴과 중복체크 통과시 변경
								System.out.println("닉네임을 성공적으로 변경했습니다.");
								save();
								break;
							}

						} else {
							System.out.println(
									"닉네임은 영문자 또는 한글로 시작해야 하며, 영문자, 한글, 숫자를 포함하고, 길이는 2~8자여야 하며, 공백과 특수문자는 사용할 수 없습니다.");
						}
					}

					return;
				default:
					System.out.println("올바른 값을 입력해주세요(1.비밀번호, 2.닉네임)");
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

		if (verifyPassword(User.hashPassword(insertPassWord))) {// 입력받은 비밀번호 로그인된 계정의 비밀번호와 일치하는지 확인
			super.getUserMap().remove(nowUserId); // 해당 계정 map 에서 삭제
			this.nowUserId = ""; // 현재 로그인된 계정이 삭제되므로 현재 로그인 id값 초기화
			System.out.println("계정이 성공적으로 삭제되었습니다.");
			save();//회원정보 삭제 후 파일저장
			// 초기 로그인 화면 호출
		} else {
			System.out.println("비밀번호가 맞지 않습니다.");
			// 이전 메뉴 호출
			return;
		}

	}


	@Override
	public void menu() {// 클라이언트 로그인 이후 메뉴
		while (true) {
			System.out.println("======================================");
			System.out.println("1.게시판 목록보기");
			System.out.println("2.회원정보 수정");
			System.out.println("3.회원탈퇴");
			System.out.println("4.로그아웃");
			System.out.println("======================================");
			String choice = getInput("원하시는 메뉴를 선택해주세요: ");

			switch (choice) {
			case "1":
				this.setNowUserId();
				super.selectBoardList();
				break;
			case "2":
				this.setNowUserId();
				this.editProfile();
				break;
			case "3":
				this.setNowUserId();
				this.deleteAccount();
				return;
			case "4":
				this.logout();
				return;

			default:
				System.out.println("올바른 메뉴를 선택해주세요");
				break;
			}
		}
	}
	
	public static void run() {
		Client.initialize();//초기 Admin 설정 및 파일 로드 
		Client main = new Client("main"," main", "main"); //회원가입 및 로그인용 객체 생성 
		Client.initializeBoard();
		
		while (true) {
			System.out.println("======================================");
			System.out.println("1.로그인");
			System.out.println("2.회원가입");
			System.out.println("3.종료");
			System.out.println("======================================");
			String choice = getInput("원하시는 메뉴를 선택해주세요: ");
			
			
			switch (choice) {
			case "1"://로그인 함수 실행
				main.login(getInput("아이디를 입력해주세요: "), getInput("비밀번호를 입력해주세요: "));
				break;
			case "2"://회원가입 진행 
				main.register();
				break;
			case "3"://종료 
				System.out.println("프로그램을 종료합니다.");
				save();
				return;

			default:
				System.out.println("올바른 메뉴를 선택해주세요");
				break;
			}
		}
	}

	public Map<String, User> getUsersMap() {
		return super.getUserMap();
	}

	Client(String userId, String password, String nickName) {// 클라이언트 생성자(회원가입시)
		super(userId, password, nickName);
	}

	public void setNowUserId() { // 현재 로그인 id 정보 설정, 로그인 이후 직접 실행해야 함
		nowUserId = super.getUserId();
	}

	public static String getNowUserId() { // 현재 로그인 id 리턴
		return nowUserId;
	}

}
