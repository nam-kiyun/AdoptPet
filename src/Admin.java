import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Admin extends User implements Serializable {
	private static final long serialVersionUID = 1L;

	public Admin(String userId, String password, String nickName) {
		// setUserId, setPassword, setNickName 메서드를 사용해도 되지만, 부모 클래스의 생성자를 호출하는 것이 더 좋다.
		/*
		 * 객체 생성과 동시에 올바르게 초기화됨. NullPointerException을 방지할 수 있음.
		 */
		super(userId, password, nickName);
		super.adoptPet = new HashMap<>();
	}

	public void searchUser(String userId) {
		final int LINE_LENGTH = 75; // 출력 라인 길이 통일

		Map<String, User> userMap = User.getUserMap();

		System.out.println("\n" + "=".repeat(LINE_LENGTH));
		String title = "📌[ 사용자 검색 결과 ]📌";
		System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
		System.out.println("=".repeat(LINE_LENGTH));

		if (userMap.containsKey(userId)) {
			User user = userMap.get(userId);
			System.out.printf("아이디: %-15s | 닉네임: %-15s\n", user.getUserId(),
					(user.getNickName() != null ? user.getNickName() : "N/A"));
		} else {
			System.err.println("등록된 사용자가 없습니다.");
		}
	}

	public void deleteUser(String userId) {
		final int LINE_LENGTH = 75; // 출력 라인 길이 통일

		Map<String, User> userMap = User.getUserMap();

		System.out.println("\n" + "=".repeat(LINE_LENGTH));
		String title = "📌[ 사용자 삭제 ]📌";
		System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
		System.out.println("=".repeat(LINE_LENGTH));

		if (userMap.containsKey(userId)) {
			userMap.remove(userId);
			User.save(); // 삭제 후 자동 저장
			System.out.println(userId + " 사용자가 삭제되었습니다.");
		} else {
			System.err.println("삭제할 사용자가 존재하지 않습니다.");
		}
		System.out.println("=".repeat(LINE_LENGTH));
	}

	public void showUsersList() {
		final int LINE_LENGTH = 75; // 출력 라인 길이 통일

		Map<String, User> userMap = User.getUserMap();

		System.out.println("\n" + "=".repeat(LINE_LENGTH));
		String title = "📌[ 전체 사용자 목록 ]📌";
		System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
		System.out.println("=".repeat(LINE_LENGTH));

		if (userMap.isEmpty()) {
			System.out.println("등록된 사용자가 없습니다.");
			return;
		}
		System.out.printf("%-15s | %-15s | %-15s | %-20s%n", "아이디", "닉네임", "비번 오류 횟수", "밴 남은 시간");
		System.out.println("-".repeat(LINE_LENGTH));

		for (Map.Entry<String, User> entry : userMap.entrySet()) {
			String userId = entry.getKey();
			User user = entry.getValue();
			String nickName = (user.getNickName() != null) ? user.getNickName() : "N/A";
			int wrongCount = user.getWrongCount();
			String banTime = (user.getBanDateTime() != null) ? user.getBanDateTime().toString() : "정지중이 아닙니다.";

			System.out.printf("%-18s | %-18s | %-14d | %-20s%n", userId, nickName, wrongCount, banTime);
		}
		System.out.println("=".repeat(LINE_LENGTH));
	}

	private void createBoard() {
		while (true) {

			String boardType = Client.getInput("게시판의 유형을 선택해주세요(1.입양게시판  2.일반게시판  3.관리자게시판): ");
			if (boardType.equals("1") || boardType.equals("2") || boardType.equals("3")) {// 게시판 유형 체크, true 면 제목입력

				String boardName = Client.getInput("새로운 게시판의 이름을 입력하세요(한글 및 영문자 2~15): ");

				if (!boardName.matches("^[a-zA-Z가-힣][a-zA-Z가-힣0-9 ]{1,14}$")) {// 게시판 제목 패턴 확인
					System.out.println("게시판 이름은 2~15자이며, 한글 또는 영어로 시작해야 합니다. (숫자와 공백은 허용, 특수문자는 불가)");
				} else {
					if (boardMap.containsKey(boardName)) {// 패턴 맞을 경우 중복확인
						System.err.println("이름이 이미 사용 중인 게시판이 있습니다.");

					}

					else {
						// 게시판 폴더 경로 설정
						File boardDirectory = new File(defaultpath + "\\" + boardName);

						// 해당 폴더가 존재하지 않으면 새 폴더 생성
						if (!boardDirectory.exists()) {
							if (boardDirectory.mkdirs()) { // 디렉터리 생성
								System.out.println("게시판에 필요한 폴더가 생성되었습니다: " + boardDirectory.getAbsolutePath());
							} else {
								System.err.println("폴더 생성 실패: " + boardDirectory.getAbsolutePath());
								return;
							}
						}

						Board newBoard = null;
						if (boardType.equals("1")) {
							newBoard = new Board(boardName, defaultpath + "//" + boardName, true, false);
						} else if (boardType.equals("2")) {
							newBoard = new Board(boardName, defaultpath + "//" + boardName);
						} else if (boardType.equals("3")) {
							newBoard = new Board(boardName, defaultpath + "//" + boardName, false, true);
						}

						boardMap.put(boardName, newBoard);

						System.out.println("새로운 게시판이 성공적으로 생성되었습니다.");

						boardSave();
						return;
					}
				}
			} else {
				System.err.println("올바른 유형을 선택해주세요(1. 입양게시판, 2. 일반게시판, 3. 관리자게시판)");
			}
		}

	}

	private void deleteBoard() {
		Scanner scanner = new Scanner(System.in);

		// 관리자에게 삭제할 게시판 이름 입력 받기
		System.out.print("삭제할 게시판의 이름을 입력하세요: ");
		String boardName = scanner.nextLine();

		// 게시판이 존재하는지 확인
		if (!boardMap.containsKey(boardName)) {
			System.err.println("해당 이름의 게시판이 존재하지 않습니다.");
			return;
		}

		// 게시판 폴더 경로 설정
		File boardDirectory = new File(defaultpath + "\\" + boardName);

		// 폴더 내 파일들을 삭제하는 메서드 호출
		deleteFolderContents(boardDirectory);

		// 폴더 삭제 시도
		if (boardDirectory.delete()) {
			System.out.println("게시판 폴더가 성공적으로 삭제되었습니다.");
		} else {
			System.err.println("게시판 폴더 삭제에 실패했습니다.");
		}

		// 게시판을 맵에서 삭제
		boardMap.remove(boardName);

		System.out.println("게시판이 성공적으로 삭제되었습니다.");
		// 옵션으로 boardMap을 저장할 수 있음
		boardSave();
	}

	// 폴더 내 모든 파일과 폴더를 삭제하는 메서드
	private void deleteFolderContents(File folder) {
		// 폴더 내 파일과 폴더 목록을 가져옴
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					// 하위 폴더가 있으면 재귀적으로 삭제
					deleteFolderContents(file);
				}
				// 파일이나 폴더를 삭제
				if (file.delete()) {
					System.out.println("삭제됨: " + file.getAbsolutePath());
				} else {
					System.err.println("삭제 실패: " + file.getAbsolutePath());
				}
			}
		}
	}

	@Override
	public void menu() {
		final int LINE_LENGTH = 75; // 전체 라인 길이

		while (true) {
			System.out.println("\n" + "=".repeat(LINE_LENGTH));

			String title = "📌[ Admin 메뉴 ]📌";
			System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
			System.out.println("\n" + "=".repeat(LINE_LENGTH));


			System.out.println("1. 전체 사용자 목록 보기");
			System.out.println("2. 사용자 검색");
			System.out.println("3. 사용자 삭제");
			System.out.println("4. 로그아웃");
			System.out.println("5. 게시판 추가");
			System.out.println("6. 게시판 삭제");
			System.out.println("7. 게시판 목록 보기");
			System.out.println("0. 종료");
			System.out.println("=".repeat(LINE_LENGTH));
			System.out.print("선택 > ");
			Scanner scanner = new Scanner(System.in);
			int choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				showUsersList();
				break;
			case 2:
				searchUser(Client.getInput("검색할 아이디 입력: "));
				break;
			case 3:
				showUsersList();
				deleteUser(Client.getInput("삭제할 아이디 입력: "));
				break;
			case 4:
				System.out.println("로그아웃 되었습니다.");
				logout();
				return;
			case 5:
				createBoard();
				break;
			case 6:
				deleteBoard();
				break;
			case 7:
				selectBoardList();
				break;
			case 0:
				System.out.println("프로그램을 종료합니다.");
				System.exit(0);
			default:
				System.err.println("잘못된 입력입니다. 다시 선택하세요.");
			}
		}
	}

}