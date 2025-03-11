import java.io.Serializable;
import java.time.LocalDateTime;

public class Comment implements Serializable {
	private static int commentCounter=1;
	private int commentNum;
	private String content;
	private String author;
	private String userId;
	private LocalDateTime createAt;
	
	
	
	public Comment(int commentNum,String content, String author, LocalDateTime createAt) {
		super();
		this.commentNum=commentNum;
		this.content = content;
		this.author = author;
		this.userId = Client.getNowUserId();
		this.createAt = createAt;
	}

	public static int getNextCommentNum() {
		return commentCounter++;
	}
	public static void setCommentCounter(int commentCounter) {
		Comment.commentCounter=commentCounter;
	}
	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
