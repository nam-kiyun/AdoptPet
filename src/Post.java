import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		String str = null;

		boolean check = false;
		System.out.println("익명으로 작성하시겠습니까? (y/n): ");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			str = br.readLine().toUpperCase();
			if (str.equals("Y")) {
				check = true;
			} else if (str.equals("N")) {
				check = false;
			} else {
				System.out.println("잘못된 입력입니다.");
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("댓글을 새로 입력해주세요 (1자 ~ 50자 입력 가능)");

		Pattern pattern = Pattern.compile("^.{1,50}$"); // 1자 이상 50자 이하

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			// 정규표현식으로 댓글 길이 검사
			while (true) {
				str = br.readLine();
				if (pattern.matcher(str).matches()) {
					break; // 유효한 입력이면 루프 종료
				}
				System.out.println("❌ 댓글은 1자 이상, 50자 이하로 입력해주세요.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 가장 높은 commentNum을 찾아서 자동 증가
		int newCommentNum = Comment.getNextCommentNum();
		String author = check ? "익명" : this.author;
		Comment comment = new Comment(newCommentNum, str, author, LocalDateTime.now());
		commentsMap.put(newCommentNum, comment);
		System.out.println("댓글이 작성되었습니다.");
	}

	public void commentPrint() {
		// 순서가 없는 Set을 Treeset으로 순서대로 정렬
		Set<Integer> set = new TreeSet<Integer>(this.commentsMap.keySet());
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH.mm.ss");
		System.out.println("번호\t댓글\t작성자\t작성시간");

		for (Integer number : set) {
			int num = this.commentsMap.get(number).getCommentNum();
			String comment = this.commentsMap.get(number).getContent();
			String author = this.commentsMap.get(number).getAuthor();
			String time = this.commentsMap.get(number).getCreateAt().format(dtf);
			System.out.printf("%d\t%s\t%s\t%s\n", num, comment, author, time);
		}
	}

	public void reverseCommentPrint() { // 최신순
		// Set은 순서가 없어서 List로 받아주고 최신순 정렬
		List<Integer> list = new ArrayList<Integer>(this.commentsMap.keySet());
		list.sort(Comparator.reverseOrder());
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH.mm.ss");
		System.out.println("번호\t댓글\t작성자\t작성시간");

		for (Integer number : list) {
			int num = this.commentsMap.get(number).getCommentNum();
			String comment = this.commentsMap.get(number).getContent();
			String author = this.commentsMap.get(number).getAuthor();
			String time = this.commentsMap.get(number).getCreateAt().format(dtf);
			System.out.printf("%d\t%s\t%s\t%s\n", num, comment, author, time);
		}
	}

	// 댓글 수정
	public void editComment() {
		Set<Integer> set = commentsMap.keySet();
		System.out.println("댓글 번호를 입력해주세요.");
		int n = 0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			n = Integer.parseInt(br.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean edit = false;
		for (Integer num : set) {
			if (n == num) {
				Comment comment = commentsMap.get(num);
				if (comment == null) {
					System.out.println("해당 댓글이 존재하지 않습니다.");
				}
				edit = true;
//				System.out.println("현재 userId: " + this.userId);
//	            System.out.println("댓글 userId: " + comment.getUserId());
				if (this.userId != null && this.userId.equals(comment.getUserId())) {
					System.out.println("댓글을 새로 입력해주세요");
					System.out.println("댓글을 새로 입력해주세요 (1자 ~ 50자 입력 가능)");

					String newContent = "";
					Pattern pattern = Pattern.compile("^.{1,50}$"); // 1자 이상 50자 이하

					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

						// 🔹 정규표현식으로 댓글 길이 검사
						while (true) {
							newContent = br.readLine();
							if (pattern.matcher(newContent).matches()) {
								break; // 유효한 입력이면 루프 종료
							}
							System.out.println("❌ 댓글은 1자 이상, 50자 이하로 입력해주세요.");
						}
						commentsMap.get(num).setContent(br.readLine());
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("댓글이 수정되었습니다.");
				} else {
					System.out.println("수정 권한이 없습니다.");
				}
				break;
			}
		}
		if (!edit) {
			System.out.println("잘못된 댓글 번호를 입력했습니다.");
		}
	}

	// 댓글 삭제
	public void deleteComment() {
		Set<Integer> set = commentsMap.keySet();
		Iterator<Integer> it = set.iterator();
		System.out.println("댓글 번호를 입력해주세요.");
		int n = 0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			n = Integer.parseInt(br.readLine());
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
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
					System.out.println("수정 권한이 없습니다.");
				}
				break;
			}
		}
		if (!delete) {
			System.out.println("잘못된 댓글 번호를 입력하였습니다.");
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
		commentLoad();
		while (true) {
			String input = null;
			System.out.println("1.댓글 작성\t2.댓글 수정\t3.댓글 삭제\t4.정렬\t0.종료");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				input = br.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			case "0":
				commentSave();
				return;
			}
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

}
