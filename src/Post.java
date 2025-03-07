import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Post {
	private int postNum;
	private String title;
	private String content;
	private String author;
	private LocalDateTime createAt;
	private LocalDateTime updateDate;
	private String userId;
	private Map<Integer, Comment> commentsMap;

	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public Post(int postNum, String title, String content, String author, LocalDateTime createAt,
			LocalDateTime updateDate, Map<Integer, Comment> commentsMap) {
		this.postNum = postNum;
		this.title = title;
		this.content = content;
		this.author = author;
		this.createAt = createAt;
		this.updateDate = updateDate;
		this.userId = Client.nowUserId;
		this.commentsMap = new HashMap<Integer, Comment>();
	}

	public void writeComment() throws IOException {
		System.out.println("댓글을 입력하세요.");
		String str = br.readLine();
		Comment comment = new Comment(commentsMap.size(), str, this.author, LocalDateTime.now(), LocalDateTime.now());
		commentsMap.put(commentsMap.size(), comment);
	}

	public void editComment() throws IOException {
		Set<Integer> set = commentsMap.keySet();
		System.out.println("댓글 번호를 입력해주세요.");
		int n = Integer.parseInt(br.readLine()); 
		for(Integer num : set) {
			if(n==num) {
				if(this.userId.equals(commentsMap.get(num).getUserId()))
				System.out.println("댓글을 새로 입력해주세요");
				commentsMap.get(num).setContent(br.readLine());
			}
		}
	}

	public void deleteComment() {

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

	public LocalDateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDateTime updateDate) {
		this.updateDate = updateDate;
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

}
