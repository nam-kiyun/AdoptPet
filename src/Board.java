import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Board {

	private int boardNum; // 게시판번호
	private String boardName; // 게시판제목
	private String boardPath; // 경로
	private int postCounter = 1; // 게시글 번호 증가
	private HashMap<Integer, Post> postsMap; // 게시글 관리
	private transient BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // 직렬화 대상에서 제외

	public Board(String boardName, String boardPath) {
		this.boardName = boardName;
		this.boardPath = boardPath;
		this.postsMap = new HashMap<Integer, Post>();
		this.br = new BufferedReader(new InputStreamReader(System.in));

		loadPost();
	}

	// 실행
	public void run() {
		while (true) {
			System.out.println("\n [" + boardName + "]");

			System.out.println("1. 모든 게시글 보기");
			System.out.println("2. 게시글 작성");
			System.out.println("3. 게시글 수정");
			System.out.println("4. 게시글 삭제");
			System.out.println("5. 검색 하기");
			System.out.println("6. 뒤로 가기");
			System.out.print("선택: ");

			try {
				int choice = Integer.parseInt(br.readLine());

				switch (choice) {
				case 1:
					listAllPosts();
					break;
				case 2:
					writePost();
					break;
				case 3:
					editPost();
					break;
				case 4:
					deletePost();
					break;
				case 5:
					searchPost();
					break;
				case 6:
					return;
				default:
					System.out.println("다시 입력해주세요.");
				}
			} catch (IOException | NumberFormatException e) {
				System.out.println("숫자를 입력해주세요.");
			}
		}
	}

	// 게시글 목록 출력 함수 (공통)
	public void printPostList(HashMap<Integer, Post> postMap) {

		if (postMap.isEmpty()) {
			System.out.println("등록된 게시글이 없습니다.");
			return;
		}

		System.out.println("========================= 게시글 목록 =========================");

		for (Post post : postMap.values()) {
			System.out
					.println("번호: " + post.getPostNum() + " | 제목: " + post.getTitle() + " | 작성자: " + post.getAuthor());
		}

		System.out.println("=============================================================");

	}

	// 게시글 상세보기 (공통)
	private void printPostDetail(Post post) {
		if (post == null) {
			System.out.println("해당 게시글이 존재하지 않습니다.");
			return;
		}

		System.out.println("======================== 게시글 상세보기 ========================");
		System.out.println("번호: " + post.getPostNum());
		System.out.println("제목: " + post.getTitle());
		System.out.println("작성자: " + post.getAuthor());
		System.out.println("작성일: " + post.getCreateAt());
		System.out.println("내용: " + post.getContent());
		System.out.println("=============================================================");
	}

	// 게시글 작성
	public void writePost() {
		// 현재 로그인한 아이디
		// String author = Client.getNowUserId();

		// 테스트 유저
		String author = "test";

		if (author == null) {
			System.out.println("로그인한 사용자만 게시글을 작성할 수 있습니다.");
			return;
		}
		try {
			System.out.print("제목: ");
			String title = br.readLine();
			System.out.print("내용: ");
			String content = br.readLine();

			int postNum = postCounter++;
			Post post = new Post(postNum, title, content, author);

			postsMap.put(postNum, post);
			savePosts();

			System.out.println("게시글이 성공적으로 작성되었습니다.");
		} catch (IOException e) {
			System.out.println("게시글이 작성에 실패하였습니다.");
		}
	}

	// 게시글 파일에 저장(직렬화)
	private void savePosts() {

		String path = boardPath + "\\post.txt";

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(new File(path));
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
	private void loadPost() {
		String path = boardPath + "\\post.txt";
		File file = new File(path);

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

			System.out.println("번호\t제목\t작성자\t작성시간\t내용");

			for (Integer number : set) {
				int postNum = this.postsMap.get(number).getPostNum();
				String title = this.postsMap.get(number).getTitle();
				String author = this.postsMap.get(number).getAuthor();
				String createAt = this.postsMap.get(number).getCreateAt().format(dtf);
				String content = this.postsMap.get(number).getContent();

				//System.out.printf("%d\t%s\t%s\t%s\n", postNum, title, author, createAt);
			}
			ois.close();
			fis.close();
		} catch (Exception e) {
		}
	}

	// 게시글 수정
	public void editPost() {
		// 모든 게시글 출력
		listAllPosts();

		try {
			System.out.print(">몇 번 게시글을 수정하시겠습니까? ");
			int postNum = Integer.parseInt(br.readLine());

			if (!postsMap.containsKey(postNum)) {
				System.out.println("해당 게시글이 존재하지 않습니다.");
				return;
			}

			Post post = postsMap.get(postNum);
//			if (!post.getAuthor().equals(Client.getNowUserId())) {
//				System.out.println("작성자만 게시글을 수정할 수 있습니다.");
//				return;
//			}

			System.out.print("제목: ");
			post.setTitle(br.readLine());

			System.out.print("내용: ");
			post.setContent(br.readLine());

//			post.setUpdateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

			savePosts();
			System.out.println("게시글이 수정되었습니다.");
		} catch (IOException e) {
			System.out.println("입력 오류가 발생했습니다.");
		}
	}

	// 게시글 삭제
	public void deletePost() {
		// 게시글 목록을 먼저 출력-> 사용자가 삭제할 게시글 확인
		listAllPosts();

		try {
			System.out.print(">몇 번 게시글을 삭제하시겠습니까? ");
			int postNum = Integer.parseInt(br.readLine());

			if (!postsMap.containsKey(postNum)) { // 존재하지 않으면
				System.out.println("해당 번호의 게시글이 존재하지 않습니다.");
				return;
			}

			Post post = postsMap.get(postNum);
//			if (!post.getAuthor().equals(Client.getNowUserId())) {
//				System.out.println("작성자만 게시글을 삭제할 수 있습니다.");
//				return;
//			}
			postsMap.remove(postNum);

			// 파일 삭제
			File postFile = new File(boardPath + "\\post_" + postNum + ".txt");
			postFile.delete();

		} catch (IOException e) {
			System.out.println("입력 오류가 발생했습니다.");
		}
	}

	// 모든 게시글 출력
	public void listAllPosts() {

		// 게시글 목록 출력 함수(공통)
		printPostList(postsMap);

//		if (postsMap.isEmpty()) {
//			System.out.println("등록된 게시글이 없습니다.");
//			return;
//		}
//
//		// 모든 게시글 보기
//		System.out.println("=========================게시글 목록=========================");
//		for (Post post : postsMap.values()) {
//			System.out.println("번호: " + post.getPostNum() + " | 제목: " + post.getTitle() + " | 내용: " + post.getContent()
//					+ " | 작성자: " + post.getAuthor());
//		}
//		System.out.println("==========================================================");

		// 게시글 자세히 보기
		System.out.println("자세히 볼 게시글의 번호를 입력해주세요. (취소하려면 0)");
		System.out.println("선택:");

		try {
			int postNum = Integer.parseInt(br.readLine());

			if (postNum == 0) {
				System.out.println("게시글 상세보기를 취소했습니다.");
				return;
			}

			// 입력한 번호가 존재하면 출력
			if (postsMap.containsKey(postNum)) {
				Post post = postsMap.get(postNum);

				printPostDetail(post);

//				System.out.println("======================== 게시글 상세보기 ========================");
//				System.out.println("번호: " + post.getPostNum());
//				System.out.println("제목: " + post.getTitle());
//				System.out.println("작성자: " + post.getAuthor());
//				System.out.println("작성일: " + post.getCreateAt());
//				System.out.println("내용: " + post.getContent());
//				System.out.println("=============================================================");

			} else {
				System.out.println("해당 번호의 게시글이 존재하지 않습니다.");
			}

		} catch (NumberFormatException e) {
			System.out.println("숫자를 입력해주세요.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 특정 게시물 검색 (제목으로)
	public void searchPost() {
		try {
			System.out.println("제목명을 검색해주세요.");
			System.out.print("검색: ");
			String word = br.readLine().trim(); // 공백제거

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

			printPostList(filteredPosts); // ✅ 검색된 게시글만 출력

			System.out.print("자세히 볼 게시글의 번호를 입력해주세요. (취소하려면 0): ");
			try {
				int postNum = Integer.parseInt(br.readLine());

				if (postNum == 0) {
					System.out.println("게시글 상세보기를 취소했습니다.");
					return;
				}

				printPostDetail(postsMap.get(postNum)); // ✅ 상세보기 함수 활용

			} catch (NumberFormatException e) {
				System.out.println("숫자를 입력해주세요.");
			}

		} catch (IOException e) {
			System.out.println("입력 오류가 발생했습니다.");
		}
	}

	// 게시물 정렬 (최신순)
	public void sortPost() {
		List<Post> postList = new ArrayList<Post>(postsMap.values());

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

	public int getPostCounter() {
		return postCounter;
	}

	public void setPostCounter(int postCounter) {
		this.postCounter = postCounter;
	}

}
