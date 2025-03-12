import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Board implements Serializable {

	private int boardNum; // 게시판번호
	private String boardName; // 게시판제목
	private String boardPath; // 경로
	private HashMap<Integer, Post> postsMap; // 게시글 관리
	private boolean adotPetBoard; // 입양게시판 여부 체크
	private boolean adminBoard;

	public Board(String boardName, String boardPath) {
		this(boardName, boardPath, false, false);
	}

	public Board(String boardName, String boardPath, boolean adotPetBoard, boolean adminBoard) {
		this.boardName = boardName;
		this.boardPath = boardPath;
		this.adotPetBoard = adotPetBoard;
		this.adminBoard = adminBoard;
		this.postsMap = new HashMap<Integer, Post>();
	}

	// 실행
	public void run() {
		final int LINE_LENGTH = 75; // 출력 라인 길이 통일

		if (!postsMap.isEmpty()) {
			savePosts();
		}
		loadPost();

		while (true) {
			// 📌 게시판 헤더 출력
			String centeredTitle = String.format("%" + ((LINE_LENGTH + boardName.length() + 10) / 2) + "s",
					"📌 [ " + boardName + " 게시판 ] 📌");

			System.out.println("\n" + "=".repeat(LINE_LENGTH));
//	        System.out.printf(" %-64s \n", "[ " + boardName + " 게시판 ]");
			System.out.println(centeredTitle); // 중앙 정렬된 제목 출력

			System.out.println("=".repeat(LINE_LENGTH));
			System.out.println("1. 모든 게시글 보기");
			System.out.println("2. 게시글 작성");
			System.out.println("3. 게시글 수정");
			System.out.println("4. 게시글 삭제");
			System.out.println("5. 검색 하기");
			System.out.println("6. 뒤로 가기");
			System.out.println("=".repeat(LINE_LENGTH));

			String choice = Client.getInput("선택 >> ");

			switch (choice) {
			case "1":
				listAllPosts();
				break;
			case "2":
				writePost();
				break;
			case "3":
				editPost();
				break;
			case "4":
				deletePost();
				break;
			case "5":
				searchPost();
				break;
			case "6":
				return;
			default:
				System.err.println("잘못된 입력입니다. 다시 선택하세요.");
			}
		}
	}

	// 게시글 목록 출력 함수 (공통)
	public void printPostList(HashMap<Integer, Post> postMap) {
		final int LINE_LENGTH = 75; // 전체 라인 길이

		if (postMap.isEmpty()) {
			System.out.println("등록된 게시글이 없습니다.");
			return;
		}

		System.out.println("\n" + "=".repeat(LINE_LENGTH));
		String title = "📌[ 게시글 목록 ]📌";
		System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
		System.out.println("=".repeat(LINE_LENGTH));

		// 게시글 테이블 헤더
		System.out.printf("| %-5s | %-15s | %-20s | %-10s |\n", "번호", "제목", "내용", "작성자");
		System.out.println("-".repeat(LINE_LENGTH));

		for (Post post : postMap.values()) {
//			// 내용 길이 제한 (15자 이상 10자까지만 출력 + "...")
//			String content = post.getContent();
//			if (content.length() > 15) {
//				content = content.substring(0, 10) + "..."; // 길이 제한 적용
//			}

			System.out.printf("| %-5d | %-15s | %-20s | %-10s |\n", post.getPostNum(), post.getTitle(),
					post.getContent().replace("\n", ", ").substring(0, 15) + ".....", post.getAuthor());
		}
		System.out.println("=".repeat(LINE_LENGTH));

	}

	// 게시글 상세보기 (공통)
	public void printPostDetail(Post post) {
		final int LINE_LENGTH = 75; // 전체 라인 길이

		if (post == null) {
			System.err.println("해당 게시글이 존재하지 않습니다.");
			return;
		}

		System.out.println("\n" + "=".repeat(LINE_LENGTH));
		String title = "📌[ 게시글 상세보기 ]📌";
		System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
		System.out.println("=".repeat(LINE_LENGTH));

		// 게시글 테이블 헤더
		System.out.println("번호: " + post.getPostNum());
		System.out.println("제목: " + post.getTitle());
		System.out.println("작성자: " + post.getAuthor());
		System.out.printf("작성일 : %s%n", post.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		System.out.println(post.getContent());

		System.out.println("-".repeat(LINE_LENGTH));

		if (this.adotPetBoard && !post.isAdoptPetCheck()) {
			post.adopPetcommentRun();
		} else {
			post.commentRun();
		}

	}

	// 입양 게시글 작성
	public void writeAdoptPost() {
		final int LINE_LENGTH = 75; // 전체 라인 길이

		String author = Client.getUserMap().get(Client.getNowUserId()).getNickName();

		// 유효성 검사 정규 표현식
		Pattern titlePattern = Pattern.compile("^.{2,}$"); // 2자 이상
		Pattern contentPattern = Pattern.compile("^.{10,}$"); // 10자 이상
		Pattern namePattern = Pattern.compile("^[가-힣a-zA-Z]{2,}$"); // 한글 또는 영문, 최소 2자 이상
		Pattern agePattern = Pattern.compile("^[0-9]{1,2}$"); // 1~3자리 숫자
		Pattern genderPattern = Pattern.compile("^[MF]$"); // M 또는 F

		System.out.println("\n" + "=".repeat(LINE_LENGTH));
		String title1 = "📌[ 🐾 입양 게시글 작성 🐾 ]📌";
		System.out.printf("%" + ((LINE_LENGTH + title1.length()) / 2) + "s\n", title1);
		System.out.println("=".repeat(LINE_LENGTH));

		String title;
		while (true) {
			title = Client.getInput("📌 제목 (2자 이상): ");
			if (titlePattern.matcher(title).matches()) {
				break;
			}
			System.out.println("❌ 제목은 최소 2자 이상 입력해야 합니다.");
		}

		String petName;
		while (true) {
			petName = Client.getInput("🐶 반려동물 이름 (2자 이상): ");
			if (namePattern.matcher(petName).matches()) {
				break;
			}
			System.out.println("❌ 이름은 최소 2자 이상 입력해야 합니다. (특수문자 및 숫자 불가)");
		}

		String age;
		while (true) {
			age = Client.getInput("🎂 반려동물 나이 (숫자 입력): ");

			if (agePattern.matcher(age).matches()) {
				break;
			}
			System.out.println("❌ 나이는 숫자로 입력해주세요.");
		}

		String gender;
		while (true) {
			gender = Client.getInput("🚻 반려동물 성별 (M/F): ");
			if (genderPattern.matcher(gender).matches()) {
				break;
			}
			System.out.println("❌ 성별은 'M' 또는 'F'로 입력해주세요.");
		}

		String content;
		while (true) {
			content = Client.getInput("📜 내용 (10자 이상): ");
			if (contentPattern.matcher(content).matches()) {
				break;
			}
			System.out.println("❌ 내용은 최소 10자 이상 입력해야 합니다.");
		}

		// 📌 게시글 폴더 생성
		File dir = new File(this.boardPath + "\\" + title);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		int postNum = postsMap.keySet().stream().max(Integer::compareTo).orElse(0) + 1;

		// 📌 입양 게시글 객체 생성
		Post post = new Post(postNum, this.boardPath, title,
				"이름: " + petName + "\n나이: " + age + "살\n성별: " + (gender.equals("M") ? "남아" : "여아") + "\n\n" + content,
				author);

		// 📌 게시글 저장
		postsMap.put(postNum, post);
		savePosts();
		post.saveAllPosts();

		System.out.println("✅ 입양 게시글이 성공적으로 작성되었습니다.");

	}

	// 게시글 작성
	public void writePost() {
		String author = Client.getUserMap().get(Client.getNowUserId()).getNickName();

		if (author == null) {
			System.out.println("로그인한 사용자만 게시글을 작성할 수 있습니다.");
			return;
		}

		// 정규 표현식 패턴 (제목: 2자 이상, 내용: 10자 이상)
		Pattern titlePattern = Pattern.compile("^.{2,}$");
		Pattern contentPattern = Pattern.compile("^.{10,}$");

		boolean check = false;

		if (this.adotPetBoard) {
			writeAdoptPost();
			return;
		}
		if (this.adminBoard && !User.getNowUserId().equals("admin")) {
			System.out.println("관리자 전용 게시판입니다. 접근 권한이 없습니다.");
			return;
		}

		while (true) {
			if (this.adminBoard)
				continue;
			System.out.print("\n> 익명으로 작성하시겠습니까? (Y/N): ");
			String choice = Client.getInput("익명으로 작성하시겠습니까? (y/n): ").toUpperCase();

			if (choice.equals("Y")) {
				check = true; // 익명 작성자로 변경
				break;
			} else if (choice.equals("N")) {
				check = false;
				break;
			} else {
				System.err.println("[오류] 잘못된 입력입니다. 'Y' 또는 'N'을 입력하세요.");
			}
		}
		String title;
		while (true) {
			title = Client.getInput("제목을 입력해주세요 (2자 이상): ").trim();
			if (titlePattern.matcher(title).matches()) {
				break; // 유효한 입력이면 루프 종료
			}
			System.out.println("제목은 최소 2자 이상 입력해야 합니다.");
		}

		String content;
		while (true) {
			content = Client.getInput("내용을 입력해주세요 (10자 이상): ").trim();

			if (contentPattern.matcher(content).matches())
				break;

			System.out.println("내용은 최소 10자 이상 입력해야 합니다.");
		}

		File dir = new File(this.boardPath + "\\" + title);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		int postNum = postsMap.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
		author = check ? "익명" : author;
		Post post = new Post(postNum, this.boardPath, title, content, author);

		postsMap.put(postNum, post);
		savePosts();
		post.saveAllPosts();

		System.out.println("게시글이 성공적으로 작성되었습니다.");
	}

	// 게시글 파일에 저장(직렬화)
	public void savePosts() {

		File file = new File(boardPath + "\\posts.txt");

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(postsMap);

			System.out.println("게시글이 저장되었습니다. ");

			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 저장된 게시글 불러오기
	public void loadPost() {
		File file = new File(boardPath + "\\posts.txt");

		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);

			HashMap<Integer, Post> map = (HashMap<Integer, Post>) ois.readObject();
			if (map != null) {
				this.postsMap.putAll(map);

				int maxNum = map.keySet().stream().max(Integer::compareTo).orElse(0);
				Post.setPostCounter(maxNum + 1);

			}
			Set<Integer> set = this.postsMap.keySet().stream().sorted().collect(Collectors.toSet());
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH.mm.ss");

			ois.close();
			fis.close();
		} catch (Exception e) {
		}

	}

	// 게시글 수정
	public void editPost() {
		if (this.adminBoard && !User.getNowUserId().equals("admin")) {
			System.out.println("관리자 전용 게시판입니다. 접근 권한이 없습니다.");
			return;
		}
		// 모든 게시글 출력
		if (postsMap.size() != 0) {
			printPostList(postsMap);

			int postNum = Integer.parseInt(Client.getInput("> 몇 번 게시글을 수정하시겠습니까? "));

			if (!postsMap.containsKey(postNum)) {
				System.out.println("해당 게시글이 존재하지 않습니다.");
				return;
			}

			Post post = postsMap.get(postNum);
			if (!post.getUserId().equals(Client.getNowUserId())) {
				System.out.println("작성자만 게시글을 수정할 수 있습니다.");
				return;
			}

			post.setTitle(Client.getInput("새 제목: "));
			post.setContent(Client.getInput("새 내용: "));

			savePosts();
			System.out.println("게시글이 수정되었습니다.");
		} else {
			System.out.println("등록된 게시글이 없습니다.");
			return;
		}
	}

	// 게시글 삭제
	public void deletePostDir(int postNum) {
		File file = new File(boardPath + "\\" + postsMap.get(postNum).getTitle());
		if (file.exists()) {
			deletePostDirFile(file);
			file.delete();
		} else {
			return;
		}
	}

	// 재귀적으로 게시글 하위 File 객체들 삭제
	public void deletePostDirFile(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deletePostDirFile(file);
				}
				file.delete();
			}
		}

	}

	// 게시글 삭제
	public void deletePost() {
		if (this.adminBoard && !User.getNowUserId().equals("admin")) {
			System.out.println("관리자 전용 게시판입니다. 접근 권한이 없습니다.");
			return;
		}
		// 게시글 목록 출력
		printPostList(postsMap);
		if (postsMap.size() != 0) {
			try {
				int postNum = Integer.parseInt(Client.getInput("삭제할 게시글 번호 >> "));

				if (!postsMap.containsKey(postNum)) { // 존재하지 않으면
					System.out.println("해당 번호의 게시글이 존재하지 않습니다.");
					return;
				}
				if (postsMap.get(postNum).getUserId().equals(User.getNowUserId())) {
					deletePostDir(postNum);
					postsMap.remove(postNum);

					savePosts();

					System.out.println("✅ 게시글이 성공적으로 삭제되었습니다.");
				} else {
					System.out.println("본인이 작성한 게시글이 아닙니다.");
					return;
				}

			} catch (NumberFormatException e) {
				System.out.println("숫자를 입력해주세요.");
			}
		} else {
			System.out.println("등록된 게시글이 없습니다.");
		}
	}

	// 모든 게시글 출력
	public void listAllPosts() {
		// 게시글 목록 출력 함수(공통)
		if (postsMap.size() != 0) {
			printPostList(postsMap);

			// 게시글 자세히 보기
			System.out.println("자세히 볼 게시글의 번호를 입력해주세요. (취소하려면 0)");

			try {
				int postNum = Integer.parseInt(Client.getInput("선택: "));

				if (postNum == 0) {
					System.out.println("게시글 상세보기를 취소했습니다.");
					return;
				}

				// 입력한 번호가 존재하면 출력
				if (postsMap.containsKey(postNum)) {
					Post post = postsMap.get(postNum);

					printPostDetail(post);
				} else {
					System.out.println("해당 번호의 게시글이 존재하지 않습니다.");
				}

			} catch (NumberFormatException e) {
				System.out.println("숫자를 입력해주세요.");
			}
		} else {
			System.out.println("등록된 게시글이 없습니다.");
			return;
		}

	}

	// 특정 게시물 검색 (제목으로)
	public void searchPost() {
		System.out.println("제목명을 검색해주세요.");
		System.out.print("검색: ");
		String word = Client.getInput("제목명을 검색해주세요: ").trim();

		HashMap<Integer, Post> filteredPosts = new HashMap<>();

		System.out.println("===========================검색 목록===========================");

		for (Post post : postsMap.values()) {
			if (post.getTitle().contains(word)) {
				filteredPosts.put(post.getPostNum(), post);
			}
		}

		if (filteredPosts.isEmpty()) {
			System.out.println("해당 제목의 게시글이 존재하지 않습니다.");
			return;
		}

		printPostList(filteredPosts); // 검색된 게시글만 출력

		System.out.print("자세히 볼 게시글의 번호를 입력해주세요. (취소하려면 0): ");
		try {
			int postNum = Integer.parseInt(Client.getInput("자세히 볼 게시글의 번호를 입력해주세요. (취소하려면 0): "));

			if (postNum == 0) {
				System.out.println("게시글 상세보기를 취소했습니다.");
				return;
			}

			printPostDetail(postsMap.get(postNum));

		} catch (NumberFormatException e) {
			System.out.println("숫자를 입력해주세요.");
		}
	}

	// 게시물 정렬 (최신순)
	public void reversePrintPostList(HashMap<Integer, Post> postMap) {

		if (postMap.isEmpty()) {
			System.out.println("등록된 게시글이 없습니다.");
			return;
		}

		List<Integer> list = new ArrayList<Integer>(postMap.keySet());
		list.sort(Comparator.reverseOrder());

		System.out.println("========================= 게시글 목록 =========================");

		for (Post post : postMap.values()) {
			// 내용 길이 제한 (15자 이상 10자까지만 출력 + "...")
			String content = post.getContent();
			if (content.length() > 15) {
				content = content.substring(0, 10) + "..."; // 길이 제한 적용
			}

			System.out.println("번호: " + post.getPostNum() + " | 제목: " + post.getTitle() + " | 내용: " + content
					+ " | 작성자: " + post.getAuthor());
		}

		System.out.println("=============================================================");

	}

	public int getBoardNum() {
		return boardNum;
	}

	public void setBoardNum(int boardNum) {
		this.boardNum = boardNum;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public HashMap<Integer, Post> getPostsMap() {
		return postsMap;
	}

	public void setPostsMap(HashMap<Integer, Post> postsMap) {
		this.postsMap = postsMap;
	}

}
