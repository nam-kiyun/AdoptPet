import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class Post implements Serializable {
	private static final long serialVersionUID = 1L;

	private int postNum;
	private String title;
	private String content;
	private String author;
	private String postPath;
	private LocalDateTime createAt;
	private String userId;
	private Map<Integer, Comment> commentsMap;
	private static int postCounter = 1; // 게시글 번호 증가
	private boolean adoptPetCheck; // 입양완료 여부 체크

	public Post(int postNum, String boardPath, String title, String content, String author) {
		this.postNum = postNum;
		this.title = title;
		this.content = content;
		this.author = author;
		this.postPath = boardPath + "\\" + title;
		this.createAt = LocalDateTime.now();
		this.userId = Client.getNowUserId();
		this.commentsMap = new HashMap<Integer, Comment>();
	}

	// 댓글 달기
	public void writeComment() {

		boolean check = false;

		// 익명 여부 확인
		while (true) {
			String choice = Client.getInput("익명으로 작성하시겠습니까? (y/n): ").trim().toUpperCase();

			if (choice.equals("Y")) {
				check = true;
				break;
			} else if (choice.equals("N")) {
				check = false;
				break;
			} else {
				System.err.println("⚠ 잘못된 입력입니다. 'y' 또는 'n'을 입력해주세요.");
			}
		}

		// 댓글 입력 유효성 검사 (1자 ~ 50자)
		Pattern pattern = Pattern.compile("^.{1,50}$");
		String commentContent;

		while (true) {
			commentContent = Client.getInput("💬 댓글을 입력해주세요 (1자 ~ 50자 가능): ").trim();
			if (pattern.matcher(commentContent).matches()) {
				break; // 유효한 입력이면 루프 종료
			}
			System.err.println("❌ 댓글은 1자 이상, 50자 이하로 입력해주세요.");
		}

		// 가장 높은 commentNum을 찾아서 자동 증가
		int newCommentNum = commentsMap.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
		String author = check ? "익명" : this.author;

		// 댓글 객체 생성 및 저장
		Comment comment = new Comment(newCommentNum, commentContent, author, LocalDateTime.now());
		commentsMap.put(newCommentNum, comment);
		System.out.println("✅ 댓글이 작성되었습니다.");
	}

	public void commentPrint() {
		final int LINE_LENGTH = 75; // 전체 라인 길이

		// 순서가 없는 Set을 Treeset으로 순서대로 정렬
		Set<Integer> set = new TreeSet<Integer>(this.commentsMap.keySet());
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");

		System.out.println("\n" + "=".repeat(LINE_LENGTH));
		String title = "📌[ 댓글 목록 ]📌";
		System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
		System.out.println("=".repeat(LINE_LENGTH));

		System.out.printf("| %-6s | %-30s | %-10s | %-20s \n", "번호", "댓글 내용", "작성자", "작성일");
		System.out.println("-".repeat(LINE_LENGTH));

		for (Comment comment : commentsMap.values()) {
			// 내용 길이 제한 (15자 이상 10자까지만 출력 + "...")
			String content = comment.getContent();
			if (content.length() > 30) {
				content = content.substring(0, 10) + "..."; // 길이 제한 적용
			}

			System.out.printf("| %-6d | %-30s | %-10s | %-20s \n", comment.getCommentNum(), content + ".....",
					comment.getAuthor(), comment.getCreateAt().format(dtf));
		}

		System.out.println("=".repeat(LINE_LENGTH));
	}

	public void reverseCommentPrint() { // 최신순
		final int LINE_LENGTH = 75; // 전체 라인 길이

		// Set은 순서가 없어서 List로 받아주고 최신순 정렬
		List<Integer> list = new ArrayList<Integer>(this.commentsMap.keySet());
		list.sort(Comparator.reverseOrder());
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");

		System.out.println("\n" + "=".repeat(LINE_LENGTH));
		String title = "📌[ 댓글 정렬 ]📌";
		System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
		System.out.println("=".repeat(LINE_LENGTH));

		System.out.printf("| %-6s | %-30s | %-10s | %-20s \n", "번호", "댓글 내용", "작성자", "작성일");
		System.out.println("-".repeat(LINE_LENGTH));

		for (Integer number : list) {
			int num = this.commentsMap.get(number).getCommentNum();
			String comment = this.commentsMap.get(number).getContent();
			String author = this.commentsMap.get(number).getAuthor();
			String time = this.commentsMap.get(number).getCreateAt().format(dtf);
//			System.out.printf("%d\t%s\t%s\t%s\n", num, comment, author, time);

			System.out.printf("| %-6d | %-30s | %-10s | %-20s \n", num, comment, author, time);
		}
	}

	// 댓글 수정
	public void editComment() {
		int n = 0;
		Set<Integer> set = commentsMap.keySet();
		while (true) {
			try {
				// 여기
				n = Integer.parseInt(Client.getInput("> 댓글 번호를 입력해주세요: "));

				if (n != 0)
					break;
			} catch (NumberFormatException e) {
				System.err.println("⚠ 댓글 번호는 숫자만 가능합니다!");
			}
		}

		boolean edit = false;
		for (Integer num : set) {
			if (n == num) {
				Comment comment = commentsMap.get(num);
				if (comment == null) {
					System.err.println("해당 댓글이 존재하지 않습니다.");
				}
				edit = true;
//	            System.out.println("현재 userId: " + this.userId);
//	               System.out.println("댓글 userId: " + comment.getUserId());
				if (this.userId != null && this.userId.equals(comment.getUserId())) {
					System.out.println("댓글을 새로 입력해주세요 (1자 ~ 50자 입력 가능)");

					String newContent = "";
					Pattern pattern = Pattern.compile("^.{1,50}$"); // 1자 이상 50자 이하

					while (true) {
						newContent = Client.getInput("💬 수정할 댓글: ");
						if (pattern.matcher(newContent).matches())
							break;
						System.out.println("⚠ 댓글은 1자 이상, 50자 이하로 입력해주세요.");
					}

					commentsMap.get(num).setContent(newContent);
					System.out.println("✅ 댓글이 수정되었습니다.");
					saveAllPosts(); // 변경 사항 저장
				} else {
					System.err.println("수정 권한이 없습니다.");
				}
				break;
			}
		}
		if (!edit) {
			System.err.println("잘못된 댓글 번호를 입력했습니다.");
		}
	}

	// 댓글 삭제
	public void deleteComment() {
		Set<Integer> set = commentsMap.keySet();
		Iterator<Integer> it = set.iterator();
		System.out.println("댓글 번호를 입력해주세요.");

		int n = 0;
		while (true) {
			try {
				n = Integer.parseInt(Client.getInput("> 댓글 번호를 입력해주세요: "));
				if (n != 0)
					break;
			} catch (NumberFormatException e) {
				System.err.println("⚠ 댓글 번호는 숫자만 가능합니다!");
			}
		}

		boolean delete = false;
		// Iterator를 통해 순회 돌아서 일치할 경우 삭제
		// 그냥 Set상태로 for문 돌게 되면 Collection 오류 발생
		while (it.hasNext()) {
			int num = it.next();
			if (n == num) {
				Comment comment = commentsMap.get(num);
				delete = true;
				if (this.userId != null && this.userId.equals(comment.getUserId())) {
					it.remove();
					System.out.println("댓글이 삭제되었습니다.");
				} else {
					System.err.println("수정 권한이 없습니다.");
				}
				break;
			}
		}
		if (!delete) {
			System.err.println("잘못된 댓글 번호를 입력하였습니다.");
		}
	}

	public void commentSave() {
		File dir = new File(this.postPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(this.postPath + "\\Comments.txt");

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			oos = new ObjectOutputStream(bos);

			oos.writeObject(commentsMap);

			oos.close();
			bos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void commentLoad() {
		File commentsFile = new File(this.postPath + "\\Comments.txt");
		if (!commentsFile.exists())
			return;
		try {
			FileInputStream fis = new FileInputStream(commentsFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);

			HashMap<Integer, Comment> map = (HashMap) ois.readObject();
			if (map != null) {
				// 불러온 map데이터를 commnetsMap에 병합
				this.commentsMap.putAll(map);
				// 가장 큰 commentNum을 찾아
				int maxNum = map.keySet().stream().max(Integer::compareTo).orElse(0);
				Comment.setCommentCounter(maxNum + 1);
				commentPrint();
			}
			ois.close();
			bis.close();
			fis.close();
		} catch (Exception e) {
		}
	}

	public void commentRun() {
		final int LINE_LENGTH = 75; // 출력 라인 길이 통일

		commentLoad();
		while (true) {
			System.out.println("\n" + "=".repeat(LINE_LENGTH));
			String title = "📌 [ 댓글 메뉴 ] 📌";
			System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
			System.out.println("=".repeat(LINE_LENGTH));

			// 메뉴 표시
			System.out.printf("| %-15s | %-15s | %-15s | %-15s | %-5s |\n", "1. 댓글 작성", "2. 댓글 수정", "3. 댓글 삭제", "4. 정렬",
					"0. 종료");
			System.out.println("-".repeat(LINE_LENGTH));

			String choice = Client.getInput("선택: ");

			switch (choice) {
			case "1":
				writeComment();
				commentPrint();
				break;
			case "2":
				editComment();
				commentPrint();
				break;
			case "3":
				deleteComment();
				commentPrint();
				break;
			case "4":
				reverseCommentPrint();
				break;
			case "0":
				commentSave();
				return;
			}
		}
	}

	public void adopPetcommentRun() {
		final int LINE_LENGTH = 75; // 출력 라인 길이 통일

		commentLoad();
		while (true) {
			System.out.println("\n📌 [ 입양 게시글 댓글 메뉴 ]");
			System.out.printf("%-10s  %-10s  %-10s  %-10s %-10s %-10s \n", "1. 댓글 작성", "2. 댓글 수정", "3. 댓글 삭제", "4. 정렬",
					"5.입양 신청", "0.종료");
			System.out.println();

			String input = Client.getInput("선택: ").trim();
			System.out.println("-".repeat(LINE_LENGTH));

			switch (input) {
			case "1":
				writeComment();
				commentPrint();
				break;
			case "2":
				editComment();
				commentPrint();
				break;
			case "3":
				deleteComment();
				commentPrint();
				break;
			case "4":
				reverseCommentPrint();
				break;
			case "5":
				writeAdoptPet();
				break;
			case "0":
				commentSave();
				return;
			}
		}
	}

	public void writeAdoptPet() {
		while (true) {
			String choice = Client.getInput("입양을 신청하시려면 \'Y\'를 입력해주세요(Y. 신청, N. 취소): ");
			if (choice.toUpperCase().equals("Y")) {
				Client.getUserMap().get(getUserId()).adoptPetMap().put(getPostNum() + "/" + Client.getNowUserId(),
						this.postPath.replace(this.getTitle(), "").replace(Client.defaultpath, "").replace("\\", "")
								+ "/입양승인요청");
				// adoptPetMap(String key, String value) > key = postNum/nowUserId, value =
				// post의 boardName/입양승인요청
				// put를 getUserId() post작성자 user 객체의 adoptPetMap()
				User.getUserMap().get(getUserId()).setAlarm("1");
				System.out.println("입양 신청이 완료 되었습니다.");

				return;
			} else if (choice.toUpperCase().equals("N")) {
				System.out.println("입양 신청을 취소합니다.");
				return;
			} else {
				System.out.println("올바른 값을 입력해주세요(Y. 신청, N. 취소)");
			}
		}
	}

	// 게시글 및 댓글 개별 파일 저장
	public void saveAllPosts() {
		String path = "C:\\AdoptPet\\download\\";
		File directory = new File(path);

		// 폴더 없으면 생성
		if (!directory.exists()) {
			if (directory.mkdir()) {
				System.out.println("폴더 생성 완료: " + directory);
			} else {
				System.err.println("폴더 생성 실패");
				return;
			}
		}

		String filePath = path + "post_" + postNum + ".txt";

		try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
			writer.println("==================================================");
			writer.println("                    📌 게시글                     ");
			writer.println("==================================================");
			writer.printf("📌 번호   : %d%n", postNum);
			writer.printf("📌 제목   : %s%n", title);
			writer.printf("📌 작성자 : %s%n", author);
			writer.printf("📌 작성일 : %s%n", createAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			writer.println("--------------------------------------------------");
			writer.println("📜 내용:");
			writer.println(content);
			writer.println("--------------------------------------------------");

			if (commentsMap.isEmpty()) {
				writer.println("💬 댓글: 등록된 댓글이 없습니다.");
			} else {
				writer.println("\n==================================================");
				writer.println("                   💬 댓글 목록                   ");
				writer.println("==================================================");

				for (Comment comment : commentsMap.values()) {
					writer.println("--------------------------------------------------");
					writer.printf("💬 번호   : %d%n", comment.getCommentNum());
					writer.printf("💬 작성자 : %s%n", comment.getAuthor());
					writer.printf("💬 작성일 : %s%n",
							comment.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
					writer.println("💬 내용:");
					writer.println(comment.getContent());
					writer.println("--------------------------------------------------");
				}
			}

			// System.out.println("✅ 게시글과 댓글이 저장되었습니다: " + filePath);
		} catch (IOException e) {
			System.err.println("파일 저장 중 오류가 발생했습니다.");
			e.printStackTrace();
		}
	}

	public int getPostNum() {
		return postNum;
	}

	public void setPostNum(int postNum) {
		this.postNum = postNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	public Map<Integer, Comment> getCommentsMap() {
		return commentsMap;
	}

	public void setCommentsMap(Map<Integer, Comment> commentsMap) {
		this.commentsMap = commentsMap;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public static void setPostCounter(int count) {
		Post.postCounter = postCounter;

	}

	public static int getNextPostNum() {
		return postCounter++;
	}

	public boolean isAdoptPetCheck() {
		return adoptPetCheck;
	}

	public void setAdoptPetCheck(boolean adoptPetCheck) {
		this.adoptPetCheck = adoptPetCheck;
	}

}
