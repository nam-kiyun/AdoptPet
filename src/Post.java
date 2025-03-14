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
	private Map<Integer, List<Comment>> repliesMap;
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
		this.repliesMap = new HashMap<>();
	}

	// 댓글 달기
	public void writeComment() {
		String author = Client.getUserMap().get(Client.getNowUserId()).getNickName();
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
		Pattern pattern = Pattern.compile("^.{1,20}$");
		String commentContent;

		while (true) {
			commentContent = Client.getInput("💬 댓글을 입력해주세요 (1자 ~ 20자 가능): ");
			if (pattern.matcher(commentContent).matches()) {
				break; // 유효한 입력이면 루프 종료
			}
			System.err.println("❌ 댓글은 1자 이상, 20자 이하로 입력해주세요.");
		}

		// 가장 높은 commentNum을 찾아서 자동 증가
		int newCommentNum = commentsMap.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
		author = check ? "익명" : author;

		Comment newComment = new Comment(newCommentNum, commentContent, author, LocalDateTime.now());
		commentsMap.put(newCommentNum, newComment);
		System.out.println("✅ 댓글이 작성되었습니다.");
	}

	public void writeReply() {
		String author = Client.getUserMap().get(Client.getNowUserId()).getNickName();
		boolean check = false;
		int parentCommentNum = Integer.parseInt(Client.getInput("📌 대댓글을 달 원본 댓글 번호를 입력해주세요: "));

		if (!commentsMap.containsKey(parentCommentNum)) {
			System.err.println("⚠ 해당 댓글 번호가 존재하지 않습니다.");
			return;
		}

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
		Pattern pattern = Pattern.compile("^.{1,20}$");
		String commentContent;

		while (true) {
			commentContent = Client.getInput("💬 댓글을 입력해주세요 (1자 ~ 20자 가능): ");
			if (pattern.matcher(commentContent).matches()) {
				break; // 유효한 입력이면 루프 종료
			}
			System.err.println("❌ 댓글은 1자 이상, 50자 이하로 입력해주세요.");
		}

		repliesMap.putIfAbsent(parentCommentNum, new ArrayList<>());

		// 부모 댓글 ID에 해당하는 대댓글 리스트가 없으면 생성
		int nextReplyNum = repliesMap.get(parentCommentNum).stream().mapToInt(Comment::getCommentNum).max().orElse(0)
				+ 1;

		author = check ? "익명" : author;
		Comment newReply = new Comment(nextReplyNum, parentCommentNum, commentContent, author, LocalDateTime.now());
		repliesMap.get(parentCommentNum).add(newReply);
		System.out.println("✅ 대댓글이 작성되었습니다.");
	}

	public void commentPrint() {
		final int LINE_LENGTH = 75;

		System.out.println("\n" + "=".repeat(LINE_LENGTH));
		String title = "📌[ 댓글 목록 ]📌";
		System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
		System.out.println("=".repeat(LINE_LENGTH));

		System.out.printf("| %-6s | %-30s | %-10s | %-20s \n", "번호", "댓글 내용", "작성자", "작성일");
		System.out.println("-".repeat(LINE_LENGTH));

		for (Comment comment : commentsMap.values()) {
			System.out.printf("| %-6d | %-30s | %-10s | %-20s \n", comment.getCommentNum(), comment.getContent(),
					comment.getAuthor(), comment.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));

			// 대댓글 출력 (있으면)
			if (repliesMap.containsKey(comment.getCommentNum())) {
				for (Comment reply : repliesMap.get(comment.getCommentNum())) {
					System.out.printf("| %-6s | %-30s | %-10s | %-20s \n", "",
							reply.getParentCommentNum() + "-" + reply.getCommentNum() + " : " + reply.getContent(), // ↳
							reply.getAuthor(), reply.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
				}
			}
		}

		System.out.println("=".repeat(LINE_LENGTH));
	}

	public void editComment() {
		String input = Client.getInput("> 수정할 댓글 번호를 입력해주세요 (대댓글은 1-1 형식으로 입력해주세요) : ").trim();

		int parentNum = 0;
		int replyNum = 0;
		boolean isReply = false;

		try {
			// 1️⃣ **대댓글인지 확인 (1-1 같은 형식)**
			if (input.contains("-")) {
				String[] parts = input.split("-");

				// 부모 댓글 번호
				parentNum = Integer.parseInt(parts[0]);
				// 대댓글 번호
				replyNum = Integer.parseInt(parts[1]);

				isReply = true;
				if (!repliesMap.get(parentNum).get(replyNum-1).getUserId().equals(User.getNowUserId())) {
					System.out.println("본인이 작성한 댓글이 아닙니다.");
					return;
				}
			}
			// 2️⃣ **메인 댓글 (숫자만 입력)**
			else {
				parentNum = Integer.parseInt(input);
				if (!commentsMap.get(parentNum).getUserId().equals(User.getNowUserId())) {
					System.out.println("본인이 작성한 댓글이 아닙니다.");
					return;
				}
			}

		} catch (NumberFormatException e) {
			System.err.println("⚠ 올바른 형식으로 입력해주세요! (예: 1 또는 1-1)");
			return;
		}

		// **대댓글 수정 로직**
		if (isReply) {
			if (repliesMap.containsKey(parentNum)) {
				List<Comment> replyList = repliesMap.get(parentNum);
				boolean edited = false;

				for (Comment reply : replyList) {
					if (reply.getCommentNum() == replyNum) {
							System.out.println("댓글을 새로 입력해주세요 (1자 ~ 20자 입력 가능)");

							String newContent = "";
							Pattern pattern = Pattern.compile("^.{1,20}$"); // 1자 이상 50자 이하

							while (true) {
								newContent = Client.getInput("💬 수정할 댓글: ");
								if (pattern.matcher(newContent).matches())
									break;
								System.out.println("⚠ 댓글은 1자 이상, 20자 이하로 입력해주세요.");
							}

							reply.setContent(newContent);
							System.out.println("✅ 대댓글이 수정되었습니다.");
							edited = true;
							break;
					}
				}
				if (!edited) {
					System.err.println("⚠ 해당 대댓글이 존재하지 않습니다.");
				}
			} else {
				System.err.println("⚠ 해당 부모 댓글에 대댓글이 없습니다.");
			}
			return;
		}

		// **메인 댓글 수정 로직**
		if (commentsMap.containsKey(parentNum)) {
			Comment comment = commentsMap.get(parentNum);
				System.out.println("댓글을 새로 입력해주세요 (1자 ~ 20자 입력 가능)");

				String newContent = "";
				Pattern pattern = Pattern.compile("^.{1,20}$"); // 1자 이상 50자 이하

				while (true) {
					newContent = Client.getInput("💬 수정할 댓글: ");
					if (pattern.matcher(newContent).matches())
						break;
					System.out.println("⚠ 댓글은 1자 이상, 20자 이하로 입력해주세요.");
				}

				comment.setContent(newContent);
				System.out.println("✅ 댓글이 수정되었습니다.");
		} else {
			System.err.println("⚠ 해당 번호의 댓글이 존재하지 않습니다.");
		}
	}

	// 댓글 삭제
	public void deleteComment() {
		System.out.println("삭제할 댓글 번호를 입력해주세요.");
		String input = Client.getInput("> 삭제할 댓글 번호를 입력해주세요 (대댓글은 1-1 형식으로 입력해주세요) : ").trim();

		int parentNum = 0;
		int replyNum = 0;
		boolean isReply = false;

		try {
			// 1️⃣ **대댓글인지 확인 (1-1 같은 형식)**
			if (input.contains("-")) {
				String[] parts = input.split("-");

				// 부모 댓글 번호
				parentNum = Integer.parseInt(parts[0]);
				// 대댓글 번호
				replyNum = Integer.parseInt(parts[1]);

				isReply = true;
				if (!repliesMap.get(parentNum).get(replyNum-1).getUserId().equals(User.getNowUserId())
						&& !User.getNowUserId().equals("admin")) {
					System.out.println("본인이 작성한 댓글이 아닙니다.");
					return;
				}
			}
			// 2️ **메인 댓글 (숫자만 입력)**
			else {
				parentNum = Integer.parseInt(input);
				System.out.println(commentsMap.get(parentNum).getUserId());
				System.out.println(User.getNowUserId());
				if (!commentsMap.get(parentNum).getUserId().equals(User.getNowUserId())
						&& !User.getNowUserId().equals("admin")) {
					System.out.println("본인이 작성한 댓글이 아닙니다.");
					return;
				} else {
					
				}
			}
		} catch (NumberFormatException e) {
			System.err.println("⚠ 올바른 형식으로 입력해주세요! (예: 1 또는 1-1)");
			return;
		}

		// **대댓글 삭제 로직**
		if (isReply) {
			if (repliesMap.containsKey(parentNum)) {
				List<Comment> replyList = repliesMap.get(parentNum);
				Iterator<Comment> iterator = replyList.iterator();
				boolean deleted = false;

				while (iterator.hasNext()) {
					Comment reply = iterator.next();
					if (reply.getCommentNum() == replyNum) {
						iterator.remove(); // 해당 대댓글 삭제
						System.out.println("✅ 대댓글 " + parentNum + "-" + replyNum + "이 삭제되었습니다.");
						deleted = true;
						break;
					}
				}

				if (!deleted) {
					System.err.println("⚠ 해당 대댓글이 존재하지 않습니다.");
				}
			} else {
				System.err.println("⚠ 해당 부모 댓글에 대댓글이 없습니다.");
			}
			return;
		}

		// **메인 댓글 삭제 로직**
		if (commentsMap.containsKey(parentNum)) {
			commentsMap.remove(parentNum);
			System.out.println("✅ 댓글 " + parentNum + "이 삭제되었습니다.");

			if (repliesMap.containsKey(parentNum)) {
				repliesMap.remove(parentNum);
				System.out.println("📌 해당 댓글에 달린 모든 대댓글이 삭제되었습니다.");
			}
		} else {
			System.err.println("⚠ 해당 번호의 댓글이 존재하지 않습니다.");
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

	public void replySave() {
		File dir = new File(this.postPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(this.postPath + "\\replies.txt");

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			oos = new ObjectOutputStream(bos);

			oos.writeObject(repliesMap);

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
			}
			ois.close();
			bis.close();
			fis.close();
		} catch (Exception e) {
		}
	}

	public void replyLoad() {
		File commentsFile = new File(this.postPath + "\\replies.txt");
		if (!commentsFile.exists())
			return;
		try {
			FileInputStream fis = new FileInputStream(commentsFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);

			HashMap<Integer, List<Comment>> map = (HashMap) ois.readObject();
			if (map != null) {
				// 불러온 map데이터를 commnetsMap에 병합
				this.repliesMap.putAll(map);
				// 가장 큰 commentNum을 찾아
				int maxNum = map.keySet().stream().max(Integer::compareTo).orElse(0);
				Comment.setCommentCounter(maxNum + 1);
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
		replyLoad();
		commentPrint();
		while (true) {
			System.out.println("\n" + "=".repeat(LINE_LENGTH));
			String title = "📌 [ 댓글 메뉴 ] 📌";
			System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
			System.out.println("=".repeat(LINE_LENGTH));

			// 메뉴 표시
			System.out.printf("| %-8s | %-8s | %-8s | %-8s | %-8s | %-5s |\n", "1. 댓글 작성", "2. 대댓글 작성", "3. 댓글 삭제",
					"4. 댓글 수정", "5. 게시글 출력", "0. 종료");
			System.out.println("-".repeat(LINE_LENGTH));

			String choice = Client.getInput("선택: ");

			switch (choice) {
			case "1":
				writeComment();
				commentPrint();
				commentSave();
				replySave();
				break;
			case "2":
				writeReply();
				commentPrint();
				commentSave();
				replySave();
				break;
			case "3":
				deleteComment();
				commentPrint();
				commentSave();
				replySave();
				break;
			case "4":
				editComment();
				commentPrint();
				commentSave();
				replySave();
				break;
			case "5":
				saveAllPosts();
				break;
			case "0":
				commentSave();
				replySave();
				return;
			}
		}
	}

	public void adopPetcommentRun() {
		final int LINE_LENGTH = 75; // 출력 라인 길이 통일

		commentLoad();
		replyLoad();
		commentPrint();
		while (true) {
			System.out.println("=".repeat(LINE_LENGTH));
			String title = "📌 [ 입양 게시글 댓글 메뉴 ]";
			System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
			System.out.println("=".repeat(LINE_LENGTH));
			System.out.printf("|%-7s |%-7s |%-7s |%-7s |%-7s |%-7s |%-4s| \n", "1. 댓글 작성", "2. 대댓글 작성", "3. 댓글 삭제",
					"4. 댓글 수정", "5. 입양 신청", "6. 게시글 저장", "0.종료");
			System.out.println("=".repeat(LINE_LENGTH));

			String input = Client.getInput("선택: ").trim();
			System.out.println("-".repeat(LINE_LENGTH));

			switch (input) {
			case "1":
				writeComment();
				commentPrint();
				commentSave();
				replySave();
				break;
			case "2":
				writeReply();
				commentPrint();
				commentSave();
				replySave();
				break;
			case "3":
				deleteComment();
				commentPrint();
				commentSave();
				replySave();
				break;
			case "4":
				editComment();
				commentPrint();
				commentSave();
				replySave();
				break;
			case "5":
				writeAdoptPet();
				break;
			case "6":
				saveAllPosts();
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
				
				Client.getUserMap().get(getUserId()).getBoardMap().
				get(this.postPath.replace(this.getTitle(), "").
						replace(Client.defaultpath, "").replace("\\", "")).savePosts(); //포스트맵 세이브
			
				return;
			} else if (choice.toUpperCase().equals("N")) {
				System.out.println("입양 신청을 취소합니다.");
				return;
			} else {
				System.out.println("올바른 값을 입력해주세요(Y. 신청, N. 취소)");
			}
		}
	
	}

	public void saveAllPosts() {
		String path = "C:\\AdoptPet\\download\\";
		File directory = new File(path);

		// 폴더 없으면 생성
		if (!directory.exists()) {
			if (directory.mkdir()) {
			} else {
				return;
			}
		}

		String filePath = path + "post_" + postNum + ".txt";

		try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
			writer.println("==============================");
			writer.println("                    📌 게시글                     ");
			writer.println("==============================");
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
				writer.println("\n==============================");
				writer.println("                   💬 댓글 목록                   ");
				writer.println("==============================");

				for (Comment comment : commentsMap.values()) {
					writer.println("--------------------------------------------------");
					writer.printf("💬 번호   : %d%n", comment.getCommentNum());
					writer.printf("💬 작성자 : %s%n", comment.getAuthor());
					writer.printf("💬 작성일 : %s%n",
							comment.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
					writer.println("💬 내용:");
					writer.println(comment.getContent());

					// ✅ 대댓글 저장 (있으면)
					if (repliesMap.containsKey(comment.getCommentNum())) {
						for (Comment reply : repliesMap.get(comment.getCommentNum())) {
							writer.println("--------------------------------------------------");
							writer.printf("↳ 💬 대댓글 번호 : %d-%d%n", reply.getParentCommentNum(), reply.getCommentNum());
							writer.printf("↳ 💬 작성자      : %s%n", reply.getAuthor());
							writer.printf("↳ 💬 작성일      : %s%n",
									reply.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
							writer.println("↳ 💬 내용:");
							writer.println(reply.getContent());
						}
					}
					writer.println("--------------------------------------------------");
				}
			}
		} catch (IOException e) {
			System.err.println("파일 저장 중 오류가 발생했습니다.");
			e.printStackTrace();
		}
		System.out.println("게시글이 파일로 저장되었습니다.");
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
