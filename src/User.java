import javax.crypto.Cipher;



import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public abstract class User implements Serializable {
	private String userId;
	private String password;
	private String nickName;
	private int wrongCount;
	private LocalDateTime banTime;
	private static String nowUserId;
	private String alarm; //알림 구현
	// 직렬화 버전을 고정
	// TODO: 직렬화 버전을 고정하는 이유를 아직 잘 모르겠음. 추후에 공부하고 추가할 것.
	private static final long serialVersionUID = 1L;
	// static으로 선언된 userMap은 프로그램 실행시 한번만 생성되고, 모든 객체가 공유한다.
	private static Map<String, User> userMap = new HashMap<>();
	protected static Map<String, Board> boardMap = new LinkedHashMap<>();
	protected  Map<String, String> adoptPet;
	// 파일경로지정 static 함으로써 객체 생성없이 사용가능
	public static final String defaultpath = "C:\\AdoptPet"; // 저장할 파일 경로
	public static final String path = "C:\\AdoptPet\\userList.txt"; // 저장할 파일 경로
	public static final String board_list_path = "C:\\AdoptPet\\boardList.txt"; // 저장할 파일 경로
	
	public static Map<String, User> getUserMap() {
		return userMap;
	}
	public Map<String, String> adoptPetMap() {
	  
	    return adoptPet;
	}
	public  void setPetMap(Map<String, String> a) {
		adoptPet = a;
	}
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
	public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘이 지원되지 않습니다.", e);
        }
    }
	// User 생성자
	public User(String userId, String password, String nickName) {
		this.userId = userId;
		this.password = password;
		this.nickName = nickName;
		this.wrongCount = 0;
		this.banTime = null;
		this.alarm="";
		
	}

	// 데이터 저장 메서드 (직렬화)
	public static void save() {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
			out.writeObject(userMap);
		} catch (IOException e) {
			System.err.println("데이터 저장 중 오류 발생!");
			e.printStackTrace();
		}
	}

	// 유저 데이터 로드 메서드
	public static void load() {
		File directory = new File(defaultpath); //데이터 로드 시 기본 폴더 생성
	    if (!directory.exists()) {
	        if (directory.mkdirs()) {
	            System.out.println("폴더가 존재하지 않아 생성되었습니다: " + directory.getAbsolutePath());
	        } else {
	            System.err.println("폴더 생성 실패: " + directory.getAbsolutePath());
	            return;
	        }
	    }
		
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("기존 데이터가 없습니다. 새로운 파일을 생성합니다.");
			return;
		}

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			Object obj = in.readObject();

			HashMap<String, User> map = (HashMap) obj;
			userMap.putAll(map);

			System.out.println("사용자 데이터 로드 완료! 현재 등록된 계정 수: " + userMap.size());
		} catch (Exception e) {
			System.err.println("데이터 로드 중 오류 발생!");
			e.printStackTrace();
		}

	}
	
	public static void boardLoad() { //board list load
		
		File file = new File(board_list_path);
		if (!file.exists()) {

			return;
		}

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			boardMap = (Map<String, Board>) in.readObject();

			System.out.println("보드 데이터 로드 완료! 현재 등록된 보드 수: " + boardMap.size());
		} catch (Exception e) {
			System.err.println("보드 데이터 로드 중 오류 발생!");
			e.printStackTrace();
		}

	}
	public static void boardSave() {//board list save
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(board_list_path))) {
			out.writeObject(boardMap);
		} catch (IOException e) {
			System.err.println("데이터 저장 중 오류 발생!");
			e.printStackTrace();
		}
	}
	
	public static void selectBoardList() { // 게시판 목록 조회

		if (boardMap.isEmpty()) {
			System.out.println("현재 등록된 게시판이 없습니다.");
		} else {
			int index = 1;
			System.out.println("======================================");
			// boardMap의 key(게시판 제목)들을 출력
			for (String boardName : boardMap.keySet()) {
				System.out.println(index++ + ". " + boardName); // 게시판 제목 출력
			}
			System.out.println("======================================");

			while (true) {
				int selectedNumber = Integer.parseInt(Client.getInput("게시판을 선택해주세요(0.뒤로가기): "));
				// 입력 받은 번호가 유효한지 체크
				if (selectedNumber != 0) {
					if (selectedNumber < 0 || selectedNumber > boardMap.size()) {
						System.out.println("잘못된 번호입니다. 다시 시도하세요.");
					} else {
						// 선택된 번호에 해당하는 게시판 찾기
						String selectedBoardName = (String) boardMap.keySet().toArray()[selectedNumber - 1];
						Board selectedBoard = boardMap.get(selectedBoardName);
						System.out.println("선택한 게시판: " + selectedBoardName);
						selectedBoard.run();
						return;
						// 이제 selectedBoard를 사용하여 해당 게시판에 접근할 수 있음
						// 예: selectedBoard.displayBoard() 또는 다른 메서드를 통해 게시판 작업을 처리}
					}
				} else {
					return;
				}
			}
		}
	}

	//
	public void login(String userId, String password) {
		System.out.println("로그인 시도: " + userId);
		if (userMap.isEmpty()) {
			System.out.println("현재 등록된 사용자가 없습니다. 회원가입을 먼저 해주세요.");
			return;
		}
		User user = userMap.get(userId);
		if (!userMap.containsKey(userId)) {
			System.out.println("존재하지 않는 아이디입니다.");
			return;
		}
		if (user.getBanDateTime() != null && user.getBanDateTime().plusMinutes(10).isAfter(LocalDateTime.now())) {
			long remainingMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(),
					user.getBanDateTime().plusMinutes(10));
			System.out.println("비밀번호를 여러 번 틀렸습니다. " + remainingMinutes + "분 후에 다시 시도해주세요.");
			return;
		}

		// 10분이 지났다면 banDateTime 초기화
		if (user.getBanDateTime() != null && user.getBanDateTime().plusMinutes(10).isBefore(LocalDateTime.now())) {
			user.setBanDateTime(null);
			user.setWrongCount(0);
		}

		if (!user.getPassword().equals(hashPassword(password))) {
			user.setWrongCount(user.getWrongCount() + 1);
			System.out.println("비밀번호가 일치하지 않습니다.");
			if (user.getWrongCount() >= 3) {
				user.setBanDateTime(LocalDateTime.now());
				System.out.println("비밀번호를 여러번 잘못 입력하셨습니다. 10분뒤에 다시 시도해 주세요");
				save();
			}
			return;
		}

		nowUserId=userId;
		System.out.println("현재 로그인 아이디 : " + userId);
		if (nowUserId!= null) {
			user.menu();
			
		}

	}

	public void logout() {
		System.out.println(nowUserId);
		if (nowUserId != null) {
			System.out.println(nowUserId+ "님이 로그아웃하였습니다.");
			nowUserId= null;
		} else {
			System.out.println("잘못된 접근입니다.");
		}
	}

	// 프로그램 실행시 시행될 데이터 로드
	public static void initialize() {
		load();
		System.out.println("사용자 데이터를 불러왔습니다.");
		// 기본 Admin 계정 생성
		System.out.println("기본 Admin 계정을 확인.");

		if (!userMap.containsKey("admin")) {
			Admin defaultAdmin = new Admin("admin", hashPassword("admin123"), "관리자");
			defaultAdmin.setWrongCount(0);
			userMap.put("admin", defaultAdmin);
			save();
			// 테스트 하기 위해 계정 생성 메시지 출력
			System.out.println("기본 Admin 계정이 생성되었습니다. (ID: admin, PW: admin123)");
		} else {
			System.out.println("기본 Admin 계정이 이미 존재합니다.");

		}
	}
	public static void initializeBoard() {
	    boardLoad(); // 기존의 board 데이터 불러오기

	    // 게시판 이름 배열
	    String[] boardNames = {"고양이 입양 게시판", "강아지 입양 게시판", "자유 게시판"};
	    
	    // 게시판 초기화 및 폴더 생성
	    for (String boardName : boardNames) {
	        String boardPath = defaultpath + "\\" + boardName; // 각 게시판에 맞는 경로 설정
	        
	        // 폴더가 없다면 폴더 생성
	        File boardFolder = new File(boardPath);
	        if (!boardFolder.exists()) {
	            boardFolder.mkdirs();  // 폴더 생성
	            System.out.println(boardName + " 폴더를 생성했습니다.");
	        }

			// 게시판이 없으면 새로 추가
			if (!boardMap.containsKey(boardName)) {
				if (boardName.equals("자유 게시판")) {
					boardMap.put(boardName, new Board(boardName, boardPath));
				} // 자유게시판만 일반게시판으로 초기화
				else {
					boardMap.put(boardName, new Board(boardName, boardPath, true));
				}
				System.out.println(boardName + " 게시판을 초기화했습니다.");
			}
		}
		boardSave();

		System.out.println("보드 데이터를 불러왔습니다.");
	}																																																																																																																																																																																																																																																																																																																																																																																																																																																																															
	

	public abstract void menu();

	public void setPassword(String password) {
		this.password = hashPassword(password);
	}

	public int getWrongCount() {
		return wrongCount;
	}

	public void setWrongCount(int wrongCount) {
		this.wrongCount = wrongCount;
	}

	public LocalDateTime getBanDateTime() {
		return banTime;
	}

	public void setBanDateTime(LocalDateTime bantime) {
		this.banTime = bantime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public void setNowUserId() { // 현재 로그인 id 정보 설정, 로그인 이후 직접 실행해야 함
		nowUserId = userId;
	}

	public static String getNowUserId() { // 현재 로그인 id 리턴
		return nowUserId;
	}
	public static void setUserMap(Map<String, User> userMap) {
		User.userMap = userMap;
	}
	public String getAlarm() {
		return alarm;
	}
	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}
	public static Map<String, Board> getBoardMap() {
		return boardMap;
	}
	

	
}