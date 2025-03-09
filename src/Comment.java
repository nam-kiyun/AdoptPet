import java.time.LocalDateTime;

public class Comment {
	private int commentNum;
	private String content;
	private String author;
	private String userId;
	private LocalDateTime createAt;
	private LocalDateTime editAt;
	
	public Comment(int commentNum,String content, String author, LocalDateTime createAt, LocalDateTime editAt) {
		super();
		this.commentNum = commentNum;
		this.content = content;
		this.author = author;
		this.userId = Client.nowUserId;
		this.createAt = createAt;
		this.editAt = editAt;
	}

	@Override
	public String toString() {
		return "Comment [CommentNum=" + commentNum + ", content=" + content + ", author=" + author + ", createAt="
				+ createAt + ", editAt=" + editAt + "]";
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

	public LocalDateTime getEditAt() {
		return editAt;
	}

	public void setEditAt(LocalDateTime editAt) {
		this.editAt = editAt;
	}
	
	
}
