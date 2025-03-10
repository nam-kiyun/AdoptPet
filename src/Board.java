import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Board {

	private int boardNum; // 게시판번호
	private String boardName; // 게시판제목
	private String boardPath; // 경로
	private int postCounter = 1; // 게시글 번호 증가
	private HashMap<Integer, Post> postsMap; // 게시글 관리
	// private transient BufferedReader br; // 직렬화 대상에서 제외
	private transient BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public Board(String boardName, String boardPath) {
		this.boardName = boardName;
		this.boardPath = boardPath;
		this.postsMap = new HashMap<Integer, Post>();
		this.br = new BufferedReader(new InputStreamReader(System.in));

		loadPost();
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject(); // 기본 직렬화
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject(); // 기본 역직렬화
		this.br = new BufferedReader(new InputStreamReader(System.in)); // 다시 초기화
	}

	// 실행
	public void run() {
		while (true) {
			System.out.println("\n [" + boardName + "]");

			System.out.println("1. 모든 게시글 보기");
			System.out.println("2. 게시글 작성");
			System.out.println("3. 게시글 수정");
			System.out.println("4. 게시글 삭제");
			System.out.println("5. 뒤로 가기");
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
					return;
				default:
					System.out.println("다시 입력해주세요.");
				}
			} catch (IOException | NumberFormatException e) {
				System.out.println("숫자를 입력해주세요.");
			}
		}
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
			savePosts(post);

			System.out.println("게시글이 성공적으로 작성되었습니다.");
		} catch (IOException e) {
			System.out.println("게시글이 작성에 실패하였습니다.");
		}
	}

	// 게시글 파일에 저장(직렬화)
	private void savePosts(Post post) {

		String path = boardPath + "\\post_" + post.getPostNum() + ".txt";

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(new File(path));
			oos = new ObjectOutputStream(fos);

			oos.writeObject(post);

			System.out.println("게시글이 저장되었습니다. ");

			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 저장된 게시글 불러오기
	private void loadPost() {
		File boardFolder = new File(boardPath);

		File[] postFiles = boardFolder.listFiles((dir, name) -> name.startsWith("post_") && name.endsWith(".txt"));

		if (postFiles == null || postFiles.length == 0) {
			System.out.println("불러올 게시글이 없습니다.");
			return;
		}

		int maxPostNum = 0; // 가장 큰 postNum찾기위해
		System.out.println("저장된 게시글 목록:");

		for (File file : postFiles) {
			try (FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis)) {

				Post post = (Post) ois.readObject(); // 개별 Post 객체 읽기
				postsMap.put(post.getPostNum(), post);

				// 가장 높은 게시글 번호 찾기
				if (post.getPostNum() > maxPostNum) {
					maxPostNum = post.getPostNum();
				}

				System.out.println("--------------------------------");
				System.out.println("번호: " + post.getPostNum());
				System.out.println("제목: " + post.getTitle());
				System.out.println("작성자: " + post.getAuthor());
				System.out.println("작성일: " + post.getCreateAt());
				System.out.println("내용: " + post.getContent());
				System.out.println("--------------------------------\n");

			} catch (IOException | ClassNotFoundException e) {
				System.out.println("게시글 불러오기 실패: " + file.getName() + " (" + e.getMessage() + ")");
			}

			// 기존 게시글 중 가장 큰 번호를 찾고 그 다음 번호로 설정
			postCounter = maxPostNum + 1;
		}

		// 가장 높은 postNum을 기반으로 postCounter 설정
		postCounter = maxPostNum + 1;
	}

	// 게시글 번호를 최신 번호로 갱신
//				if (post.getPostNum() >= postCounter) {
//					postCounter = post.getPostNum() + 1;
//				}

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

			savePosts(post);
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
		if (postsMap.isEmpty()) {
			System.out.println("등록된 게시글이 없습니다.");
			return;
		}

		System.out.println("=========================게시글 목록=========================");
		for (Post post : postsMap.values()) {
			System.out.println("번호: " + post.getPostNum() + " | 제목: " + post.getTitle() + " | 내용: " + post.getContent()
					+ " | 작성자: " + post.getAuthor());
		}
		System.out.println("==========================================================");
	}

	// 특정 게시물 검색 (제목으로)
	public void searchPost(String word) {
		boolean find = false;

		for (Post post : postsMap.values()) {
//			if(post.getTitle().contains(word)) 
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