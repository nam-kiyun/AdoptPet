import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class Board implements Serializable {
	private int boardNum; // 게시판번호
	private String boardName; // 게시판제목
	private String boardPath; // 경로
	private String base_path = "C:\\AdoptPet\\cat";
	private int postCounter = 1; // 게시글 번호 증가
	private HashMap<Integer, Post> postsMap; // 게시글 관리

	public Board(String BoardName) {
		this.boardName = boardName;
		// this.boardNum = boardNum;

		this.boardPath = base_path; // 게시판 파일 이름"
		this.postsMap = new HashMap<Integer, Post>();

		loadPost();
	}

	// 게시글 작성
	public void writePost(String title, String content, String author) {
		int postNum = postCounter++;
		Post post = new Post(postNum, title, author, content);

		// 확인용
		System.out.println("생성된 postNum: " + post.getPostNum());

		postsMap.put(postNum, post);
		savePosts(post);
	}

	// 게시글 파일에 저장
	private void savePosts(Post post) {
		String fileName = boardPath + "\\post_" + post.getPostNum() + ".txt";

		// 저장할 때 덮어씌우지 않기
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

			writer.write("번호: " + post.getPostNum() + "\n");
			writer.write("제목: " + post.getTitle() + "\n");
			// 로그인한 작성자 닉네임으로
			// writer.write("작성자: " + post.getAuthor() + "\n");
			writer.write("작성일: " + post.getCreateAt() + "\n");
			writer.write("내용: " + post.getContent() + "\n");
			writer.write("--------------------------------\n");
			System.out.println("게시글 저장완료: " + fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 저장된 파일 불러오기
	private void loadPost() {
		File folder = new File(boardPath);
//		if (!folder.exists()) {
//			folder.mkdir();
//		}

	}

	// 게시글 수정 (해당 postNum/author)
	public void editPost(int postNum, String newTitle, String newContent) {
		if (postsMap.containsKey(postNum)) {
			Post post = postsMap.get(postNum);

			post.setTitle(newTitle);
			post.setContent(newContent);

			savePosts(post); // 파일 다시 업데이트
			System.out.println("게시글 수정 완료되었습니다.");
		} else {
			System.out.println("해당 게시글이 없습니다.");
		}
	}

	// 게시글 삭제
	public void deletePost(int postNum) {
		//게시글 목록을 먼저 출력-> 사용자가 삭제할 게시글 확인
		listAllPosts();
		
		// postMap에 해당 postNum 있는지 확인
		if (postsMap.containsKey(postNum)) {
			System.out.println("해당 번호의 게시글이 존재하지 않습니다.");
			return;
		}
		// 게시글 지우기
		postsMap.remove(postNum);

		// 기존 파일도 삭제
		File postFile = new File(boardPath + "\\post_" + postNum + ".txt");
		if (postFile.exists() && postFile.delete()) {
			System.out.println("게시글" + postNum + "삭제 완료.");
		} else {
			System.out.println("게시글 삭제 완료, 파일 삭제 실패");
		}

	}

	// 모든 게시글 출력
	public void listAllPosts() {
		if (postsMap.isEmpty()) {
			System.out.println("등록된 게시글이 없습니다.");
			return;
		}

		System.out.println("=========게시글 목록=========");
		for (Post post : postsMap.values()) {
			System.out
					.println("번호: " + post.getPostNum() + " | 제목: " + post.getTitle() + " | 작성자: " + post.getAuthor());
		}
		System.out.println("==========================");
	}

	// 특정 게시물 검색 (미정)
	public void searchPost(String word) {
		boolean find = false;

		for (Post post : postsMap.values()) {
//			if(post.getTitle().contains(word)) 
		}
	}

	// 게시물 정렬 (최신순)
	public void sortPost() {

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
