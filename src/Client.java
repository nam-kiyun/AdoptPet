import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Client extends User implements Serializable {
	private static final long serialVersionUID = 1L;

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
		return insertPassword.equals(super.getUserMap().get(super.getNowUserId()).getPassword());
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
		final String NICKNAME_RULES = "^(?!익명$)[A-Za-z가-힣][A-Za-z가-힣0-9]{1,7}$";
		// 영문자나 한글로 시작, 영문자와 한글 그리고 숫자가 포함된, 2~8 길이, 공백과 특수문자는 사용불가,"익명" 허용하지 않음
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
				System.out.println("닉네임은 영문자 또는 한글로 시작해야 하며, 영문자, 한글, 숫자를 포함하고, \n길이는 2~8자여야 하며, 공백과 특수문자는 사용할 수 없습니다.");
			}
		}
		super.getUserMap().put(userId, new Client(userId, User.hashPassword(password), nickName));
		System.out.println("계정이 성공적으로 생성되었습니다.");
		save(); // 회원가입 이후 파일저장

	}

	public void editProfile() {
		final int LINE_LENGTH = 75; // 출력 라인 길이 통일

		System.out.println("\n" + "=".repeat(LINE_LENGTH));
		String title = "📌[ 회원정보 수정 ]📌";
		System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
		System.out.println("=".repeat(LINE_LENGTH));

		String insertPassWord, changeNickName;

		insertPassWord = getInput("\n회원정보 수정을 진행하려면 비밀번호를 입력해주세요: ");
		if (verifyPassword(User.hashPassword(insertPassWord))) {
			// 현재 로그인된 아이디를 키값으로 해당 유저리스트 맵 벨류인 유저 객체의 패스워드를 겟 한 후 입력한 패스워드와 비교
			while (true) {
				String choice = getInput("\n변경할 정보를 선택해주세요(1.비밀번호, 2.닉네임): ");
				switch (choice) {
				case "1":
					while (true) {
						String chpass = getInput("\n변경할 비밀번호를 입력해주세요: ");
						if (isValidPassword(chpass)) {// password 패턴 확인
							super.getUserMap().get(super.getNowUserId()).setPassword(chpass);
							// 클라이언트 맵 키값으로 현재 로그인 아이디 값으로 밸류값으로 클라이언트 객체불러와 set로 패스워드 변경
							System.out.println("\n✅ 비밀번호가 성공적으로 변경되었습니다.");
							save();
							break;
						} else {
							System.out.println("\n⚠️ 비밀번호는 영문자, 숫자, 특수문자를 포함해야 하며, 길이는 8~15자여야 하고, 공백은 사용할 수 없습니다.");
						}
					}
					return;

				case "2":
					while (true) {
						changeNickName = getInput("\n변경할 닉네임을 입력해주세요: ");
						if (isValidNickname(changeNickName)) { // nickName 패턴 체크
							if (isNicknameTaken(changeNickName)) {// nickName 형식 맞으면 중복 체크
								System.out.println("\n⚠️ 해당 닉네임은 이미 사용중입니다.");
							}

							else {
								super.getUserMap().get(super.getNowUserId()).setNickName(changeNickName); // 패턴과 중복체크
																											// 통과시 변경
								System.out.println("\n✅ 닉네임을 성공적으로 변경했습니다.");
								save();
								break;
							}

						} else {
							System.out.println(
									"\n⚠️ 닉네임은 영문자 또는 한글로 시작해야 하며, 영문자, 한글, 숫자를 포함하고, 길이는 2~8자여야 하며, 공백과 특수문자는 사용할 수 없습니다.");
						}
					}

					return;
				default:
					System.out.println("\n⚠️ 올바른 값을 입력해주세요(1.비밀번호, 2.닉네임)");
					break;
				}
			}
		} else {
			System.out.println("\n비밀번호가 맞지 않습니다.");
			return;
		}
	}

	public void deleteAccount() {
		String insertPassWord = getInput("회원탈퇴를 진행하려면 비밀번호를 입력해주세요: ");// 비밀번호 입력받음

		if (verifyPassword(User.hashPassword(insertPassWord))) {// 입력받은 비밀번호 로그인된 계정의 비밀번호와 일치하는지 확인
			super.getUserMap().remove(super.getNowUserId()); // 해당 계정 map 에서 삭제
			super.setUserId(""); // 현재 로그인된 계정이 삭제되므로 현재 로그인 id값 초기화
			System.out.println("계정이 성공적으로 삭제되었습니다.");
			save();// 회원정보 삭제 후 파일저장
			logout();
			// 초기 로그인 화면 호출
		} else {
			System.out.println("비밀번호가 맞지 않습니다.");
			// 이전 메뉴 호출
			return;
		}

	}

	@Override
	public void menu() {// 클라이언트 로그인 이후 메뉴
		final int LINE_LENGTH = 75; // 출력 라인 길이 통일

		switch (getUserMap().get(getNowUserId()).getAlarm()) {
		case "1":
			String ch = getInput("확인하지 않은 입양승인요청이 존재합니다. 해당 메뉴로 이동할까요?(Y/N):");
			if (ch.toUpperCase().equals("Y")) {
				animalAdoptionRequest();
			}
			getUserMap().get(getNowUserId()).setAlarm("");
			break;
		case "2":
			String ch1 = getInput("확인하지 않은 입양확정요청이 존재합니다. 해당 메뉴로 이동할까요?(Y/N):");
			if (ch1.toUpperCase().equals("Y")) {
				animalAdoptionAnswer();
			}
			getUserMap().get(getNowUserId()).setAlarm("");
			break;

		default:
			break;
		}

		while (true) {
			System.out.println("\n" + "=".repeat(LINE_LENGTH));
			String title = "📌[ 메인 메뉴 ]📌";
			System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
			System.out.println("=".repeat(LINE_LENGTH));

			System.out.println("1.게시판 목록보기");
			System.out.println("2.입양 요청 승인 목록보기");
			System.out.println("3.입양 승인 확정 목록보기");
			System.out.println("4.회원정보 수정");
			System.out.println("5.회원탈퇴");
			System.out.println("0.로그아웃");
			System.out.println("=".repeat(LINE_LENGTH));
			String choice = getInput("원하시는 메뉴를 선택해주세요: ");

			switch (choice) {
			case "1":
				super.selectBoardList();
				break;
			case "2":
				animalAdoptionRequest();
				break;
			case "3":
				animalAdoptionAnswer();
				break;
			case "4":
				this.editProfile();
				break;
			case "5":
				this.deleteAccount();
				break;
			case "0":
				super.logout();
				return;

			default:
				System.out.println("올바른 메뉴를 선택해주세요");
				break;
			}
		}
	}

	public void animalAdoptionRequest() {
		final int LINE_LENGTH = 75; // 출력 라인 길이 통일

		Map<String, String> adoptPetMap = Client.getUserMap().get(getNowUserId()).adoptPetMap();
		// System.out.println(adoptPetMap.toString());
		if (adoptPetMap != null && !adoptPetMap.isEmpty()) {
			String adoptPetMapString = adoptPetMap.toString();
			String[] entries = adoptPetMapString.substring(1, adoptPetMapString.length() - 1).split(", ");
			List<String[]> list = new ArrayList<>();

			// 신청 상태가 "입양신청"인 경우만 리스트에 추가
			for (String entry : entries) {
				String[] keyValue = entry.split("="); // key와 value를 '='로 분리
				String[] keyParts = keyValue[0].split("/"); // key 부분을 '/'로 분리
				String[] valueParts = keyValue[1].split("/"); // value 부분을 '/'로 분리

				String postNum = keyParts[0]; // 게시글 번호
				String userId = keyParts[1]; // 유저 아이디
				String boardTitle = valueParts[0]; // 게시글 게시판 이름
				String applicationStatus = valueParts[1]; // 신청 상태

				// 신청 상태가 "입양신청"인 경우에만 리스트에 추가
				if ("입양승인요청".equals(applicationStatus)) {
					list.add(new String[] { postNum, userId, boardTitle, applicationStatus });
				}
			}

			// 저장된 내용 출력
			if (!list.isEmpty()) {
				System.out.println("=".repeat(LINE_LENGTH));
				for (String[] data : list) {
					System.out.println(data[2] + "의 " + data[0] + "번 게시글 \"" + data[1] + "님이 " + data[3] + "했습니다.");
				}
				System.out.println("=".repeat(LINE_LENGTH));

				int selectedIndex = -1; // 인덱스를 찾기 위한 변수, -1은 찾지 못했을 경우

				while (true) {
					String selectedChoice = getInput("1.요청승인  2.요청취소  0.뒤로가기");
					indexnumerr: switch (selectedChoice) {
					case "1":
						String selectedPostNum = getInput("승인할 게시글 번호를 입력해주세요");

						for (int i = 0; i < list.size(); i++) {
							String[] data = list.get(i);
							if (data[0].equals(selectedPostNum)) {
								selectedIndex = i; // 게시글 번호가 일치하는 인덱스를 찾음
								break; // 일치하는 게시글을 찾으면 루프 종료
							}
						}
						if (selectedIndex == -1) {
							System.out.println("입력하신 게시글 번호가 목록에 없습니다. 다시 확인해주세요.");
							break indexnumerr;
						} else {
							// 해당 게시글 번호로 입양 확정 처리
							String[] selectedData = list.get(selectedIndex);
							User.getUserMap().get(selectedData[1]).adoptPetMap()
									.put(selectedData[0] + "/" + getNowUserId(), selectedData[2] + "/입양승인처리");
							User.getUserMap().get(selectedData[1]).setAlarm("2");
							System.out.println(selectedData[0] + "번 게시글의 입양을 승인했습니다.");
							adoptPetMap.remove(selectedData[0] + "/" + selectedData[1]);
						}

						return;
					case "2":
						while (true) {
							String selectedPostNum1 = getInput("취소할 게시글 번호를 입력해주세요");
							for (int i = 0; i < list.size(); i++) {
								String[] data = list.get(i);
								if (data[0].equals(selectedPostNum1)) {
									selectedIndex = i; // 게시글 번호가 일치하는 인덱스를 찾음
									break; // 일치하는 게시글을 찾으면 루프 종료
								}
							}
							if (selectedIndex == -1) {
								System.out.println("입력하신 게시글 번호가 목록에 없습니다. 다시 확인해주세요.");
							} else {
								String[] selectedData = list.get(selectedIndex);
								adoptPetMap.remove(selectedData[0] + "/" + selectedData[1]);
								System.out.println("입양요청이 성공적으로 취소되었습니다.");
								return;
							}
						}

					case "0":
						return;

					default:
						break;
					}
				}

			} else {
				System.out.println("입양 신청 목록이 비어 있습니다.");
			}
		} else {
			System.out.println("입양 신청 목록이 비어 있습니다.");
		}
	}

	public void animalAdoptionAnswer() {
		Map<String, String> adoptPetMap = Client.getUserMap().get(getNowUserId()).adoptPetMap();
		// System.out.println(adoptPetMap.toString());
		if (adoptPetMap != null && !adoptPetMap.isEmpty()) {
			String adoptPetMapString = adoptPetMap.toString();
			String[] entries = adoptPetMapString.substring(1, adoptPetMapString.length() - 1).split(", ");
			List<String[]> list = new ArrayList<>();

			// "입양승인" 상태인 경우만 리스트에 추가
			for (String entry : entries) {
				String[] keyValue = entry.split("="); // key와 value를 '='로 분리
				String[] keyParts = keyValue[0].split("/"); // key 부분을 '/'로 분리
				String[] valueParts = keyValue[1].split("/"); // value 부분을 '/'로 분리

				String postNum = keyParts[0]; // 게시글 번호
				String userId = keyParts[1]; // 유저 아이디
				String boardTitle = valueParts[0]; // 게시글 게시판 제목
				String applicationStatus = valueParts[1]; // 신청 상태

				// 신청 상태가 "입양승인"인 경우에만 리스트에 추가
				if ("입양승인처리".equals(applicationStatus)) {
					list.add(new String[] { postNum, userId, boardTitle, applicationStatus });
				}
			}

			// 저장된 내용 출력
			if (!list.isEmpty()) {
				final int LINE_LENGTH = 75; // 출력 라인 길이 통일

				System.out.println("=".repeat(LINE_LENGTH));

				for (String[] data : list) {
					System.out.println(data[2] + "의 " + data[0] + "번 게시글 " + data[1] + "님이 " + data[3] + "했습니다.");
				}
				System.out.println("=".repeat(LINE_LENGTH));
				String choice = getInput("1. 입양 확정, 2. 입양 취소, 0. 뒤로가기");
				int selectedIndex = -1;

				switch (choice) {
				case "1":
					while (true) {
						String selectedPostNum1 = getInput("입양 확정할 게시글 번호를 입력해주세요");
						for (int i = 0; i < list.size(); i++) {
							String[] data = list.get(i);
							if (data[0].equals(selectedPostNum1)) {
								selectedIndex = i; // 게시글 번호가 일치하는 인덱스를 찾음
								break; // 일치하는 게시글을 찾으면 루프 종료
							}
						}
						if (selectedIndex == -1) {
							System.out.println("입력하신 게시글 번호가 목록에 없습니다. 다시 확인해주세요.");
						} else {
							// 해당 게시글 번호로 입양 확정 처리
							String[] selectedData = list.get(selectedIndex);
							System.out.println(selectedData[0] + "번 게시글의 입양이 확정되었습니다.");

							User.getBoardMap().get(selectedData[2]).getPostsMap().get(Integer.parseInt(selectedData[0]))
									.setTitle("(입양완료)" + User.getBoardMap().get(selectedData[2]).getPostsMap()
											.get(Integer.parseInt(selectedData[0])).getTitle());
							// 보드이름(key)로 보드 맵 value인 보드 접근, 포스트 넘버(key)로 포스트맵 vlaue인 포스트 접근
							// >> 해당 포스트의 Title를 "(입양완료)"+ 기존제목으로 변경;

							User.getBoardMap().get(selectedData[2]).getPostsMap().get(Integer.parseInt(selectedData[0]))
									.setAdoptPetCheck(true);
							// 입양완료된 포스트는 AdoptPetCheck true 로변경, 다시 입양신청 안되도록 일반게시판의 commentRun()를 실행 if문으로
							adoptPetMap.remove(selectedData[0] + "/" + selectedData[1]);
							return;
						}
					}

				case "2":
					while (true) {
						String selectedPostNum1 = getInput("입양 취소할 게시글 번호를 입력해주세요");
						for (int i = 0; i < list.size(); i++) {
							String[] data = list.get(i);
							if (data[0].equals(selectedPostNum1)) {
								selectedIndex = i; // 게시글 번호가 일치하는 인덱스를 찾음
								break; // 일치하는 게시글을 찾으면 루프 종료
							}
						}
						if (selectedIndex == -1) {
							System.out.println("입력하신 게시글 번호가 목록에 없습니다. 다시 확인해주세요.");
						} else {

							String[] selectedData = list.get(selectedIndex);
//							
							adoptPetMap.remove(selectedData[0] + "/" + selectedData[1]);
							System.out.println("입양요청이 성공적으로 취소되었습니다.");

							return;
						}
					}

				case "0":

					return;

				default:
					break;
				}

			} else {
				System.out.println("입양 확정 목록이 비어 있습니다.");
			}
		} else {
			System.out.println("입양 확정 목록이 비어 있습니다.");
		}
	}

	public static void run() {
		//AnimalAsciiArt.display();

		final int LINE_LENGTH = 75; // 전체 라인 길이

		Client.initialize();// 초기 Admin 설정 및 파일 로드
		Client main = new Client("main", " main", "main"); // 회원가입 및 로그인용 객체 생성
		Client.initializeBoard();

		while (true) {
			System.out.println("\n" + "=".repeat(LINE_LENGTH));
			String title = "📌[ 회원 관리 ]📌";
			System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
			System.out.println("=".repeat(LINE_LENGTH));

			System.out.println("1.로그인");
			System.out.println("2.회원가입");
			System.out.println("0.종료");
			System.out.println("=".repeat(LINE_LENGTH));
			String choice = getInput("원하시는 메뉴를 선택해주세요: ");

			switch (choice) {
			case "1":// 로그인 함수 실행
				main.login(getInput("아이디를 입력해주세요: "), getInput("비밀번호를 입력해주세요: "));
				break;
			case "2":// 회원가입 진행
				main.register();
				break;
			case "0":// 종료
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
		super.adoptPet = new HashMap<String, String>();
	}

}