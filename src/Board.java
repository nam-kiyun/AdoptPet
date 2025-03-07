import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;

public class Board {
	private int BoardNum; // 게시판번호
	private String BoardName; // 게시판제목
	private HashMap<Integer, Post> postsMap; // 게시글
	private int postCounter = 1; // 게시글 번호 증가
	private String fileName; // 파일 이름
	private String folderPath = "C:\\AdoptPet\\cat";

	public Board(int BoardNum, String BoardName) {
		this.BoardNum = BoardNum;
		this.BoardName = BoardName;
		this.fileName = folderPath + "\\" + BoardName + ".txt"; // 게시판 파일 이름"
		this.postsMap = new HashMap<Integer, Post>();
		// 저장된 게시글 불러오기
		loadPost();
	}

	// 게시글 생성
	public Post writePost(String title, String content, String author) {
		Post post = new Post(postCounter++, title, content, author);
		postsMap.put(post.getPostNum(), post);
		savePosts();
		return post;
	}

	// 게시글 파일에 저장
	private void savePosts() {

		try {
			PrintWriter writer = new PrintWriter(new FileWriter(fileName));

			for (Post post : postsMap.values()) {
					writer.print(post);
			}
		} catch (Exception e) {
			System.out.println("파일을 저장할 수 없습니다." + e.getMessage());
			e.printStackTrace();
		}

	}

	// 저장된 파일 불러오기
	private void loadPost() {
		
	}

	// 게시글 수정 (해당 id랑 author)
	public void editPost(int postNum, String newTitle, String newContent) {
		if (postsMap.containsKey(postNum)) {
			Post post = postsMap.get(postNum);
			// post.editPost(newTitle, newContent);
			savePosts();
		} else {
			System.out.println("해당 게시글이 존재하지 않습니다.");
		}
	}

	// 게시글 삭제
	public void deletePost(int postNum) {
		// postMap에 해당 postNum 있는지 확인
		if (postsMap.containsKey(postNum)) {
			// 게시글 지우기
			postsMap.remove(postNum);

			// 게시글 지운거 (파일) 업데이트
			savePosts();
		}
	}

	// 특정 게시물 검색
	public void searchPost() {

	}

	// 게시물 정렬
	public void sortPost() {

	}

	public int getBoardNum() {
		return BoardNum;
	}

	public void setBoardNum(int boardNum) {
		BoardNum = boardNum;
	}

	public String getBoardName() {
		return BoardName;
	}

	public void setBoardName(String boardName) {
		BoardName = boardName;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
